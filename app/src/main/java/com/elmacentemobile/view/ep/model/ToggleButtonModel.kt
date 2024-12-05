package com.elmacentemobile.view.ep.model

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.runtime.mutableStateListOf
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.databinding.BlockToggleButtonLayoutBinding
import com.elmacentemobile.databinding.BlockToggleGroupContainerBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.setChildren
import com.elmacentemobile.view.ep.controller.LinkedVault
import com.elmacentemobile.view.ep.data.GroupForm
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

@EpoxyModelClass
open class ToggleButtonModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_toggle_group_container

    override fun setDataBindingVariables(binding: ViewDataBinding?) {

        (binding as? BlockToggleGroupContainerBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockToggleGroupContainerBinding) {

        val group = binding.toggle
        group.removeAllViews()


        data.children.forEachIndexed { index, formControl ->
            val inflater = LayoutInflater.from(group.context)
            val buttonBinding = BlockToggleButtonLayoutBinding
                .inflate(inflater, null, false)
            buttonBinding.root.id = index
            group.addView(buttonBinding.root)
            buttonBinding.data = formControl
            buttonBinding.callback = callbacks

            val button = buttonBinding.root as MaterialButton

            button.setOnClickListener {
                callbacks.clearInputData()
                setLinked(formControl, binding.childContainer)
            }
            if (formControl == data.children.first()) {
                group.check(button.id)
                setLinked(formControl, binding.childContainer)
            }
        }

    }

    private fun setLinked(formControl: FormControl, child: EpoxyRecyclerView) {
        val list = mutableStateListOf<FormControl>()

        val linked = data.mainData.forms.form?.filter {
            it.linkedToControl == formControl.controlID
        }
        linked?.let { list.addAll(it) }

        child.setChildren(
            callbacks = callbacks,
            dynamic = GroupForm(
                module = data.mainData.forms.module,
                form = list,
                aux = data.mainData.forms.form
            ),
            storage = data.mainData.storage
        )

    }

}

fun TypedEpoxyController<*>.toggleLayout(
    vault: LinkedVault,
    appCallbacks: AppCallbacks
) {
    toggleButton {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}

class MaterialButtonToggleGroupWithRadius
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : MaterialButtonToggleGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val button = getChildAt(i) as MaterialButton
            if (button.visibility == GONE) {
                continue
            }
            val builder = button.shapeAppearanceModel.toBuilder()
            val radius = resources.getDimension(R.dimen.dimens_8dp)
            button.shapeAppearanceModel = builder
                .setTopLeftCornerSize(radius)
                .setBottomLeftCornerSize(radius)
                .setTopRightCornerSize(radius)
                .setBottomRightCornerSize(radius).build()
        }
    }

}