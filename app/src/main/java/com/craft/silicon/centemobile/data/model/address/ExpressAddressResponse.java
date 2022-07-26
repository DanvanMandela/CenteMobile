package com.craft.silicon.centemobile.data.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExpressAddressResponse {
    @SerializedName("Status")
    @Expose
    private String Status;

    @SerializedName("Message")
    @Expose
    private String Message;

    @SerializedName("CenteAddressCodes")
    @Expose
    private List<AddressStaticData> address;


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ExpressAddressResponse(String status, String message, List<AddressStaticData> address) {
        Status = status;
        Message = message;
        this.address = address;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<AddressStaticData> getAddress() {
        return address;
    }

    public void setAddress(List<AddressStaticData> address) {
        this.address = address;
    }
}
