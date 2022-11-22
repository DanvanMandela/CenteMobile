package com.elmacentemobile.view.composable.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.elmacentemobile.R
import com.elmacentemobile.data.model.CarouselData
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.drawableToBitmap
import com.elmacentemobile.view.binding.navigate
import com.elmacentemobile.view.composable.disabledVerticalPointerInputScroll
import com.elmacentemobile.view.ep.data.LandingData
import com.elmacentemobile.view.ep.data.LandingPageEnum
import com.elmacentemobile.view.ep.data.LandingPageItem
import com.elmacentemobile.view.model.AuthViewModel
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.SplashViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.accompanist.pager.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.util.*

@AndroidEntryPoint
class LandingPageCompose : Fragment(), AppCallbacks {
    private val authViewModel: AuthViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val adverts = MutableLiveData<List<CarouselData>>()
    private val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAdverts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Parent(
                    PageData(
                        storage = authViewModel.storage,
                        callbacks = this@LandingPageCompose,
                        greetings = setTimeOfDay()
                    )
                )
            }
        }
    }

    private fun getAdverts() {
        compositeDisposable.add(
            widgetViewModel.carousel
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        adverts.value = it
                    }
                }, { it.printStackTrace() })
        )
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
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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


}


@Composable
fun Parent(data: PageData?) {
    val scroll = rememberScrollState()
    var palette by remember { mutableStateOf(Color.White) }
    if (data?.greetings?.color != null) {
        palette = Color(data.greetings.color.rgb)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        palette
                    )
                )
            )
    ) {
        Header(data = data)
        Body(data = data)
    }

}


@Composable
fun Greeting(greetings: Greetings?) {
    var palette by remember { mutableStateOf(Color.White) }
    if (greetings?.color != null) {
        palette = Color(greetings.color.bodyTextColor)
    }

    Text(
        text = greetings?.message!!,
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
fun Header(data: PageData?) {

    val systemUiController = rememberSystemUiController()
    var palette by remember { mutableStateOf(0) }
    if (data?.greetings?.color != null) {
        palette = data.greetings.color.rgb
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
                    painterResource(id = data?.greetings?.image!!),
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
            Greeting(greetings = data.greetings)
            LoginOption(data = data)
            NavigationOption(
                listData = LandingData.landingData,
                callbacks = data.callbacks
            )

        }
    }
}

@Composable
fun LoginOption(data: PageData?) {

    val callbacks = data?.callbacks
    val storage = data?.storage

    var palette by remember { mutableStateOf(Color(R.color.app_blue_light)) }
    var textColor by remember { mutableStateOf(Color(R.color.white)) }
    if (data?.greetings?.color != null) {
        palette = Color(data.greetings.color.rgb)
        textColor= Color(data.greetings.color.bodyTextColor)
    }

    val activated = storage?.isActivated?.collectAsState()
    var paddingEnd by remember { mutableStateOf(16.dp) }
    if (activated?.value == false) paddingEnd = 8.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
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
                style = Typography().body2,
                color =textColor,
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
                        contentColor = colorResource(id = R.color.white),
                        backgroundColor = palette
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
fun NavigationOption(
    listData: MutableList<LandingPageItem>,
    callbacks: AppCallbacks?
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .disabledVerticalPointerInputScroll()
    ) {
        items(listData) { data ->
            NavigationOptionItem(data = data, callbacks = callbacks)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationOptionItem(data: LandingPageItem, callbacks: AppCallbacks?) {
    Card(
        modifier = Modifier
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
                    .weight(2f)
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
                    .weight(1f)
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


@ExperimentalPagerApi
@Composable
fun BodyCarousel(data: PageData?, modifier: Modifier) {
    val pagerState = rememberPagerState()
    val storage = data?.storage
    val adverts = storage?.carouselData?.collectAsState()?.value


    if (!adverts.isNullOrEmpty()) {
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
            count = adverts.size,
            state = pagerState,
            modifier = modifier
        ) { page ->
            CarouselItem(
                data = adverts[page],
                pagerState = pagerState,
                callbacks = data.callbacks
            )
        }
    }
}

data class PageData(
    val storage: StorageDataSource?,
    val callbacks: AppCallbacks?,
    val greetings: Greetings?
)

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun CarouselItem(
    data: CarouselData,
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
                    width = Dimension.ratio("16:7")
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp),
            elevation = 1.dp,
            onClick = {
                callbacks?.openUrl(data.imageInfoURL)
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(data.imageURL),
                    contentDescription = data.category,
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
                width = Dimension.ratio("16:7")
                height = Dimension.fillToConstraints
            }) {

            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .padding(16.dp),
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
fun Body(data: PageData?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),

            ) {
            BodyCarousel(
                data = data, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Footer(
                callbacks = data?.callbacks, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )

        }

    }
}


data class Greetings(
    val message: String?,
    val color: Palette.Swatch?,
    @DrawableRes val image: Int?
)


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Parent(
        PageData(
            storage = null, callbacks = null, greetings = Greetings(
                message = "Good\nMorning", color = null, image = R.drawable.noon
            )
        )
    )
}

