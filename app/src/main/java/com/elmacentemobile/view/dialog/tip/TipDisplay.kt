package com.elmacentemobile.view.dialog.tip

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.HtmlCompat
import coil.compose.rememberAsyncImagePainter
import com.elmacentemobile.R
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.findActivity
import com.elmacentemobile.view.composable.html.data.HtmlText
import com.elmacentemobile.view.composable.shot.ImageResult
import com.elmacentemobile.view.composable.shot.ScreenshotBox
import com.elmacentemobile.view.composable.shot.rememberScreenshotState
import com.elmacentemobile.view.dialog.DayTipData
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DisplayMain(
    data: DayTipData,
    appCallbacks: AppCallbacks?,
    color: StateFlow<Int>?,
    bitmap: StateFlow<Bitmap>?
) {
    val activity = LocalContext.current.findActivity()
    val colorItem = color?.collectAsState()
    var message by remember { mutableStateOf("") }
    var share by remember { mutableStateOf("") }
    val image = bitmap?.collectAsState()

    val view = LocalView.current
    var s by remember {
        mutableStateOf(
            BitmapFactory.decodeResource(
                activity.resources,
                R.drawable.logo
            )
        )
    }

    if (!data.textData.isNullOrBlank()) {
        message = BaseClass.decodeBase64(data.textData)
        share = HtmlCompat.fromHtml(
            message, HtmlCompat.FROM_HTML_MODE_LEGACY,
            null, null
        ).toString()
    }
    val scroll: ScrollState = rememberScrollState()

    val screenshotState = rememberScreenshotState()
    val imageResult: ImageResult = screenshotState.imageState.value


//    LaunchedEffect(key1 = imageResult) {
//        when (imageResult) {
//            is ImageResult.Success -> {
//                BaseClass.shareTip(
//                    activity, imageResult.data,
//                    "${data.title}\n\n${share}"
//                )
//            }
//            is ImageResult.Error -> {
//                AppLogger.instance.appLog("Capture", "Error: ${imageResult.exception.message}")
//            }
//            else -> {}
//        }
//    }


    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .fillMaxWidth()
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${data.title}",
                    fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                    style = Typography().body1,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        BaseClass.shareTip(
                            activity, image?.value,
                            "${data.title}\n\n${share}"
                        )
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .size(38.dp)
                        .background(colorResource(id = R.color.app_blue_light))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "",
                        tint = Color.White
                    )

                }

            }

            if (!data.bigImage.isNullOrBlank())
                ScreenshotBox(screenshotState = screenshotState, content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(color = Color.White)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = data.bigImage,
                                error = painterResource(id = R.drawable.logo),
                                placeholder = painterResource(id = R.drawable.logo)
                            ),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                })

            if (message.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(id = R.color.ghost_white))
                        .padding(8.dp)

                ) {
                    HtmlText(
                        html = message, modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Button(
                    onClick = {
                        appCallbacks?.onDialog()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(48.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(32.dp)),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(id = R.color.white),
                        backgroundColor = colorResource(id = R.color.app_blue_light)
                    ),

                    ) {
                    Text(
                        text = "${data.buttonText}",
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = Typography().button,
                        modifier = Modifier
                            .wrapContentSize()

                    )
                }
            }
        }
    }

}

@Composable
fun TipDialogMain(
    data: DayTipData,
    appCallbacks: AppCallbacks?,
    color: StateFlow<Int>?,
    bitmap: StateFlow<Bitmap>?
) {
    val activity = LocalContext.current.findActivity()
    val scroll: ScrollState = rememberScrollState()
    val screenshotState = rememberScreenshotState()
    var message by remember { mutableStateOf("") }
    var share by remember { mutableStateOf("") }
    val image = bitmap?.collectAsState()
    if (!data.textData.isNullOrBlank()) {
        message = BaseClass.decodeBase64(data.textData)
        share = HtmlCompat.fromHtml(
            message, HtmlCompat.FROM_HTML_MODE_LEGACY,
            null, null
        ).toString()
    }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .fillMaxWidth()
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${data.title}",
                    fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                    style = Typography().body1,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        BaseClass.shareTip(
                            activity, image?.value,
                            "${data.title}\n\n${share}"
                        )
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .size(32.dp)
                        .background(colorResource(id = R.color.app_blue_light))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "",
                        tint = Color.White
                    )

                }

            }

            if (!data.bigImage.isNullOrBlank())
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    val (box) = createRefs()
                    Box(
                        modifier = Modifier
                            .constrainAs(box) {
                                linkTo(
                                    parent.start,
                                    parent.end,
                                    startMargin = 0.dp,
                                    endMargin = 0.dp
                                )
                                linkTo(
                                    parent.top,
                                    parent.bottom,
                                    topMargin = 0.dp,
                                    bottomMargin = 0.dp
                                )
                                width = Dimension.ratio("1:1")
                                height = Dimension.fillToConstraints
                            }
                    ) {
                        ScreenshotBox(screenshotState = screenshotState, content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color.White)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = data.bigImage,
                                        error = painterResource(id = R.drawable.icon_logo),
                                        placeholder = painterResource(id = R.drawable.icon_logo)
                                    ),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        })
                    }

                }

            if (message.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(id = R.color.ghost_white))
                        .padding(8.dp)

                ) {
                    HtmlText(
                        html = message, modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Button(
                    onClick = {
                        appCallbacks?.onDialog()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(48.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(32.dp)),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(id = R.color.white),
                        backgroundColor = colorResource(id = R.color.app_blue_light)
                    ),

                    ) {
                    Text(
                        text = "${data.buttonText}",
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        style = Typography().button,
                        modifier = Modifier
                            .wrapContentSize()

                    )
                }
            }
        }

    }
}


@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
   // TipDialogMain(dataList[0])
}


