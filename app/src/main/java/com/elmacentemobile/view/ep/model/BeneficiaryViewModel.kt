package com.elmacentemobile.view.ep.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.Color
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
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.user.Beneficiary
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockBeneficiaryViewLayoutBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.model.helper.blueColor

@EpoxyModelClass
open class BeneficiaryViewModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var module: Modules

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
        layoutBinding.parent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val beneficiary = storage.beneficiary.collectAsState().value
                    val beneficiaryData = remember { mutableStateListOf<Beneficiary?>() }

                    LaunchedEffect(beneficiary) {
                        if (!beneficiary.isNullOrEmpty()) {
                            beneficiaryData.clear()
                            beneficiaryData.addAll(beneficiary.filter { it?.merchantID == module.merchantID })
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
                                formControl = form,
                                module = module
                            )
                        }
                        items(beneficiaryData) { ben ->
                            BeneficiaryItem(beneficiary = ben!!)
                        }
                    }
                }
            }
        }
    }

}

fun TypedEpoxyController<*>.beneficiaryViewModel(
    formControl: FormControl,
    storage: StorageDataSource,
    appCallbacks: AppCallbacks,
    module: Modules
) {
    beneficiaryView {
        id(formControl.controlID)
        form(formControl)
        storage(storage)
        callbacks(appCallbacks)
        module(module)
    }
}


@Composable
fun BeneficiaryItem(beneficiary: Beneficiary) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = { }, modifier = Modifier
                .background(color = blueColor, shape = CircleShape)
                .border(
                    width = 1.dp,
                    shape = CircleShape, color = Color.LightGray
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
            style = MaterialTheme.typography.caption,
            fontFamily = FontFamily(Font(R.font.poppins_medium))
        )
    }
}

@Composable
fun BeneficiaryAddItem(
    callbacks: AppCallbacks,
    formControl: FormControl,
    module: Modules
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = {
                callbacks.addBeneficiary(
                    module,
                    formControl
                )
            }, modifier = Modifier
                .background(color = blueColor, shape = CircleShape)
                .border(
                    width = 1.dp,
                    shape = CircleShape,
                    color = Color.LightGray
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
            fontFamily = FontFamily(Font(R.font.poppins_medium))
        )
    }
}