package com.craft.silicon.centemobile.view.binding

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.*
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
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.craft.silicon.centemobile.data.model.user.AlertServices
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.databinding.BlockRadioButtonLayoutBinding
import com.craft.silicon.centemobile.databinding.DotLayoutBinding
import com.craft.silicon.centemobile.databinding.RectangleILayoutBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.HorizontalMarginItemDecoration
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.AccountAdapterItem
import com.craft.silicon.centemobile.view.ep.controller.*
import com.craft.silicon.centemobile.view.ep.data.*
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("callback", "controller", "setIndicator")
fun EpoxyRecyclerView.setHeader(callbacks: AppCallbacks, data: AccountData, @IdRes indicator: Int) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    val helper: SnapHelper = LinearSnapHelper()
    // helper.attachToRecyclerView(this)
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

@BindingAdapter("callback", "module")
fun EpoxyRecyclerView.setModule(callbacks: AppCallbacks, module: BodyData?) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    this.layoutManager = GridLayoutManager(this.context, 3)
    val controller = ModuleController(callbacks)
    controller.setData(module?.module)
    this.setController(controller)
}

@BindingAdapter("callback", "frequent")
fun EpoxyRecyclerView.setFrequent(callbacks: AppCallbacks, frequent: BodyData?) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
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
            }, 1500)
        }
    }

}

@BindingAdapter("account", "callbacks")
fun TextView.setBalance(account: Accounts?, callbacks: AppCallbacks) {
    if (text != null) {
        this.text = BaseClass.maskCardNumber("####")
        this.setOnClickListener {
            callbacks.onBalance(this, account)
        }
    }

}

@BindingAdapter("username")
fun TextView.setUsername(data: ActivationData?) {
    if (data != null) {
        if (data.firstName != null) {
            val name =
                "${this.context.getString(R.string.hello_jane)} ${data.firstName} ${data.lastName}"
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
                "${this.context.getString(R.string.hello_jane)} ${data.firstName} ${data.lastName}"
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
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    var layout: RecyclerView.LayoutManager? = null
    var controller: EpoxyController? = null
    if (dynamic != null) {
        when (dynamic) {
            is GroupModule -> {
                layout = GridLayoutManager(this.context, 3)
                controller = ModuleController(callbacks)
                controller.setData(dynamic.module)
            }
            is GroupForm -> {
                layout = LinearLayoutManager(this.context)
                controller = FormController(callbacks)
                controller.setData(FormData(forms = dynamic, storage = storage!!))
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

    val layoutManager = FlexboxLayoutManager(context)
    layoutManager.flexDirection = FlexDirection.COLUMN
    layoutManager.justifyContent = JustifyContent.FLEX_END

    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
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
                    controller = FormController(callbacks)
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

@BindingAdapter("callback", "display")
fun TextView.setDisplay(
    callbacks: AppCallbacks,
    formControl: FormControl?
) {
    if (formControl != null) {
        callbacks.onDisplay(formControl)
    }
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

















