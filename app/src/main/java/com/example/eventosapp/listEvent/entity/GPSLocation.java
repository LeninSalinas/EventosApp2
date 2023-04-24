package com.example.eventosapp.listEvent.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "gps_location")
public class GPSLocation {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id_location")
    private Integer idLocation;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    public GPSLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public Integer getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(@NonNull Integer idLocation) {
        this.idLocation = idLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toText(){
        return latitude + ","+longitude;
    }
}
