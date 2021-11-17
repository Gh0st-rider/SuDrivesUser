package com.sudrives.sudrives.utils;

/**
 * Created by admin on 08/11/17.
 */


import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtil {

    /**
     * Show opened soft keyboard
     */
    public static void showSoftKeyboard(EditText editText) {
        if (null != editText) {
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, 0);
        }
    }


    public static void showSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /*
    * Hide softkeyboard
    * */
    public static void setupUI(View view, final Activity activity) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    if (activity != null && v != null) {
                        hideSoftKeyboard(activity);
                    }
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                if (activity != null) {
                    setupUI(innerView, activity);
                }
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText()) {
                inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);

            }
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyBoardDialog(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    public static void hideKeyBoardDialog(Context context, EditText tv) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);

    }

}

