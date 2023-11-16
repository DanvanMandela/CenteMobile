package com.elmacentemobile.view.ep.model

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.ControlFormatEnum
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockAmountTextInputLayoutBinding
import com.elmacentemobile.databinding.BlockDisabledAmountTextInputLayoutBinding
import com.elmacentemobile.databinding.BlockDropDownLayoutBinding
import com.elmacentemobile.databinding.BlockLinkedDropDownLayoutBinding
import com.elmacentemobile.databinding.BlockTextInputDisabledLayoutBinding
import com.elmacentemobile.databinding.BlockTextInputLayoutBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.auto.DropDownData
import com.elmacentemobile.view.binding.auto.dropDownLinking
import com.elmacentemobile.view.binding.linkedToInput
import com.elmacentemobile.view.binding.setDropDownData
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


@EpoxyModelClass
open class DropDownAdvancedModel : DataBindingEpoxyModel() {


    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var module: Modules

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    private val dispose = CompositeDisposable()

    override fun getDefaultLayout(): Int = R.layout.block_linked_drop_down_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockLinkedDropDownLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(parent: BlockLinkedDropDownLayoutBinding) {
        parent.childContainer.removeAllViews()
        val model = storage.baseVewModel.value
        dispose.add(
            model!!.linkedForms(form.controlID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNullOrEmpty()) nonLinkedParent(parent)
                    else linkedChildren(it, parent)
                }, { it.printStackTrace() })
        )


    }

    private fun nonLinkedParent(parent: BlockLinkedDropDownLayoutBinding) {
        parent.childContainer.removeAllViews()
        parent.data = form
        AppLogger.instance.appLog("CHILD:DROP", Gson().toJson(form))
        val formUpdate = form
        parent.autoEdit.setDropDownData(
            callbacks = callbacks,
            formControl = formUpdate,
            storage = storage,
            modules = module
        )
    }

    private fun linkedChildren(
        it: List<FormControl>,
        parent: BlockLinkedDropDownLayoutBinding
    ) {
        AppLogger.instance.appLog("CHILDREN:DROP", Gson().toJson(it))
        parent.data = form
        parent.childContainer.removeAllViews()
        for (s in it) {
            when (BaseClass.nonCaps(s.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> {
                    when (BaseClass.nonCaps(s.controlFormat)) {
                        BaseClass.nonCaps(ControlFormatEnum.AMOUNT.type) -> {
                            if (!s.displayControl.isNullOrBlank()) {
                                if (s.displayControl == "true") {
                                    val binding =
                                        BlockDisabledAmountTextInputLayoutBinding.inflate(
                                            LayoutInflater.from(parent.root.context)
                                        )
                                    binding.data = s
                                    binding.callback = callbacks
                                    parent.childContainer.addView(binding.root)
                                    setLinkedToInput(parent, binding.child)

                                } else {
                                    val binding =
                                        BlockAmountTextInputLayoutBinding.inflate(
                                            LayoutInflater.from(parent.root.context)
                                        )
                                    binding.data = s
                                    binding.callback = callbacks
                                    parent.childContainer.addView(binding.root)
                                    setLinkedToInput(parent, binding.child)

                                }
                            } else {
                                val binding =
                                    BlockAmountTextInputLayoutBinding.inflate(
                                        LayoutInflater.from(parent.root.context)
                                    )
                                binding.data = s
                                binding.callback = callbacks
                                parent.childContainer.addView(binding.root)
                                setLinkedToInput(parent, binding.child)

                            }
                        }

                        else -> {
                            if (!s.displayControl.isNullOrBlank()) {
                                if (s.displayControl == "true") {
                                    val binding =
                                        BlockTextInputDisabledLayoutBinding.inflate(
                                            LayoutInflater.from(parent.root.context)
                                        )
                                    binding.data = s
                                    binding.callback = callbacks
                                    parent.childContainer.addView(binding.root)
                                    setLinkedToInput(parent, binding.child)

                                } else {
                                    val binding =
                                        BlockTextInputLayoutBinding.inflate(
                                            LayoutInflater.from(parent.root.context)
                                        )
                                    binding.data = s
                                    binding.callback = callbacks
                                    parent.childContainer.addView(binding.root)
                                    setLinkedToInput(parent, binding.child)

                                }
                            } else {
                                val binding =
                                    BlockTextInputLayoutBinding.inflate(
                                        LayoutInflater.from(parent.root.context)
                                    )
                                binding.data = s
                                binding.callback = callbacks
                                parent.childContainer.addView(binding.root)
                                setLinkedToInput(parent, binding.child)

                            }
                        }
                    }
                }

                BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> {
                    val binding = BlockDropDownLayoutBinding.inflate(
                        LayoutInflater
                            .from(parent.root.context)
                    )
                    setLinkedToDropDown(binding, parent, s)
                }
            }
        }
    }

    private fun setLinkedToDropDown(
        childLayout: BlockDropDownLayoutBinding,
        parent: BlockLinkedDropDownLayoutBinding,
        s: FormControl
    ) {
        parent.childContainer.removeAllViews()
        childLayout.data = s
        childLayout.callback = callbacks
        parent.childContainer.addView(childLayout.root)
        dropDownLinking(
            DropDownData(
                callback = callbacks,
                form = form,
                storage = storage,
                view = parent.autoEdit,
                modules = module,
                child = DropDownData(
                    callback = callbacks,
                    form = s,
                    storage = storage,
                    view = childLayout.autoEdit,
                    modules = module,
                )
            )
        )
    }

    private fun setLinkedToInput(
        parent: BlockLinkedDropDownLayoutBinding,
        s: TextInputEditText
    ) {
        parent.autoEdit.linkedToInput(
            callbacks = callbacks,
            formControl = form,
            storage = storage,
            modules = module,
            view = s
        )
    }

}

fun TypedEpoxyController<*>.dropDownAdvancedLayout(
    formControl: FormControl,
    storage: StorageDataSource,
    appCallbacks: AppCallbacks,
    module: Modules
) {
    dropDownAdvanced {
        id(formControl.controlID)
        form(formControl)
        storage(storage)
        callbacks(appCallbacks)
        module(module)
    }
}