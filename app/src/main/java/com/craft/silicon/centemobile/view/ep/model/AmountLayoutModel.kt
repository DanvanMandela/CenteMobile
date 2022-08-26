package com.craft.silicon.centemobile.view.ep.model

import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.databinding.BlockAmountTextInputLayoutBinding
import com.craft.silicon.centemobile.util.NumberTextWatcherForThousand
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.setDefaultValue
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


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

        binding.child.addTextChangedListener(thousandSeparatorListener(callbacks, form))
//        binding.child.addTextChangedListener(
//            NumberTextWatcherForThousand(
//                binding.child,
//                callbacks,
//                form
//            )
//        )
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
fun TextInputEditText.setAmountInputLayout(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
    value: String?
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    callbacks?.onServerValue(formControl, this)
    this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    if (!TextUtils.isEmpty(value)) {
        this.setText(value)
        callbacks?.userInput(
            InputData(
                name = formControl?.controlText,
                key = formControl?.serviceParamID,
                value = NumberTextWatcherForThousand.trimCommaOfString(value),
                encrypted = formControl!!.isEncrypted,
                mandatory = formControl.isMandatory
            )
        )
    }
}

private fun thousandSeparatorListener(callbacks: AppCallbacks?, form: FormControl?): TextWatcher {
    return object : TextWatcher {
        var formatting = false
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (formatting) return
            formatting = true
            val str = editable.toString().replace("[^\\d]".toRegex(), "")
            if (str.isNotEmpty()) {
                val s1 = str.toDouble()
                val nf2: NumberFormat = NumberFormat.getInstance(Locale.getDefault())
                (nf2 as DecimalFormat).applyPattern("#,###,###")
                editable.replace(0, editable.length, nf2.format(s1))
                callbacks?.userInput(
                    InputData(
                        name = form?.controlText,
                        key = form?.serviceParamID,
                        value = NumberTextWatcherForThousand.trimCommaOfString(editable.toString()),
                        encrypted = form!!.isEncrypted,
                        mandatory = form.isMandatory
                    )
                )
            }
            formatting = false
        }
    }
}