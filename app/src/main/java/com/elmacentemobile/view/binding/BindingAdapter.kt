package com.elmacentemobile.view.binding

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.MaskFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.MaskFilterSpan
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.paris.utils.getFont
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.elmacentemobile.R
import com.elmacentemobile.data.model.DeviceData
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.user.Accounts
import com.elmacentemobile.data.model.user.ActivationData
import com.elmacentemobile.data.model.user.AlertServices
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.sync.SyncData
import com.elmacentemobile.databinding.BlockCardReaderLayoutBinding
import com.elmacentemobile.databinding.BlockRadioButtonLayoutBinding
import com.elmacentemobile.databinding.DotLayoutBinding
import com.elmacentemobile.databinding.RectangleILayoutBinding
import com.elmacentemobile.util.*
import com.elmacentemobile.util.BaseClass.nonCaps
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.adapter.AccountAdapterItem
import com.elmacentemobile.view.ep.controller.*
import com.elmacentemobile.view.ep.data.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.*
import java.time.LocalDate
import java.util.*


@BindingAdapter("callback", "controller", "setIndicator")
fun EpoxyRecyclerView.setHeader(callbacks: AppCallbacks, data: AccountData, @IdRes indicator: Int) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    val controller = HeaderController(callbacks)
    controller.setData(data)
    this.setController(controller)


    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val offset: Int = this@setHeader.computeHorizontalScrollOffset()
            if (offset % this@setHeader.width == 0) {
                val position: Int = offset / this@setHeader.width
                setIndicator(position, this@setHeader, controller, indicator)
            }
        }
    })

}

fun setIndicator(
    state: Int,
    parent: EpoxyRecyclerView,
    controller: HeaderController,
    indicator: Int
) {
    var dot: ViewDataBinding? = null
    val child: LinearLayout? = parent.rootView.findViewById(indicator)
    dot?.lifecycleOwner = parent.findViewTreeLifecycleOwner()
    child?.removeAllViews()
    if (child != null) {
        for (s in 1..controller.adapter.itemCount) {
            dot = when (s) {
                state.plus(1) ->
                    RectangleILayoutBinding.inflate(
                        LayoutInflater.from(parent.context), child, false
                    )
                else -> DotLayoutBinding.inflate(LayoutInflater.from(parent.context), child, false)
            }
            child.addView(dot.root)
        }
    }

}


@BindingAdapter("textSet")
fun TextView.textSet(text: String?) {
    if (text != null) {
        this.text = text
    }
}

@BindingAdapter("textStrRes")
fun TextView.textStrRes(@StringRes text: Int?) {
    if (text != null) {
        this.text = this.context.getString(text)
    }
}


@BindingAdapter("callback")
fun MaterialToolbar.setToolbar(callbacks: AppCallbacks) {
    val title = this.getChildAt(0) as TextView
    val subTitle = this.getChildAt(1) as TextView
    title.typeface = this.context.getFont(R.font.poppins_semi_bold)
    subTitle.typeface = this.context.getFont(R.font.poppins_medium)

    this.setOnMenuItemClickListener { item ->
        when (item?.itemId) {
            R.id.actionLogout -> {
                callbacks.logOut()
                true
            }
            R.id.actionNotification -> {
                callbacks.navigateToNotification()
                true
            }
            else -> false
        }
    }
}

@BindingAdapter("imageUri")
fun ImageView.setImageUri(imageURL: String?) {
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.photos)
        .error(R.drawable.warning)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)
    Glide.with(this)
        .load(imageURL)
        .apply(options)
        .listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}

@BindingAdapter("image")
fun ImageView.setImage(imageURL: String?) {
    if (imageURL != null) {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.photos)
            .error(R.drawable.warning)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
        Glide.with(this)
            .load(imageURL)
            .apply(options)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(this)
    }
}

@BindingAdapter("callback", "module")
fun EpoxyRecyclerView.setModule(callbacks: AppCallbacks, module: BodyData?) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    this.layoutManager = GridLayoutManager(this.context, 3)
    val controller = ModuleController(callbacks)
    controller.setData(ModuleData(module?.module, true))
    this.setController(controller)
}

