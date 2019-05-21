package com.ventus.ibs.gui;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by ventus0905 on 05/07/2019
 */
public class TimeAxisValueFormatter implements IAxisValueFormatter {
    private SimpleDateFormat mFormat;

    public TimeAxisValueFormatter() {
        mFormat = new SimpleDateFormat("HH:mm:ss");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long millis = TimeUnit.SECONDS.toMillis((long) value);
        return mFormat.format(millis);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
