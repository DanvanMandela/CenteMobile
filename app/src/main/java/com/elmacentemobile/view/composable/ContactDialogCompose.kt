package com.elmacentemobile.view.composable

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import coil.compose.rememberAsyncImagePainter
import com.elmacentemobile.R
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.ContactData
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_DATA = "data"

@AndroidEntryPoint
class ContactDialogCompose : DialogFragment(), AppCallbacks {

    private var contactData: ContactData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_DATA, ContactData::class.java)
            } else it.getParcelable(ARG_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            AppLogger.instance.appLog(
                this@ContactDialogCompose::class.java.simpleName,
                Gson().toJson(contactData)
            )
            setContent {
                if (contactData != null) {
                    ContactDialog(
                        data = contactData!!,
                        this@ContactDialogCompose
                    )
                }
            }
        }
    }

    companion object {
        private lateinit var callback: AppCallbacks

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data ContactData
         * @return A new instance of fragment GeneralServiceReceiptFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            data: ContactData,
            manager: FragmentManager, callback: AppCallbacks
        ) =
            ContactDialogCompose().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
                this@Companion.callback = callback
                show(manager, this.tag)
            }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
    }

    override fun onDialog() {
        dialog?.dismiss()
    }

    override fun setContact(contact: String?) {
        callback.setContact(contact)
    }

}

@Composable
fun NumberList(
    numbers: MutableList<String>,
    callbacks: AppCallbacks?
) {
    LazyColumn {
        items(numbers) { number ->
            Column(modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .height(54.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            callbacks?.setContact(number)
                            callbacks?.onDialog()
                        })
                ) {
                    Text(
                        text = number,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = Typography().body2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 16.dp)
                            .align(Alignment.CenterStart)
                    )

                }
                if (number != numbers.last()) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(1.dp)
                            .background(color = colorResource(id = R.color.ghost_white))
                    )
                }
            }

        }
    }
}

@Composable
fun ContactDialog(
    data: ContactData,
    callback: AppCallbacks?
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .align(CenterVertically)
            ) {
                Image(
                    painter = if (data.avatar != null)
                        rememberAsyncImagePainter(data.avatar)
                    else painterResource(id = R.drawable.user_icon),
                    contentDescription = data.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterVertically)
            ) {
                Text(
                    text = data.name!!,
                    fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                    style = Typography().body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp)
                        .align(Alignment.CenterStart)
                )
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
                .background(color = colorResource(id = R.color.ghost_white))
        )
        NumberList(numbers = data.numbers!!, callbacks = callback)
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NumberList(numbers = mutableListOf("07124586521", "07124586527"), null)
}