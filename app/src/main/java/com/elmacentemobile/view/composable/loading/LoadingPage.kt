package com.elmacentemobile.view.composable.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elmacentemobile.R
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Composable
fun LoadingPage(progress: StateFlow<String>) {

    val percentage =progress.collectAsState()


    Box(modifier = Modifier.fillMaxSize()) {


        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(75.dp)
        ) {
            ProgressIndicator(
                modifier = Modifier
            )
            Image(
                painter = painterResource(id = R.drawable.cente),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp)
                    .size(60.dp)
                    .align(Alignment.Center)

            )

        }



        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = percentage.value,
                fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                style = Typography().caption,
                modifier = Modifier
                    .shimmer()
                    .padding(16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.cs_logo_powered),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(80.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DefaultLandingPreview() {
    LoadingPage(progress = MutableStateFlow("70.0%"))
}