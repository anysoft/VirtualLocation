package space.jonhy.app.gossip.util;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by xuqingfu on 2017/4/17.
 */

public class XposedUtil {

    private static List<String> pkgs = new ArrayList<>();

    private static String defaulTag = "XposedLogs";

    static {
        pkgs.add("com.tencent.mm");
        pkgs.add("com.tencent.mobileqq");
        pkgs.add("com.alibaba.android.rimet");
        pkgs.add("com.sdu.didi.psnger");
    }

    public static void hookAndChange(ClassLoader classLoader){

    }

    public static boolean isNeedHook(String pkgName){
        return pkgs.contains(pkgName);
    }

    public static void log(String log){
        log(defaulTag, log);
    }
    public static void log(String tag,String log){
        XposedBridge.log(String.format("[%s] %s", tag,log));
    }

}
