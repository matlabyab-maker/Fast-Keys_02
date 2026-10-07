package com.fastkeyboard.nova;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/** Quick Settings control for changing the size of the mouse-control window. */
public class MouseResizeQuickTileService extends TileService {
    @Override public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override public void onClick() {
        super.onClick();
        MouseAccessibilityService s = MouseAccessibilityService.getInstance();
        if (s != null) s.cycleMousePanelSize();
        updateTile();
    }

    private void updateTile() {
        Tile t = getQsTile();
        if (t == null) return;
        t.setState(Tile.STATE_ACTIVE);
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            t.setIcon(Icon.createWithResource(this, com.fastkeyboard.nova.R.drawable.ic_launcher));
        }
        t.setLabel("تغییر اندازه موس");
        t.updateTile();
    }
}
