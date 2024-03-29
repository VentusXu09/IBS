package com.ventus.ibs.gui.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.LongSparseArray;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ventus.ibs.entity.*;
import com.ventus.ibs.model.serializer.*;
import com.ventus.ibs.util.Constants;
import com.ventus.ibs.gui.interf.DataContract;
import com.ventus.ibs.model.DataHelper;
import com.ventus.ibs.model.DeviceManager;
import com.ventus.ibs.pojo.UploadResponse;
import com.ventus.ibs.util.Utils;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by LiaoShanhe on 2017/07/30/030.
 */

public class DataPresenter implements DataContract.Presenter {

    private static final String TAG = "DataPresenter";

    private static final String sep = ",";

    private LongSparseArray<String> mUploadIDs;

    private DataContract.View mDataView;

    private DeviceManager mDeviceManager;

    private Realm mRealm;

    private long currentUploadedIndex;

    private UploadServiceBroadcastReceiver uploadServiceBroadcastReceiver =
            new UploadServiceBroadcastReceiver() {

                @Override
                public void onProgress(Context context, UploadInfo uploadInfo) {
                }

                @Override
                public void onError(Context context, UploadInfo uploadInfo,
                                    ServerResponse serverResponse, Exception exception) {
                    Toast.makeText(context, "upload failed!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                    if (Constants.FAKE_API) {
                        changeDataSetUploadStatus(currentUploadedIndex, true);
                        return;
                    }
                    if (serverResponse != null) {
                        UploadResponse response = new Gson()
                                .fromJson(serverResponse.getBodyAsString(), UploadResponse.class);
                        if (response.isSuccess()){
                            changeDataSetUploadStatus(currentUploadedIndex, true);
                        }
                    }
                }

                @Override
                public void onCancelled(Context context, UploadInfo uploadInfo) {
                }
            };

    public DataPresenter(DataContract.View dataView, DeviceManager deviceManager) {
        mDataView = dataView;
        mDeviceManager = deviceManager;
        mUploadIDs = new LongSparseArray<>();
    }

    @Override
    public void subscribe() {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void unsubscribe() {
        mRealm.close();
    }

    @Override
    public RealmResults<DataSet> getDataSets() {
        return DataHelper.getAllDataSet(mRealm);
    }

    @Override
    public void exportDataSet(long index, String name, String desc) {
        if (!isExternalStorageWritable()) {
            mDataView.showExternalStorageNotWritable();
            return;
        }
        RealmResults<Sample> samples = DataHelper.getSamplesByIndex(mRealm, index);
        File dir1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "IBS");
        File dir = new File(dir1, "ExportedData");
        if (!dir.mkdirs()) {
            Log.e(TAG, "Directory not created or exist");
        }
        try {
            String filename = exportName(name, desc);
            File file = new File(dir, filename);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.e(TAG, "File not created");
                }
            }
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // write file
            String s1 = "{\"samples\":" +
                    SerializeToJson(samples) + "}";
            bufferedWriter.write(s1);

