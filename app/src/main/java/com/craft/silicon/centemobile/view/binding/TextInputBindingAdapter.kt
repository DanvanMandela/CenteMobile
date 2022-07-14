package com.craft.silicon.centemobile.view.binding

import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
import android.view.View
import androidx.databinding.BindingAdapter
import com.chaos.view.PinView
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("callback", "hidden")
fun TextInputEditText.setHidden(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
) {
    if (!formControl?.controlValue.isNullOrBlank())
        this.setText(formControl?.controlValue)

    if (formControl?.controlValue != null)
        if (!TextUtils.isEmpty(formControl.controlValue)) {
            callbacks?.userInput(
                InputData(
                    name = formControl.controlText,
                    key = formControl.serviceParamID,
                    value = formControl.controlValue,
                    encrypted = formControl.isEncrypted,
                    mandatory = formControl.isMandatory
                )
            )
            callbacks?.onServerValue(formControl, this)
        }
}

@BindingAdapter("callback", "form")
fun TextInputEditText.setInputLayout(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    callbacks?.onServerValue(formControl, this)

    val parent = this.parent.parent as TextInputLayout
    //parent.setFormat(formControl)

    setControl(this, formControl)


    if (formControl?.controlFormat != null)
        this.setInput(formControl.controlFormat)




    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            callbacks?.userInput(
                InputData(
                    name = formControl?.controlText,
                    key = formControl?.serviceParamID,
                    value = e.toString(),
                    encrypted = formControl?.isEncrypted!!,
                    mandatory = formControl.isMandatory
                )
            )
        }
    })
}

fun setControl(view: TextInputEditText, form: FormControl?) {
    if (form?.displayControl != null)
        if (!TextUtils.isEmpty(form.displayControl))
            if (nonCaps(form.displayControl) == nonCaps("TRUE")) {
                view.clearFocus()
            }
}

@BindingAdapter("max", "min")
fun TextInputEditText.setInputLen(max: String?, min: String?) {
    if (max != null && min != null) {
        AppLogger.instance.appLog("InputLen", "max:$max min:$min")
        if (!TextUtils.isEmpty(max)) {
            this.filters = arrayOf<InputFilter>(LengthFilter(max.toInt()))
        }
    }
}

@BindingAdapter("format")
fun TextInputLayout.setFormat(form: FormControl?) {
    if (form != null) {
        if (nonCaps(form.controlFormat) == nonCaps(ControlFormatEnum.AMOUNT.type)) {
            this.prefixTextView.visibility = View.VISIBLE
            this.prefixText = context.getString(R.string.currency_)
            this.isExpandedHintEnabled = true
        }
    }
}

@BindingAdapter("setInput")
fun TextInputEditText.setInput(input: String?) {
    if (TextUtils.isEmpty(input))
        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
    else
        if (nonCaps(input) == nonCaps(ControlFormatEnum.AMOUNT.type)) {
            this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        } else if (nonCaps(input) == nonCaps(ControlFormatEnum.PIN_NUMBER.type)) {
            this.inputType = InputType.TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
        } else if (nonCaps(input) == nonCaps(ControlFormatEnum.PIN.type)) {
            this.inputType = InputType.TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
        } else this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
}

val inputTypes = mutableListOf(
    InputTypeItem(
        name = ControlFormatEnum.DATE.type,
        value = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
    ),
    InputTypeItem(
        name = "datetime",
        value = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL
    ),
    InputTypeItem("none", InputType.TYPE_NULL),
    InputTypeItem(
        name = ControlFormatEnum.NUMBER.type,
        value = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
    ),
    InputTypeItem(
        name = ControlFormatEnum.AMOUNT.type,
        value = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    ),
    InputTypeItem(
        name = ControlFormatEnum.PIN_NUMBER.type,
        value = InputType.TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
    ),
    InputTypeItem(
        name = ControlFormatEnum.NUMERIC.type,
        value = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
    ),
    InputTypeItem(ControlFormatEnum.PHONE.type, InputType.TYPE_CLASS_PHONE),
    InputTypeItem(
        name = "text",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
    ),
    InputTypeItem(name = "textAutoComplete", value = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE),
    InputTypeItem(name = "textAutoCorrect", value = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT),
    InputTypeItem(name = "textCapCharacters", value = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS),
    InputTypeItem(name = "textCapSentences", value = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES),
    InputTypeItem(name = "textCapWords", value = InputType.TYPE_TEXT_FLAG_CAP_WORDS),
    InputTypeItem(
        name = "textEmailAddress",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    ),
    InputTypeItem(
        name = "textEmailSubject",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
    ),
    InputTypeItem(
        name = "textFilter",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_FILTER
    ),
    InputTypeItem(
        name = "textLongMessage",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
    ),
    InputTypeItem("textMultiLine", InputType.TYPE_TEXT_FLAG_MULTI_LINE),
    InputTypeItem(
        name = "textPassword",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    ),
    InputTypeItem(
        name = "textPersonName",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    ),
    InputTypeItem(name = "textNoSuggestions", value = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS),
    InputTypeItem(
        name = "textPhonetic",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PHONETIC
    ),
    InputTypeItem(
        name = "textPostalAddress",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
    ),
    InputTypeItem(
        name = "textShortMessage",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
    ),
    InputTypeItem(
        name = "textUri",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
    ),
    InputTypeItem(
        name = "textVisiblePassword",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    ),
    InputTypeItem(
        name = "textWebEditText",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
    ),
    InputTypeItem(
        name = "textWebEmailAddress",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
    ),
    InputTypeItem(
        name = "textWebPassword",
        value = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    ),
    InputTypeItem(
        name = "time",
        value = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
    )
)

data class InputTypeItem(val name: String, val value: Int)

@BindingAdapter("callback", "otp", "module")
fun PinView.setOTP(callbacks: AppCallbacks?, formControl: FormControl?, modules: Modules) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    callbacks?.onOTP(formControl, this)

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            callbacks?.userInput(
                InputData(
                    name = formControl?.controlText,
                    key = formControl?.serviceParamID,
                    value = e.toString(),
                    encrypted = formControl?.isEncrypted!!,
                    mandatory = formControl.isMandatory
                )
            )
        }
    })

}


