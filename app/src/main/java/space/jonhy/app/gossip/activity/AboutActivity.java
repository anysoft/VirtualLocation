package space.jonhy.app.gossip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import space.jonhy.app.gossip.R;
import space.jonhy.app.gossip.view.TopBanner;

public class AboutActivity extends AppCompatActivity {

    TopBanner mTopbanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        initViews();
        bindListener();
    }

    private void initViews() {
        mTopbanner = (TopBanner) findViewById(R.id.topbanner);
    }

    private void bindListener() {
        mTopbanner.setTopBannerListener(new TopBanner.OnTopBannerListener() {
            @Override
            public void leftClick(View v) {
                finish();
            }

            @Override
            public void rightClick(View v) {

            }
        });
    }

    public static void openActivity(Context conetxt){
        Intent intent = new Intent(conetxt, AboutActivity.class);
        conetxt.startActivity(intent);
    }
}
