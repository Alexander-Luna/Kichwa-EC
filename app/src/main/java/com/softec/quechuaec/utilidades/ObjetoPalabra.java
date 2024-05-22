package com.softec.quechuaec.utilidades;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;

public class ObjetoPalabra implements Parcelable {
    public static final Parcelable.Creator<ObjetoPalabra> CREATOR = new Parcelable.Creator<ObjetoPalabra>() {
        @Override
        public ObjetoPalabra createFromParcel(Parcel in) {
            return new ObjetoPalabra(in);
        }

        @Override
        public ObjetoPalabra[] newArray(int size) {
            return new ObjetoPalabra[size];
        }
    };
    private String spanish, quechua, descripcion;
    private String imagen, vozqu;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    protected ObjetoPalabra(Parcel in) {
        spanish = in.readString();
        quechua = in.readString();
        descripcion = in.readString();
        descripcion = in.readString();
    }

    public String getVozqu() {
        try {
            return vozqu;
        } catch (Exception e) {
            return "";
        }
    }

    public void setVozqu(String vozqu) {
        this.vozqu = vozqu;
    }

    public ObjetoPalabra() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(spanish);
        dest.writeString(quechua);
        dest.writeString(descripcion);
        dest.writeString(descripcion);
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        try {
            return descripcion;
        } catch (Exception e) {
            Log.d("TAGerror", "getGenero: " + e.getMessage());
            return "10";
        }
    }

    public String getImagen() {
        try {
            return imagen;
        } catch (Exception e) {
            Log.d("TAGerror", "getGenero: " + e.getMessage());
            return Utilidades.IMAGEN_DEFAULT;
        }
    }

    public void setLink_imagen(String link_imagen) {
        this.imagen = link_imagen;
    }


    public String getSpanish() {
        try {
            return spanish;
        } catch (Exception e) {
            Log.d("TAGerror", "getNombre: " + e.getMessage());
            return Utilidades.VACIO;
        }
    }

    public void setSpanish(String spanish) {
        this.spanish = spanish;
    }

    public String getQuechua() {
        try {
            return quechua;
        } catch (Exception e) {
            Log.d("TAGerror", "getGenero: " + e.getMessage());
            return Utilidades.VACIO;
        }

    }

    public void setQuechua(String quechua) {
        this.quechua = quechua;
    }


    @Override
    public boolean equals(Object o) {
        boolean v = false;
        ObjetoPalabra sC = (ObjetoPalabra) o;
        if (this.getDescripcion().equals(sC.getDescripcion())) {
            v = true;
        }
        return v;
    }
}