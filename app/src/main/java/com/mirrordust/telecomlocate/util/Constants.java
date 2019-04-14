package com.mirrordust.telecomlocate.util;

import com.mirrordust.telecomlocate.util.TextUtils;

/**
 * Created by ventus0905 on 04/01/2019
 */

public class Constants {
    public static String LINE_SOURCE = "line_source";
    public static boolean FAKE_API = true;
    public static String SAMPLE_INDEX = "sample_index";

    public enum BaseStationType {
        GSM("GSM"), CDMA("CDMA"), LTE("LTE"), WCDMA("WCDMA");
        String value;

        BaseStationType(String value) {
            this.value =value;
        }

        public String getValue() {
            return value;
        }

        public static BaseStationType map(String value) {
            BaseStationType type = null;
            if (TextUtils.isEmpty(value)) {
                //Do nothing
            } else if (value.equalsIgnoreCase(GSM.value)) {
                type = GSM;
            } else if (value.equalsIgnoreCase(CDMA.value)) {
                type = CDMA;
            } else if (value.equalsIgnoreCase(LTE.value)) {
                type = LTE;
            }
            return type;
        }
    }
}
