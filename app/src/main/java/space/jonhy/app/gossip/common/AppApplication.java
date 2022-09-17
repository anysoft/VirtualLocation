package space.jonhy.app.gossip.common;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import space.jonhy.app.gossip.model.Gps;

/**
 * Created by xuqingfu on 2017/4/15.
 */
public class AppApplication extends Application {

	/**全局GPS*/
	public static Gps mMockGps;

	static {
		mMockGps = Config.COMPANY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
		SDKInitializer.initialize(getApplicationContext());
		SDKInitializer.setCoordType(CoordType.BD09LL);
	}

}