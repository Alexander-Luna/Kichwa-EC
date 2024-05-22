package com.softec.quechuaec.activitys;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softec.quechuaec.R;
import com.softec.quechuaec.utilidades.Complementos;
import com.softec.quechuaec.utilidades.Utilidades;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityAgregarPalabra extends AppCompatActivity {
    FirebaseFirestore firestore;

    TextInputEditText palabraes, descripcion, palabraqu;
    Map<String, Object> item;
    String IMG = "";
    ImageView img;
    FirebaseStorage storage;
    StorageReference storageRef;
    private Complementos complementos;
    private String palabraID = "";
    boolean isAudioUploader = false;
    private String VOZQU = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_palabra);
        complementos = new Complementos(this);
        complementos.setDelayPantallaDeCarga(true);
        complementos.PANTALLA_CARGA_INICIO = false;
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        palabraes = findViewById(R.id.TIpes);
        palabraqu = findViewById(R.id.TIpqu);
        descripcion = findViewById(R.id.TIdesc);
        img = findViewById(R.id.imagen);
        img.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });
        findViewById(R.id.BAceptar).setOnClickListener(v -> {
            complementos.PantallaDeCarga(true);
            if (palabraID.equals("")) {
                Agregar();
            } else {
                Actualizar();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            palabraID = extras.getString("id");
            cargarPalabra(palabraID);
        }
        Button btnRecord = findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(v -> {
            if (!isAudioUploader) {
                audioLauncher.launch("audio/*");
            } else {
                btnRecord.setText("Seleccionar Archivo");
            }
            isAudioUploader = !isAudioUploader;
        });
    }


    private void cargarPalabra(String id) {
        firestore.collection(Utilidades.PALABRAS).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String descripcionp = document.getString(Utilidades.DESCRIPCION);
                    String spanish = document.getString(Utilidades.SPANISH);
                    String quechua = document.getString(Utilidades.QUECHUA);
                    IMG = document.getString(Utilidades.IMAGEN);
                    VOZQU = document.getString(Utilidades.VOZQU);
                    descripcion.setText(descripcionp);
                    palabraes.setText(spanish);
                    palabraqu.setText(quechua);
                } else {
                    Toast.makeText(getApplicationContext(), "No se encontró la palabra", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error al cargar la palabra", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), selectedImageUri -> {
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });


    public void Agregar() {
        if (!palabraes.getText().toString().equals("") && !palabraqu.getText().toString().equals("") && (img != null || img == null) && !descripcion.getText().toString().equals("")) {
            item = new HashMap<>();
            GuardarAudio(new AudioUploadCallback() {
                @Override
                public void onSuccess(String audioUrl) {
                    GuardarIMG();
                }

                @Override
                public void onFailure() {
                    item.put(Utilidades.VOZQU, "");
                    GuardarIMG();
                }
            });
        } else {
            complementos.PantallaDeCarga(false);
            Toast.makeText(this, "Llene los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void GuardarIMG() {
        try {
            Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String nombreImagen = UUID.randomUUID().toString();
            StorageReference referenciaImagen = storageRef.child("imagenes/" + nombreImagen + ".jpg");
            UploadTask uploadTask = referenciaImagen.putBytes(data);
            uploadTask.addOnCompleteListener(taskSnapshot -> {
                referenciaImagen.getDownloadUrl().addOnCompleteListener(uri -> {
                    item.put(Utilidades.IMAGEN, uri.toString());
                    guardarPalabra();
                });
            }).addOnFailureListener(exception -> {
                complementos.PantallaDeCarga(false);
                Toast.makeText(getApplicationContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Agregar: " + exception.getMessage());
            });
        } catch (Exception e) {
            item.put(Utilidades.IMAGEN, "");
            guardarPalabra();
            Log.d("TAGerror", "GuardarIMG: " + e.getMessage());
        }
    }

    private Uri audiouri1 = null;
    private ActivityResultLauncher<String> audioLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), audioUri -> {
        if (audioUri != null) {
            audiouri1 = audioUri;
            Toast.makeText(this, "Audio seleccionado: " + audioUri.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al seleccionar el audio", Toast.LENGTH_SHORT).show();
        }
    });

    private interface AudioUploadCallback {
        void onSuccess(String audioUrl);

        void onFailure();
    }

    private void GuardarAudio(AudioUploadCallback callback) {
        if (audiouri1 != null) {
            try {
                if (!VOZQU.equals("")) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference referenciaStorage = storage.getReferenceFromUrl(VOZQU);
                    referenciaStorage.delete().addOnSuccessListener(aVoid -> {
                    });
                }
            } catch (Exception e) {
                Log.d("TAGerror", "Eliminar_Palabra: " + e.getMessage());
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(audiouri1);
                byte[] audioBytes = IOUtils.toByteArray(inputStream);
                String nombreAudio = UUID.randomUUID().toString() + ".mp3";
                StorageReference referenciaAudio = storageRef.child("audios/" + nombreAudio);

                UploadTask uploadTask = referenciaAudio.putBytes(audioBytes);
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continuar con la tarea de obtener la URL de descarga
                    return referenciaAudio.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            // Actualizar el item con la URL del audio
                            item.put(Utilidades.VOZQU, downloadUri.toString());
                            callback.onSuccess(downloadUri.toString());
                        } else {
                            callback.onFailure();
                        }
                    } else {
                        callback.onFailure();
                    }
                });
            } catch (Exception e) {
                Log.d("TAGerror", "GuardarAudio: " + e.getMessage());
                callback.onFailure();
            }
        } else {
            callback.onFailure();
        }
    }


    private void guardarPalabra() {
        item.put(Utilidades.DESCRIPCION, descripcion.getText().toString().trim());
        item.put(Utilidades.SPANISH, palabraes.getText().toString().trim());
        item.put(Utilidades.QUECHUA, palabraqu.getText().toString().trim());
        firestore.collection(Utilidades.PALABRAS).document(palabraes.getText().toString().trim()).set(item).addOnCompleteListener(task -> {
            palabraes.setText("");
            palabraqu.setText("");
            descripcion.setText("");
            complementos.PantallaDeCarga(false);
            Toast.makeText(getApplicationContext(), "Palabra agregada con éxito", Toast.LENGTH_SHORT).show();
            this.finish();
        });
    }

    public void Actualizar() {
        if (!palabraes.getText().toString().equals("") && !palabraqu.getText().toString().equals("") && (img != null || img == null) && !descripcion.getText().toString().equals("")) {
            item = new HashMap<>();
            GuardarAudio(new AudioUploadCallback() {
                @Override
                public void onSuccess(String audioUrl) {
                    ActualizarIMG();
                }

                @Override
                public void onFailure() {
                    ActualizarIMG();
                }
            });
        } else {
            complementos.PantallaDeCarga(false);
            Toast.makeText(this, "Llene los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void ActualizarIMG() {
        try {
            if (audiouri1 == null) {
                item.put(Utilidades.VOZQU, VOZQU);
            }
            if (img != null) {
                try {
                    if (!IMG.equals("")) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference referenciaImagen = storage.getReferenceFromUrl(IMG);
                        referenciaImagen.delete().addOnSuccessListener(aVoid -> {

                        });
                    }
                } catch (Exception e) {
                    Log.d("TAGerror", "Eliminar_Palabra: " + e.getMessage());
                }
                Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                String nombreImagen = UUID.randomUUID().toString();
                StorageReference referenciaImagen = storageRef.child("imagenes/" + nombreImagen + ".jpg");
                UploadTask uploadTask = referenciaImagen.putBytes(data);
                uploadTask.addOnFailureListener(exception -> {
                    complementos.PantallaDeCarga(false);
                    Toast.makeText(getApplicationContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(taskSnapshot -> {
                    referenciaImagen.getDownloadUrl().addOnCompleteListener(uri -> {
                        item.put(Utilidades.IMAGEN, uri.toString());
                        actualizarPalabra();
                    });
                });
            } else {
                actualizarPalabra();
            }
        } catch (Exception e) {
            item.put(Utilidades.IMAGEN, IMG);
            actualizarPalabra();
            Log.d("TAGerror", "ActualizarIMG: " + e.getMessage());
        }
    }


    private void actualizarPalabra() {
        item.put(Utilidades.DESCRIPCION, descripcion.getText().toString().trim());
        item.put(Utilidades.SPANISH, palabraes.getText().toString().trim());
        item.put(Utilidades.QUECHUA, palabraqu.getText().toString().trim());
        complementos.PantallaDeCarga(true);
        firestore.collection(Utilidades.PALABRAS).document(palabraID).set(item).addOnCompleteListener(task -> {
            palabraes.setText("");
            palabraqu.setText("");
            descripcion.setText("");
            complementos.PantallaDeCarga(false);
            this.finish();
            Toast.makeText(getApplicationContext(), "Palabra actualizada con éxito", Toast.LENGTH_SHORT).show();
        });
    }
}
