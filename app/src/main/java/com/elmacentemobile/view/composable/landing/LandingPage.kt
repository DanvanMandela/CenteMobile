package com.elmacentemobile.view.composable.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.elmacentemobile.R
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.drawableToBitmap
import com.elmacentemobile.view.binding.navigate
import com.elmacentemobile.view.dialog.*
import com.elmacentemobile.view.ep.data.LandingData
import com.elmacentemobile.view.ep.data.LandingPageEnum
import com.elmacentemobile.view.ep.data.LandingPageItem
import com.elmacentemobile.view.model.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.util.*

@AndroidEntryPoint
class LandingPage : Fragment(), AppCallbacks {
    private val authViewModel: AuthViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val staticModel: StaticDataViewModel by viewModels()
    private val viewMap = HashMap<String, ViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setModels()
    }

    private fun setModels() {
        viewMap["baseModel"] = baseViewModel
        viewMap["static"] = staticModel
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
                        storage = authViewModel.storage,
                        callbacks = this@LandingPage,
                        greetings = setTimeOfDay(),
                        viewModel = viewMap
                    )
                )

            }
        }
    }

    private fun setColorPalette(drawable: Int): Palette.Swatch? {
        val bitmap = ContextCompat.getDrawable(requireContext(), drawable)
            ?.let { drawableToBitmap(it) }
        val p = bitmap?.let { Palette.from(it).generate() }!!
        AppLogger.instance.appLog("COLOR", p.lightVibrantSwatch?.titleTextColor.toString())
        return if (p.darkVibrantSwatch == null) p.lightVibrantSwatch
        else p.darkVibrantSwatch

    }

    private fun setTimeOfDay(): Greetings {
        val calendar: Calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> {
                setDynamicImage(
                    message = R.string.good_morining,
                    image = R.drawable.noon_one
                )
            }
            in 12..15 -> {
                setDynamicImage(
                    message = R.string.good_afternoon,
                    image = R.drawable.morning
                )
            }
            in 16..20 -> {
                setDynamicImage(
                    message = R.string.good_evening,
                    image = R.drawable.noon
                )
            }
            else -> {
                setDynamicImage(
                    message = R.string.good_night,
                    image = R.drawable.night
                )
            }
        }
    }

    private fun setDynamicImage(message: Int, image: Int): Greetings {
        return Greetings(
            message = getString(message),
            color = setColorPalette(image),
            image = image
        )
    }

    override fun openUrl(url: String?) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun toLogin() {
        navigate(splashViewModel.navigation)
    }

    override fun toBranch() {
        navigate(widgetViewModel.navigation().navigateMap())
    }

    override fun onTheGo() {
        navigate(widgetViewModel.navigation().navigateOnTheGo())
    }

    override fun toSelf() {
        navigate(widgetViewModel.navigation().navigateSelfReg())
    }

    override fun toOnline() {
        openUrl("https://centeonlinebanking.centenarybank.co.ug/iProfits2Prod/")
    }

    override fun onLanding(data: LandingPageItem?) {
        when (data?.enum) {
            LandingPageEnum.LOGIN -> navigate(splashViewModel.navigation)
            LandingPageEnum.REGISTRATION -> navigate(
                authViewModel.navigationDataSource
                    .navigateSelfReg()
            )
            LandingPageEnum.BRANCH -> navigate(
                authViewModel.navigationDataSource
                    .navigateMap()
            )

            LandingPageEnum.ONLINE_BANKING ->
                openUrl("https://centeonlinebanking.centenarybank.co.ug/iProfits2Prod/")

            LandingPageEnum.ON_THE_GO -> navigate(
                authViewModel.navigationDataSource
                    .navigateOnTheGo()
            )
            else -> {
                throw Exception("No direction implemented")
            }
        }
    }

    override fun twitter() {
        openUrl(Constants.Contacts.url_twitter)
    }

    override fun facebook() {
        openUrl(Constants.Contacts.url_facebook)
    }

    override fun telephone() {
        BaseClass.callPhone(requireActivity(), Constants.Contacts.call_center_number)
    }

    override fun email() {
        val title = "Contact Us"
        val body = ""
        BaseClass.emailCustomerCare(
            requireActivity(),
            title,
            body,
            Constants.Contacts.contact_us_email
        )
    }

    override fun chat() {
        openUrl(Constants.Contacts.url_chat)
    }

    override fun openTipDialog(data: DayTipData?) {
        navigate(baseViewModel.navigationData.navigateToTips(data))
    }

}







@Composable
private fun GreetingMessage(
    greetings: Greetings?,
    modifier: Modifier = Modifier
) {
    var palette by remember { mutableStateOf(Color.White) }
    if (greetings?.color != null) {
        palette = Color(greetings.color.bodyTextColor)
    }
    Text(
        text = greetings?.message!!,
        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
        style = Typography().h6,
        color = palette,
        modifier = modifier
            .padding(16.dp)
    )
}


@Composable
private fun LoginOptions(
    data: PageData?,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier,
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
                style = Typography().body2,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)

            )
            Text(
                text = stringResource(id = R.string.welcome_cente_mobile),
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                style = Typography().body1,
                color = textColor,
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
                        border = BorderStroke(
                            1.dp,
                            color = palette
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = palette),
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
    pageData: PageData?,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(LandingData.landingData) { data ->
            NavigationOptionItem(
                data = data,
                callbacks = pageData?.callbacks
            )
        }
    }
}


