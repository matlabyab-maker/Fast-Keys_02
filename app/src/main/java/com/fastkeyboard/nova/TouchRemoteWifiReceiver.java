package com.fastkeyboard.nova;

import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/** Wi-Fi receiver for Fast_Remote_1. Listens for the shared Fast Keyboard remote protocol. */
public final class TouchRemoteWifiReceiver {
    public static final int PORT = 38555;
    private static final String HELLO = "FKREMOTE 1";
    private final MouseAccessibilityService service;
    private final Handler main = new Handler(Looper.getMainLooper());
    private volatile boolean running;
    private ServerSocket server;
    private Thread acceptThread;
    private volatile Socket client;

    public TouchRemoteWifiReceiver(MouseAccessibilityService service) {
        this.service = service;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        acceptThread = new Thread(this::acceptLoop, "FastKeyboard-WifiRemote");
        acceptThread.start();
    }

    private void acceptLoop() {
        try {
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(PORT));
            while (running) {
                Socket s = server.accept();
                if (!running) { closeQuietly(s); break; }
                Socket old = client;
                client = s;
                closeQuietly(old);
                configure(s);
                handleClient(s);
                if (client == s) client = null;
                closeQuietly(s);
            }
        } catch (IOException ignored) {
            // Normal when the service is destroyed and the socket is closed.
        } finally {
            closeQuietly(server);
            server = null;
        }
    }

    private void configure(Socket s) throws SocketException {
        s.setTcpNoDelay(true);
        s.setKeepAlive(true);
        s.setSoTimeout(0);
    }

    private void handleClient(Socket s) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {
            String hello = reader.readLine();
            if (!HELLO.equals(hello)) return;
            writer.write("OK FKREMOTE 1\n");
            writer.flush();
            // The remote connection itself is enough to make the desktop cursor
            // visible. Previously the cursor was created only after the first
            // MOVE/PAGE command, so a newly connected remote looked connected
            // but showed no pointer. Keep the cursor independent of the mouse
            // control panel and show it immediately after the handshake.
            main.post(service::showCursorFromRemote);

            String line;
            while (running && !s.isClosed() && (line = reader.readLine()) != null) {
                dispatch(line.trim());
            }
        } catch (IOException ignored) {
            // Remote disconnected.
        }
    }

    private void dispatch(String line) {
        if (line.isEmpty()) return;
        main.post(() -> handleOnMain(line));
    }

    private void handleOnMain(String line) {
        try {
            String[] p = line.split("\\s+");
            if (p.length == 0) return;
            if ("MOVE".equals(p[0]) && p.length >= 3) {
                float dx = Float.parseFloat(p[1]);
                float dy = Float.parseFloat(p[2]);
                service.moveRelativeFromRemote(dx, dy);
                return;
            }
            if ("SCROLL".equals(p[0]) && p.length >= 2) {
                float dy = Float.parseFloat(p[1]);
                service.scrollFromRemote(dy);
                return;
            }
            if ("BUTTON".equals(p[0]) && p.length >= 2) {
                switch (p[1]) {
                    case "LEFT_CLICK": service.clickFromRemote(false); break;
                    case "RIGHT_CLICK": service.clickFromRemote(true); break;
                    case "DRAG_START": service.beginDragFromRemote(); break;
                    case "DRAG_END": service.endDragFromRemote(); break;
                    case "POINT_ZOOM": MouseAccessibilityService.toggleMagnifierFromKeyboard(); break;
                    case "COPY": service.copyFromRemote(); break;
                    case "PASTE": service.pasteFromRemote(); break;
                    case "BACK": service.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK); break;
                    case "PAGE_UP": service.pageFromRemote(-1); break;
                    case "PAGE_DOWN": service.pageFromRemote(1); break;
                    default: break;
                }
                return;
            }
            if ("KEY".equals(p[0]) && p.length >= 2) {
                switch (p[1]) {
                    case "BACK": service.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK); break;
                    case "COPY": service.copyFromRemote(); break;
                    case "PASTE": service.pasteFromRemote(); break;
                    case "PAGE_UP": service.pageFromRemote(-1); break;
                    case "PAGE_DOWN": service.pageFromRemote(1); break;
                    default: break;
                }
            }
        } catch (Exception ignored) {
            // Ignore malformed remote packets; keep the connection alive.
        }
    }

    public synchronized void stop() {
        running = false;
        closeQuietly(client);
        closeQuietly(server);
        client = null;
        server = null;
        if (acceptThread != null) acceptThread.interrupt();
        acceptThread = null;
    }

    public boolean isRunning() { return running; }

    public static String[] getLocalIpv4Addresses() {
        ArrayList<String> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress a = addresses.nextElement();
                    if (a instanceof Inet4Address && !a.isLoopbackAddress()) {
                        result.add(a.getHostAddress());
                    }
                }
            }
        } catch (Exception ignored) {}
        return result.toArray(new String[0]);
    }

    private static void closeQuietly(Closeable c) { if (c != null) try { c.close(); } catch (Exception ignored) {} }
    private static void closeQuietly(Socket s) { if (s != null) try { s.close(); } catch (Exception ignored) {} }
    private static void closeQuietly(ServerSocket s) { if (s != null) try { s.close(); } catch (Exception ignored) {} }
}
