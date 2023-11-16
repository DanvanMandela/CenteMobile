package com.elmacentemobile.view.ep.model

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockTextInputPanLayoutBinding
import com.elmacentemobile.util.TextHelper
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.setDefaultValue
import com.google.android.material.textfield.TextInputEditText
import java.util.*


open class TextInputPanModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_text_input_pan_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockTextInputPanLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addChildren(binding: BlockTextInputPanLayoutBinding) {
        binding.data = form
        binding.callback = callbacks
        binding.storage = storage
        binding.child.setOnTouchListener { _, _ ->
            return@setOnTouchListener binding.child.hasFocus()

        }
        binding.child.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.child.setSelection(
                    binding.child.text!!.length
                )
            }
        }

    }
}

@BindingAdapter("callback", "pan", "value")
fun TextInputEditText.setPanInputLayout(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
    value: String?
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    setPanTextWatcher(this, callbacks, formControl!!)
    callbacks?.onServerValue(formControl, this)
    if (!formControl.controlValue.isNullOrBlank())
        this.setText(formControl.controlValue)

    if (!TextUtils.isEmpty(value)) {
        this.setText(value)
        callbacks?.userInput(
            InputData(
                name = formControl.controlText,
                key = formControl.serviceParamID,
                value = Objects.requireNonNull(value)
                    .toString().replace("-", ""),
                encrypted = formControl.isEncrypted,
                mandatory = formControl.isMandatory
            )
        )
    }

    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            callbacks?.userInput(
                InputData(
                    name = formControl.controlText,
                    key = formControl.serviceParamID,
                    value = Objects.requireNonNull(this@setPanInputLayout.text.toString())
                        .toString().replace("-", ""),
                    encrypted = formControl.isEncrypted,
                    mandatory = formControl.isMandatory
                )
            )
        }
    })

}


fun setPanTextWatcher(
    inputEdit: TextInputEditText,
    callbacks: AppCallbacks?,
    formControl: FormControl
) {
    inputEdit.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun afterTextChanged(editable: Editable) {

            if (!formControl.controlValue.isNullOrBlank()) {
                val prefix = "${formControl.controlValue} "
                val count = editable.toString().length
                if (count < prefix.length) {
                    inputEdit.setText(prefix)
                    val selectionIndex = inputEdit.length()
                    inputEdit.setSelection(selectionIndex)
                }
            }

            if (!TextHelper.isInputCorrect(
                    editable,
                    TextHelper.CARD_NUMBER_TOTAL_SYMBOLS,
                    TextHelper.CARD_NUMBER_DIVIDER_MODULO,
                    TextHelper.CARD_NUMBER_DIVIDER
                )
            ) {
                editable.replace(
                    0, editable.length,
                    TextHelper.concatString(
                        TextHelper.getDigitArray(
                            editable,
                            TextHelper.CARD_NUMBER_TOTAL_DIGITS
                        ),
                        TextHelper.CARD_NUMBER_DIVIDER_POSITION, TextHelper.CARD_NUMBER_DIVIDER
                    )
                )

            }
        }
    })
}

fun TypedEpoxyController<*>.inputPanModel(
    form: FormControl?,
    storage: StorageDataSource?,
    callbacks: AppCallbacks?
) {
    textInputPan {
        id(form?.controlID)
        form(form)
        storage(storage)
        callbacks(callbacks)
    }
}
