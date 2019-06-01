package com.ventus.ibs.util;

/**
 * Created by ventus0905 on 04/01/2019
 */

public class Constants {
    public static String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoibWlycm9yZHVzdCIsImEiOiJjaXNjeXc2Y3owMDBmMnpwZjd2YnZvaWZhIn0.luGgUhgNCUpyNL4cACktHA";
    public static String UPLOAD_URL = "http://116.62.124.55:8080/uploads";

    public static Long DEFAULT_SAMPLING_INTERVAL = 10l;

    public static String LINE_SOURCE = "line_source";
    public static boolean FAKE_API = false;
    public static String SAMPLE_INDEX = "sample_index";

    public static final float CHART_MAXINUM = 150f;
    public static final float CHART_MININUM = -120f;


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
            if (StringUtils.isEmpty(value)) {
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
