package com.dalrise.laudyou.laudyou_app_service.views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class HeaderView {
    private final Map<String, Object> headerMap;
    private final Context context;

    public HeaderView(Context context, Map<String, Object> headerMap) {
        this.context = context;
        this.headerMap = headerMap;
    }

    public LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(Color.GREEN);

        View textColumn = createTextColumn();
        linearLayout.addView(textColumn);

        return linearLayout;
    }

    private View createTextColumn() {
        TextView textView = new TextView(context);
        textView.setText("overlay  화면");
        return textView;
    }
}
