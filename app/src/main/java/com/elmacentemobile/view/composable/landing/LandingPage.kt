package com.elmacentemobile.view.composable.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.asLiveData
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.elmacentemobile.R
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.drawableToBitmap
import com.elmacentemobile.view.binding.navigate
import com.elmacentemobile.view.composable.landing.utils.Greeting
import com.elmacentemobile.view.composable.landing.utils.NavigationOptionItem
import com.elmacentemobile.view.composable.loading.LoadingPage
import com.elmacentemobile.view.composable.theme.AppBlueColorLight
import com.elmacentemobile.view.dialog.*
import com.elmacentemobile.view.ep.data.LandingData
import com.elmacentemobile.view.ep.data.LandingPageEnum
import com.elmacentemobile.view.ep.data.LandingPageItem
import com.elmacentemobile.view.model.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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

    private fun setCarouselData() {
        baseViewModel.dataSource.dayTipData.asLiveData()
            .observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    it.forEach { dayTipData ->
                        staticModel.carouselData.value.add(
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
                }

            }

        baseViewModel.dataSource.carouselData.asLiveData().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                it.forEach { carouselData ->
                    staticModel.carouselData.value.add(
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
            }
        }
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
            setCarouselData()
            val sync = baseViewModel.dataSource.sync.asLiveData()
            sync.observe(viewLifecycleOwner) {
                if (it != null && it.work >= 8 || it?.complete == true) {
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
                } else setContent {
                    LoadingPage(progress = MutableStateFlow("${it?.percentage}%"))
                }
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
                    image = R.drawable.noon_one,
                    emoji = "\uD83E\uDD2D"
                )
            }
            in 12..15 -> {
                setDynamicImage(
                    message = R.string.good_afternoon,
                    image = R.drawable.morning,
                    emoji = "\uD83D\uDE0A"
                )
            }
            in 16..20 -> {
                setDynamicImage(
                    message = R.string.good_evening,
                    image = R.drawable.noon,
                    emoji = "\uD83D\uDE0A"
                )
            }
            else -> {
                setDynamicImage(
                    message = R.string.good_night,
                    image = R.drawable.night,
                    emoji = "\uD83D\uDE34"
                )
            }
        }
    }

    private fun setDynamicImage(
        message: Int,
        image: Int, emoji: String
    ): Greetings {
        return Greetings(
            message = getString(message),
            color = setColorPalette(image),
            image = image, emoji = emoji
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Main(pageData: PageData) {

    val systemUiController = rememberSystemUiController()
    var palette by remember { mutableStateOf(0) }
    if (pageData.greetings?.color != null) {
        palette = pageData.greetings.color.rgb
    }
    systemUiController.setStatusBarColor(
        color = Color(palette)
    )

    val scroll = rememberScrollState(0)
    val activated = pageData.storage?.isActivated?.collectAsState()

    var paddingEnd by remember { mutableStateOf(16.dp) }
    if (activated?.value == false) paddingEnd = 8.dp

    val linearGradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(palette),
            Color.Transparent,
            Color(palette)
        ),
        start = Offset(Float.POSITIVE_INFINITY, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .paint(
                painter = painterResource(id = pageData.greetings?.image!!),
                contentScale = ContentScale.Crop
            )

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    shape = RoundedCornerShape(
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
        ) {
            Image(
                painterResource(id = R.drawable.mapeera_house),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .blur(
                        15.dp,
                        edgeTreatment = BlurredEdgeTreatment.Rectangle
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(palette),
                                Color(palette).copy(alpha = 0.2f),
                                colorResource(id = R.color.translucent),
                                Color.Transparent

                            )

                        )

                    )
            ) {
                Greeting(greetings = pageData.greetings)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp, end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                        .wrapContentHeight(),
                    backgroundColor = colorResource(id = R.color.white),
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = stringResource(id = R.string.welcome_to),
                            fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp)

                        )
                        Text(
                            text = stringResource(id = R.string.welcome_cente_mobile),
                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp, end = 16.dp)

                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, top = 8.dp)
                                .wrapContentHeight()
                        ) {

                            if (activated?.value == false) {
                                Button(
                                    onClick = { pageData.callbacks?.toSelf() },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White,
                                        backgroundColor = AppBlueColorLight
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .padding(start = 16.dp, end = paddingEnd),
                                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.register),
                                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                                        style = Typography().button
                                    )
                                }
                            }
                            Button(
                                onClick = { pageData.callbacks?.toLogin() },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    backgroundColor = Color(palette)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .padding(start = paddingEnd, end = 16.dp),
                                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
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

                ConstraintLayout(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {

                    val (branch, go, internet) = createRefs()
                    NavigationOptionItem(
                        data = LandingData.landingData[0],
                        callbacks = pageData.callbacks,
                        modifier = Modifier.constrainAs(branch) {
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(go.start, 8.dp)
                            top.linkTo(parent.top, 16.dp)
                            bottom.linkTo(parent.bottom, 16.dp)
                            width = Dimension.fillToConstraints
                        }
                    )

                    NavigationOptionItem(
                        data = LandingData.landingData[1],
                        callbacks = pageData.callbacks,
                        modifier = Modifier.constrainAs(go) {
                            start.linkTo(branch.end, 8.dp)
                            end.linkTo(internet.start, 8.dp)
                            top.linkTo(parent.top, 16.dp)
                            bottom.linkTo(parent.bottom, 16.dp)
                            width = Dimension.fillToConstraints
                        }
                    )

                    NavigationOptionItem(
                        data = LandingData.landingData[2],
                        callbacks = pageData.callbacks,
                        modifier = Modifier.constrainAs(internet) {
                            start.linkTo(go.end, 8.dp)
                            end.linkTo(parent.end, 16.dp)
                            top.linkTo(parent.top, 16.dp)
                            bottom.linkTo(parent.bottom, 16.dp)
                            width = Dimension.fillToConstraints
                        }
                    )

                }


            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BodyCarousel(
                        data = pageData, modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    )

                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Footer(
                        callbacks = pageData.callbacks,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )
                }

            }
        }


    }
}

@ExperimentalPagerApi
@Composable
private fun BodyCarousel(data: PageData?, modifier: Modifier) {
    val viewModel = data?.viewModel!!["static"] as StaticDataViewModel
    val tipData = viewModel.carouselData.collectAsState()
    val pagerState = rememberPagerState()
    if (tipData.value.isNotEmpty()) {
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
                pagerState = pagerState,
                callbacks = data.callbacks
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
private fun CarouselItem(
    data: CarouselTip,
    pagerState: PagerState,
    callbacks: AppCallbacks?
) {


    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (box) = createRefs()
        val guidelineBottom = createGuidelineFromBottom(0.1f)
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(box) {
                    linkTo(parent.start, parent.end, startMargin = 0.dp, endMargin = 0.dp)
                    linkTo(parent.top, parent.bottom, topMargin = 0.dp, bottomMargin = 0.dp)
                    width = Dimension.ratio("16:6")
                    height = Dimension.fillToConstraints
                }
                .padding(start = 14.dp, end = 14.dp, bottom = 7.dp, top = 7.dp),
            elevation = 2.dp,
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


@Preview(showBackground = true)
@Composable
private fun DefaultLandingPreview() {
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

