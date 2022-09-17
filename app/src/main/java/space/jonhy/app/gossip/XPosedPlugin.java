package space.jonhy.app.gossip;

import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import space.jonhy.app.gossip.presenter.HookApis;
import space.jonhy.app.gossip.presenter.SdkHookManager;
import space.jonhy.app.gossip.util.XposedUtil;

/**
 * Created by xuqingfu on 2017/4/15.
 */

public class XPosedPlugin implements IXposedHookLoadPackage {

    private static final String TAG = "silence";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!XposedUtil.isNeedHook(loadPackageParam.packageName)) { //过滤程序
            return;
        }
        XposedUtil.log(TAG, "加载Hook程序：" + loadPackageParam.packageName);
        // MockProvider false
        SdkHookManager.findMethodIsFromMockProvider();

        SdkHookManager.findMethodGetInt(loadPackageParam);

        XposedHelpers.findAndHookMethod("android.app.Application", loadPackageParam.classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedUtil.log(TAG, "启动程序：" + param.thisObject.toString());
                ClassLoader appClassLoader = ((Context)param.args[0]).getClassLoader();
                try {
                    HookApis.findMethodAmapLongitudeAndLatitude(appClassLoader);
                }
                catch(Exception e) {
                    XposedUtil.log(TAG, "Hook发生异常：" + e.toString());
                }
            }
        });
    }
}