@BindingAdapter("callback", "frequent")
fun EpoxyRecyclerView.setFrequent(callbacks: AppCallbacks, frequent: BodyData?) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    if (frequent != null)
        if (frequent.frequentList.isNotEmpty())
            this.layoutManager = GridLayoutManager(this.context, 4)
    val controller = FrequentController(callbacks)
    controller.setData(frequent?.frequentList)
    this.setController(controller)
}

@BindingAdapter("imageRes")
fun ImageView.setImageRes(@DrawableRes res: Int?) {
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.photos)
        .error(R.drawable.warning)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)
    Glide.with(this)
        .load(res)
        .apply(options)
        .listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}


@BindingAdapter("accountNumber")
fun TextView.setAccountNumber(text: String?) {
    if (text != null) {
        this.text = BaseClass.maskCardNumber(text)
        this.setOnClickListener {
            this.text = text
            Handler(Looper.getMainLooper()).postDelayed({
                this.text = BaseClass.maskCardNumber(text)
            }, 3500)
        }
    }
}

@BindingAdapter("amountSet")
fun TextView.setAmount(amount: String?) {
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("UGX")
    if (amount != null) {
        val s = amount.replace(",", "")
        s.replace(".00", "")
        this.text = s
    }
}

@BindingAdapter("removeNegativeSet")
fun TextView.setRemoveNegAmount(amount: String?) {
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("UGX")
    if (amount != null) {
        this.text = amount.replace("-", "")
    }
}

@BindingAdapter("setArrow")
fun ImageView.setArrow(amount: String?) {
    if (amount != null) {
        val newAmount = NumberTextWatcherForThousand.trimCommaOfString(amount)
        if (amount.contains("-")) {
            this.setImageRes(R.drawable.left_arrow_red)
        } else this.setImageRes(R.drawable.left_arrow_green)
    }
}

@BindingAdapter("amountSet", "type")
fun TextView.setAmountColor(amount: String?, type: String?) {
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("UGX")
    if (amount != null) {
        this.text = amount
        if (nonCaps(type) == nonCaps("C")) this.setTextColor(
            ContextCompat.getColor(
                this.context,
                R.color.green_indicator
            )
        ) else this.setTextColor(
            ContextCompat.getColor(
                this.context,
                R.color.red_app
            )
        )
    }
}

@BindingAdapter("account", "callbacks")
fun LinearLayout.setBalance(account: Accounts?, callbacks: AppCallbacks) {
    var isVisible = false
    val blurMask: MaskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
    val balanceText = findViewById<TextView>(R.id.balance)
    val info = findViewById<TextView>(R.id.info)
    val string = SpannableString("XXX,XXX,XXX")
    string.setSpan(
        MaskFilterSpan(blurMask), 0, string.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    balanceText.text = string
    this.setOnClickListener {
        if (!isVisible) {
            callbacks.onBalance(balanceText, account, info, true)
            isVisible = true
        } else {
            callbacks.onBalance(balanceText, account, info, false)
            isVisible = false
            balanceText.text = string
        }
    }

}


@BindingAdapter("username")
fun TextView.setUsername(data: ActivationData?) {
    if (data != null) {
        if (data.firstName != null) {
            val name =
                "${this.context.getString(R.string.hello_jane)} ${data.firstName}"
            this.text = name
        } else this.text = this.context.getString(R.string.hello_there)
    } else {
        this.text = this.context.getString(R.string.hello_there)
    }
}

@BindingAdapter("messageFromUs")
fun TextView.setMessageFromUs(data: ActivationData?) {
    if (data != null) {
        if (!data.message.isNullOrEmpty()) {
            this.text = data.message
        } else this.text = this.context.getString(R.string.welcome_back)
    } else {
        this.text = this.context.getString(R.string.welcome_back)
    }
}

@BindingAdapter("title")
fun MaterialToolbar.setTitle(data: ActivationData?) {
    if (data != null) {
        if (data.firstName != null) {
            val name =
                "${this.context.getString(R.string.hello_jane)} ${data.firstName}"
            this.title = name
        } else this.title = this.context.getString(R.string.hello_there)
    } else {
        this.title = this.context.getString(R.string.hello_there)
    }
}

@BindingAdapter("subTitle")
fun MaterialToolbar.setSTitle(data: ActivationData?) {
    if (data != null) {
        if (data.loginDate != null) {
            this.subtitle = "Last Login ${data.loginDate}"
        }
    }
}

@BindingAdapter("modules", "callback")
fun MaterialToolbar.setDynamicToolbar(dynamic: DynamicData?, callbacks: AppCallbacks) {
    this.navigationIcon?.setTint(ContextCompat.getColor(this.context, R.color.white))
    this.setTitleTextColor(ContextCompat.getColor(this.context, R.color.white))
    this.setNavigationOnClickListener { callbacks.navigateUp() }
    if (dynamic != null) {
        when (dynamic) {
            is GroupModule -> this.title = dynamic.parent.moduleName
            is GroupForm -> this.title = dynamic.module.moduleName
        }
    }
    this.setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.actionRecent -> {
                callbacks.onMenuItem()
                true
            }
            else -> false
        }
    }
}


