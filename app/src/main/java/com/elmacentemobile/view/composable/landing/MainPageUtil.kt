package com.elmacentemobile.view.composable.landing

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.elmacentemobile.R
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.callbacks.AppCallbacks


data class PageData(
    val storage: StorageDataSource?,
    val callbacks: AppCallbacks?,
    val greetings: Greetings?,
    val viewModel: HashMap<String, ViewModel>? = null
)


data class Greetings(
    val emoji: String? = "",
    val message: String?,
    val color: Palette.Swatch?,
    @DrawableRes val image: Int?
)

@Composable
fun Footer(callbacks: AppCallbacks?, modifier: Modifier) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (box) = createRefs()
        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .constrainAs(box) {
                linkTo(parent.start, parent.end, startMargin = 0.dp, endMargin = 0.dp)
                linkTo(parent.top, parent.bottom, topMargin = 0.dp, bottomMargin = 0.dp)
                width = Dimension.ratio("16:6")
                height = Dimension.fillToConstraints
            }) {

            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .padding(start = 14.dp, end = 14.dp, top = 7.dp, bottom = 7.dp),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1.2f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.connect_with_us),
                                fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                                style = Typography().h6,
                                color = colorResource(id = R.color.app_blue_light),
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = {
                                callbacks?.twitter()
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.twitter),
                                    contentDescription = stringResource(id = R.string.twitter),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                )
                            }

                            IconButton(onClick = {
                                callbacks?.facebook()
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.facebook),
                                    contentDescription = stringResource(id = R.string.face_book),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                )
                            }

                            IconButton(onClick = { callbacks?.telephone() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.telephone),
                                    contentDescription = stringResource(id = R.string.mobile),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                )
                            }

                            IconButton(onClick = { callbacks?.email() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.email),
                                    contentDescription = stringResource(id = R.string.email_address),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                )
                            }

                            IconButton(onClick = { callbacks?.chat() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.conversation),
                                    contentDescription = stringResource(id = R.string.message_),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                )
                            }
                        }

                    }

                    Image(
                        painter = painterResource(id = R.drawable.contactus_bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .wrapContentWidth()
                            .fillMaxHeight()
                            .weight(0.5f)
                    )

                }

            }
        }

    }
}




