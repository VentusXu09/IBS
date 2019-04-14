package com.mirrordust.telecomlocate.model;

import android.content.Context;
import android.os.Build;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.mirrordust.telecomlocate.entity.BaseStation;
import com.mirrordust.telecomlocate.util.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.functions.FuncN;

/**
 * Created by LiaoShanhe on 2017/07/18/018.
 */

public class BaseStationManager {
    private static final String TAG = "BaseStationManager";

    private TelephonyManager mTelephonyManager;
    private Context mContext;

    public BaseStationManager(Context context) {
        mContext = context;
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private BaseStation checkInvalidData(BaseStation cellularTower) {
        if (cellularTower == null) {
            return null;
        }
        if (cellularTower.getMcc() != Integer.MAX_VALUE &&
                cellularTower.getMnc() != Integer.MAX_VALUE &&
                cellularTower.getCid() != Integer.MAX_VALUE &&
                cellularTower.getLac() != Integer.MAX_VALUE) {
            return cellularTower;
        }
        return null;
    }

    private BaseStation bindData(CellInfo cellInfo) {
        BaseStation baseStation = null;
        Log.v(TAG, cellInfo.toString());
        Log.v(TAG, "" + Integer.MAX_VALUE);
        int mcc, mnc;
        mcc = Integer.valueOf(mTelephonyManager.getNetworkOperator().substring(0, 3));
        mnc = Integer.valueOf(mTelephonyManager.getNetworkOperator().substring(3, 5));

        if (cellInfo instanceof CellInfoWcdma) {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
            if (null != cellSignalStrengthWcdma) {
                baseStation = new BaseStation().newInstance(mcc, mnc, cellIdentityWcdma.getLac(), cellIdentityWcdma.getCid(),
                        getArfcn(cellIdentityWcdma), cellIdentityWcdma.getPsc(), 0, 0,
                        cellSignalStrengthWcdma.getAsuLevel(), cellSignalStrengthWcdma.getLevel(), cellSignalStrengthWcdma.getDbm(),
                        Constants.BaseStationType.WCDMA.getValue()
                        );
            } else {
                baseStation = new BaseStation().newInstance(mcc, mnc, cellIdentityWcdma.getLac(), cellIdentityWcdma.getCid(),
                        getArfcn(cellIdentityWcdma), cellIdentityWcdma.getPsc(), 0, 0,
                        Constants.BaseStationType.WCDMA.getValue());
            }
        } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
            if (cellSignalStrengthLte != null) {
                baseStation = new BaseStation().newInstance(cellIdentityLte.getMcc(), cellIdentityLte.getMnc(), cellIdentityLte.getTac(), cellIdentityLte.getCi(),
                        getArfcn(cellIdentityLte), cellIdentityLte.getPci(), 0, 0,
                        cellSignalStrengthLte.getAsuLevel(), cellSignalStrengthLte.getLevel(), cellSignalStrengthLte.getDbm(),
                        Constants.BaseStationType.LTE.getValue());
            } else {
                baseStation = new BaseStation().newInstance(cellIdentityLte.getMcc(), cellIdentityLte.getMnc(), cellIdentityLte.getTac(), cellIdentityLte.getCi(),
                        getArfcn(cellIdentityLte), cellIdentityLte.getPci(), 0, 0,
                        Constants.BaseStationType.LTE.getValue());
            }
        } else if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
            if (cellSignalStrengthGsm != null) {
                baseStation = new BaseStation().newInstance(mcc, mnc, cellIdentityGsm.getLac(), cellIdentityGsm.getCid(),
                        getArfcn(cellIdentityGsm), cellIdentityGsm.getPsc(), 0, 0,
                        cellSignalStrengthGsm.getAsuLevel(), cellSignalStrengthGsm.getLevel(), cellSignalStrengthGsm.getDbm(),
                        Constants.BaseStationType.GSM.getValue());
            } else {
                baseStation = new BaseStation().newInstance(mcc, mnc, cellIdentityGsm.getLac(), cellIdentityGsm.getCid(),
                        getArfcn(cellIdentityGsm), cellIdentityGsm.getPsc(), 0, 0,
                        Constants.BaseStationType.GSM.getValue());
            }
        } else if (cellInfo instanceof CellInfoCdma) {
            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
            CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
            CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
            if (null == cellSignalStrengthCdma) {
                baseStation = new BaseStation().newInstance(mcc, mnc, cellIdentityCdma.getNetworkId(), cellIdentityCdma.getBasestationId(),
                        cellIdentityCdma.getNetworkId(), cellIdentityCdma.getBasestationId(), cellIdentityCdma.getLongitude(), cellIdentityCdma.getLatitude(),
                        Constants.BaseStationType.CDMA.getValue());
            } else {
                baseStation = new BaseStation().newInstance(mcc, mnc, cellIdentityCdma.getNetworkId(), cellIdentityCdma.getBasestationId(),
                        cellIdentityCdma.getNetworkId(), cellIdentityCdma.getBasestationId(), cellIdentityCdma.getLongitude(), cellIdentityCdma.getLatitude(),
                        cellSignalStrengthCdma.getAsuLevel(), cellSignalStrengthCdma.getCdmaLevel(), cellSignalStrengthCdma.getCdmaDbm(),
                        Constants.BaseStationType.CDMA.getValue());
            }
        }
        return baseStation;
    }

    public BaseStation getConnectedTower() {
        int mcc, mnc;
        mcc = Integer.valueOf(mTelephonyManager.getNetworkOperator().substring(0, 3));
        mnc = Integer.valueOf(mTelephonyManager.getNetworkOperator().substring(3, 5));
        BaseStation tower = new BaseStation();
        tower.setMcc(mcc);
        tower.setMnc(mnc);
        try {
            CellLocation cellLocation = mTelephonyManager.getCellLocation();
            if (cellLocation instanceof GsmCellLocation) {
                GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                tower.setBsic_psc_pci(gsmCellLocation.getPsc());
                tower.setCid(gsmCellLocation.getCid());
                tower.setLac(gsmCellLocation.getLac());
                tower.setType(Constants.BaseStationType.GSM.getValue());
            } else if (cellLocation instanceof CdmaCellLocation){
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
                tower.setCid(cdmaCellLocation.getBaseStationId());
                tower.setLac(cdmaCellLocation.getNetworkId());
                tower.setBsic_psc_pci(0);
                tower.setType(Constants.BaseStationType.CDMA.getValue());
            } else {
            }
            Log.v(TAG, "Get connected tower" + tower.getType());
            Log.v(TAG, cellLocation.toString());
        } catch (SecurityException e) {
            Log.e(TAG, "No permission Manifest.permission.ACCESS_COARSE_LOCATION");
        }
        return tower;
    }

    /**
     * Get information of all cells that can be listened by the phone.
     * Call getAllCellInfo() and getNeighboringCellInfo() respectively,
     * since one or two of these api may return null.
     *
     * @return List of BaseStation, BaseStation is a class contains cell info
     */
    public List<BaseStation> getTowerList() throws SecurityException{
        List<BaseStation> cellularTowerList = new ArrayList<>();

        /* Use {@link #getAllCellInfo} which returns a superset of the information
         * from NeighboringCellInfo.
         */
        List<CellInfo> cellInfoList = null;
        cellInfoList = mTelephonyManager.getAllCellInfo();

        /*
        * decide which list will be used:
        * flag = 0, use cellInfoList,
        * flag = -1, use getCellLocation.
        * */
        int flag = null == cellInfoList ? -1 : 0;

        Log.v(TAG, "flag = " + flag);

        switch (flag) {
            case 0: {
                for (int i = 0; i < cellInfoList.size(); i++) {
                    final CellInfo cellInfo = cellInfoList.get(i);
                    final BaseStation cellularTower = bindData(cellInfo);
                    if (cellularTower != null) {
                        cellularTowerList.add(cellularTower);
                    }
                }
                return cellularTowerList;
            }
            case -1: {
                BaseStation cellularTower = getConnectedTower();
                if (cellularTower != null) {
                    cellularTowerList.add(cellularTower);
                }
                return cellularTowerList;
            }
            default: {
                Log.v(TAG, "getAllCellInfo() & getNeighboringCellInfo() both return null");
                return cellularTowerList;
            }
        }
    }

    private rx.Observable<BaseStation> locationTower(final BaseStation cellularTower) {
        return rx.Observable.create(new Observable.OnSubscribe<BaseStation>() {
            @Override
            public void call(final Subscriber<? super BaseStation> subscriber) {
                subscriber.onNext(cellularTower);
                subscriber.onCompleted();
                /*ServiceGenerator.changeApiBaseUrl("http://opencellid.org");
                final CellIdClient cellIdClient = ServiceGenerator.createService(CellIdClient.class);
                Call<CellIdResponse> cellIdResponseCall = cellIdClient.cellInformations("1085e718-c5a6-4392-9062-e57527c7bd97",
                        cellularTower.getMcc(),
                        cellularTower.getMnc(),
                        cellularTower.getLac(),
                        cellularTower.getCid(),
                        "json");

                cellIdResponseCall.enqueue(new Callback<CellIdResponse>() {
                    @Override
                    public void onResponse(Call<CellIdResponse> call, Response<CellIdResponse> response) {
                        Log.v(TAG, "request : " + call.request().toString());
                        Log.v(TAG, "response request : " + response.body());
                        Log.v(TAG, "response errorBody : " + response.errorBody());

                        CellIdResponse cellIdResponse = response.body();

                        if (cellIdResponse != null) {
                            cellularTower.setLat(cellIdResponse.getLat());
                            cellularTower.setLon(cellIdResponse.getLon());
                        }

                        subscriber.onNext(cellularTower);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Call<CellIdResponse> call, Throwable t) {
                        Log.e(TAG, call.request().toString());
                        Log.e(TAG, t.toString());
//                        subscriber.onError(t);
                        subscriber.onNext(cellularTower);
                        subscriber.onCompleted();
                    }
                });*/
            }
        });
    }

    public rx.Observable<List<BaseStation>> nerbyTower() {
        return rx.Observable.create(new Observable.OnSubscribe<List<BaseStation>>() {
            @Override
            public void call(final Subscriber<? super List<BaseStation>> subscriber) {
                final List<BaseStation> cellularTowerList = getTowerList();
                List<Observable<BaseStation>> observableList = new ArrayList<>();

                if (cellularTowerList.size() == 0) {
                    subscriber.onNext(cellularTowerList);
                    subscriber.onCompleted();
                }

                for (BaseStation tower : cellularTowerList) {
                    Observable<BaseStation> observable = locationTower(tower);
                    observableList.add(observable);
                }

                rx.Observable.zip(observableList, new FuncN<List<BaseStation>>() {
                    @Override
                    public List<BaseStation> call(Object... args) {
                        List<BaseStation> listResponse = new ArrayList<>();

                        for (int i = 0; i < args.length; i++) {
                            listResponse.add((BaseStation) args[0]);
                        }
                        Log.v(TAG, "Request finished length data : " + args.length);
                        Log.v(TAG, "Get list response : " + listResponse.size());
                        return listResponse;
                    }
                }).subscribe(new Subscriber<List<BaseStation>>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "Completed all services API");
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "get error : " + e.toString());
                    }

                    @Override
                    public void onNext(List<BaseStation> towerList) {
                        Realm realm = Realm.getDefaultInstance();

                        realm.beginTransaction();
                        realm.copyToRealm(towerList);
                        realm.commitTransaction();

                        Log.v(TAG, "Get list response cell Id : " + towerList.size());
                        subscriber.onNext(cellularTowerList);
                    }
                });
            }
        });
    }

    private int getArfcn(Object cellIdentity) {
        int arfcn = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (cellIdentity instanceof CellIdentityWcdma) {
                CellIdentityWcdma cellIdentityCdma = (CellIdentityWcdma) cellIdentity;
                arfcn = cellIdentityCdma.getUarfcn();
            } else if (cellIdentity instanceof CellIdentityGsm) {
                CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) cellIdentity;
                arfcn = cellIdentityGsm.getArfcn();
            } else if (cellIdentity instanceof CellIdentityLte) {
                CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
                arfcn = cellIdentityLte.getEarfcn();
            }
        }
        return arfcn;
    }
}
