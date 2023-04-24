package com.example.eventosapp.listEvent.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "evento_table",
        foreignKeys = @ForeignKey(entity = GPSLocation.class,
                parentColumns = "id_location",
                childColumns = "idLocation",
                onDelete = ForeignKey.CASCADE))
public class Eventos {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer idEvento;

    //@NonNull
    @ColumnInfo(name="tema")
    private String tema;

    //@NonNull
    @ColumnInfo(name = "fecha")
    public String fecha;

    //@NonNull
    @ColumnInfo(name = "expositor")
    private String expositor;

    //@NonNull
    @ColumnInfo(name = "estado")
    private String estado;

    @ColumnInfo(name = "idLocation")
    private int idLocation;

    public Eventos() {
    }

    public Eventos(@NonNull String tema, @NonNull String fecha, @NonNull String expositor, @NonNull String estado) {

        this.tema = tema;
        this.fecha = fecha;
        this.expositor = expositor;
        this.estado = estado;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    //@NonNull
    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(@NonNull Integer idEvento) {
        this.idEvento = idEvento;
    }

    //@NonNull
    public String getTema() {
        return tema;
    }

    public void setTema(@NonNull String tema) {
        this.tema = tema;
    }

    //@NonNull
    public String getFechaEvento() {
        return fecha;
    }

    public void setFechaEvento(@NonNull String fecha) {
        this.fecha = fecha;
    }

    //@NonNull
    public String getExpositor() {
        return expositor;
    }

    public void setExpositor(@NonNull String expositor) {
        this.expositor = expositor;
    }

    //@NonNull
    public String getEstado() {
        return estado;
    }

    public void setEstado(@NonNull String estado) {
        this.estado = estado;
    }

}