@BindingAdapter("callback", "dynamic", "storage")
fun EpoxyRecyclerView.setDynamic(
    callbacks: AppCallbacks,
    dynamic: DynamicData?,
    storage: StorageDataSource?
) {
    var layout: RecyclerView.LayoutManager? = null
    var controller: EpoxyController? = null
    if (dynamic != null) {
        when (dynamic) {
            is GroupModule -> {
                this.animation =
                    AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
                layout = GridLayoutManager(this.context, 3)
                controller = ModuleController(callbacks)
                controller.setData(ModuleData(dynamic.module, false))
            }
            is GroupForm -> {
                layout = LinearLayoutManager(this.context)
                controller = NewFormController(callbacks, storage!!)
                controller.setData(FormData(forms = dynamic, storage = storage))
            }
        }
        this.layoutManager = layout
        if (controller != null) {
            this.setController(controller)
        }
    }
}

@BindingAdapter("callback", "form", "module")
fun MaterialButton.setDynamicButton(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules
) {
    if (formControl != null) {
        this.setTextColor(ContextCompat.getColor(this.context, R.color.white))
        this.text = formControl.controlText

        this.setOnClickListener {
            callbacks.onForm(formControl, modules)
        }
    }

}


@BindingAdapter("callback", "form")
fun RadioGroup.setRadioButton(callbacks: AppCallbacks?, form: GroupForm?) {
    if (form != null) {
        this.tag = "mainGroup"
        val data =
            form.form?.filter { a -> nonCaps(a.controlType) == nonCaps(ControlTypeEnum.R_BUTTON.type) }
        this.removeAllViews()

        for (d in 0..data!!.size.minus(1)) {
            val inflater = LayoutInflater.from(this.context)
            val binding = BlockRadioButtonLayoutBinding.inflate(inflater, null, false)
            binding.root.id = d.plus(1)
            this.addView(binding.root)
            binding.callback = callbacks
            binding.data = data[d]

        }
    }
}

@BindingAdapter("setTag")
fun View.setID(tag: String?) {
    if (tag != null) this.tag = tag
}

@BindingAdapter("callback", "data")
fun RadioButton.setButton(callbacks: AppCallbacks, data: FormControl?) {
    if (data?.isChecked != null) {
        this.isChecked = data.isChecked!!
    }
    this.setOnCheckedChangeListener { _, p1 ->
        if (p1) {
            callbacks.userInput(
                InputData(
                    name = data?.controlText,
                    key = data?.serviceParamID,
                    value = if (this.isChecked) {
                        "TRUE"
                    } else "FALSE",
                    encrypted = data?.isEncrypted!!,
                    mandatory = data.isMandatory
                )
            )
        }
    }
}

@BindingAdapter("name", "ap")
fun TextView.setAp(data: String?, app: String) {
    if (data != null) {
        val name = "$app : $data"
        this.text = name
    }
}

