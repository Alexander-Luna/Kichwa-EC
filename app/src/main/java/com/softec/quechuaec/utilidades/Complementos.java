package com.softec.quechuaec.utilidades;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.softec.quechuaec.R;
import com.softec.quechuaec.activitys.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Complementos {
    public boolean PANTALLA_CARGA_INICIO = true;
    int TI = 20000;
    private Activity activity;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private boolean DELAY_PANTALLA_DE_CARGA = false, TIMER_ACTIVO = false;

    public Complementos(Activity activity) {
        this.activity = activity;
    }



    public void setDelayPantallaDeCarga(boolean DELAY_PANTALLA_DE_CARGA) {
        this.DELAY_PANTALLA_DE_CARGA = DELAY_PANTALLA_DE_CARGA;
    }

    public void PantallaDeCarga(boolean inicio) {
        try {
            MetodoOcultarTeclado();
            if (inicio) {
                builder = new AlertDialog.Builder(activity, R.style.tamanio_dialog);
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView;
                if (PANTALLA_CARGA_INICIO) {
                    dialogView = inflater.inflate(R.layout.progres_dialog_inicio, null);
                    PANTALLA_CARGA_INICIO = false;
                } else {
                    dialogView = inflater.inflate(R.layout.progres_dialog, null);
                }
                builder.setView(dialogView);
                builder.setCancelable(false);
                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                try {
                    new CountDownTimer(TI, 1000) {
                        public void onTick(final long millisUntilFinished) {
                            try {
                                if (!dialog.isShowing()) {
                                    TI = 0;
                                }
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onFinish() {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                Log.d("TAGerror", "onFinish: " + e.getMessage());
                            }
                        }
                    }.start();
                } catch (Exception e) {
                    dialog.dismiss();
                }
            } else {
                if (dialog != null && dialog.isShowing()) {
                    if (DELAY_PANTALLA_DE_CARGA) {
                        DELAY_PANTALLA_DE_CARGA = false;
                        final int[] TIEMPO = {0};
                        if (!TIMER_ACTIVO) {
                            TIEMPO[0] = 1000;
                            TIMER_ACTIVO = true;
                            try {
                                new CountDownTimer(TIEMPO[0], 1000) {
                                    public void onTick(final long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        try {
                                            TIEMPO[0] = 0;
                                            TIMER_ACTIVO = false;
                                            dialog.dismiss();
                                        } catch (Exception e) {

                                        }
                                    }
                                }.start();
                            } catch (Exception e) {
                                TIEMPO[0] = 0;
                                TIMER_ACTIVO = false;
                                dialog.dismiss();
                            }
                        }
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        } catch (Exception e) {
            Log.d("TAGerror", "PantallaDeCarga: " + e.getMessage());
        }
    }

    public String getVersionName() {
        try {
            return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void MetodoOcultarTeclado() {
        View focus = activity.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (focus != null) {
            inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }


    public void GuardarOpcion(String KeyDoc, String KeyLog, boolean Valor) {
        SharedPreferences sp = activity.getSharedPreferences(KeyDoc, MODE_PRIVATE);
        sp.edit().putBoolean(KeyLog, Valor).apply();
    }


    public void setDarkMode(Window window) {
        DarkModePrefManager darkModePrefManager = new DarkModePrefManager(activity);
        darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());

        if (new DarkModePrefManager(activity).isNightMode()) {
            GuardarOpcion("Configuraciones", "Tema Seleccionado", true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            changeStatusBar(0, window);
        } else {
            GuardarOpcion("Configuraciones", "Tema Seleccionado", false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            changeStatusBar(1, window);
        }
    }

    public void changeStatusBar(int mode, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorFondo));
            //white mode
            if (mode == 1) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    public void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        CardView bottomSheet = bottomSheetDialog.findViewById(R.id.card_busqueda);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
    }

    private int getWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public String getRemoverTildes(String input) {
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }
}
