package com.softec.quechuaec.utilidades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.softec.quechuaec.R;
import com.softec.quechuaec.activitys.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AdaptadorTransacciones extends RecyclerView.Adapter<AdaptadorTransacciones.ViewHolerDatos> implements View.OnClickListener, View.OnLongClickListener {
    ArrayList<ObjetoPalabra> objetoTransacciones;
    Activity activity;
    boolean VERTICAL = true;
    private View.OnClickListener listener;
    private View.OnLongClickListener listenerLong;
    private double Rating;

    public AdaptadorTransacciones(ArrayList<ObjetoPalabra> objetoTransacciones, Activity activity) {

        this.objetoTransacciones = objetoTransacciones;
        this.activity = activity;
        this.VERTICAL = VERTICAL;
    }


    @NonNull
    @Override
    public ViewHolerDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.palabra_item, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new ViewHolerDatos(view);
    }

    private Button lastPlayButton = null;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolerDatos holder, int position) {
        ObjetoPalabra objetoOperacion = this.objetoTransacciones.get(position);
        holder.palabraQ.setText(objetoOperacion.getQuechua());
        holder.descripcion.setText(objetoOperacion.getDescripcion());
        Glide.with(activity).load(objetoOperacion.getImagen()).apply(new RequestOptions().placeholder(R.drawable.ic_logo).transform(new RoundedCorners(16))).into(holder.logo);
        holder.palabraS.setText(objetoOperacion.getSpanish());
        try {
            if (objetoOperacion.getVozqu().equals("")) {
                holder.play.setVisibility(View.GONE);
            } else {
                holder.play.setVisibility(View.VISIBLE);
            }

            holder.play.setOnClickListener(v -> {
                holder.play.setVisibility(View.GONE);
                holder.loadingIndicator.setVisibility(View.VISIBLE);
                String audioUrl = objetoOperacion.getVozqu();
                if (!audioUrl.equals("")) {
                    if (lastPlayButton != null && lastPlayButton != holder.play && ((MainActivity) activity).mediaPlayer != null && ((MainActivity) activity).mediaPlayer.isPlaying()) {
                        ((MainActivity) activity).mediaPlayer.stop();
                        ((MainActivity) activity).mediaPlayer.release();
                        ((MainActivity) activity).mediaPlayer = null;
                        lastPlayButton.setBackgroundResource(R.drawable.ic_play);
                    }
                    lastPlayButton = holder.play;
                    if (((MainActivity) activity).mediaPlayer != null && ((MainActivity) activity).mediaPlayer.isPlaying()) {
                        ((MainActivity) activity).mediaPlayer.stop();
                        ((MainActivity) activity).mediaPlayer.release();
                        ((MainActivity) activity).mediaPlayer = null;
                        holder.play.setBackgroundResource(R.drawable.ic_play);
                        holder.loadingIndicator.setVisibility(View.GONE);
                        holder.play.setVisibility(View.VISIBLE);
                    } else {
                        holder.play.setVisibility(View.GONE);
                        holder.loadingIndicator.setVisibility(View.VISIBLE);
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("AudioLinks", Context.MODE_PRIVATE);
                        String cachedAudioUrl = sharedPreferences.getString("audio_" + position, null);
                        if (cachedAudioUrl != null && cachedAudioUrl.equals(audioUrl)) {
                            File audioFile = new File(activity.getCacheDir(), "audio" + position + ".mp3");
                            if (audioFile.exists()) {
                                playAudio(audioFile, holder.play, holder.loadingIndicator);
                            } else {
                                downloadAndSaveAudio(audioUrl, audioFile, holder.play, holder.loadingIndicator);
                            }
                        } else {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("audio_" + position, audioUrl);
                            editor.apply();
                            File audioFile = new File(activity.getCacheDir(), "audio" + position + ".mp3");
                            downloadAndSaveAudio(audioUrl, audioFile, holder.play, holder.loadingIndicator);
                        }


                    }
                }
            });
        } catch (Exception e) {
            holder.play.setVisibility(View.GONE);
        }
    }

    private void downloadAndSaveAudio(String audioUrl, File audioFile, Button play, ProgressBar loadingIndicator) {
        try {
           play.setVisibility(View.GONE);
         loadingIndicator.setVisibility(View.VISIBLE);
            URL url = new URL(audioUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            FileOutputStream fos = new FileOutputStream(audioFile);
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            inputStream.close();
            playAudio(audioFile, play, loadingIndicator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playAudio(File audioFile, Button play, ProgressBar loadingIndicator) {
        try {


            ((MainActivity) activity).mediaPlayer = new MediaPlayer();
            ((MainActivity) activity).mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            ((MainActivity) activity).mediaPlayer.prepareAsync();
            ((MainActivity) activity).mediaPlayer.setOnPreparedListener(mp -> {
                ((MainActivity) activity).mediaPlayer.start();
                play.setBackgroundResource(R.drawable.ic_stop);
                play.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            });
            ((MainActivity) activity).mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                return false;
            });
            ((MainActivity) activity).mediaPlayer.setOnCompletionListener(mp -> {
                ((MainActivity) activity).mediaPlayer.release();
                ((MainActivity) activity).mediaPlayer = null;
                play.setBackgroundResource(R.drawable.ic_play);
                play.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setOnclickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongclickListener(View.OnLongClickListener listenerLong) {
        this.listenerLong = listenerLong;
    }

    @Override
    public int getItemCount() {
        try {
            return objetoTransacciones.size();
        } catch (Exception e) {
            Log.d("TAGerror1", "getItemCount: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (listenerLong != null) {
            listenerLong.onLongClick(view);
        }
        return false;
    }

    public class ViewHolerDatos extends RecyclerView.ViewHolder {
        TextView palabraQ, descripcion, palabraS;
        ImageView logo;
        Button play;
        ProgressBar loadingIndicator;

        public ViewHolerDatos(@NonNull View itemView) {
            super(itemView);
            loadingIndicator = itemView.findViewById(R.id.loadingIndicator);
            play = itemView.findViewById(R.id.btnplay);
            logo = itemView.findViewById(R.id.item_logo);
            palabraQ = itemView.findViewById(R.id.item_quechua);
            palabraS = itemView.findViewById(R.id.item_spanish);
            descripcion = itemView.findViewById(R.id.item_descripcion);
        }
    }
}