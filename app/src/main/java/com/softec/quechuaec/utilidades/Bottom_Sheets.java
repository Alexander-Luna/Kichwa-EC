package com.softec.quechuaec.utilidades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.softec.quechuaec.R;
import com.softec.quechuaec.activitys.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Bottom_Sheets {
    Activity activity;
    ArrayList<ObjetoPalabra> palabrasArrayListTemp;
    private boolean Click = true;

    public Bottom_Sheets(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged", "ClickableViewAccessibility"})
    public void BottomSheetSearchView(ArrayList<ObjetoPalabra> palabrasArrayList) {
        try {
            palabrasArrayListTemp = new ArrayList<>();
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomShet);
            View bottomShetView = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_search, (LinearLayout) activity.findViewById(R.id.linear1));

            TextInputEditText searchView = bottomShetView.findViewById(R.id.TIbuscar);
            RecyclerView recyclerView = bottomShetView.findViewById(R.id.RecyclerView_item);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

            final AdaptadorTransacciones cargarSeries = new AdaptadorTransacciones(palabrasArrayListTemp, activity);

            cargarSeries.setOnclickListener(view -> {
              /*  ObjetoPalabra transaccion = palabrasArrayListTemp.get(recyclerView.getChildAdapterPosition(view));
                FragmentVerPalabra verPartitura = new FragmentVerPalabra();
                verPartitura.setPalabra(transaccion);
                ((MainActivity) activity).MostrarFragments(verPartitura, Utilidades.FRAGMENT_VER_PARTITURA);
                ((MainActivity) activity).BbarIzq.setBackground(activity.getResources().getDrawable(R.drawable.ic_atras));
                bottomSheetDialog.dismiss();
*/
            });
            recyclerView.setAdapter(cargarSeries);
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    palabrasArrayListTemp.clear();
                    if (searchView.getText() != null && searchView.getText().toString().length() > 0) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        for (ObjetoPalabra item : palabrasArrayList) {
                            if (((MainActivity) activity).complementos.getRemoverTildes(item.getSpanish()).toLowerCase(Locale.getDefault()).contains(((MainActivity) activity).complementos.getRemoverTildes(searchView.getText().toString().toLowerCase(Locale.getDefault()).trim())) || ((MainActivity) activity).complementos.getRemoverTildes(item.getQuechua()).toLowerCase(Locale.getDefault()).contains(((MainActivity) activity).complementos.getRemoverTildes(searchView.getText().toString().toLowerCase(Locale.getDefault()).trim()))) {
                                if (!palabrasArrayListTemp.contains(item)) {
                                    palabrasArrayListTemp.add(item);
                                    try {
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                    } else {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        palabrasArrayListTemp.clear();
                    }
                }
            });

            bottomSheetDialog.setContentView(bottomShetView);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();

            ((MainActivity) activity).complementos.setupFullHeight(bottomSheetDialog);
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        } catch (Exception e) {
        }

    }
}
