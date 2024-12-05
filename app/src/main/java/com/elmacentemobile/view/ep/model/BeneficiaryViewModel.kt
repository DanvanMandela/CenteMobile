package com.elmacentemobile.view.ep.model

import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.user.Beneficiary
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockBeneficiaryViewLayoutBinding
import com.elmacentemobile.databinding.BlockContactInputLayoutBinding
import com.elmacentemobile.databinding.BlockTextInputLayoutBinding
import com.elmacentemobile.disabledAmountTextInputLayout
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.controller.LinkedVault
import com.elmacentemobile.view.ep.model.helper.blueColor
import com.google.android.material.textfield.TextInputEditText

@EpoxyModelClass
open class BeneficiaryViewModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault


    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout(): Int = R.layout.block_beneficiary_view_layout


    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockBeneficiaryViewLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(layoutBinding: BlockBeneficiaryViewLayoutBinding) {
        var textField: AutoCompleteTextView? = null
        var textField2: TextInputEditText? = null
        layoutBinding.children.removeAllViews()
        for (s in data.children) {
            when (BaseClass.nonCaps(s.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.PHONE_CONTACTS.type) -> {
                    val binding =
                        BlockContactInputLayoutBinding.inflate(
                            LayoutInflater.from(layoutBinding.root.context)
                        )
                    binding.data = s
                    binding.callback = callbacks
                    layoutBinding.children.addView(binding.root)
                    textField = binding.autoEdit


                    if (s.displayControl.isNullOrEmpty()) {
                        if (s.displayControl == "true") {
                            textField.isFocusable = false
                        }
                    }
                    binding.autoInput.setEndIconOnClickListener {
                        callbacks.onContactSelect(binding.autoEdit)
                    }
                }

                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> {
                    val binding =
                        BlockTextInputLayoutBinding.inflate(
                            LayoutInflater.from(layoutBinding.root.context)
                        )
                    binding.data = s
                    binding.callback = callbacks
                    layoutBinding.children.addView(binding.root)
                    textField2 = binding.child
                    if (s.displayControl.isNullOrEmpty()) {
                        if (s.displayControl == "true") {
                            textField?.isFocusable = false
                        }
                    }
                }
            }
        }

        layoutBinding.parent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val beneficiary = storage.beneficiary.collectAsState().value
                    val beneficiaryData = remember { mutableStateListOf<Beneficiary?>() }

                    LaunchedEffect(beneficiary) {
                        if (!beneficiary.isNullOrEmpty()) {
                            beneficiaryData.clear()
                            beneficiaryData.addAll(beneficiary.filter { it?.merchantID == data.module?.merchantID })
                        }
                    }


                    LazyRow(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            BeneficiaryAddItem(
                                callbacks = callbacks,
                                formControl = data.container
                            )
                        }
                        items(beneficiaryData) { ben ->
                            BeneficiaryItem(
                                beneficiary = ben!!,
                                child = textField,
                                child2 = textField2
                            )
                        }
                    }
                }
            }
        }
    }

}


fun TypedEpoxyController<*>.beneficiaryViewModel(
    vault: LinkedVault,
    storage: StorageDataSource,
    appCallbacks: AppCallbacks,
) {
    beneficiaryView {
        id(vault.container.controlID)
        storage(storage)
        callbacks(appCallbacks)
        data(vault)
    }
}


@Composable
fun BeneficiaryItem(
    beneficiary: Beneficiary,
    child: AutoCompleteTextView?,
    child2: TextInputEditText?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .wrapContentHeight()
            .padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = {
                val account = "${beneficiary.accountID}"
                child?.setText(account)
                child2?.setText(account)
            }, modifier = Modifier
                .background(color = blueColor, shape = CircleShape)
                .border(
                    width = 1.dp,
                    shape = CircleShape, color = blueColor
                )
        ) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "${beneficiary.accountAlias}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            style = MaterialTheme.typography.caption,
            fontFamily = FontFamily(Font(R.font.poppins_medium))
        )
    }
}

@Composable
fun BeneficiaryAddItem(
    callbacks: AppCallbacks,
    formControl: FormControl
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .wrapContentHeight()
            .padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = {
                callbacks.addBeneficiary(formControl)
            }, modifier = Modifier
                .background(color = blueColor, shape = CircleShape)
                .border(
                    width = 1.dp,
                    shape = CircleShape,
                    color = blueColor
                )

        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(id = R.string.add_),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            fontFamily = FontFamily(Font(R.font.poppins_medium))
        )
    }
}