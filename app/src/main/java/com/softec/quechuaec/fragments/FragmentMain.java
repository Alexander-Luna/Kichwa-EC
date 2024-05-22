package com.softec.quechuaec.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.softec.quechuaec.R;
import com.softec.quechuaec.activitys.ActivityAgregarPalabra;
import com.softec.quechuaec.activitys.MainActivity;
import com.softec.quechuaec.utilidades.BottomBarBehavior;
import com.softec.quechuaec.utilidades.VerificarConexionAInternet;

public class FragmentMain extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public FragmentConfiguraciones configuraciones;
    public FragmentHome home;
    public BubbleNavigationLinearView navigationBar;
    private View vista;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String mParam1;
    private String mParam2;

    public FragmentMain() {
        // Required empty public constructor
    }

    public static FragmentMain newInstance(String param1, String param2) {
        FragmentMain fragment = new FragmentMain();
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
        vista = inflater.inflate(R.layout.fragment_main, container, false);
        MenuInferior();
        if (((MainActivity) getActivity()).auth.getCurrentUser() != null) {
            vista.findViewById(R.id.fab).setVisibility(View.VISIBLE);
            vista.findViewById(R.id.fab).setOnClickListener(v -> startActivity(new Intent(getContext(), ActivityAgregarPalabra.class)));
        } else {
            vista.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        }
        ((MainActivity) getActivity()).BbarIzq.setBackground(getResources().getDrawable(R.drawable.ic_share));
        return vista;
    }

    @SuppressLint("NonConstantResourceId")
    private void MenuInferior() {
        configuraciones = new FragmentConfiguraciones();
        home = new FragmentHome();
        navigationBar = vista.findViewById(R.id.navigationBar);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationBar.getLayoutParams();
        layoutParams.setBehavior(new BottomBarBehavior());
        MostrarFragments(home);
        navigationBar.setCurrentActiveItem(0);
        navigationBar.setNavigationChangeListener((view, position) -> {
            switch (position) {
                case 0:
                    MostrarFragments(home);
                    break;
                case 1:
                    MostrarFragments(configuraciones);
                    break;
            }
        });
    }

    public void MostrarFragments(Fragment fragment) {
        try {
            if (((MainActivity) getActivity()).mediaPlayer != null) {
                ((MainActivity) getActivity()).mediaPlayer.stop();
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment1, fragment).addToBackStack(null).commit();
        } catch (Exception e) {
            Log.d("TAGerror", "MostrarFragments: " + e.getMessage());
        }
    }
}