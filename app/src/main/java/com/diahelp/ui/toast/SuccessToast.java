package com.diahelp.ui.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.diahelp.R;

public class SuccessToast extends Fragment {
    Activity activity;
    View view;
    TextView title;
    Toast toast;

    public SuccessToast() { }

    @SuppressLint("ValidFragment")
    public SuccessToast(Activity activity) {
        this.activity = activity;
        view = View.inflate(activity.getBaseContext(), R.layout.success_toast, null);
        title = view.findViewById(R.id.txt_custom_success_toast_message);
        toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 250);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
    }

    public void showToast(String text) {
        title.setText(text);
        toast.show();
    }
}