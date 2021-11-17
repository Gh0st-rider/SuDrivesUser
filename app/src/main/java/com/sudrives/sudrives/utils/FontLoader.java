package com.sudrives.sudrives.utils;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;



public class FontLoader {
    private static Typeface helRegular,helBold,helLight,keepcalmRegular;

    public static void loadFonts(Context context) {
        helRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica-Normal.ttf");
        helBold = Typeface.createFromAsset(context.getAssets(), "fonts/HELR65W.ttf");
        helLight = Typeface.createFromAsset(context.getAssets(), "fonts/HLT.ttf");
        keepcalmRegular = Typeface.createFromAsset(context.getAssets(), "fonts/KeepCalm-Regular.ttf");


    }

    public static void unloadFonts() {
        helRegular = null;
        helBold = null;
        helLight = null;
        keepcalmRegular = null;


    }



    public static void setHelRegular(View... view) {
        if (null != view) {
            if (null == helRegular) {
                helRegular = Typeface.createFromAsset(view[0].getContext().getAssets(), "fonts/Helvetica-Normal.ttf");
            }
            for (int i = 0; i < view.length; i++) {
                if (view[i] instanceof TextView) {
                    ((TextView) view[i]).setTypeface(helRegular);
                }else if (view[i] instanceof EditText) {
                    ((EditText) view[i]).setTypeface(helRegular);
                } else if (view[i] instanceof RadioButton) {
                    ((RadioButton) view[i]).setTypeface(helRegular);
                } else if (view[i] instanceof CheckBox) {
                    ((CheckBox) view[i]).setTypeface(helRegular);
                } else if (view[i] instanceof AutoCompleteTextView) {
                    ((AutoCompleteTextView) view[i]).setTypeface(helRegular);
                } else if (view[i] instanceof Button) {
                    ((Button) view[i]).setTypeface(helRegular);
                } else if (view[i] instanceof TextInputLayout) {
                    ((TextInputLayout) view[i]).setTypeface(helRegular);
                }
            }
        }
    }

    public static void setHelBold(View... view) {
        if (null != view) {
            if (null == helBold) {
                helBold = Typeface.createFromAsset(view[0].getContext().getAssets(), "fonts/HELR65W.ttf");
            }
            for (int i = 0; i < view.length; i++) {
                if (view[i] instanceof TextView) {
                    ((TextView) view[i]).setTypeface(helBold);
                } else if (view[i] instanceof EditText) {
                    ((EditText) view[i]).setTypeface(helBold);
                } else if (view[i] instanceof RadioButton) {
                    ((RadioButton) view[i]).setTypeface(helBold);
                } else if (view[i] instanceof CheckBox) {
                    ((CheckBox) view[i]).setTypeface(helBold);
                } else if (view[i] instanceof AutoCompleteTextView) {
                    ((AutoCompleteTextView) view[i]).setTypeface(helBold);
                } else if (view[i] instanceof Button) {
                    ((Button) view[i]).setTypeface(helBold);
                } else if (view[i] instanceof TextInputLayout) {
                    ((TextInputLayout) view[i]).setTypeface(helBold);
                }
            }
        }
    }
}
