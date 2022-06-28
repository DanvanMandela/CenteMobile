package com.craft.silicon.centemobile.view.binding

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
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
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.craft.silicon.centemobile.databinding.BlockRadioButtonLayoutBinding
import com.craft.silicon.centemobile.databinding.DotLayoutBinding
import com.craft.silicon.centemobile.databinding.RectangleILayoutBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.AutoTextArrayAdapter
import com.craft.silicon.centemobile.view.ep.controller.*
import com.craft.silicon.centemobile.view.ep.data.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.util.HashMap


@BindingAdapter("callback", "controller", "setIndicator")
fun EpoxyRecyclerView.setHeader(callbacks: AppCallbacks, data: HeaderData, @IdRes indicator: Int) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    val helper: SnapHelper = LinearSnapHelper()
    helper.attachToRecyclerView(this)
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
fun EpoxyRecyclerView.setModule(callbacks: AppCallbacks, module: BodyData) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    this.layoutManager = GridLayoutManager(this.context, 3)
    val controller = ModuleController(callbacks)
    controller.setData(module.module)
    this.setController(controller)
}

@BindingAdapter("callback", "frequent")
fun EpoxyRecyclerView.setFrequent(callbacks: AppCallbacks, frequent: BodyData) {
    this.animation =
        AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    this.layoutManager = GridLayoutManager(this.context, 4)
    val controller = FrequentController(callbacks)
    controller.setData(frequent.frequentList)
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
        if (data.email != null) {
            this.subtitle = data.email
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
}


@BindingAdapter("callback", "dynamic")
fun EpoxyRecyclerView.setDynamic(callbacks: AppCallbacks, dynamic: DynamicData?) {
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
                controller.setData(dynamic)
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
            Log.e("DANVAN", "DANVANA")
            callbacks.onForm(formControl, modules)
        }
    }

}

@BindingAdapter("callback", "form", "module")
fun TextInputEditText.setInputLayout(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    modules: Modules
) {
    if (formControl != null) {
        this.tag = formControl.controlID
        when (nonCaps(formControl.controlFormat)) {
            nonCaps(ControlFormatEnum.PHONE.type) -> InputType.TYPE_CLASS_PHONE
            nonCaps(ControlFormatEnum.PIN.type) -> InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
    }

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            val hashMap = HashMap<String, String>()
            hashMap[tag.toString()] = e.toString()
            callbacks.inputData(hashMap)
        }
    })


}


@BindingAdapter("callback", "form")
fun RadioGroup.setRadioButton(callbacks: AppCallbacks?, form: GroupForm?) {
    if (form != null) {
        this.tag = "mainGroup"
        val data =
            form.form.filter { a -> nonCaps(a.controlType) == nonCaps(ControlTypeEnum.R_BUTTON.type) }
        this.removeAllViews()
//        for (d in data) {
//
//        }
        for (d in 0..data.size.minus(1)) {
            val inflater = LayoutInflater.from(this.context)
            val binding = BlockRadioButtonLayoutBinding.inflate(inflater, null, false)
            binding.root.id = d.plus(1)
            this.addView(binding.root)
            binding.callback = callbacks
            binding.data = data[d]
            binding.module = form.module
        }
    }
}

@BindingAdapter("setTag")
fun View.setID(tag: String?) {
    if (tag != null) this.tag = tag
}

@BindingAdapter("callback", "data", "modules")
fun RadioButton.setButton(callbacks: AppCallbacks, data: FormControl?, modules: Modules?) {
    if (data?.isChecked != null) {
        this.isChecked = data.isChecked!!
    }
//    this.setOnClickListener {
//        callbacks.onRadioCheck(data)
//    }
    this.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            if (p1) {
                callbacks.onRadioCheck(data)
            }
        }
    })
}


@BindingAdapter("callback", "controller")
fun EpoxyRecyclerView.setLandingPage(callbacks: AppCallbacks, data: GroupLanding) {

//    val layoutManager = FlexboxLayoutManager(context)
//    layoutManager.flexDirection = FlexDirection.COLUMN
//    layoutManager.justifyContent = JustifyContent.FLEX_END

    this.animation = AnimationUtils.loadAnimation(this.context, R.anim.home_anim)
    this.layoutManager = GridLayoutManager(this.context, 3)
    val controller = LandingPageController(callbacks)
    controller.setData(data)
    this.setController(controller)

}

@BindingAdapter("callback", "form")
fun AutoCompleteTextView.setDropDownData(callbacks: AppCallbacks, data: GroupForm?) {
    if (data != null) {
        if (data.data.isNotEmpty()) {
            val staticData =
                data.data.filter { a -> nonCaps(a.id) == nonCaps(this.tag.toString()) }
            val adapter = AutoTextArrayAdapter(context, 1, staticData)
            this.setAdapter(adapter)
            this.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    val hashMap = HashMap<String, String>()
                    hashMap[tag.toString()] = adapter.getItem(p2)!!.subCodeID
                    callbacks.inputData(hashMap)
                }
        }
    }
}











