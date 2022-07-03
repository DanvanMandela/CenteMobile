package com.craft.silicon.centemobile.util.callbacks;

import android.widget.CheckBox;
import android.widget.RadioButton;

import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public interface FormCallback {


    default void linkedInput(TextInputEditText view, FormControl formControl) {
    }

    default void linkedDropDown(FormControl formControl, StaticDataDetails data) {
    }


    default void setFormNavigation(List<FormControl> forms, Modules modules) {
    }

    default void onRadioCheck(FormControl formControl, RadioButton view) {

    }

    default void onCheckBox(FormControl formControl, CheckBox view) {

    }

    default void onToggleButton(FormControl formControl) {

    }


}
