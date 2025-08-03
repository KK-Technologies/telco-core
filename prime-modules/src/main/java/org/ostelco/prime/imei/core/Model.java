// Converted from Kotlin: Model.kt
package org.ostelco.prime.imei.core


package org.ostelco.prime.imei.core

public public class Imei {
    private String tac;
    private String marketingName;
    private String manufacturer;
    private String brandName;
    private String modelName;
    private String operatingSystem;
    private String deviceType;
    private String oem;

    public Imei(String tac, String marketingName, String manufacturer, String brandName, String modelName, String operatingSystem, String deviceType, String oem) {
        this.tac = tac;
        this.marketingName = marketingName;
        this.manufacturer = manufacturer;
        this.brandName = brandName;
        this.modelName = modelName;
        this.operatingSystem = operatingSystem;
        this.deviceType = deviceType;
        this.oem = oem;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getMarketingname() {
        return marketingName;
    }

    public void setMarketingname(String marketingName) {
        this.marketingName = marketingName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrandname() {
        return brandName;
    }

    public void setBrandname(String brandName) {
        this.brandName = brandName;
    }

    public String getModelname() {
        return modelName;
    }

    public void setModelname(String modelName) {
        this.modelName = modelName;
    }

    public String getOperatingsystem() {
        return operatingSystem;
    }

    public void setOperatingsystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDevicetype() {
        return deviceType;
    }

    public void setDevicetype(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

}