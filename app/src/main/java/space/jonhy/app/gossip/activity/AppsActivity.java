package space.jonhy.app.gossip.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import space.jonhy.app.gossip.R;
import space.jonhy.app.gossip.adapter.AppAdapter;
import space.jonhy.app.gossip.model.MyAppInfo;
import space.jonhy.app.gossip.util.ApkTool;
import space.jonhy.app.gossip.view.TopBanner;

public class AppsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    AppAdapter mAppAdapter;
    public Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    private TopBanner mTopbanner;
    private EditText mSearchText;
    private Button mSearchButton;

    boolean isFilter =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        initView();
        bindListener();

        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        mAppAdapter = new AppAdapter(AppsActivity.this);
        mRecyclerView.setAdapter(mAppAdapter);
        initAppList(false);
    }

    private void bindListener() {
        mTopbanner.setTopBannerListener(new TopBanner.OnTopBannerListener() {
            @Override
            public void leftClick(View v) {
                finish();
            }

            @Override
            public void rightClick(View v) {
                if (mProgressBar.isShown()) {
                    Toast.makeText(AppsActivity.this, R.string.app_list_not_ready, Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    isFilter = !isFilter;
                    initAppList(isFilter);
                }
            }
        });
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgressBar.isShown()) {
                    Toast.makeText(AppsActivity.this, R.string.app_list_not_ready, Toast.LENGTH_SHORT).show();
                    return;
                }
                initAppList(mSearchText.getText().toString(),isFilter);
            }
        });


    }

    private void initView() {
        mTopbanner = (TopBanner) findViewById(R.id.topbanner);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        mSearchText = findViewById(R.id.text_search);
        mSearchButton = findViewById(R.id.button_search);
    }

    private void initAppList(final boolean isFilter) {
        initAppList(null,isFilter);
    }
    private void initAppList(final String appName ,final boolean isFilter) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //扫描得到APP列表
                final List<MyAppInfo> appInfos = ApkTool.scanLocalInstallAppList(getPackageManager(), isFilter);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(appName) && !TextUtils.isEmpty(appName.trim()) && !appName.trim().equals(getString(R.string.search_default))){
                            final List<MyAppInfo> tmplist = new ArrayList<>();
                            tmplist.addAll(appInfos);
                            appInfos.clear();
                            tmplist.forEach(appInfo ->{
                                if (appInfo.getAppName().contains(appName) || appInfo.getPkgName().contains(appName)){
                                    appInfos.add(appInfo);
                                }
                            });
                        }
                        mAppAdapter.setData(appInfos);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }.start();
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, AppsActivity.class);
        context.startActivity(intent);
    }

}
