package com.softec.quechuaec.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softec.quechuaec.R;
import com.softec.quechuaec.activitys.ActivityLogin;
import com.softec.quechuaec.activitys.MainActivity;
import com.softec.quechuaec.utilidades.DarkModePrefManager;
import com.softec.quechuaec.utilidades.Utilidades;


public class FragmentConfiguraciones extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View vista;
    private String mParam1;
    private String mParam2;


    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView email;
    private Button cerrar_sesion;
    private Switch aSwitch;
    private LinearLayout usuario, login;

    public FragmentConfiguraciones() {
        // Required empty public constructor
    }

    public static FragmentConfiguraciones newInstance(String param1, String param2) {
        FragmentConfiguraciones fragment = new FragmentConfiguraciones();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_configuraciones, container, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        usuario = vista.findViewById(R.id.cardUsuario);
        login = vista.findViewById(R.id.cardLogin);
        TextView tv_version = vista.findViewById(R.id.tv_version);
        tv_version.setText(getString(R.string.app_name) + " v" + ((MainActivity) getActivity()).complementos.getVersionName());
        aSwitch = vista.findViewById(R.id.switchTema);
        if (new DarkModePrefManager(getContext()).isNightMode()) {
            aSwitch.setChecked(true);
            aSwitch.setTextColor(getResources().getColor(R.color.colorBlue));
        }
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ((MainActivity) getActivity()).complementos.setDarkMode(getActivity().getWindow());
                aSwitch.setTextColor(getResources().getColor(R.color.colorBlue));
            } else {
                ((MainActivity) getActivity()).complementos.setDarkMode(getActivity().getWindow());
                aSwitch.setTextColor(getResources().getColor(R.color.colorTexto));
            }
        });

        email = vista.findViewById(R.id.Bemail);
        cerrar_sesion = vista.findViewById(R.id.Bcerrar_sesion);
        cerrar_sesion.setOnClickListener(v -> MetodoCerrarSesion());

        LoginIniciado();

        return vista;
    }

    private void LoginIniciado() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (((MainActivity) getActivity()).auth.getCurrentUser() != null) {
            email.setText(auth.getCurrentUser().getEmail());
            usuario.setLayoutParams(lparams);
            lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            login.setLayoutParams(lparams);
        } else {
            login.setLayoutParams(lparams);
            lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            usuario.setLayoutParams(lparams);
        }
        vista.findViewById(R.id.Blogin).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ActivityLogin.class));
        });
    }

    private void MetodoCerrarSesion() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.cerrar_sesion));
        builder.setMessage(getString(R.string.seguro_que_desea_cerrar_sesion));
        builder.setPositiveButton(getString(R.string.si), (dialogInterface, i) -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                dialogInterface.dismiss();
                //startActivity(intent);
                //getActivity().finish();
                ((MainActivity) getActivity()).MostrarFragments(new FragmentMain(), Utilidades.FRAGMENT_MAIN);
            } else {
                Toast.makeText(getContext(), getString(R.string.error_al_cerrar_sesion), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setCancelable(false).create().show();
    }

}