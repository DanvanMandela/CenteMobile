package com.craft.silicon.centemobile.data.model.converter;

import androidx.room.TypeConverter;

import com.craft.silicon.centemobile.data.model.address.ExpressAddressResponse;
import com.google.gson.Gson;

public class AddressResponseTypeConverter {
    @TypeConverter
    public String from(ExpressAddressResponse response) {
        if (response == null) {
            return (null);
        }
        return new  Gson().toJson(response, ExpressAddressResponse.class);
    }

    @TypeConverter
    public ExpressAddressResponse to(String s) {
        if (s == null) {
            return (null);
        }
        return new Gson().fromJson(s, ExpressAddressResponse.class);
    }
}
