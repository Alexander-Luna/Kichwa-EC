package com.softec.quechuaec.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softec.quechuaec.R;
import com.softec.quechuaec.activitys.ActivityAgregarPalabra;
import com.softec.quechuaec.activitys.MainActivity;
import com.softec.quechuaec.utilidades.AdaptadorTransacciones;
import com.softec.quechuaec.utilidades.Bottom_Sheets;
import com.softec.quechuaec.utilidades.ObjetoPalabra;
import com.softec.quechuaec.utilidades.Utilidades;

public class FragmentHome extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public RecyclerView recyclerView;
    View vista;
    private String mParam1;
    private String mParam2;
    private boolean Click = true;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_home, container, false);
        ConstruirRecycler();
        ((MainActivity) getActivity()).recyclerView = recyclerView;
        TextInputEditText TIbuscar = vista.findViewById(R.id.TIbuscar);
        TIbuscar.setOnClickListener(view -> {
            new Bottom_Sheets(getActivity()).BottomSheetSearchView(((MainActivity) getActivity()).palabrasArrayList);
        });

        return vista;
    }


    @SuppressLint({"SetJavaScriptEnabled", "NotifyDataSetChanged"})
    private void ConstruirRecycler() {
        recyclerView = vista.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final AdaptadorTransacciones adaptadorTransacciones = new AdaptadorTransacciones(((MainActivity) getActivity()).palabrasArrayList, getActivity());
        adaptadorTransacciones.setOnclickListener(v -> {
            ObjetoPalabra transaccion = ((MainActivity) getActivity()).palabrasArrayList.get(recyclerView.getChildAdapterPosition(v));
            if (((MainActivity) getActivity()).auth.getCurrentUser() != null) {
                if (Click) {
                    Click = false;
                    Intent intent = new Intent(getContext(), ActivityAgregarPalabra.class);
                    intent.putExtra("id", transaccion.getId()); // Suponiendo que el ID se llama "id"
                    startActivity(intent);

                    try {
                        new CountDownTimer(2000, 1000) {
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                Click = true;
                            }
                        }.start();
                    } catch (Exception e) {
                        Log.d("TAGerror", "ClickActivity: " + e.getMessage());
                    }
                }
            }
        });
        adaptadorTransacciones.setOnLongclickListener(v -> {
            if (((MainActivity) getActivity()).auth.getCurrentUser() != null) {
                ObjetoPalabra palabra = ((MainActivity) getActivity()).palabrasArrayList.get(recyclerView.getChildAdapterPosition(v));
                Eliminar_Palabra(palabra);
                adaptadorTransacciones.notifyDataSetChanged();
            }
            return false;
        });
        recyclerView.setAdapter(adaptadorTransacciones);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

    }

    public void Eliminar_Palabra(ObjetoPalabra palabra) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar esta palabra?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            try {
                if (!palabra.getVozqu().equals("")) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference referenciaStorage = storage.getReferenceFromUrl(palabra.getVozqu());
                    referenciaStorage.delete().addOnSuccessListener(aVoid -> {

                    });
                }
            } catch (Exception e) {
                Log.d("TAGerror", "Eliminar_Palabra: " + e.getMessage());
            }
            try {
                if (!palabra.getVozqu().equals(Utilidades.IMAGEN_DEFAULT)) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference referenciaStorage = storage.getReferenceFromUrl(palabra.getImagen());
                    referenciaStorage.delete().addOnSuccessListener(aVoid -> {

                    });
                }
            } catch (Exception e) {
                Log.d("TAGerror", "Eliminar_Palabra: " + e.getMessage());
            }
            try {

                eliminarPalabraFirestore(palabra);

            } catch (Exception e) {
                eliminarPalabraFirestore(palabra);
                Log.d("TAGerror", "Eliminar_Palabra: " + e.getMessage());
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(getContext(), "Operación cancelada", Toast.LENGTH_SHORT).show();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarPalabraFirestore(ObjetoPalabra palabra) {
        ((MainActivity) getActivity()).firestore.collection(Utilidades.PALABRAS).document(palabra.getId()).delete().addOnSuccessListener(aVoid -> {
            ((MainActivity) getActivity()).palabrasArrayList.remove(palabra);
            recyclerView.getAdapter().notifyDataSetChanged();
            Toast.makeText(getContext(), "Palabra eliminada correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e1 -> {
            // Si falla la eliminación de Firestore, mostrar un mensaje de error
            Toast.makeText(getContext(), "Error al eliminar los datos en Firestore", Toast.LENGTH_SHORT).show();
        });
    }
}