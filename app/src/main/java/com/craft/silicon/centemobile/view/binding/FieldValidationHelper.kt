package com.craft.silicon.centemobile.view.binding

import android.app.Activity
import android.text.TextUtils
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.google.gson.Gson
import org.json.JSONObject


class FieldValidationHelper {
    private var validForm = false

    companion object {
        private var INSTANCE: FieldValidationHelper? = null

        @get:Synchronized
        val instance: FieldValidationHelper
            get() {
                if (INSTANCE == null) {
                    INSTANCE = FieldValidationHelper()
                }
                return INSTANCE!!
            }

    }

    fun validateFields(
        inputList: MutableList<InputData>,
        params: List<String>,
        json: JSONObject,
        encrypted: JSONObject,
        activity: Activity
    ): Boolean {
        if (inputList.isNotEmpty()) {
            AppLogger.instance.appLog("FIELD:Validation", Gson().toJson(inputList))
            for (i in inputList) {
                if (TextUtils.isEmpty(i.value) && i.mandatory) {
                    activity.runOnUiThread {
                        ShowToast(
                            activity,
                            "${i.name} ${activity.getString(R.string._required)}",
                            true
                        )
                    }
                    validForm = false
                    break
                } else validForm = true
            }
            if (validForm) {
                for (s in inputList) {
                    for (p in params) {
                        if (s.key == p) {
                            if (s.encrypted) {
                                encrypted.put(s.key!!, BaseClass.newEncrypt(s.value))
                            } else {
                                json.put(s.key!!, s.value)
                            }
                        }
                    }
                }
            }
        } else {
            activity.runOnUiThread {
                ShowToast(
                    activity,
                    activity.getString(R.string.all_field_required),
                    true
                )
            }

        }
        return validForm
    }
}