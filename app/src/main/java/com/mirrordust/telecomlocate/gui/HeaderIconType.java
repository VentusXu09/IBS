package com.mirrordust.telecomlocate.gui;

import com.mirrordust.telecomlocate.R;

/**
 * Created by ventus0905 on 04/14/2019
 */
public enum HeaderIconType {
    HAMBURGER(R.drawable.ic_menu_black), // "☰" icon for access to the menu
    BACK(R.drawable.ic_home_arrow_back), // "<-" icon for going back in the call stack
    CLOSE(R.drawable.ic_close_wht_24dp)  // "X" icon to close (similar to BACK)
    ;

    private int mIconResource;

    HeaderIconType(int resource) {
        mIconResource = resource;
    }

    public int getIcon() {
        return this.mIconResource;
    }

    public static HeaderIconType getIconByName(final String iconName) {
        for (HeaderIconType headerIconType : values()) {
            if (headerIconType.name().equalsIgnoreCase(iconName)) {
                return headerIconType;
            }
        }
        return null;
    }
}
