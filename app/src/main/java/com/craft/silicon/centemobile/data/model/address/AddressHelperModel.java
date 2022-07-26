package com.craft.silicon.centemobile.data.model.address;

import androidx.annotation.NonNull;

public class AddressHelperModel {
    private String address;
    private String code;


    public AddressHelperModel(String address, String code) {
        this.address = address;
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @NonNull
    @Override
    public String toString() {
        return address;
    }
}
