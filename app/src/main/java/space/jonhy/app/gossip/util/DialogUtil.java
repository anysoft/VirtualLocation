package space.jonhy.app.gossip.util;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

public class DialogUtil {
    public static void showEditDialog(Context context, String title,
                                      String content, String contentHint, final OnEditListener listener) {

        int top = DisplayUtil.dip2px(context, 20f);
        int left = DisplayUtil.dip2px(context, 26f);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.setPadding(left, top, left, 0);

        int padding = DisplayUtil.dip2px(context, 5);
        final EditText editText = new EditText(context);
        editText.setPadding(padding, padding, padding, padding);
        editText.setTextSize(15);
        editText.setText(content);
        editText.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setSingleLine(true);
        editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(15)});
        editText.setHint(contentHint);
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        frameLayout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(frameLayout);
        builder.setPositiveButton("确定", (dialog, which) -> {
            // 返回文本的内容
            listener.onTextChange(editText, editText.getText().toString());
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public static void showSearchDialog(Context context, String keyWord, final OnSearchListener listener) {

        int top = DisplayUtil.dip2px(context, 20f);
        int left = DisplayUtil.dip2px(context, 26f);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.setPadding(left, top, left, 0);

        int topPadding = DisplayUtil.dip2px(context, 10);
        int leftPadding = DisplayUtil.dip2px(context, 4);
        final EditText editText = new EditText(context);
        editText.setPadding(leftPadding, topPadding, leftPadding, topPadding);
        editText.setTextSize(15);
        editText.setText(keyWord);
        editText.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setSingleLine(true);
        editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(50)});
        editText.setHint("请输入搜索的关键字");
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        frameLayout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("搜索");
        builder.setView(frameLayout);
        builder.setPositiveButton("搜索", (dialog, which) -> {
            // 返回文本的内容
            listener.onSearch(editText.getText().toString());
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public interface OnEditListener {

        void onTextChange(View view, String value);
    }

    public interface OnSearchListener {

        void onSearch(String keyWord);
    }
}
