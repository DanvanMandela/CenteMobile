package com.elmacentemobile.view.composable.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.PasswordEnum
import com.elmacentemobile.util.AppLogger.Companion.instance
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.composable.landing.PageData
import com.elmacentemobile.view.model.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomKeyboard : BottomSheetDialogFragment(), AppCallbacks {
    private val model: AuthViewModel by viewModels()
    private val map = hashMapOf<String, ViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        map["auth"] = model
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Main(
                    pageData = PageData(
                        callbacks = this@CustomKeyboard,
                        viewModel = map,
                        extra = max
                    )
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }


    override fun getTheme(): Int {
        return R.style.ClearBackgroundDialog
    }

    companion object {
        private lateinit var callback: AppCallbacks
        private var max: Int? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param manager FragmentManager
         * @return A new instance of fragment CustomKeyboard.
         */
        @JvmStatic
        fun instance(
            manager: FragmentManager,
            callback: AppCallbacks
        ) =
            CustomKeyboard().apply {
                this@Companion.callback = callback
                show(manager, this.tag)
            }

        @JvmStatic
        fun instanceExtra(
            manager: FragmentManager,
            callback: AppCallbacks,
            max: Int?
        ) =
            CustomKeyboard().apply {
                this@Companion.callback = callback
                this@Companion.max = max
                show(manager, this.tag)
            }
    }

    override fun onType(data: CustomKeyData?) {
        when (data?.type) {
            KeyFunctionEnum.Finish -> dialog?.dismiss()
            else -> callback.onType(data)
        }
    }

}

@Composable
private fun Main(pageData: PageData?) {
    val model = pageData?.viewModel?.get("auth") as AuthViewModel
    var cols by remember { mutableStateOf(4) }
    var keys: MutableList<CustomKeyData> = remember {
        numericKey
    }

    val pinType = model.storage.passwordType.collectAsState()
    if (!pinType.value.isNullOrBlank() && pageData.extra == null) {
        instance.appLog("CustomKeyboard:PIN:TYPE", "${pinType.value}")
        when (pinType.value) {
            PasswordEnum.TEXT_PASSWORD.type -> {
                cols = 10
                keys = alphaNumericKey
            }

            PasswordEnum.NUMERIC_PASSWORD.type -> {
                cols = 4
                keys = numericKey
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = colorResource(id = R.color.ghost_white))
    ) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(keys.chunked(cols)) { keys ->
                Row(modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)) {
                    for ((index, item) in keys.withIndex()) {
                        Box(modifier = Modifier.fillMaxWidth(1f / (cols - index))) {
                            CustomKeyItem(data = item) { key ->
                                pageData.callbacks?.onType(key)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun MainPreview() {
    Main(pageData = null)
}