@BindingAdapter("dateM")
fun TextView.setDay(data: String?) {
    val c = Calendar.getInstance()
    if (data != null) {
        val date: Date? = SimpleDateFormat("MMM d yyyy h:mma", Locale.getDefault()).parse(data)
        c.time = date!!
        val day = c[Calendar.DAY_OF_MONTH]
        this.text = day.toString()
    }
}

@BindingAdapter("month")
fun TextView.setMon(data: String?) {
    val c = Calendar.getInstance()
    if (data != null) {
        val date: Date? = SimpleDateFormat("MMM d yyyy h:mma", Locale.getDefault()).parse(data)
        c.time = date!!
        val day = c[Calendar.DAY_OF_MONTH]
        this.text = day.toString()

        val month = c[Calendar.MONTH]
        val dfs = DateFormatSymbols()
        val months: Array<String> = dfs.months
        if (month in 0..11) {
            this.text = months[month]
        }
    }
}


@BindingAdapter("callback", "controller")
fun EpoxyRecyclerView.setLandingPage(callbacks: AppCallbacks, data: GroupLanding) {


    this.layoutManager = GridLayoutManager(this.context, 3)
    val controller = LandingPageController(callbacks)
    controller.setData(data)
    this.setController(controller)

}


@BindingAdapter("callback", "form", "storage")
fun EpoxyRecyclerView.setForm(
    callbacks: AppCallbacks,
    dynamic: DynamicData?,
    storage: StorageDataSource?
) {
    var layout: RecyclerView.LayoutManager? = null
    var controller: EpoxyController? = null
    if (storage != null)
        if (dynamic != null) {
            when (dynamic) {
                is GroupForm -> {
                    layout = LinearLayoutManager(this.context)
                    controller = FormController(callbacks, storage)
                    controller.setData(FormData(forms = dynamic, storage = storage))
                }
            }
            this.layoutManager = layout
            if (controller != null) {
                this.setController(controller)
            }
        }
}

@BindingAdapter("callback", "display")
fun EpoxyRecyclerView.setDisplayController(callbacks: AppCallbacks?, data: DisplayVaultData?) {
    val controller = callbacks?.let { DisplayController(it) }
    controller?.setData(data)
    controller?.let { this.setController(it) }
}

@BindingAdapter("callback", "data", "modules")
fun CheckBox.setCheckBox(callbacks: AppCallbacks, data: FormControl?, modules: Modules?) {
    this.isChecked = false
    this.setOnCheckedChangeListener { _, p1 ->
        if (p1)
            callbacks.userInput(
                InputData(
                    name = data?.controlText,
                    key = data?.serviceParamID,
                    value = "TRUE",
                    encrypted = data?.isEncrypted!!,
                    mandatory = data.isMandatory
                )
            )
    }
}


@BindingAdapter("callback", "form", "module")
fun View.setRecentList(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules
) {
    if (formControl != null) {
        callbacks.onRecent(formControl)
    }
}

@BindingAdapter("callback", "list", "module")
fun EpoxyRecyclerView.setList(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules
) {
    if (formControl != null) {
        callbacks.onList(formControl, this, modules)
    }
}

@BindingAdapter("callback", "list_label", "module")
fun EpoxyRecyclerView.setLabelList(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules
) {
    if (formControl != null) {
        callbacks.onLabelList(this, formControl, modules)
    }
}


@BindingAdapter("callback", "display", "module")
fun TextView.setDisplay(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules?
) {
    if (formControl != null) {
        callbacks.onDisplay(formControl, modules)
    }
}

@BindingAdapter("callback", "json", "module")
fun EpoxyRecyclerView.setDisplayJson(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules?
) {
    callbacks.onDisplay(formControl, modules, this)
}


@BindingAdapter("callback", "accounts")
fun ViewPager2.setRecentList(
    callbacks: AppCallbacks?,
    accounts: AccountData?
) {
    setupCarousel(this, context)
    if (accounts != null) {
        val adapter = AccountAdapterItem(accounts.account, callbacks!!)
        this.adapter = adapter
        callbacks.currentAccount(adapter.currentItem(0))
        callbacks.setOnIndicator(this)
        this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                callbacks.currentAccount(adapter.currentItem(position))
            }
        })
    }
}

