package com.zsy.callandroid;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jing on 2018-1-18.
 */
public class Unity2Android {

    /**
     * unity项目启动时的的上下文
     */
    private Activity _unityActivity;
    /**
     * 获取unity项目的上下文
     * @return
     */
    Activity getActivity(){
        if(null == _unityActivity) {
            try {
                Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
                Activity activity = (Activity) classtype.getDeclaredField("currentActivity").get(classtype);
                _unityActivity = activity;
            } catch (ClassNotFoundException e) {

            } catch (IllegalAccessException e) {

            } catch (NoSuchFieldException e) {

            }
        }
        return _unityActivity;
    }

    /**
     * 调用Unity的方法
     * @param gameObjectName    调用的GameObject的名称
     * @param functionName      方法名
     * @param args              参数
     * @return                  调用是否成功
     */
    boolean callUnity(String gameObjectName, String functionName, String args){
        try {
            Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
            Method method =classtype.getMethod("UnitySendMessage", String.class,String.class,String.class);
            method.invoke(classtype,gameObjectName,functionName,args);
            return true;
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return false;
    }

    /**
     * Toast显示unity发送过来的内容
     * @param content           消息的内容
     * @return                  调用是否成功
     */
    public boolean showToast(String content){
        Toast.makeText(getActivity(),content,Toast.LENGTH_SHORT).show();
        //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
        callUnity("Main Camera","FromAndroid", "hello unity i'm android");
        return true;
    }


    /***
     * 将Unity发送过来的文本内容复制到粘贴板
     * @param str
     * @throws Exception
     */
    public  boolean CopyTextToClipboard( String str){

        if(Looper.myLooper() ==null){
            Looper.prepare();
        }

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("Label", str);
        clipboard.setPrimaryClip(textCd);

        return true;
    }

    /**
     * 获取设备唯一值
     * @return
     */
    public  String getUniversalID( )
    {
        String androidId = "" + Settings.Secure.getString(getActivity().getContentResolver(), "android_id");
        String uuid;
        try
        {
            if (!"9774d56d682e549c".equals(androidId))
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
            else
                uuid = UUID.randomUUID().toString();
        }
        catch (Exception e)
        {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    /**
     * 打开商店
     * @param appPkg
     * @param marketPkg
     * @return
     */
    public  boolean toMarket(String appPkg, String marketPkg)
    {
        Uri uri = Uri.parse("market://details?id=" + appPkg);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addFlags(268435456);

        if (marketPkg != null)
        {
            intent.setPackage(marketPkg);
        }

        try
        {
            getActivity().startActivity(intent);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 获取国家码
     */
    public  String getCountryZipCode( ) {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager)  getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        Log.d("ss", "CountryID--->>>" + CountryID);
        String[] rl =  getActivity().getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return CountryZipCode;
    }

    /**
     * 判断手机是否安装某个应用
     * @param appPackageName  应用包名
     * @return   true：安装，false：未安装
     */
    public  boolean isApplicationAvilible( String appPackageName) {
        PackageManager packageManager = getActivity().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }



}