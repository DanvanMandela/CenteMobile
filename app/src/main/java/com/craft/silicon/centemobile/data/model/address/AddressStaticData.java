package com.craft.silicon.centemobile.data.model.address;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "address_data_tbl")
public class AddressStaticData implements Serializable {

    @SerializedName("AddressCode")
    @Expose
    private String AddressCode;

    @SerializedName("Region")
    @Expose
    private String Region;

    @SerializedName("DistrictName")
    @Expose
    private String DistrictName;

    @SerializedName("CountyName")
    @Expose
    private String CountyName;

    @SerializedName("SubCountyName")
    @Expose
    private String SubCountyName;

    @SerializedName("ParishName")
    @Expose
    private String ParishName;

    @SerializedName("VillageName")
    @Expose
    private String VillageName;

    @SerializedName("EAName")
    @Expose
    private String EAName;


    public AddressStaticData() {
        AddressCode = "";
    }


    public AddressStaticData(@NonNull String addressCode, @Nullable String region,
                             @Nullable String districtName, @Nullable String countyName, @Nullable String subCountyName,
                             @Nullable String parishName, @Nullable String villageName, @Nullable String EAName) {
        AddressCode = addressCode;
        Region = region;
        DistrictName = districtName;
        CountyName = countyName;
        SubCountyName = subCountyName;
        ParishName = parishName;
        VillageName = villageName;
        this.EAName = EAName;
    }

    @NonNull
    public String getAddressCode() {
        return AddressCode;
    }

    public void setAddressCode(@NonNull String addressCode) {
        AddressCode = addressCode;
    }

    @Nullable
    public String getRegion() {
        return Region;
    }

    public void setRegion(@Nullable String region) {
        Region = region;
    }

    @Nullable
    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(@Nullable String districtName) {
        DistrictName = districtName;
    }

    @Nullable
    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(@Nullable String countyName) {
        CountyName = countyName;
    }

    @Nullable
    public String getSubCountyName() {
        return SubCountyName;
    }

    public void setSubCountyName(@Nullable String subCountyName) {
        SubCountyName = subCountyName;
    }

    @Nullable
    public String getParishName() {
        return ParishName;
    }

    public void setParishName(@Nullable String parishName) {
        ParishName = parishName;
    }

    @Nullable
    public String getVillageName() {
        return VillageName;
    }

    public void setVillageName(@Nullable String villageName) {
        VillageName = villageName;
    }

    @Nullable
    public String getEAName() {
        return EAName;
    }

    public void setEAName(@Nullable String EAName) {
        this.EAName = EAName;
    }
}