private fun setupCarousel(viewPager: ViewPager2, context: Context) {

    val nextItemVisiblePx = context.resources.getDimension(R.dimen.viewpager_next_item_visible)
    val currentItemHorizontalMarginPx =
        context.resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
        page.translationX = -pageTranslationX * position
        page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
        page.alpha = 0.25f + (1 - kotlin.math.abs(position))
    }
    viewPager.setPageTransformer(pageTransformer)

    val itemDecoration = HorizontalMarginItemDecoration(
        context,
        R.dimen.viewpager_current_item_horizontal_margin
    )
    viewPager.addItemDecoration(itemDecoration)
}

@BindingAdapter("callback", "alerts")
fun EpoxyRecyclerView.serviceAlerts(callbacks: AppCallbacks?, data: AppData?) {
    val controller = callbacks?.let { AlertServiceController(it) }
    controller?.setData(data)
    controller?.let { this.setController(it) }
}

@BindingAdapter("callback", "data")
fun ImageButton.imageSelect(callbacks: AppCallbacks?, data: FormControl?) {
    this.setImageDrawable(null)
    setDefaultValue(data, callbacks)
    this.setOnClickListener {
        callbacks?.onImageSelect(this, data)
    }
}


@RequiresApi(Build.VERSION_CODES.O) //TODO CHECK API LEVEL O
@BindingAdapter("period")
fun CircularProgressIndicator.setPeriod(data: AlertServices?) {
    val editTime = data!!.dueDate.split("T")
    val date = LocalDate.parse(editTime[0])
    val maxDay = date.lengthOfMonth()
    this.max = maxDay
    val progress = data.days.toInt()
    this.progress = progress

    setIndicatorColor(this, maxDay)
}

fun setIndicatorColor(indicator: CircularProgressIndicator, maxDay: Int) {
    val half = maxDay.div(2)
    val third = maxDay.div(3)
    val forth = maxDay.div(4)
    val current = indicator.progress



    if (current >= half) {
        val color = R.color.green_indicator
        val lightColor =
            ColorUtil().lightenColor(ContextCompat.getColor(indicator.context, color), 0.35f)
        indicator.trackColor = ColorDrawable(lightColor).color

        indicator.setIndicatorColor(
            ContextCompat.getColor(
                indicator.context,
                color
            )
        )


    } else if (current in third until half) {
        val color = R.color.yellow_indicator
        val lightColor =
            ColorUtil().lightenColor(ContextCompat.getColor(indicator.context, color), 0.35f)
        indicator.trackColor = ColorDrawable(lightColor).color

        indicator.setIndicatorColor(
            ContextCompat.getColor(
                indicator.context,
                color
            )
        )

    } else if (current <= forth) {
        val color = R.color.red_app
        val lightColor =
            ColorUtil().lightenColor(ContextCompat.getColor(indicator.context, color), 0.35f)
        indicator.trackColor = ColorDrawable(lightColor).color

        indicator.setIndicatorColor(
            ContextCompat.getColor(
                indicator.context,
                color
            )
        )
    }
}

@BindingAdapter("active")
fun MaterialCardView.setActiveGrid(data: LandingPageItem?) {
    if (data!!.visible != null) {
        if (!data.visible!!) this.visibility = View.GONE
    }
}

@BindingAdapter("form", "module", "callback")
fun FrameLayout.listOption(
    data: FormControl?,
    modules: Modules?,
    callbacks: AppCallbacks
) {
    callbacks.onListOption(data, modules)
}

@BindingAdapter("stand", "module", "callback", "storage")
fun EpoxyRecyclerView.standingOrder(
    form: FormControl?,
    modules: Modules?,
    callbacks: AppCallbacks,
    storage: StorageDataSource?
) {

    callbacks.listDataServer(this, form, modules)
}


@BindingAdapter("preview", "module", "callback")
fun LinearLayout.setPreviewWindow(
    formControl: FormControl?,
    modules: Modules,
    callbacks: AppCallbacks
) {
    val binding = BlockCardReaderLayoutBinding.inflate(LayoutInflater.from(this.context))
    this.removeAllViews()
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )
    binding.parent.layoutParams = layoutParams
    this.addView(binding.root)
    callbacks.onQRCode(binding, formControl, modules)
}


