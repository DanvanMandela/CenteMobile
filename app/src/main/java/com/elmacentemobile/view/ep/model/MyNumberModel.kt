package com.elmacentemobile.view.ep.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
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
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockMyNumberLayoutBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.controller.LinkedVault
import com.elmacentemobile.view.ep.model.helper.blueColor
import com.elmacentemobile.view.ep.model.helper.blueColorMain

@EpoxyModelClass
open class MyNumberModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault


    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_my_number_layout


    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockMyNumberLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(layoutBinding: BlockMyNumberLayoutBinding) {
        layoutBinding.parent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    MyNumber(
                        callbacks = callbacks,
                        vault = data,
                        storage = storage
                    )
                }
            }
        }
    }

}

fun TypedEpoxyController<*>.myNumberViewModel(
    vault: LinkedVault,
    storage: StorageDataSource,
    appCallbacks: AppCallbacks,
) {
    myNumber {
        id(vault.container.controlID)
        storage(storage)
        callbacks(appCallbacks)
        data(vault)
    }
}

@Composable
fun MyNumber(
    callbacks: AppCallbacks,
    vault: LinkedVault,
    storage: StorageDataSource
) {
    val number = storage.activationData.collectAsState().value?.mobile
    LaunchedEffect(number) {
        callbacks.userInput(
            InputData(
                name = vault.container.controlText,
                key = vault.container.serviceParamID,
                value = number,
                encrypted = vault.container.isEncrypted,
                mandatory = vault.container.isMandatory,
                linked = !vault.container.linkedToControl.isNullOrBlank()
            )
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
            }, modifier = Modifier
                .background(color = blueColor, shape = CircleShape)
                .border(
                    width = 1.dp,
                    shape = CircleShape, color = blueColor
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.account_icon),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = blueColorMain
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = "$number",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                color = blueColorMain,
                style = MaterialTheme.typography.body1,
                fontFamily = FontFamily(Font(R.font.poppins_semi_bold))
            )
            Text(
                text = "${vault.container.controlText}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.caption,
                fontFamily = FontFamily(Font(R.font.poppins_medium))
            )
        }
    }

}