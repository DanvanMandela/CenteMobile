package com.elmacentemobile.view.composable.landing.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elmacentemobile.R
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.composable.disabledVerticalPointerInputScroll
import com.elmacentemobile.view.composable.landing.Greetings
import com.elmacentemobile.view.composable.landing.PageData
import com.elmacentemobile.view.ep.data.LandingData
import com.elmacentemobile.view.ep.data.LandingPageItem
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Preview(showBackground = true)
@Composable
private fun DefaultLandingPreview() {
    LandHeaderItem(
        PageData(
            storage = null,
            callbacks = null,
            greetings = Greetings(
                message = "Good\nNight",
                color = null,
                image = R.drawable.night
            )
        )
    )
}

@Composable
fun LandHeaderItem(pageData: PageData?) {
    val systemUiController = rememberSystemUiController()
    var palette by remember { mutableStateOf(0) }
    if (pageData?.greetings?.color != null) {
        palette = pageData.greetings.color.rgb
    }
    systemUiController.setSystemBarsColor(
        color = Color(palette)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .paint(
                    painterResource(id = pageData?.greetings?.image!!),
                    contentScale = ContentScale.Crop
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(palette),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Greeting(greetings = pageData.greetings)
            LoginOption(data = pageData)
            NavigationOption(
                listData = LandingData.landingData,
                callbacks = pageData.callbacks
            )

        }
    }
}

@Composable
fun Greeting(greetings: Greetings?) {
    var palette by remember { mutableStateOf(Color.White) }
    if (greetings?.color != null) {
        palette = Color(greetings.color.bodyTextColor)
    }

    Text(
        text = "${greetings?.message} ${greetings?.emoji}",
        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
        style = Typography().h5,
        color = palette,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    )
}

@Composable
private fun LoginOption(data: PageData?) {

    val callbacks = data?.callbacks
    val storage = data?.storage

    var palette by remember { mutableStateOf(Color(R.color.app_blue_light)) }
    var textColor by remember { mutableStateOf(Color(R.color.white)) }
    if (data?.greetings?.color != null) {
        palette = Color(data.greetings.color.rgb)
        textColor = Color(data.greetings.color.bodyTextColor)
    }

    val activated = storage?.isActivated?.collectAsState()
    var paddingEnd by remember { mutableStateOf(16.dp) }
    if (activated?.value == false) paddingEnd = 8.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp, end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
            .wrapContentHeight(),
        border = BorderStroke(
            1.dp,
            color = colorResource(id = R.color.white)
        ),
        backgroundColor = colorResource(id = R.color.transparent)
            .compositeOver(colorResource(id = R.color.transparent_white)),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to),
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)

            )
            Text(
                text = stringResource(id = R.string.welcome_cente_mobile),
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                style = Typography().body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp)

            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 4.dp)
                    .wrapContentHeight()
            ) {
                if (activated?.value == false) {
                    Button(
                        onClick = { callbacks?.toSelf() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textColor,
                            backgroundColor = palette
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 16.dp, end = paddingEnd)
                    ) {
                        Text(
                            text = stringResource(id = R.string.register),
                            fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                            style = Typography().button
                        )
                    }
                }
                Button(
                    onClick = { callbacks?.toLogin() },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = palette,
                        backgroundColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = paddingEnd, end = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                        style = Typography().button
                    )
                }

            }


        }
    }
}

@Composable
private fun NavigationOption(
    listData: MutableList<LandingPageItem>,
    callbacks: AppCallbacks?
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .disabledVerticalPointerInputScroll()
    ) {
        items(listData) { data ->
            NavigationOptionItem(data = data, callbacks = callbacks)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationOptionItem(
    data: LandingPageItem,
    callbacks: AppCallbacks?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .height(150.dp),
        border = BorderStroke(
            1.dp,
            color = colorResource(id = R.color.white)
        ),
        backgroundColor = colorResource(id = R.color.transparent).compositeOver(colorResource(id = R.color.transparent_white)),
        onClick = {
            callbacks?.onLanding(data)
        }, elevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                Image(
                    painter = painterResource(id = data.avatar),
                    contentDescription = stringResource(id = data.title),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )

            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.6f)
                    .background(colorResource(id = R.color.white))
            ) {
                Text(
                    text = stringResource(id = data.title),
                    fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                    style = Typography().caption,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.dar_color_one),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Center)
                        .padding(4.dp)
                )
            }
        }

    }
}

