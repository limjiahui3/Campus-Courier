package com.example.campuscourier.shared;

import android.content.Context;

import com.example.campuscourier.R;

public abstract class ThemeManager {

    public static void set(Context context, String activeTheme) {
        int themeRecourseID = R.style.ReqAppTheme;
        if (activeTheme.equals("SupAppTheme")) {
            themeRecourseID = R.style.SupAppTheme;
        } else if (activeTheme.equals("NeutralAppTheme")) {
            themeRecourseID = R.style.NeutralAppTheme;
        }
        context.setTheme(themeRecourseID);
    }
}