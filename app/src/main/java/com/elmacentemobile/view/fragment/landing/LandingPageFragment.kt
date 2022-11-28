package com.elmacentemobile.view.fragment.landing


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import androidx.viewbinding.ViewBinding
import com.elmacentemobile.R
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.databinding.FragmentLandingPageBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ScreenHelper
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.drawableToBitmap
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.binding.navigate
import com.elmacentemobile.view.dialog.TipConverter
import com.elmacentemobile.view.dialog.TipItem
import com.elmacentemobile.view.dialog.TipItemConverter
import com.elmacentemobile.view.dialog.TipTypeEnum
import com.elmacentemobile.view.ep.data.LandingPageEnum
import com.elmacentemobile.view.ep.data.LandingPageItem
import com.elmacentemobile.view.model.AuthViewModel
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.SplashViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LandingPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LandingPageFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLandingPageBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val adverts = MutableLiveData<List<CarouselItem>>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        setBinding()
        getAdverts()
        setAdvert()
        setLoading()
        return binding.root.rootView
    }


    private fun setLoading() {
        val sync = baseViewModel.dataSource.sync.asLiveData()
        sync.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.loadingFrame.loading.root.visibility = View.VISIBLE
                binding.loadingFrame.loading.data = it
                AppLogger().appLog("PROGRESS", Gson().toJson(it))
                if (it.work >= 8) {
                    setLoading(false)
                } else setLoading(true)
            }
        }
    }

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }


    private fun getAdverts() {
        compositeDisposable.add(
            widgetViewModel.carousel
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        val itemList = mutableListOf<CarouselItem>()
                        it.forEach { a ->
                            itemList.add(
                                CarouselItem(
                                    imageUrl = a.imageURL,
                                    caption = a.imageInfoURL
                                )
                            )
                        }
                        adverts.value = itemList
                    }
                }, { it.printStackTrace() })
        )
    }

    private fun setTimeOfDay() {
        val calendar: Calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
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

    private fun setDynamicImage(message: Int, image: Int) {
        //setColorPalette(image)
        //if (Constants.Data.TEST) binding.banner.setImageRes(image)
        binding.textView15.text = getString(message)
    }


    private fun setColorPalette(drawable: Int) {

        val bitmap = ContextCompat.getDrawable(requireContext(), drawable)
            ?.let { drawableToBitmap(it) }

        val p = bitmap?.let { Palette.from(it).generate() }!!

        AppLogger.instance.appLog("COLOR", p.lightMutedSwatch.toString())
        val vibrant: Swatch? = p.darkVibrantSwatch


        if (vibrant != null) {
            ScreenHelper.fullScreen(requireActivity(), false, false, vibrant.rgb)
//            when (val background: Drawable = binding.textView15.background) {
//                is ShapeDrawable -> {
//                    background.paint.color = vibrant.rgb
//                }
//                is GradientDrawable -> {
//                    background.setColor(vibrant.rgb)
//
//                }
//                is ColorDrawable -> {
//                    background.color = vibrant.rgb
//                }
//            }
        }

    }

    private suspend fun doWorld() {
        delay(1000L)
        println("World!")
    }

    private fun setAdvert() {
        val carouselList = MutableLiveData(mutableListOf<CarouselItem>())
        val carousel = authViewModel.storage.carouselData.asLiveData()
        val tips = authViewModel.storage.dayTipData.asLiveData()
        runBlocking {
            tips.observe(viewLifecycleOwner) { tip ->
                if (!tip.isNullOrEmpty()) {
                    tip.forEach { dayTipData ->
                        carouselList.value?.add(
                            CarouselItem(
                                imageUrl = dayTipData.bannerImage,
                                caption = TipItemConverter().from(
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
            delay(200)
            carousel.observe(viewLifecycleOwner) { cal ->
                if (!cal.isNullOrEmpty()) {
                    cal.forEach { carouselData ->
                        carouselList.value?.add(
                            CarouselItem(
                                imageUrl = carouselData.imageURL,
                                caption = TipItemConverter().from(
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
            delay(200)
            carouselList.observe(viewLifecycleOwner) {
                binding.carousel.setData(it)
            }

        }

        binding.carousel.carouselListener = object : CarouselListener {
            override fun onCreateViewHolder(
                layoutInflater: LayoutInflater,
                parent: ViewGroup
            ): ViewBinding? {
                return null
            }

            override fun onBindViewHolder(
                binding: ViewBinding,
                item: CarouselItem, position: Int
            ) {
            }

            override fun onClick(position: Int, carouselItem: CarouselItem) {
                val action = TipItemConverter().to(carouselItem.caption)
                AppLogger.instance.appLog("CAROUSEL", Gson().toJson(action?.tip))
                if (action?.type == TipTypeEnum.Url) {
                    if (carouselItem.caption != null) openUrl(action.tip as String)
                } else navigate(
                    widgetViewModel.navigation()
                        .navigateToTips(TipConverter().to(action?.tip as String))
                )
            }

            override fun onLongClick(position: Int, carouselItem: CarouselItem) {}
        }
    }


    override fun openUrl(url: String?) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            ShowToast(requireContext(), getString(R.string.nothing_show))
        }
    }

    override fun setBinding() {
        binding.callback = this
        binding.lifecycleOwner = viewLifecycleOwner
        val active = widgetViewModel.storageDataSource.isActivated.asLiveData()
        active.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.registration.visibility = View.GONE
            }
        }
    }


    override fun onStart() {
        super.onStart()
        setTimeOfDay()
    }

    override fun onResume() {
        super.onResume()
        setTimeOfDay()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LandingPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LandingPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onLanding(data: LandingPageItem?) {
        when (data?.enum) {
            LandingPageEnum.LOGIN -> setNavigation(
                splashViewModel.navigation
            )
            LandingPageEnum.REGISTRATION -> setNavigation(
                authViewModel.navigationDataSource
                    .navigateSelfReg()
            )
            LandingPageEnum.BRANCH -> setNavigation(
                authViewModel.navigationDataSource
                    .navigateMap()
            )

            LandingPageEnum.ONLINE_BANKING ->
                openUrl("https://centeonlinebanking.centenarybank.co.ug/iProfits2Prod/")

            LandingPageEnum.ON_THE_GO -> setNavigation(
                authViewModel.navigationDataSource
                    .navigateOnTheGo()
            )
            else -> {
                throw Exception("No direction implemented")
            }
        }
    }


    private fun setNavigation(direction: NavDirections?) {
        (requireActivity() as MainActivity).provideNavigationGraph().navigate(direction!!)
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