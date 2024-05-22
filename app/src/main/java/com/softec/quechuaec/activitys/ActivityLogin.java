package com.softec.quechuaec.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softec.quechuaec.R;
import com.softec.quechuaec.utilidades.Complementos;

public class ActivityLogin extends AppCompatActivity {
    private AlertDialog alertDialog;
    private boolean DIALOG_ACTIVO = false;
    private Complementos complementos;
    private FirebaseAuth mAuth;
    private String user, pass;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        complementos = new Complementos(this);
        complementos.MetodoOcultarTeclado();
        usernameEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
    }

    public void MetodoRecuperarContra(View view) {
        DialogRecuperarContra();
    }

    public void DialogRecuperarContra() {
        if (!DIALOG_ACTIVO) {
            DIALOG_ACTIVO = true;
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View VistaDialog;
            VistaDialog = inflater.inflate(R.layout.dialog_recuperar_contra, null);
            alert.setView(VistaDialog);
            alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            /////////////////////////////////////////////////
            final EditText cantidadPro = VistaDialog.findViewById(R.id.recuperar_email);
            Button BotonCancelar = VistaDialog.findViewById(R.id.BRecuperarContraCancelar);
            Button BotonRecuperar = VistaDialog.findViewById(R.id.BRecuperarContra);
            BotonRecuperar.setOnClickListener(view -> {
                if (!cantidadPro.getText().toString().trim().equals("")) {
                    MetodoRecuperarContra(cantidadPro.getText().toString().trim());
                    alertDialog.dismiss();
                    DIALOG_ACTIVO = false;
                }


            });

            BotonCancelar.setOnClickListener(view -> {
                alertDialog.dismiss();
                DIALOG_ACTIVO = false;
            });
        }
    }

    public void MetodoIniciarSesion(View view) {
        user = usernameEditText.getText().toString();
        pass = passwordEditText.getText().toString();
        if (!user.trim().equals("") && !pass.trim().equals("")) {
            IniciarSesionComoUsuarioExistente(user.trim(), pass.trim());
        } else {
            Toast.makeText(this, getString(R.string.llene_todos_los_campos), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            this.finish();
            this.finishAndRemoveTask();
        }
    }


    private void IniciarSesionComoUsuarioExistente(String email, String password) {
        complementos.PantallaDeCarga(true);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                complementos.PantallaDeCarga(false);
                updateUI(user);
            } else {
                Log.w("TAG", "signInWithEmail:failure", task.getException());
                if (task.getException().getLocalizedMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                    try {
                        Snackbar.make(usernameEditText, getString(R.string.correo_incorrecto), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.correo_incorrecto), Toast.LENGTH_SHORT).show();
                        Log.d("TAGerror", e.getMessage());
                    }
                } else if (task.getException().getLocalizedMessage().equals("The password is invalid or the user does not have a password.")) {
                    try {
                        Snackbar.make(usernameEditText, getString(R.string.contrasenias_invalida), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.contrasenias_invalida), Toast.LENGTH_SHORT).show();
                        Log.d("TAGerror", e.getMessage());
                    }
                } else {
                    try {
                        Snackbar.make(usernameEditText, getString(R.string.algo_salio_mal), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.algo_salio_mal), Toast.LENGTH_SHORT).show();
                        Log.d("TAGerror", e.getMessage());
                    }
                }
                complementos.PantallaDeCarga(false);
                updateUI(null);
            }
            // ...
        });
    }


    public void MetodoRecuperarContra(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (!email.toLowerCase().trim().equals("")) {
            complementos.PantallaDeCarga(true);
            auth.sendPasswordResetEmail(email.toLowerCase().trim()).addOnCompleteListener(task -> {
                complementos.PantallaDeCarga(false);
                Toast.makeText(getApplicationContext(), getString(R.string.correo_enviado), Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                complementos.PantallaDeCarga(false);
                Toast.makeText(getApplicationContext(), getString(R.string.algo_salio_mal), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, getString(R.string.ingrese_un_correo_valido), Toast.LENGTH_SHORT).show();
        }
    }

}