@ExperimentalPagerApi
@Composable
private fun BodyCarousel(
    data: PageData?,
    modifier: Modifier
) {
    val viewModel = data?.viewModel!!["static"] as StaticDataViewModel
    val tipData = viewModel.carouselData.collectAsState()
    val pagerState = rememberPagerState()
    val storage = data.storage
    val adverts = storage?.carouselData?.collectAsState()?.value
    val tips = storage?.dayTipData?.collectAsState()?.value

    tips?.forEach { dayTipData ->
        viewModel.carouselData.value.add(
            CarouselTip(
                banner = dayTipData.bannerImage,
                data = TipItemConverter().from(
                    TipItem(
                        tip = Gson().toJson(dayTipData),
                        type = TipTypeEnum.Dialog
                    )
                )
            )
        )
    }

    adverts?.forEach { carouselData ->
        viewModel.carouselData.value.add(
            CarouselTip(
                banner = carouselData.imageURL,
                data = TipItemConverter().from(
                    TipItem(
                        tip = carouselData.imageInfoURL as String,
                        type = TipTypeEnum.Url
                    )
                )
            )
        )
    }

    if (tipData.value.isNotEmpty()) {
        AppLogger.instance.appLog("DAR", Gson().toJson(tipData))
        LaunchedEffect(Unit) {
            while (true) {
                yield()
                delay(2000)
                tween<Float>(600)
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % (pagerState.pageCount)
                )
            }
        }
        HorizontalPager(
            count = tipData.value.size,
            state = pagerState,
            modifier = modifier
        ) { page ->
            CarouselItem(
                data = tipData.value[page],
                callbacks = data.callbacks
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CarouselItem(
    data: CarouselTip,
    callbacks: AppCallbacks?
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (box) = createRefs()
        Card(modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(box) {
                    linkTo(parent.start, parent.end, startMargin = 0.dp, endMargin = 0.dp)
                    linkTo(parent.top, parent.bottom, topMargin = 0.dp, bottomMargin = 0.dp)
                    width = Dimension.ratio("16:6")
                    height = Dimension.fillToConstraints
                },
            elevation = 1.dp,
            onClick = {
                val action = TipItemConverter().to(data.data)
                AppLogger.instance.appLog("CAROUSEL", Gson().toJson(action?.tip))
                if (action?.type == TipTypeEnum.Url) {
                    if (data.data != null) callbacks?.openUrl(action.tip as String)
                } else callbacks?.openTipDialog(TipConverter().to(action?.tip as String))
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = data.banner, error = painterResource(
                            id = R.drawable.logo,
                        ), placeholder = painterResource(id = R.drawable.logo)
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
//                HorizontalPagerIndicator(
//                    pagerState = pagerState,
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp),
//                )
            }

        }
    }
}


@Composable
private fun Footer(
    callbacks: AppCallbacks?,
    modifier: Modifier
) {
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
                    .clip(RoundedCornerShape(8.dp)),
                elevation = 1.dp
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


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Main(pageData: PageData?) {
    val systemUiController = rememberSystemUiController()
    var palette by remember { mutableStateOf(0) }
    if (pageData?.greetings?.color != null) {
        palette = pageData.greetings.color.rgb
    }
    systemUiController.setSystemBarsColor(color = Color(palette))
    ConstraintLayout(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(palette)
                    )
                )
            )
    ) {
        val (header, body) = createRefs()
        ConstraintLayout(modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
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
            .constrainAs(header) {
                linkTo(parent.start, parent.end, startMargin = 0.dp, endMargin = 0.dp)
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
                height = Dimension.ratio("1:1")
            }) {
            val (title, login, navigation) = createRefs()
            GreetingMessage(
                greetings =
                pageData.greetings,
                modifier =
                Modifier.constrainAs(title) {
                    linkTo(
                        parent.start,
                        parent.end,
                        startMargin = 0.dp,
                        endMargin = 0.dp
                    )
                    top.linkTo(parent.top, 8.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
            )
            LoginOptions(data = pageData,
                modifier = Modifier.constrainAs(login) {
                    linkTo(
                        parent.start,
                        parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp
                    )
                    top.linkTo(title.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                })

            NavigationOption(pageData = pageData,
                modifier = Modifier.constrainAs(navigation) {
                    linkTo(
                        parent.start,
                        parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp
                    )
                    top.linkTo(login.bottom, 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                })

        }
        ConstraintLayout(modifier = Modifier
            .constrainAs(body) {
                linkTo(parent.start, parent.end, startMargin = 0.dp, endMargin = 0.dp)
                top.linkTo(header.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.ratio("1:1")
            }) {
            val (carousel, contact) = createRefs()
            BodyCarousel(pageData,
                modifier = Modifier.constrainAs(carousel) {
                    top.linkTo(parent.top, 16.dp)
                    linkTo(
                        parent.start,
                        parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp
                    )
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                })
            Footer(callbacks = pageData.callbacks,
                modifier = Modifier.constrainAs(contact) {
                    top.linkTo(carousel.bottom, 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(
                        parent.start,
                        parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp
                    )
                })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultLandingPreview() {
    Main(
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


