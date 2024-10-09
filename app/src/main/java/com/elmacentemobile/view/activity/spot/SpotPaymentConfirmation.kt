package com.elmacentemobile.view.activity.spot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elmacentemobile.R
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpotPaymentConfirmation : ComponentActivity(), AppCallbacks {

    private val hashMap = hashMapOf<String?, String?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setIntentData()
            Main(hashMap = hashMap, callbacks = this)
        }


    }

    private fun setIntentData() {
        val intent: Intent = intent
        hashMap["merchantId"] = intent.getStringExtra("merchant_id")
        hashMap["merchantName"] = intent.getStringExtra("merchant_name")
        hashMap["repaymentTerm"] = intent.getStringExtra("repayment_term")
        hashMap["purchaseCode"] = intent.getStringExtra("purchase_code")
        hashMap["purchaseAmount"] = intent.getStringExtra("purchase_amount")
        hashMap["purchaseDate"] = intent.getStringExtra("purchase_date")
        hashMap["installmentAmount"] = intent.getStringExtra("installment_amount")
        hashMap["principalAmount"] = intent.getStringExtra("principal_amount")
        hashMap["selectedCategory"] = intent.getStringExtra("selected_category")

        AppLogger.instance.appLog("ZZDD", hashMap.toString())

    }

    override fun navigateUp() {
        finish()
    }

    override fun auth(pin: String?) {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }


}


@Composable
fun Main(hashMap: HashMap<String?, String?>, callbacks: AppCallbacks) {
    DialogDisplay(hashMap = hashMap, callbacks = callbacks)
}


@Composable
fun DialogDisplay(hashMap: HashMap<String?, String?>, callbacks: AppCallbacks?) {
    var input by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                callbacks?.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.padding()
                )
            }
            Text(
                text = "Invoice Details",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Justify,
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
            )
        }
        Spacer(modifier = Modifier.size(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "${hashMap["purchaseCode"]}",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                )

                Text(
                    text = "${hashMap["installmentAmount"]}",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                )
                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Selected Category",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${hashMap["selectedCategory"]}",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Merchant Name",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${hashMap["merchantName"]}",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.size(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Merchant Name",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${hashMap["merchantName"]}",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                TextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(0.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    placeholder = {
                        Text(
                            text = "Enter Your Pin",
                            style = MaterialTheme.typography.caption,
                            textAlign = TextAlign.Justify,
                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        )
                    }
                )

                Spacer(modifier = Modifier.size(4.dp))

                Button(
                    onClick = {
                        if (input.isBlank()) {
                            ShowToast(context, context.getString(R.string.enter_pin))
                        } else {
                            callbacks?.auth(input)
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.app_blue_dark),
                        contentColor = Color.White
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Justify,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))

            }
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewDialogDisplay() {
    DialogDisplay(hashMap = hashMapOf(), callbacks = null)
}

