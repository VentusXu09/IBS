package com.ventus.ibs.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.mapbox.mapboxsdk.log.Logger;
import com.ventus.ibs.R;

/**
 * Created by ventus0905 on 05/05/2019
 */
public class ThemeController {

    private static final String TAG = "ThemeController";

    private static ThemeListener themeListener;

    private ThemeChangeReceiver receiver = new ThemeChangeReceiver();

    private static ThemeController instance;

    private static final String BRAND_BLUE_COLOR = "#3f5161";
    private static final String TILE_BACKGROUND_COLOR = "#ffffff";
    private static final String BRAND_FONT_COLOR = "#CAE4FB";

    private ThemeController() {
    }

    public static ThemeController getInstance() {
        if (instance == null) {
            synchronized (ThemeController.class) {
                instance = new ThemeController();
            }
        }
        return instance;
    }

    public interface ThemeListener {
        void setupTheme();
    }

    public static class ColorTheme {
        public Integer mNavBackgroundColor;
        public Integer mNavTitleTextColor;
        public Integer mNavButtonTextColor;

        /**
         * Check it is the default color theme or not.
         * @return
         */
        public boolean isDefaultColorTheme() {
            return mNavBackgroundColor == Color.parseColor(BRAND_BLUE_COLOR)
                    && mNavTitleTextColor == Color.parseColor(TILE_BACKGROUND_COLOR)
                    && mNavButtonTextColor == Color.parseColor(BRAND_FONT_COLOR);
        }
    }

    public void initTheme(ThemeListener listener) {
        themeListener = listener;
        listener.setupTheme();
    }

    public static ThemeConfig getThemeConfig() {
        ThemeConfig themeConfig = new ThemeConfig();
        themeConfig.setToDefault();
        return themeConfig;
    }

    public static ColorTheme getColorTheme(Context ctx) {
        ColorTheme colorTheme = new ColorTheme();
        // in case ctx is null, pass a default colorTheme to avoid crash
        if (ctx == null) {
            colorTheme.mNavBackgroundColor = Color.parseColor(BRAND_BLUE_COLOR);
            colorTheme.mNavTitleTextColor = Color.parseColor(TILE_BACKGROUND_COLOR);
            colorTheme.mNavButtonTextColor = Color.parseColor(BRAND_FONT_COLOR);
            return colorTheme;
        }
        ThemeConfig theme = getThemeConfig();
        colorTheme.mNavBackgroundColor = ContextCompat.getColor(ctx, R.color.brand_blue_color);
        colorTheme.mNavTitleTextColor = ContextCompat.getColor(ctx, R.color.tile_background_color);
        colorTheme.mNavButtonTextColor = ContextCompat.getColor(ctx, R.color.brand_font_color);
        try {
            if(theme == null){
                return colorTheme;
            }
            String bColorStr = theme.getCustomHeaderBgColor();
            String fColorStr = theme.getCustomHeaderFontColor();

            // SFLogger.info(TAG, ">>>>>Setting Theme Front=" + fColorStr + ",
            // Back=" + bColorStr);
            if (!theme.isUseDefaultHeaderBgColor() && !TextUtils.isEmpty(bColorStr)) {
                colorTheme.mNavBackgroundColor = Color.parseColor(bColorStr);
            }
            // foreground color, default to SF color if not defined
            if (!theme.isUseDefaultHeaderFontColor() && !TextUtils.isEmpty(fColorStr)) {
                colorTheme.mNavTitleTextColor = Color.parseColor(fColorStr);
                colorTheme.mNavButtonTextColor = Color.parseColor(fColorStr);
            }
        } catch (Exception ex) {
            Logger.e(TAG, "Unable to get theme", ex);
        }

        return colorTheme;
    }


    public void uninstallListener(){
        themeListener = null;
    }

    private static class ThemeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (themeListener != null)
                themeListener.setupTheme();
        }
    }

    public static boolean isDefaultTheme(ThemeController.ColorTheme colorTheme) {
        return (colorTheme.mNavBackgroundColor == Color.parseColor(BRAND_BLUE_COLOR)
                && colorTheme.mNavButtonTextColor == Color.parseColor(BRAND_FONT_COLOR));
    }
}
