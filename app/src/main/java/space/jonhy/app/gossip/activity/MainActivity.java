package space.jonhy.app.gossip.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import space.jonhy.app.gossip.R;
import space.jonhy.app.gossip.base.BaseActivity;
import space.jonhy.app.gossip.common.AppApplication;
import space.jonhy.app.gossip.listener.AsyncLocationResultListener;
import space.jonhy.app.gossip.listener.GeoCoderListener;
import space.jonhy.app.gossip.listener.MapClickListener;
import space.jonhy.app.gossip.listener.MarkerDragListener;
import space.jonhy.app.gossip.util.DialogUtil;
import space.jonhy.app.gossip.util.LocationUtil;
import space.jonhy.app.gossip.view.TopBanner;

import static space.jonhy.app.gossip.util.LocationUtil.hasAddTestProvider;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.List;

/**
 * Created by xuqingfu on 2017/4/15.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public Context mContext = null;
    private String mMockProviderName = LocationManager.GPS_PROVIDER;
    private Button bt_Ok;
    private LocationManager locationManager;
    public static double latitude = 25.2358842413, longitude = 119.2035484314;

    private Thread thread;// 需要一个线程一直刷新
    private Boolean RUN = true;
    private TextView tv_location;
    boolean isFirstLoc = true;// 是否首次定位
    // 百度定位相关
    private LocationClient mLocClient;
    private MyLocationConfiguration.LocationMode mCurrentMode;// 定位模式
    private BitmapDescriptor mCurrentMarker;// 定位图标
    private MapView mMapView;
    private static BaiduMap mBaiduMap;
    // 初始化全局 bitmap 信息，不用时及时 recycle
//    private BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
    private BitmapDescriptor bd = BitmapDescriptorFactory.fromAssetWithDpi("SDK_Default_Icon_Start.png");
    private static Marker mMarker;
    private static LatLng curLatlng;
    private static GeoCoder mSearch;
    public static double myGpslatitude, myGpslongitude;
    DrawerLayout mDrawerLayout;
    TopBanner mTopbanner;
    private TextView mAboutAuthor;
    private ImageView mCurrentLocation;
    private Animation mOperatingAnim;
    private TextView mPreciseLocation;
    private TextView mAddProcess;
    private ImageView mStopMock;

    @Override
    protected Object getContentViewId() {
        return R.layout.layout_schema;
    }

    @Override
    protected void IniView() {
        mContext = getApplicationContext();
        bt_Ok = (Button) findViewById(R.id.bt_Ok);
        tv_location = (TextView) findViewById(R.id.tv_location);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        mTopbanner = (TopBanner) findViewById(R.id.topbanner);
        mAboutAuthor = (TextView) findViewById(R.id.tv_about_me);
        mCurrentLocation = (ImageView) findViewById(R.id.iv_location);
        mStopMock = (ImageView) findViewById(R.id.iv_stop_location);
        mPreciseLocation = (TextView) findViewById(R.id.tv_precise);
        mAddProcess = (TextView) findViewById(R.id.tv_add_app);
        //加载旋转动画
        mOperatingAnim = AnimationUtils.loadAnimation(this, R.anim.spinloaing);
        LinearInterpolator lin = new LinearInterpolator();

        mOperatingAnim.setInterpolator(lin);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //隐藏地图比例尺
        mMapView.showScaleControl(false);
        //关闭缩放放大控件
        mMapView.showZoomControls(false);
        mMapView.removeViewAt(1);
        // 定位初始化
        try {
            mLocClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void IniLister() {
        bt_Ok.setOnClickListener(this);
        mLocClient.registerLocationListener(new AsyncLocationResultListener(mMapView, isFirstLoc));
        mBaiduMap.setOnMapClickListener(new MapClickListener(bt_Ok));
        mBaiduMap.setOnMarkerDragListener(new MarkerDragListener());

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new GeoCoderListener(MainActivity.this, tv_location));
        mTopbanner.setTopBannerListener(new TopBanner.OnTopBannerListener() {
            @Override
            public void leftClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }

            @Override
            public void rightClick(View v) {
                DialogUtil.showSearchDialog(MainActivity.this, "深圳市", (keyWord) -> {
                    if (TextUtils.isEmpty(keyWord)) {
                        Toast.makeText(mContext,"搜索的关键字不能为空!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 开始搜索
                    SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
                    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
                        @Override
                        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                            //处理sug检索结果
                            if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                                Log.i("result: ","没有找到");
                                return;
                                //未找到相关结果
                            }else
                            {
                                //获取在线建议检索结果，并显示到listview中
                                List<SuggestionResult.SuggestionInfo> resl = suggestionResult.getAllSuggestions();
//                                Log.i("11111111111",resl.toString());
                                Toast.makeText(mContext,resl.toString(),Toast.LENGTH_SHORT);
                                MyLocationData locData = new MyLocationData.Builder()
                                        .accuracy(0)
                                        // 此处设置开发者获取到的方向信息，顺时针0-360
                                        .direction(0).latitude(resl.get(0).pt.latitude)
                                        .longitude(resl.get(0).pt.longitude).build();
//                                mBaiduMap.setMyLocationData(locData);
                                LatLng ll = new LatLng(resl.get(0).pt.latitude, resl.get(0).pt.longitude);
                                setCurrentMapLatLng(ll);
                            }
                        }
                    };
                    mSuggestionSearch.setOnGetSuggestionResultListener(listener);
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(""+keyWord)
                            .city("亚洲")
                            .citylimit(false)
                    );
                });
            }
        });
        mAboutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.openActivity(MainActivity.this);
            }
        });
        mCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng ll = new LatLng(myGpslatitude, myGpslongitude);
                setCurrentMapLatLng(ll);
            }
        });
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        mCurrentLocation.clearAnimation();
                    }

                }, 1000 * 6);
            }
        });
        mPreciseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreciseLocationActivity.openActivity(MainActivity.this);
            }
        });
        mAddProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppsActivity.openActivity(MainActivity.this);
            }
        });
        mStopMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtil.stopMockLocation();
                Toast.makeText(MainActivity.this, getString(R.string.virtual_location_stoped), Toast.LENGTH_SHORT).show();
                bt_Ok.setText(R.string.start_shuttle);
            }
        });

    }

    @Override
    protected void IniData() {
        inilocation();
        iniMap();

        if (mOperatingAnim != null) {
            mCurrentLocation.startAnimation(mOperatingAnim);
        }

    }

    /**
     * inilocation 初始化 位置模拟
     */
    private void inilocation() {
        try {
            if (LocationUtil.initLocation(MainActivity.this)) {
                LocationUtil.initLocationManager();
            } else {//未开启ADB模拟定位功能，则只能选择HOOK技术

            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "虚拟定位功能未打开!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * iniMap 初始化地图
     */
    private void iniMap() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType(CoordType.BD09LL.name()); // 设置坐标类型
        option.setScanSpan(3000);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        // 缩放
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);

        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        mLocClient.setLocOption(option);
        mLocClient.start();
        initOverlay();

        // 开启线程，一直修改GPS坐标
        LocationUtil.startLocaton();
    }

    /**
     * initOverlay 设置覆盖物，这里就是地图上那个点
     */
    private void initOverlay() {
        LatLng ll = new LatLng(AppApplication.mMockGps.mLatitude, AppApplication.mMockGps.mLongitude);
        bd = BitmapDescriptorFactory.fromAssetWithDpi("SDK_Default_Icon_Start.png");
        OverlayOptions oo = new MarkerOptions().position(ll).icon(bd).zIndex(9)
                .draggable(true);
        mMarker = (Marker) (mBaiduMap.addOverlay(oo));
    }

    /**
     * setCurrentMapLatLng 设置当前坐标
     */
    public static void setCurrentMapLatLng(LatLng arg0) {
        curLatlng = arg0;
        mMarker.setPosition(arg0);

        // 设置地图中心点为这是位置
        LatLng ll = new LatLng(arg0.latitude, arg0.longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        // 根据经纬度坐标 找到实地信息，会在接口onGetReverseGeoCodeResult中呈现结果
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(arg0));
    }

    @Override
    protected void thisFinish() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle(R.string.notification);
        build.setMessage(R.string.exit_notification);
        build.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        build.setNeutralButton(R.string.minimize, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
            }
        });
        build.setNegativeButton(R.string.cancel, null);
        build.show();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        thisFinish();
    }

    @Override
    protected void onDestroy() {
        RUN = false;
        thread = null;
        //清除旋转加载动画
        mCurrentLocation.clearAnimation();

        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        bd.recycle();
        mSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_Ok:
                latitude = curLatlng.latitude;
                longitude = curLatlng.longitude;
                try {
                    if (!hasAddTestProvider) {
                        LocationUtil.initLocation(MainActivity.this);
                        LocationUtil.initLocationManager();
                    }
                    LocationUtil.setLongitudeAndLatitude(curLatlng.longitude, curLatlng.latitude);
                    AppApplication.mMockGps.mLatitude = curLatlng.latitude;
                    AppApplication.mMockGps.mLongitude = curLatlng.longitude;
                    bt_Ok.setText(R.string.finish_shuttle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}