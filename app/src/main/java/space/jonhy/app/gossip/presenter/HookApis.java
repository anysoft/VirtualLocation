package space.jonhy.app.gossip.presenter;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import space.jonhy.app.gossip.common.AppApplication;
import space.jonhy.app.gossip.util.XposedUtil;

/**
 * Created by xuqingfu on 2017/5/26.
 */

public class HookApis {

    private static final  String TAG = "Amap";

    /**
     * 修改高德地图的经纬度值
     * @param
     * @throws ClassNotFoundException
     */
    public static void findMethodAmapLongitudeAndLatitude(ClassLoader classLoader) {
        try {
            Class aMapLocationClazz = XposedHelpers.findClass("com.amap.api.location.AMapLocation", classLoader);
            if (null == aMapLocationClazz){
                XposedUtil.log(TAG,"该程序无高德地图模块,无法HOOK");
            }
//            Class aMapLocationClazz = classLoader.loadClass("com.amap.api.location.AMapLocation"); //
            XposedHelpers.findAndHookMethod(aMapLocationClazz, "getLongitude", new Object[]{new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam arg3) throws Throwable {
                    arg3.setResult(Double.valueOf(AppApplication.mMockGps.mLongitude));
                    Log.d(TAG, "修改高德地图的经纬度值:" + AppApplication.mMockGps.mLongitude + ":" + AppApplication.mMockGps.mLatitude);
                }
            }});

            XposedHelpers.findAndHookMethod(aMapLocationClazz, "getLatitude", new Object[]{new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam arg3) throws Throwable {
                    arg3.setResult(Double.valueOf(AppApplication.mMockGps.mLatitude));
                }
            }});
        } catch (Exception e) {
            Log.i(TAG, "该程序无高德地图模块,无法HOOK");
        }
    }


}