            bufferedWriter.close();
            fileWriter.close();
            changeDataSetExportStatus(index, true);
            mDataView.showExportSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Saving data error");
            changeDataSetExportStatus(index, false);
            mDataView.showExportFail();
        }
    }

    private String deviceInfo() {
        StringBuilder sb = new StringBuilder();
        Device device = mDeviceManager.information();
        sb.append("IMEI").append(":")
                .append(device.getIMEI() == null ? "null" : device.getIMEI()).append("\n");
        sb.append("IMSI").append(":")
                .append(device.getIMSI() == null ? "null" : device.getIMSI()).append("\n");
        sb.append("OSVersion").append(":")
                .append(device.getOSVersion()).append("\n");
        sb.append("apiLevel").append(":")
                .append(device.getApiLevel()).append("\n");
        sb.append("model").append(":")
                .append(device.getModel()).append("\n");
        sb.append("device").append(":")
                .append(device.getDevice()).append("\n");
        sb.append("product").append(":")
                .append(device.getProduct()).append("\n");
        return sb.toString();
    }

    // TODO: 2017/10/20 将 ！！！运动模式！！！ 导出 // FIXME: 2017/10/20
    private String MRHeader(int size) {
        return String.format("%s\n", size);
    }

    private String sampleMR(Sample s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.getTime()).append(sep)
                .append(s.getMode()).append(sep)
                .append(buildLatLongString(s.getLatLng())).append(sep)
                .append(buildSignalString(s.getSignal())).append(sep)
                .append(buildBatteryString(s.getBtry())).append(sep)
                .append(buildGeomagnetismString(s.getGm())).append(sep)
                .append(buildBarometricString(s.getBaro())).append(sep)
                .append(buildBaseStationString(s.getMBS())).append(sep)
                .append(s.getBSList().size());
        for (int i = 0; i < s.getBSList().size(); i++) {
            sb.append(sep).append(buildBaseStationString(s.getBSList().get(i)));
        }
        sb.append("\n");
        return sb.toString();
    }

    private String buildLatLongString(LatLng latLng) {
        return String.valueOf(latLng.getLongitude()) + sep + latLng.getLatitude() + sep +
                latLng.getAltitude() + sep + latLng.getAccuracy() + sep +
                latLng.getSpeed();
    }

    private String buildSignalString(Signal signal) {
        return String.valueOf(signal.getCellid() + sep + signal.getDbm()) + sep + signal.isGsm() + sep +
                signal.getSignalToNoiseRatio() + sep +
                signal.getEvdoEcio() + sep + signal.getLevel();
    }

    private String buildBatteryString(Battery btry) {
        return String.valueOf(btry.getLevel()) + sep + btry.getCapacity();
    }

    private String buildGeomagnetismString(Geomagnetism gm) {
        return String.valueOf(gm.getX()) + sep + gm.getY() + sep +
                gm.getZ() + sep + gm.getAlpha() + sep +
                gm.getBeta() + sep + gm.getGamma();
    }

    private String buildBarometricString(Barometric baro) {
        return String.valueOf(baro.getPressure());
    }

    private String buildBaseStationString(BaseStation bs) {
        return String.valueOf(bs.getMcc()) + sep + bs.getMnc() + sep +
                bs.getLac() + sep + bs.getCid() + sep +
                bs.getArfcn() + sep + bs.getBsic_psc_pci() + sep +
                bs.getLon() + sep + bs.getLat() + sep +
                bs.getAsuLevel() + sep + bs.getSignalLevel() + sep +
                bs.getDbm() + sep + bs.getType();
    }

    @Override
    public void uploadDataSet(long index, String name, String desc) {
        String url = mDataView.uploadUrl();
        String fileName = exportName(name, desc);
        File dir1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "IBS");
        File dir = new File(dir1, "ExportedData");
        File file = new File(dir, fileName);
        String filePath = file.getAbsolutePath();
        currentUploadedIndex = index;
        String uploadID = uploadMultipart(mDataView.getContext(), url, filePath);
        mUploadIDs.put(index, uploadID);
    }

    private String uploadMultipart(Context context, String url, String filePath) {
        String uploadID = null;
        try {
            uploadID = new MultipartUploadRequest(context, url)
                    .setUtf8Charset()
                    .addFileToUpload(filePath, "file")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();
        } catch (FileNotFoundException | MalformedURLException e) {
            Log.e("AndroidUploadService", e.getMessage(), e);
        }
        return uploadID;
    }

    @Override
    public void changeDataSetExportStatus(long index, boolean done) {
        DataHelper.updateDataSetExport(mRealm, index, done);
        mDataView.updateDataSetStatus();
    }

    @Override
    public void changeDataSetUploadStatus(long index, boolean done) {
        DataHelper.updateDataSetUpload(mRealm, index, done);
        mDataView.updateDataSetStatus();
    }

    @Override
    public void deleteDataSet(long index) {
        DataHelper.deleteSamples(mRealm, index);
        mDataView.deleteDataSet();
    }

    @Override
    public void checkDataSetStatus() {
        RealmResults<DataSet> dataSetList = DataHelper.getAllDataSet(mRealm);
        for (DataSet ds : dataSetList) {
            if (ds.isExported() || ds.isUploaded()) {
                checkDataSetStatus(ds);
            }
        }
    }

    @Override
    public void onResume(Context context) {
        uploadServiceBroadcastReceiver.register(context);
    }

    @Override
    public void onPause(Context context) {
        uploadServiceBroadcastReceiver.unregister(context);
    }

    private void checkDataSetStatus(DataSet dataSet) {
        if (dataSet.isExported() && !isDataSetExported(dataSet)) {
            DataHelper.updateDataSetExport(mRealm, dataSet.getIndex(), false);
            mDataView.updateDataSetStatus();
        }
        // TODO: 2017/08/02/002 add check data set upload status
    }

    private boolean isDataSetExported(DataSet dataSet) {
        return isDataSetExported(dataSet.getName(), dataSet.getDesc());
    }

    private boolean isDataSetExported(String name, String desc) {
        String checkName = exportName(name, desc);
        return fileExisted(checkName);
    }

    private String exportName(DataSet dataSet) {
        return exportName(dataSet.getName(), dataSet.getDesc());
    }

    private String exportName(String name, String desc) {
        String suffix = Utils.dataSetDesc2FileSuffix(desc);
        return String.format("%s_%s.json", name, suffix);
    }

    private boolean fileExisted(String name) {
        File dir1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "IBS");
        File dir = new File(dir1, "ExportedData");
        File file = new File(dir, name);
        return file.exists();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String SerializeToJson(RealmResults<Sample> sample) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(Barometric.class, new BarometricSerializer())
                .registerTypeAdapter(BaseStation.class, new BaseStationSerializer())
                .registerTypeAdapter(Battery.class, new BatterySerializer())
                .registerTypeAdapter(Geomagnetism.class, new GeomagnetismSerializer())
                .registerTypeAdapter(LatLng.class, new LatLngSerializer())
                .registerTypeAdapter(Signal.class, new SignalSerialzer())
                .registerTypeAdapter(Sample.class, new SampleSerializer())
                .registerTypeAdapter(Wifi.class, new WifiSerializer())
                .create();
        return gson.toJson(mRealm.copyFromRealm(sample));
    }
}
