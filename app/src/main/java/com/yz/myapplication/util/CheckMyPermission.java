package com.yz.myapplication.util;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yz.myapplication.listener.RxPermissionsResultListener;

public class CheckMyPermission {
    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;
    /**
     * 判断是是否有录音权限
     */
    public static boolean isAudioCanUse(Activity activity){
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        @SuppressLint("MissingPermission") AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();

        return true;
    }

    /**
     * 判断摄像头是否可用
     * 主要针对6.0 之前的版本，现在主要是依靠try...catch... 报错信息，感觉不太好，
     * 以后有更好的方法的话可适当替换
     *
     * @return
     */
    public static boolean isCameraCanUse(Activity activity) {
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
            // 对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            return false;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    /**申请的权限有没有被授予*/
    public static boolean selfPermissionGranted(Context context, String permissionName) {
        return ContextCompat.checkSelfPermission(context, permissionName) == PermissionChecker.PERMISSION_GRANTED;
    }

    public static void permissionPrompt(final Activity activity, final String appName,final String permissionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,
                    new String[] {permissionName},
                    getResultCodeForPermission(permissionName));
        } else {
            new DialogUtil() {
                @Override
                public CharSequence getMessage() {
                    return getDesForPermission(appName,permissionName);
                }
                @Override
                public Context getContext() {
                    return activity;
                }
                @Override
                public void doConfirmThings() {
                    activity.startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
                }
                @Override
                public void doCancelThings() {
                }
            }.showDialog();
        }
    }

    private static int getResultCodeForPermission(String permissionName){
        switch(permissionName){
            case permission.RECORD_AUDIO:
                return REQUEST_RECORD_AUDIO;
            case permission.CAMERA:
                return REQUEST_CAMERA;
            case permission.ACCESS_FINE_LOCATION:
                return REQUEST_LOCATION;
            case permission.WRITE_EXTERNAL_STORAGE:
                return REQUEST_EXTERNAL_STORAGE;
            case permission.READ_PHONE_STATE:
                return REQUEST_PHONE_STATE;
                default:return -1;
        }
    }

    public static String getDesForPermission( String appName,String... permissionName){
        StringBuffer sb = new StringBuffer();
        for (String fileName : permissionName) {
            String str = getgetDesForPermission(fileName);
            if(!TextUtils.isEmpty(str)){
                sb.append(str).append("、");
            }
        }
        if(!TextUtils.isEmpty(sb.toString())){
            sb.deleteCharAt(sb.length()-1);
        }
        return "要允许"+appName+sb.toString()+"吗？";
    }

    private static String getgetDesForPermission(String permissionName){
        if (permissionName.equals(permission.RECORD_AUDIO)){
            return "录制音频";
        }else if (permissionName.equals(permission.CAMERA)){
            return "使用摄像头";
        }else if (permissionName.equals(permission.ACCESS_FINE_LOCATION)){
            return "使用此设备的位置信息";
        }else if (permissionName.equals(permission.READ_EXTERNAL_STORAGE)){
            return "访问您设备上的照片、媒体内容和文件";
        }else if (permissionName.equals(permission.WRITE_EXTERNAL_STORAGE)){
//            return "访问您设备上的照片、媒体内容和文件";
            return "存储空间写入";
        }else if (permissionName.equals(permission.READ_PHONE_STATE)){
            return "获取手机状态";
        }else if (permissionName.equals(permission.WRITE_CONTACTS)){
            return "添加联系人";
        }else if (permissionName.equals(permission.READ_CONTACTS)){
            return "读取联系人";
        }else if (permissionName.equals(permission.SEND_SMS)){
            return "发送短信";
        }else if (permissionName.equals(permission.CALL_PHONE)){
            return "打电话";
        }

        return "";
    }
    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_RECORD_AUDIO = 2;
    public static final int REQUEST_LOCATION = 3;
    public static final int REQUEST_PHONE_STATE = 4;
    public static final int REQUEST_CAMERA = 5;
    public static String[] PERMISSIONS_STORAGE = {
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE };
    public static String[] PERMISSIONS_LOCATION = {
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION};
    public static String[] PERMISSIONS_RECORD_AUDIO = {permission.RECORD_AUDIO };
    public static String[] PERMISSIONS_PHONE_STATE = {permission.READ_PHONE_STATE };
    public static String[] PERMISSIONS_CAMERA = {permission.CAMERA };

    public static boolean verifyLocationPermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION,
                    REQUEST_LOCATION);
            return false;
        }
        return true;
    }
    public static boolean verifyRecordAudioPermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD_AUDIO,
                    REQUEST_RECORD_AUDIO);
            return false;
        }
        return true;
    }

    public static boolean verifyPhoneStatePermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_PHONE_STATE,
                    REQUEST_PHONE_STATE);
            return false;
        }
        return true;
    }
    public static boolean verifyStoragePermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }
    public static boolean verifyCameraPermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA,
                    REQUEST_CAMERA);
            return false;
        }
        return true;
    }

    public static void applyRecordAudioPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD_AUDIO,
                REQUEST_RECORD_AUDIO);
    }
    public static void applyCameraPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA,
                REQUEST_CAMERA);
    }

    public static void rxPermissions(FragmentActivity activity, String permission, RxPermissionsResultListener listener){
        try {
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.shouldShowRequestPermissionRationale(activity, permission).blockingSubscribe(aBoolean -> {
                if(aBoolean){
                    if(rxPermissions.isGranted(permission)){
                        if(listener!=null){
                            listener.isGranted();
                        }
                    }else {
                        if(listener!=null){
                            listener.showToast();
                        }
                    }
                }else {
                    rxPermissions.request(permission).subscribe(granted -> {
                        if (granted) {
                            if(listener!=null){
                                listener.isGranted();
                            }
                        } else {
                            if(listener!=null){
                                listener.isDenied();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
