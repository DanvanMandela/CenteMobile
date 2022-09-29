package com.elmacentemobile.view.ep.model

import android.text.InputType
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockPasswordTextInputLayoutBinding
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.BaseClass.setMaxLength
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.setDefaultValue
import com.elmacentemobile.view.binding.setDefaultWatcher
import com.google.android.material.textfield.TextInputEditText


@EpoxyModelClass
open class PasswordLayoutModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_password_text_input_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockPasswordTextInputLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockPasswordTextInputLayoutBinding) {
        binding.data = form
        binding.callback = callbacks
        binding.storage = storage
        setDefaultWatcher(binding.child, callbacks, form)
        val state = storage.bio.value
        if (BaseClass.nonCaps(form.controlText) == BaseClass.nonCaps("PIN") || BaseClass.nonCaps(
                form.controlText
            ) == BaseClass.nonCaps("OLD PIN") || BaseClass.nonCaps(form.controlText)
            == BaseClass.nonCaps("PASSWORD")
        )
            if (state != null) {
                if (state) {
                    binding.parent.startIconDrawable = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.fingerprint_small
                    )
                }
            }
        binding.child.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.parent.setStartIconOnClickListener {
            callbacks.bioPayment(binding.child)
        }

        if (form.maxValue != null) {
            if (!TextUtils.isEmpty(form.maxValue)) {
                setMaxLength(binding.child, form.maxValue!!.toInt())
            }
        }
    }
}

fun TypedEpoxyController<*>.passwordModel(
    form: FormControl?,
    storage: StorageDataSource?,
    callbacks: AppCallbacks?
) {
    passwordLayout {
        id(form?.controlID)
        form(form)
        storage(storage)
        callbacks(callbacks)
    }
}

@BindingAdapter("callback", "password")
fun TextInputEditText.setPasswordInputLayout(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    callbacks?.onServerValue(formControl, this)
}