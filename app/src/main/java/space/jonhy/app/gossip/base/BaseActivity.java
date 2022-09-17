package space.jonhy.app.gossip.base;

import android.app.Activity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.widget.Toolbar;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

import space.jonhy.app.gossip.view.CustomProgressDialog;


/**
 * Created by xuqingfu on 2017/4/15.
 */
public abstract class BaseActivity extends Activity {
    protected View mView;
    protected CustomProgressDialog progressDialog;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationClient.setAgreePrivacy(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Object object = getContentViewId();
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        if (object instanceof Integer) {
            mView = LayoutInflater.from(this).inflate((Integer) object, null);
        } else if (object instanceof View) {
            mView = (View) object;
        }
        setContentView(mView);
       // toolbar = (Toolbar) findViewById(R.id.toolbar);
        IniView();
        progressDialog = new CustomProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        IniLister();
        IniData();
    }

    /**
     * @Title: getContentViewId
     * @Description: 布局文件Id
     */
    protected abstract Object getContentViewId();

    /**
     * @Title: IniView
     * @Description: 初始化View
     */
    protected abstract void IniView();

    /**
     * @Title: IniLister
     * @Description: 初始化接口
     */
    protected abstract void IniLister();

    /**
     * @Title: IniData
     * @Description: 初始化数据
     */
    protected abstract void IniData();

    /**
     * thisFinish 当前关闭
     */
    protected abstract void thisFinish();

    @Override
    public void onBackPressed() {
        thisFinish();
    }

    /**
     * showProgressDialog 显示等待框
     *
     * @param text 显示文字
     */
    public void showProgressDialog(String text) {
        if (progressDialog != null) {
            progressDialog.show();
            progressDialog.setMessage(text);
        }
    }

    /**
     * cancelProgressDialog 取消等待框
     */
    public void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
