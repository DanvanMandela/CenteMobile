package com.elmacentemobile.view.ep.model

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockAmountTextInputLayoutBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.NumberTextWatcherForThousand
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.custom.CurrencyInput
import com.elmacentemobile.view.binding.setDefaultValue
import com.google.android.material.textfield.TextInputEditText


@EpoxyModelClass
open class AmountLayoutModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_amount_text_input_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockAmountTextInputLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockAmountTextInputLayoutBinding) {
        binding.data = form
        binding.callback = callbacks
        binding.storage = storage
        binding.child.removeTextChangedListener(
            NumberTextWatcherForThousand(
                binding.child,
                callbacks,
                form
            )
        )
        if (form.displayControl != null) {
            if (!TextUtils.isEmpty(form.displayControl)) {
                if (form.displayControl == "true")
                    binding.child.isEnabled = false
            }
        }
    }
}

fun TypedEpoxyController<*>.amountModel(
    form: FormControl?,
    storage: StorageDataSource?,
    callbacks: AppCallbacks?
) {
    amountLayout {
        id(form?.controlID)
        form(form)
        storage(storage)
        callbacks(callbacks)
    }
}

@BindingAdapter("callback", "amount", "value")
fun CurrencyInput.setAmountInputLayout(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
    value: String?
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    setDefaultAmountWatcher(this, callbacks, formControl!!)
    callbacks?.onServerValue(formControl, this)
    if (!TextUtils.isEmpty(value)) {
        this.setText(value)
        callbacks?.userInput(
            InputData(
                name = formControl.controlText,
                key = formControl.serviceParamID,
                value = NumberTextWatcherForThousand.trimCommaOfString(value),
                encrypted = formControl.isEncrypted,
                mandatory = formControl.isMandatory,
                linked = !formControl.linkedToControl.isNullOrBlank()
            )
        )
    }
}


fun setDefaultAmountWatcher(
    inputEdit: TextInputEditText,
    callbacks: AppCallbacks?, formControl: FormControl
) {
    inputEdit.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            try {
                if (!e.isNullOrEmpty()) {
                    val amount =
                        NumberTextWatcherForThousand
                            .trimCommaOfString(e.toString()).split(".")[0].toInt()
                    if (!formControl.maxValue.isNullOrEmpty() && !formControl.minValue.isNullOrEmpty()) {
                        val max = formControl.maxValue!!.toInt()
                        val min = formControl.minValue!!.toInt()
                        AppLogger.instance.appLog("Max", "$max")
                        if (amount > max) {
                            callbacks?.userInput(
                                InputData(
                                    name = formControl.controlText,
                                    key = formControl.serviceParamID,
                                    value = NumberTextWatcherForThousand.trimCommaOfString(e.toString()),
                                    encrypted = formControl.isEncrypted,
                                    mandatory = formControl.isMandatory,
                                    validation = "Amount more  than maximum",
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )

                        } else if (amount < min) {
                            callbacks?.userInput(
                                InputData(
                                    name = formControl.controlText,
                                    key = formControl.serviceParamID,
                                    value = NumberTextWatcherForThousand.trimCommaOfString(e.toString()),
                                    encrypted = formControl.isEncrypted,
                                    mandatory = formControl.isMandatory,
                                    validation = "Amount less than minimum",
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )
                        } else {
                            callbacks?.userInput(
                                InputData(
                                    name = formControl.controlText,
                                    key = formControl.serviceParamID,
                                    value = NumberTextWatcherForThousand.trimCommaOfString(e.toString()),
                                    encrypted = formControl.isEncrypted,
                                    mandatory = formControl.isMandatory,
                                    validation = null,
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )
                        }
                    } else callbacks?.userInput(
                        InputData(
                            name = formControl.controlText,
                            key = formControl.serviceParamID,
                            value = NumberTextWatcherForThousand.trimCommaOfString(e.toString()),
                            encrypted = formControl.isEncrypted,
                            mandatory = formControl.isMandatory,
                            validation = null,
                            linked = !formControl.linkedToControl.isNullOrBlank()
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    })
}

