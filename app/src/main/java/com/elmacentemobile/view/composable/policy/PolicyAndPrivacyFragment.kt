package com.elmacentemobile.view.composable.policy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.FragmentManager
import com.elmacentemobile.R
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.composable.landing.PageData
import com.elmacentemobile.view.composable.theme.AppBlueColorLight
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyAndPrivacyFragment : BottomSheetDialogFragment(), AppCallbacks {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Main(
                    pageData = PageData(
                        callbacks = this@PolicyAndPrivacyFragment
                    )
                )
            }
        }
    }

    override fun navigateUp() {
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { data ->
                val behaviour = BottomSheetBehavior.from(data)
                setupFullHeight(data)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.setDraggable(false)
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    companion object {
        private lateinit var module: Modules

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PolicyAndPrivacyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun show(manager: FragmentManager, module: Modules) =
            PolicyAndPrivacyFragment().apply {
                this@Companion.module = module
                show(manager, this.tag)
            }

    }

}


@Composable
private fun Main(pageData: PageData?) {
    val pCheckedState = remember { mutableStateOf(false) }
    val aCheckedState = remember { mutableStateOf(false) }
    val cCheckedState = remember { mutableStateOf(false) }


    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = AppBlueColorLight,
            contentColor = Color.White,
            navigationIcon = {
                IconButton(onClick = {
                    pageData?.callbacks?.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.privacy_settings),
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    style = MaterialTheme.typography.body1,
                )
            })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(), elevation = 2.dp
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val (title, switch, info) = createRefs()
                    Text(
                        text = stringResource(id = R.string.capture_perfomance_data),
                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .constrainAs(title) {
                                start.linkTo(parent.start, 8.dp)
                                top.linkTo(parent.top, 8.dp)
                                end.linkTo(switch.start, 8.dp)
                                width = Dimension.fillToConstraints
                            }, color = colorResource(id = R.color.app_blue_light)
                    )

                    Switch(
                        checked = pCheckedState.value,
                        onCheckedChange = { pCheckedState.value = it },
                        modifier = Modifier.constrainAs(switch) {
                            end.linkTo(parent.end, 8.dp)
                            top.linkTo(parent.top, 8.dp)
                            bottom.linkTo(title.bottom, 8.dp)
                        }
                    )

                    Text(
                        text = stringResource(id = R.string.allow_performance_capture),
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.constrainAs(info) {
                            top.linkTo(title.bottom, 8.dp)
                            start.linkTo(parent.start, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom, 8.dp)
                            width = Dimension.fillToConstraints
                        }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(), elevation = 2.dp
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val (title, switch, info) = createRefs()
                    Text(
                        text = stringResource(id = R.string.anonymize_personal_data),
                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .constrainAs(title) {
                                start.linkTo(parent.start, 8.dp)
                                top.linkTo(parent.top, 8.dp)
                                end.linkTo(switch.start, 8.dp)
                                width = Dimension.fillToConstraints
                            }, color = colorResource(id = R.color.app_blue_light)
                    )

                    Switch(
                        checked = aCheckedState.value,
                        onCheckedChange = { aCheckedState.value = it },
                        modifier = Modifier.constrainAs(switch) {
                            end.linkTo(parent.end, 8.dp)
                            top.linkTo(parent.top, 8.dp)
                            bottom.linkTo(title.bottom, 8.dp)
                        }
                    )

                    Text(
                        text = stringResource(id = R.string.don_include_my_personal_data),
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.constrainAs(info) {
                            top.linkTo(title.bottom, 8.dp)
                            start.linkTo(parent.start, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom, 8.dp)
                            width = Dimension.fillToConstraints
                        }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(), elevation = 2.dp
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val (title, switch, info) = createRefs()
                    Text(
                        text = stringResource(id = R.string.crash_report),
                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .constrainAs(title) {
                                start.linkTo(parent.start, 8.dp)
                                top.linkTo(parent.top, 8.dp)
                                end.linkTo(switch.start, 8.dp)
                                width = Dimension.fillToConstraints
                            }, color = colorResource(id = R.color.app_blue_light)
                    )

                    Switch(
                        checked = cCheckedState.value,
                        onCheckedChange = { cCheckedState.value = it },
                        modifier = Modifier.constrainAs(switch) {
                            end.linkTo(parent.end, 8.dp)
                            top.linkTo(parent.top, 8.dp)
                            bottom.linkTo(title.bottom, 8.dp)
                        }
                    )

                    Text(
                        text = stringResource(id = R.string.allow_crash_report),
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.constrainAs(info) {
                            top.linkTo(title.bottom, 8.dp)
                            start.linkTo(parent.start, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom, 8.dp)
                            width = Dimension.fillToConstraints
                        }
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = { }, modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.app_blue_light),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.save_),
                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    Main(pageData = null)
}