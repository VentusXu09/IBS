package com.ventus.ibs.gui;

import org.json.JSONObject;

/**
 * Created by ventus0905 on 05/05/2019
 */
public class ThemeConfig {

    private static final String TAG = "ThemeConfig";

    private static final String KEY_USE_DEFAULT_BG_COLOR = "headerBckgrdColorUsingDefault";

    private static final String KEY_CUSTOM_BG_COLOR = "headerBckgrdColor";

    private static final String KEY_USE_DEFAULT_FONT_COLOR = "headerFontColorUsingDefault";

    private static final String KEY_CUSTOM_FONT_COLOR = "headerFontColor";

    private static final String KEY_USE_DEFAULT_LOGO = "logoUsingDefault";

    private static final String KEY_CUSTOM_LOGO = "logoFilePath";

    private static final String KEY_CUSTOM_LOGO_X2 = "logo2XFilePath";

    private static final String KEY_CUSTOM_LOGO_X3 = "logo3XFilePath";

    private boolean useDefaultHeaderBgColor;

    private boolean useDefaultHeaderFontColor;

    private boolean useDefaultLogo;

    private String customHeaderBgColor;

    private String customHeaderFontColor;

    private String customLogoUrl;

    private String customLogoX2Url;

    private String customLogoX3Url;

    ThemeConfig() {
        super();
        setToDefault();
    }

    void parse(JSONObject themeConfigJson) {

        setToDefault();

        if (themeConfigJson != null) {
            useDefaultHeaderBgColor = themeConfigJson.optBoolean(KEY_USE_DEFAULT_BG_COLOR, true);
            useDefaultHeaderFontColor = themeConfigJson.optBoolean(KEY_USE_DEFAULT_FONT_COLOR, true);
            useDefaultLogo = themeConfigJson.optBoolean(KEY_USE_DEFAULT_LOGO, true);
            customHeaderBgColor = themeConfigJson.optString(KEY_CUSTOM_BG_COLOR, null);
            customHeaderFontColor = themeConfigJson.optString(KEY_CUSTOM_FONT_COLOR, null);
            customLogoUrl = themeConfigJson.optString(KEY_CUSTOM_LOGO, null);
            customLogoX2Url = themeConfigJson.optString(KEY_CUSTOM_LOGO_X2, null);
            customLogoX3Url = themeConfigJson.optString(KEY_CUSTOM_LOGO_X3, null);
        }
    }

    public boolean isUseDefaultHeaderBgColor() {
        return useDefaultHeaderBgColor;
    }

    public boolean isUseDefaultHeaderFontColor() {
        return useDefaultHeaderFontColor;
    }

    public boolean isUseDefaultLogo() {
        return useDefaultLogo;
    }

    public String getCustomHeaderBgColor() {
        return customHeaderBgColor;
    }

    public String getCustomHeaderFontColor() {
        return customHeaderFontColor;
    }

    public String getCustomLogoUrl() {
        return customLogoUrl;
    }

    public String getCustomLogoX2Url() {
        return customLogoX2Url;
    }

    public String getCustomLogoX3Url() {
        return customLogoX3Url;
    }

    void setToDefault() {
        useDefaultHeaderBgColor = true;
        useDefaultHeaderFontColor = true;
        useDefaultLogo = true;
        customHeaderBgColor = null;
        customHeaderFontColor = null;
        customLogoUrl = null;
        customLogoX2Url = null;
        customLogoX3Url = null;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ThemeConfig)) {
            return false;
        }

        ThemeConfig theme = (ThemeConfig)object;

        if (this.useDefaultHeaderBgColor != theme.useDefaultHeaderBgColor) {
            return false;
        } else if (this.useDefaultHeaderFontColor != theme.useDefaultHeaderFontColor) {
            return false;
        } else if (this.useDefaultLogo != theme.useDefaultLogo) {
            return false;
        } else if (this.customHeaderBgColor == null && theme.customHeaderBgColor != null) {
            return false;
        } else if (this.customHeaderBgColor != null
                && (!this.customHeaderBgColor.equals(theme.customHeaderBgColor))) {
            return false;
        } else if (this.customHeaderFontColor == null && theme.customHeaderFontColor != null) {
            return false;
        } else if (this.customHeaderFontColor != null
                && (!this.customHeaderFontColor.equals(theme.customHeaderFontColor))) {
            return false;
        } else if (this.customLogoUrl == null && theme.customLogoUrl != null) {
            return false;
        } else
        if (this.customLogoUrl != null && (!this.customLogoUrl.equals(theme.customLogoUrl))) {
            return false;
        } else if (this.customLogoX2Url == null && theme.customLogoX2Url != null) {
            return false;
        } else if (this.customLogoX2Url != null
                && (!this.customLogoX2Url.equals(theme.customLogoX2Url))) {
            return false;
        } else if (this.customLogoX3Url == null && theme.customLogoX3Url != null) {
            return false;
        } else if (this.customLogoX3Url != null
                && (!this.customLogoX3Url.equals(theme.customLogoX3Url))) {
            return false;
        }

        return true;
    }
}
