package com.sudrives.sudrives.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudrives.sudrives.R;


public class AppDialogs {

    public static Dialog dialog, networkDialogLoader;

    private static Toast toast;
     static Dialog dialogDoubleButton;
    public static void noNetworkConnectionDialog(final Activity context) {

        AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
        alertadd.setTitle(context.getResources().getString(R.string.error_no_connection));
        alertadd.setMessage(context.getResources().getString(R.string.error_no_con_msg));
        alertadd.create();
        alertadd.setPositiveButton(context.getResources().getString(R.string.text_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int sumthin) {

                context.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                dialogInterface.dismiss();
            }
        });

        alertadd.setNegativeButton(context.getResources().getString(R.string.text_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                context.finish();
            }
        });

        alertadd.show();
    }



    public static void singleButtonDialog(final Context mcontext, final int id, final String title, final String icon, final String message, final String buttonName,
                                          final SingleButoonCallback singleButoonCallback, boolean value) {


        ((Activity) mcontext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView tv_Done, tv_text, tv_title, tv_icon;

                final Dialog dialog = new Dialog(mcontext, R.style.DialogTheme);

                LayoutInflater layoutInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(id, null);
                tv_text = view.findViewById(R.id.SingleButton_message);
                tv_Done = view.findViewById(R.id.SingleButton_done);
                tv_title = view.findViewById(R.id.SingleButton_title);
                // tv_icon = view.findViewById(R.id.SingleButton_icon);
                tv_title.setTypeface(null, Typeface.BOLD);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                dialog.setCancelable(true);

                // tv_icon.setText(icon);
                tv_text.setText(Html.fromHtml(message));
                tv_title.setText(title);
                tv_Done.setText(buttonName);


                tv_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        singleButoonCallback.singleButtonSuccess("text");
                    }
                });

                tv_Done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        singleButoonCallback.singleButtonSuccess(buttonName);
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

            }
        });
    }

    public static void DoubleButtonWithCallBackVersionDialog(final Context mcontext, final int id, boolean cancel, boolean cross, final int icon, final String message, final String title, final String buttonname1, final String buttonname2, final Doublebuttonpincallback doubleButoonCallback, final DoublebuttonCancelcallback cancelButoonCallback, final Crosscallback crossButtonCallback) {


        ((Activity) mcontext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView tv_Submit, tv_confirm_yes, tv_confirm_no, tv_title, tv_message, Cancel_icon;
                ImageView SingleButton_icon;
                  dialogDoubleButton = new Dialog(mcontext, R.style.DialogTheme);

                LayoutInflater layoutInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(id, null);


                tv_confirm_yes = view.findViewById(R.id.SingleButton_done);
                Cancel_icon = view.findViewById(R.id.Cancel_icon);

                tv_confirm_no = view.findViewById(R.id.SingleButton_cancel);
                tv_title = view.findViewById(R.id.SingleButton_title);
                tv_title.setTypeface(null, Typeface.BOLD);
                SingleButton_icon = view.findViewById(R.id.SingleButton_icon);
                tv_message = view.findViewById(R.id.SingleButton_message);
                dialogDoubleButton.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogDoubleButton.setContentView(view);
                dialogDoubleButton.setCancelable(true);

                if (cancel) {
                    tv_confirm_no.setVisibility(View.VISIBLE);
                } else {
                    tv_confirm_no.setVisibility(View.GONE);
                }
                tv_confirm_no.setText(buttonname2);
                tv_confirm_yes.setText(buttonname1);
                tv_message.setText(Html.fromHtml(message));
                if (icon != 0) {
                    SingleButton_icon.setVisibility(View.VISIBLE);
                    SingleButton_icon.setImageResource(icon);
                } else {
                    SingleButton_icon.setVisibility(View.GONE);
                    SingleButton_icon.setImageResource(icon);

                }
                if (title.length() != 0) {
                    tv_title.setVisibility(View.VISIBLE);
                    tv_title.setText(title);
                } else {
                    tv_title.setVisibility(View.GONE);


                }
                //   tv_icon.setVisibility(View.GONE);

                tv_confirm_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doubleButoonCallback.doublebuttonok("text");

//                AppUtil.showToast(mcontext, "successfully done");
                        dialogDoubleButton.dismiss();


                    }
                });
                if (cross) {
                    Cancel_icon.setVisibility(View.VISIBLE);
                } else {
                    Cancel_icon.setVisibility(View.GONE);
                }
                Cancel_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        crossButtonCallback.crossButtonCallback("text");
                        dialogDoubleButton.dismiss();

                    }
                });

                tv_confirm_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelButoonCallback.doublebuttonCancel("text");

//                AppUtil.showToast(mcontext, "successfully done");
                        dialogDoubleButton.dismiss();

                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogDoubleButton.getWindow().getAttributes());
                lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialogDoubleButton.getWindow().setAttributes(lp);
                dialogDoubleButton.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogDoubleButton.show();

            }
        });

    }


    public static void singleButtonVersionDialog(final Context mcontext, final int id, final String title, final int icon, final String message, final String buttonName,
                                                 final SingleButoonCallback singleButoonCallback, boolean value) {


        ((Activity) mcontext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView tv_Done, tv_text, tv_title;
                ImageView tv_icon;

                final Dialog dialog = new Dialog(mcontext, R.style.DialogTheme);

                LayoutInflater layoutInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(id, null);
                tv_text = view.findViewById(R.id.SingleButton_message);
                tv_Done = view.findViewById(R.id.SingleButton_done);
                tv_title = view.findViewById(R.id.SingleButton_title);
                tv_icon = view.findViewById(R.id.SingleButton_icon);
                tv_title.setTypeface(null, Typeface.BOLD);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                dialog.setCancelable(true);
                if (icon == 0) {
                    tv_icon.setVisibility(View.GONE);

                } else {
                    tv_icon.setImageResource(icon);

                }

                tv_text.setText(Html.fromHtml(message));
                tv_title.setText(title);
                tv_Done.setText(buttonName);
                if (title == "") {
                    tv_title.setVisibility(View.GONE);
                } else {
                    tv_title.setVisibility(View.VISIBLE);
                }
                if (message == "") {
                    tv_text.setVisibility(View.GONE);
                } else {
                    tv_text.setVisibility(View.VISIBLE);
                }
//                tv_icon.setVisibility(View.GONE);
                tv_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        singleButoonCallback.singleButtonSuccess("text");
                        dialog.dismiss();
                    }
                });
                tv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tv_Done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        singleButoonCallback.singleButtonSuccess(buttonName);
                        dialog.dismiss();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

            }
        });
    }

    public interface SingleButoonCallback {
        public void singleButtonSuccess(String from);
    }

    public interface Doublebuttonpincallback {
        public void doublebuttonok(String from);
    }

    public interface DoublebuttonCancelcallback {
        public void doublebuttonCancel(String from);
    }

    public interface Crosscallback {
        public void crossButtonCallback(String from);
    }
    public static void dialogDismiss() {
        if (dialogDoubleButton != null) {
            dialogDoubleButton.dismiss();
        }
    }
}