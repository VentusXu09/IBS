package com.ventus.ibs.util;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;

/**
 * Created by ventus0905 on 05/05/2019
 */
public class UIUtils {
    //icon name
    public static final String ID_ICON_MARKER = "marker_64dp";
    public static final String ID_CHECKED_FLAG = "checkered_flag_64dp";

    public static void setToolbarBackIcon(Toolbar toolbar, int resId, Integer toColor){
        if (toolbar == null || toColor == null)
            return;

        toolbar.setNavigationIcon(resId);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(createColorFilter(toColor));
        }
    }

    public static void setToolbarBackIcon(Toolbar toolbar, int resId, Integer toColor, PorterDuff.Mode mode) {
        if (toolbar == null || toColor == null)
            return;

        Drawable navigationIcon = AppCompatResources.getDrawable(toolbar.getContext(), resId);
        if (navigationIcon != null) {
            navigationIcon.mutate().setColorFilter(new PorterDuffColorFilter(toColor, mode));
            if (toolbar.getNavigationIcon() != navigationIcon) {
                toolbar.setNavigationIcon(navigationIcon);
                //ensure toolbar is updated with theme, causes slight delay
                toolbar.post(() -> toolbar.setNavigationIcon(navigationIcon));
            }
        }
    }

    public static ColorFilter createColorFilter(int toColor) {

        int red = (toColor >> 16) & 0xff;
        int green = (toColor >> 8) & 0xff;
        int blue = toColor & 0xff;

        // a 4x5 color matrix
        float[] matrix = {
                0, 0, 0, 0, red,   // change RED part to red
                0, 0, 0, 0, green, // change GREEN part to green
                0, 0, 0, 0, blue,  // change BLUE part to blue
                0, 0, 0, 1, 0      // keep alpha
        };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);

        return colorFilter;
    }
}
