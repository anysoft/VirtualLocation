package space.jonhy.app.gossip.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import space.jonhy.app.gossip.R;
import space.jonhy.app.gossip.activity.MainActivity;
import space.jonhy.app.gossip.model.MyAppInfo;

/**
 * Created by xuqingfu on 2017/4/24.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    List<MyAppInfo> mListData = new ArrayList<MyAppInfo>();
    private Context mContext;

    public AppAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_info, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyAppInfo myAppInfo = mListData.get(position);
        holder.iv_app_icon.setImageDrawable(myAppInfo.getImage());
        holder.tx_app_name.setText(myAppInfo.getAppName());
        holder.tx_app_pkg.setText(myAppInfo.getPkgName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo
                Toast.makeText(mContext, "待开发 intent activity！", Toast.LENGTH_SHORT).show();
            }
        });
        holder.tv_app_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // todo get shared perfenrence
                Toast.makeText(mContext, "待开发 sharedperfrence！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    public void setData(List<MyAppInfo> myAppInfos) {
        this.mListData = myAppInfos;
        notifyDataSetChanged();
    }

    /*




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        MyAppInfo myAppInfo = mListData.get(position);
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_info, null);
            mViewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            mViewHolder.tx_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.iv_app_icon.setImageDrawable(myAppInfo.getImage());
        mViewHolder.tx_app_name.setText(myAppInfo.getAppName());
        return convertView;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_app_icon;
        TextView tx_app_name;
        TextView tx_app_pkg;
        CheckBox tv_app_checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_app_icon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
            tx_app_name = (TextView) itemView.findViewById(R.id.tv_app_name);
            tx_app_pkg = (TextView)itemView.findViewById(R.id.tv_app_pkg);
            tv_app_checkbox = (CheckBox)itemView.findViewById(R.id.tv_checkbox);
        }
    }
}
