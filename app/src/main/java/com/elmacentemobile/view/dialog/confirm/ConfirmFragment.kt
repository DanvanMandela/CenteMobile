package com.elmacentemobile.view.dialog.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.elmacentemobile.R
import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.databinding.BlockDisplayItemLayoutBinding
import com.elmacentemobile.databinding.FragmentConfirmBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.callbacks.Confirm
import com.elmacentemobile.view.ep.data.DisplayContent
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ConfirmFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentConfirmBinding

    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()

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
        binding = FragmentConfirmBinding.inflate(inflater, container, false)
        setViewModel()
        setOnClick()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.confirm.setOnClickListener {
            dialog?.dismiss()
            confirm?.onPay(
                json = json,
                inputList = data,
                action = action,
                module = module,
                encrypted = encrypted,
                formControl = form
            )
        }
        binding.cancel.setOnClickListener { dialog?.dismiss() }
    }

    override fun setViewModel() {
        subscribe.add(
            widgetViewModel.getFormControlNoSq(ControlTypeEnum.CONFORM.type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        setLayout(it)
                    }
                }, { it.printStackTrace() })
        )
    }

    private fun setLayout(it: List<FormControl>) {
        it.toMutableList()
        it.reversed()
        for (t in it) {
            for (e in data) {
                if (t.controlID == e.key) {
                    val display = BlockDisplayItemLayoutBinding.inflate(layoutInflater)
                    val m = DisplayContent(key = t.controlText!!, value = e.value!!)
                    display.data = m
                    binding.container.addView(display.root)
                }
            }
        }
    }

    companion object {
        private val TAG = ConfirmFragment::class.simpleName
        private var data = mutableListOf<InputData>()
        private var confirm: Confirm? = null
        private var json: JSONObject? = null
        private var encrypted: JSONObject? = null
        private var module: Modules? = null
        private var action: ActionControls? = null
        private var form: FormControl? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfirmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun showDialog(
            manager: FragmentManager,
            json: JSONObject?,
            encrypted: JSONObject?,
            inputList: MutableList<InputData>,
            module: Modules?,
            action: ActionControls?,
            formControl: FormControl?,
            confirm: Confirm
        ) =
            ConfirmFragment().apply {
                this@Companion.data = inputList
                this@Companion.confirm = confirm
                this@Companion.encrypted = encrypted
                this@Companion.json = json
                this@Companion.module = module
                this@Companion.action = action
                this@Companion.form = formControl
                show(manager, TAG)
            }
    }

    override fun getTheme(): Int {
        return R.style.ThemeOverlay_Demo_BottomSheetDialog
    }

}