@BindingAdapter("label", "callback", "data")
fun TextView.setLabel(
    formControl: FormControl?,
    callbacks: AppCallbacks?,
    data: String?
) {

    this.text = formControl?.controlText
    if (data != null) {
        this.text = data
    }


    if (formControl != null)
        if (formControl.controlValue != null)
            if (!TextUtils.isEmpty(formControl.controlValue)) {
                callbacks?.userInput(
                    InputData(
                        name = formControl.controlText,
                        key = formControl.serviceParamID,
                        value = formControl.controlValue,
                        encrypted = formControl.isEncrypted,
                        mandatory = formControl.isMandatory
                    )
                )
                this.text = formControl.controlValue
            }
}


@BindingAdapter("labelText", "callback", "data")
fun TextView.setLabelText(
    formControl: FormControl?,
    callbacks: AppCallbacks?,
    data: String?
) {

    this.text = formControl?.controlText
    if (data != null) {
        this.text = data
    }
    if (formControl != null)
        if (formControl.controlValue != null)
            if (!TextUtils.isEmpty(formControl.controlValue)) {
                callbacks?.userInput(
                    InputData(
                        name = formControl.controlText,
                        key = formControl.serviceParamID,
                        value = formControl.controlValue,
                        encrypted = formControl.isEncrypted,
                        mandatory = formControl.isMandatory
                    )
                )
            }
    callbacks?.onServerValue(formControl, this)
}


fun Fragment.navigate(directions: NavDirections) {
    lifecycleScope.launchWhenResumed {
        NavHostFragment.findNavController(this@navigate).navigate(directions)
    }
}

@SuppressLint("WeekBasedYear", "NewApi")
fun calendarToDate(calendar: Calendar?): String? {
    val datePattern = "dd MMM YYYY"
    if (calendar == null) {
        return null
    }
    val df: DateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
    val timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    df.timeZone = timeZone
    val d: Date = calendar.time
    return df.format(d)
}

fun View.findViewTreeLifecycleOwner(): LifecycleOwner? = ViewTreeLifecycleOwner.get(this)


fun Activity.hideSoftKeyboard(view: View) {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(view.windowToken, 0)
}

@BindingAdapter("setDateTime")
fun TextView.setDateTime(date: Date?) {
    if (date != null) {
        val dateFormatter: Format = SimpleDateFormat(
            android.text.format.DateFormat.getBestDateTimePattern(
                Locale.getDefault(),
                "dMMyyjjmmss"
            ),
            Locale.getDefault()
        )
        this.text = dateFormatter.format(date)

    }
}


fun otpLive(optState: StateFlow<String>): LiveData<String> {
    return optState.asLiveData()
}

fun syncLive(sync: StateFlow<SyncData>): LiveData<SyncData> {
    return sync.asLiveData()
}

fun deviceLive(optState: StateFlow<DeviceData>): LiveData<DeviceData> {
    return optState.asLiveData()
}


fun Context.isOnline(): Boolean {
    return try {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        netInfo != null && netInfo.isConnected
    } catch (e: NullPointerException) {
        e.printStackTrace()
        false
    }
}

fun TextInputEditText.updateText(text: String) {
    val focussed = hasFocus()
    if (focussed) {
        clearFocus()
    }
    setText(text)
    if (focussed) {
        requestFocus()
    }
}

fun Activity.statusColor(color: Int) {
    val window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = this.resources.getColor(color)
}

fun findBinary(binaryName: String): Boolean {
    var found = false
    if (!found) {
        val places = arrayOf(
            "/sbin/", "/system/bin/", "/system/xbin/",
            "/data/local/xbin/", "/data/local/bin/",
            "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"
        )
        for (where in places) {
            if (File(where + binaryName).exists()) {
                found = true
                break
            }
        }
    }
    return found
}


fun parameters(obj: Any): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    for (field in obj.javaClass.declaredFields) {
        field.isAccessible = true
        try {
            map[field.name] = field[obj] as Any
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    return map
}









