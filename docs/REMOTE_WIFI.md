# Fast_Remote_1 ↔ Fast-Keys_02 — Wi‑Fi

Fast-Keys_02 v1.43 includes the Wi‑Fi receiver for the separate `Fast_Remote_1` repository.

- Transport: TCP
- Port: `38555`
- Handshake: `FKREMOTE 1`
- MOVE: `MOVE <dx> <dy>`
- SCROLL: `SCROLL <dy>`
- Buttons: `BUTTON LEFT_CLICK`, `BUTTON RIGHT_CLICK`, `BUTTON DRAG_START`, `BUTTON DRAG_END`

Both devices should be on the same Wi‑Fi network. Enable **موس سیستمی** in Android Accessibility on the FastKeyboard device. In Fast-Keys_02 open **اطلاعات اتصال ریموت Wi‑Fi** to see the local IPv4 address, then enter that address in Fast_Remote_1.

## Remote_For_Fast_Keys safety mapping

The receiver accepts explicit `COPY`, `PASTE`, `PAGE_UP` and `PAGE_DOWN` commands.
Only `LEFT_CLICK` calls the generic mouse click routine. Copy/Paste use the focused
accessibility text node; Page Up/Page Down use the dedicated page/scroll handler.
This prevents the remote menu controls from falling through to an unintended left click.
