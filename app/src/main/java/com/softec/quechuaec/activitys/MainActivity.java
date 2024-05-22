package com.softec.quechuaec.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softec.quechuaec.R;
import com.softec.quechuaec.fragments.FragmentMain;
import com.softec.quechuaec.utilidades.Complementos;
import com.softec.quechuaec.utilidades.ObjetoPalabra;
import com.softec.quechuaec.utilidades.Utilidades;
import com.softec.quechuaec.utilidades.VerificarConexionAInternet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<ObjetoPalabra> palabrasArrayList;
    @SuppressLint("StaticFieldLeak")
    public static Complementos complementos;
    public MediaPlayer mediaPlayer;
    public FirebaseAuth auth;
    public FirebaseFirestore firestore;
    public Button BbarIzq;
    private View vista;
    public RecyclerView recyclerView;

    private static void MetodoOrdenamientoQuicksort(ArrayList<ObjetoPalabra> a, int n) {//INICIO FUNCION
        quicksortRecursivo(a, 0, n - 1);
    }

    private static void quicksortRecursivo(ArrayList<ObjetoPalabra> a, int primero, int ultimo) {//INICIO FUNCION
        int i, j, central;
        ObjetoPalabra pivote;
        central = (primero + ultimo) / 2;
        pivote = a.get(central);
        i = primero;
        j = ultimo;
        do {
            while (a.get(i).getQuechua().compareToIgnoreCase(pivote.getQuechua()) < 0) i++;
            while (a.get(j).getQuechua().compareToIgnoreCase(pivote.getQuechua()) > 0) j--;
            if (i <= j) {
                ObjetoPalabra aux = a.get(i);
                a.set(i, a.get(j));
                a.set(j, aux);
                i++;
                j--;
            }
        } while (i <= j);
        if (primero < j) quicksortRecursivo(a, primero, j);
        if (i < ultimo) quicksortRecursivo(a, i, ultimo);//¨APLICAR RECURSIVIDAD
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mediaPlayer = new MediaPlayer();
        complementos = new Complementos(this);

        try {
            MostrarOperaciones();
        } catch (Exception e) {
            Log.d("TAGerror", "onCreate: " + e.getMessage());
            throw new RuntimeException(e);
        }
        vista = findViewById(R.id.app_bar);
        complementos.setDelayPantallaDeCarga(true);
        complementos.PantallaDeCarga(true);
        BbarIzq = vista.findViewById(R.id.Bcompartir);
        BbarIzq.setOnClickListener(v -> MetodoShare());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (recyclerView != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void MetodoShare() {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            FragmentManager manager = getSupportFragmentManager();
            FragmentManager.BackStackEntry stackEntry;
            switch (fragment.getTag()) {
                case Utilidades.FRAGMENT_MAIN:
                    Intent compartir = new Intent(Intent.ACTION_SEND).setType("text/plain");
                    String Mensaje = getString(R.string.recomiendo) + getString(R.string.link_play_store);
                    compartir.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    compartir.putExtra(Intent.EXTRA_TEXT, Mensaje);
                    startActivity(Intent.createChooser(compartir, getString(R.string.compartir_via)));
                    break;
                default:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {

        }
    }

    private void GuardarOpcion(String KeyDoc, String KeyLog, int Valor) {
        SharedPreferences sp = this.getSharedPreferences(KeyDoc, MODE_PRIVATE);
        sp.edit().putInt(KeyLog, Valor).apply();
    }


    public void MostrarFragments(Fragment fragment, String TAG) {

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, TAG).addToBackStack(null).commit();
        } catch (Exception e) {
            Log.d("TAGerror", "MostrarFragments: " + e.getMessage());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBackPressed() {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            FragmentManager manager = getSupportFragmentManager();
            FragmentManager.BackStackEntry stackEntry;
            switch (fragment.getTag()) {
                case Utilidades.FRAGMENT_MAIN:
                    try {
                        Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment1);
                        stackEntry = manager.getBackStackEntryAt(0);
                        manager.popBackStack(stackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        MostrarFragments(new FragmentMain(), Utilidades.FRAGMENT_MAIN);
                        switch (fragment1.getTag()) {
                            case Utilidades.FRAGMENT_HOME:
                                stackEntry = manager.getBackStackEntryAt(0);
                                manager.popBackStack(stackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                ((FragmentMain) fragment).navigationBar.setCurrentActiveItem(1);
                                //MostrarFragments(new FragmentMain(), Utilidades.FRAGMENT_MAIN);
                                //MetodoFinalizarApp();
                                break;
                            case Utilidades.FRAGMENT_MI_PERFIL:
                                stackEntry = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1);
                                manager.popBackStack(stackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                ((FragmentMain) fragment).navigationBar.setCurrentActiveItem(2);
                                break;
                            default:
                                stackEntry = manager.getBackStackEntryAt(0);
                                manager.popBackStack(stackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                MostrarFragments(new FragmentMain(), Utilidades.FRAGMENT_MAIN);
                                break;
                        }
                    } catch (Exception e) {
                        Log.d("TAGerror", "MenuInferior: " + e.getMessage());
                    }


                    break;
                case Utilidades.FRAGMENT_VER_PARTITURA:
                    stackEntry = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1);
                    manager.popBackStack(stackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    BbarIzq.setBackground(getResources().getDrawable(R.drawable.ic_atras));
                    break;
            }
        } catch (Exception e) {
            Log.d("TAGerror", "onBackPressed: " + e.getMessage());
        }

    }


    public int VerificarPreferencias(String KeyLog) {
        SharedPreferences sp = getSharedPreferences("Configuraciones", MODE_PRIVATE);
        return sp.getInt(KeyLog, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @SuppressLint("SetTextI18n")
    public void MostrarOperaciones() throws Exception {
        palabrasArrayList = new ArrayList<>();
        firestore.collection(Utilidades.PALABRAS).orderBy(Utilidades.QUECHUA).addSnapshotListener((value, error) -> {
            if (error != null) {
                complementos.PantallaDeCarga(false);
                Log.e("Firestore", "Error al obtener datos", error);
                return;
            }
            for (DocumentChange document : value.getDocumentChanges()) {
                ObjetoPalabra palabra = new ObjetoPalabra();
                palabra.setId(document.getDocument().getId());
                palabra.setDescripcion(document.getDocument().get(Utilidades.DESCRIPCION).toString());
                palabra.setQuechua(document.getDocument().get(Utilidades.QUECHUA).toString());
                palabra.setSpanish(document.getDocument().get(Utilidades.SPANISH).toString());
                try {
                    palabra.setVozqu(document.getDocument().get(Utilidades.VOZQU).toString());
                } catch (Exception e) {
                    Log.d("TAG", "MostrarOperaciones: " + e.getMessage());
                }
                // Manejo de la imagen
                if (document.getDocument().getData().containsKey(Utilidades.IMAGEN)) {
                    palabra.setLink_imagen(document.getDocument().get(Utilidades.IMAGEN).toString());
                } else {
                    palabra.setLink_imagen(Utilidades.IMAGEN_DEFAULT);
                }
                switch (document.getType()) {
                    case ADDED:
                        if (!palabrasArrayList.contains(palabra)) {
                            palabrasArrayList.add(palabra);
                            if (recyclerView != null) {
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }
                        break;
                    case MODIFIED:
                        if (palabrasArrayList.contains(palabra)) {
                            palabrasArrayList.set(palabrasArrayList.indexOf(palabra), palabra);
                            if (recyclerView != null) {
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }
                        break;
                    case REMOVED:
                        if (palabrasArrayList.contains(palabra)) {
                            palabrasArrayList.remove(palabra);
                            if (recyclerView != null) {
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }
            // Ordenamos la lista
            if (palabrasArrayList.size() > 1) {
                MetodoOrdenamientoQuicksort(palabrasArrayList, palabrasArrayList.size());
            }

            // Notificamos a cualquier componente que muestra la lista que los datos han cambiado
            try {
                if (palabrasArrayList.size() >= value.size()) {
                    complementos.setDelayPantallaDeCarga(true);
                    complementos.PantallaDeCarga(false);
                    complementos.PANTALLA_CARGA_INICIO = false;
                    MostrarFragments(new FragmentMain(), Utilidades.FRAGMENT_MAIN);
                }
            } catch (Exception e) {
                complementos.PantallaDeCarga(false);
            }
        });
    }


}