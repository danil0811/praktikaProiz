package com.example.praktika;

import android.os.Parcel;
import android.os.Parcelable;

public class DataPoint implements Parcelable {
    private String material;
    private String materialUsage;
    private String energyUsage;

    public DataPoint() {
        // Пустой конструктор требуется для Firebase
    }

    public DataPoint(String material, String materialUsage, String energyUsage) {
        this.material = material;
        this.materialUsage = materialUsage;
        this.energyUsage = energyUsage;
    }

    protected DataPoint(Parcel in) {
        material = in.readString();
        materialUsage = in.readString();
        energyUsage = in.readString();
    }

    public static final Creator<DataPoint> CREATOR = new Creator<DataPoint>() {
        @Override
        public DataPoint createFromParcel(Parcel in) {
            return new DataPoint(in);
        }

        @Override
        public DataPoint[] newArray(int size) {
            return new DataPoint[size];
        }
    };

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialUsage() {
        return materialUsage;
    }

    public void setMaterialUsage(String materialUsage) {
        this.materialUsage = materialUsage;
    }

    public String getEnergyUsage() {
        return energyUsage;
    }

    public void setEnergyUsage(String energyUsage) {
        this.energyUsage = energyUsage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(material);
        dest.writeString(materialUsage);
        dest.writeString(energyUsage);
    }
}
