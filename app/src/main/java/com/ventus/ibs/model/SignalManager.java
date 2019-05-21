package com.ventus.ibs.model;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.ventus.ibs.entity.Signal;
import com.ventus.ibs.util.PermissionHelper;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by LiaoShanhe on 2017/07/18/018.
 */

public class SignalManager extends PhoneStateListener implements PermissionHelper.PermissionListener {
    private static final String TAG = "SignalObserver";

    private Context mContext;
    private TelephonyManager mTelephonyManager;

    private Subscriber<? super Signal> mSubscriber;

    public static final String[] sPermissionRequested = {Manifest.permission.ACCESS_COARSE_LOCATION};

    public SignalManager(Context context) {
        mContext = context;
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
//        mTelephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void signalMeasuring(SignalStrength signalStrength) throws SecurityException {
        String ssignal = signalStrength.toString();
        String[] parts = ssignal.split(" ");

        Log.v(TAG, ssignal);

        int dB = -120;

        if (null == mTelephonyManager.getAllCellInfo() || mTelephonyManager.getAllCellInfo().size() == 0) {
            CellLocation cellLocation = mTelephonyManager.getCellLocation();
            if (cellLocation instanceof GsmCellLocation) {
                if (signalStrength.getGsmSignalStrength() != 99) {
                    // For GSM Signal Strength: dbm =  (2*ASU)-113.
                    int strengthInteger = -113 + 2 * signalStrength.getGsmSignalStrength();
                    dB = strengthInteger;
                    Log.e(TAG, "getEvdoDbm: " + signalStrength.getEvdoDbm());
                    Log.e(TAG, "getCdmaDbm: " + signalStrength.getCdmaDbm());
                }
            } else if (cellLocation instanceof CdmaCellLocation) {
                dB = signalStrength.getCdmaDbm();
            }
        } else {
            CellInfo connectedCell = mTelephonyManager.getAllCellInfo().get(0);

            if (connectedCell instanceof CellInfoLte) {
                // For Lte SignalStrength: dbm = ASU - 140.
                dB = Integer.parseInt(parts[8]) - 140;
            } else if (connectedCell instanceof CellInfoGsm) {
                if (signalStrength.getGsmSignalStrength() != 99) {
                    // For GSM Signal Strength: dbm =  (2*ASU)-113.
                    int strengthInteger = -113 + 2 * signalStrength.getGsmSignalStrength();
                    dB = strengthInteger;
                    Log.e(TAG, "getEvdoDbm: " + signalStrength.getEvdoDbm());
                    Log.e(TAG, "getCdmaDbm: " + signalStrength.getCdmaDbm());
                }
            } else if (connectedCell instanceof CellInfoCdma) {
                dB = signalStrength.getCdmaDbm();
            }
        }

        Log.e(TAG, "dB: " + dB);

        Signal signalRecord = new Signal();
//        PermissionHelper.checkPermissions((Activity) mContext, this, sPermissionRequested);
        int cellId = 0;
        try {
            CellLocation cellLocation = mTelephonyManager.getCellLocation();
            if (cellLocation instanceof GsmCellLocation) {
                GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                cellId = gsmCellLocation.getCid();
            } else if (cellLocation instanceof CdmaCellLocation) {
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
                cellId = cdmaCellLocation.getBaseStationId();
            }
        } catch (SecurityException e) {

        }
        signalRecord.setCellid(cellId);
        signalRecord.setDbm(dB);
        signalRecord.setGsm(signalStrength.isGsm());
        signalRecord.setSignalToNoiseRatio(signalStrength.getEvdoSnr()); //Get the signal to noise ratio.
        signalRecord.setEvdoEcio(signalStrength.getEvdoEcio());  //Get the EVDO Ec/Io value in dB*10
        signalRecord.setLevel(signalStrength.getLevel());   //Retrieve an abstract level value for the overall signal strength.

        mSubscriber.onNext(signalRecord);
        mSubscriber.onCompleted();
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        Log.e(TAG, "信号强度变化");
        signalMeasuring(signalStrength);
    }

    private void startListening() {
        mTelephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void stopListening() {
        mTelephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
    }

    public rx.Observable<Signal> observeOnce() {
        return observe().flatMap(new Func1<Signal, Observable<Signal>>() {
            @Override
            public rx.Observable<Signal> call(Signal signalRecord) {
                stopListening();
                return rx.Observable.just(signalRecord);
            }
        });
    }

    public rx.Observable<Signal> observe() {
        return rx.Observable.create(new rx.Observable.OnSubscribe<Signal>() {
            @Override
            public void call(Subscriber<? super Signal> subscriber) {
                mSubscriber = subscriber;
                startListening();
            }
        });
    }

    @Override
    public void onPermissionGranted(@NonNull String... permission) {
    }

    @Override
    public void onPermissionDenied(@NonNull String permission) {

    }

    @Override
    public void onPermissionDisable(@NonNull String permission) {

    }
}
