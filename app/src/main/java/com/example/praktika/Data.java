package com.example.praktika;

public class Data {
    private String material;
    private String materialUsage;
    private String energyUsage;

    public Data() {
        // Пустой конструктор требуется для Firebase
    }

    public Data(String material, String materialUsage, String energyUsage) {
        this.material = material;
        this.materialUsage = materialUsage;
        this.energyUsage = energyUsage;
    }

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
}
