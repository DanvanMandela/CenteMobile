package com.elmacentemobile.view.fragment.dynamic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.control.FormNavigation
import com.elmacentemobile.data.model.converter.InsuranceTypeConverter
import com.elmacentemobile.data.model.converter.LoanTypeConverter
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.databinding.FragmentDynamicDialogBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.callbacks.Confirm
import com.elmacentemobile.view.binding.FieldValidationHelper
import com.elmacentemobile.view.binding.parameters
import com.elmacentemobile.view.binding.setDynamic
import com.elmacentemobile.view.ep.adapter.InsuranceAdapterItem
import com.elmacentemobile.view.ep.adapter.LoanAdapterItem
import com.elmacentemobile.view.ep.controller.DisplayData
import com.elmacentemobile.view.ep.controller.HashTypeConverter
import com.elmacentemobile.view.ep.controller.MainDisplayController
import com.elmacentemobile.view.ep.data.BusData
import com.elmacentemobile.view.ep.data.GroupForm
import com.elmacentemobile.view.model.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DynamicDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class DynamicDialogFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var binding: FragmentDynamicDialogBinding
    private val inputList = mutableListOf<InputData>()
    private lateinit var busData: BusData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEvent(busData: BusData?) {
        AppLogger.instance.appLog("BUS", Gson().toJson(busData))
    }

    override fun setController() {
        busData = EventBus.getDefault().getStickyEvent(BusData::class.java)
        setDynamicInputs(busData.inputs)
        binding.lifecycleOwner = this
        AppLogger.instance.appLog("BUS", Gson().toJson(busData))
        binding.dialogContainer.setDynamic(
            callbacks = this,
            dynamic = busData.data,
            storage = baseViewModel.dataSource
        )

    }

    private fun setDynamicInputs(inputData: MutableList<InputData>?) {
        AppLogger.instance.appLog(
            "${DynamicDialogFragment::class.java.simpleName}:Dynamic:Inputs",
            Gson().toJson(inputData)
        )
        inputList.clear()
        if (inputData != null)
            if (inputData.isNotEmpty()) {
                for (filter in inputData) {
                    userInput(
                        InputData(
                            name = filter.name,
                            key = filter.key,
                            value = filter.value,
                            encrypted = filter.encrypted,
                            mandatory = filter.mandatory
                        )
                    )
                }
            }

    }

    override fun setBinding() {
        EventBus.getDefault().register(this)
        setController()
    }

    @SuppressLint("NewApi")
    override fun userInput(inputData: InputData?) {
        AppLogger.instance.appLog(
            "${DynamicDialogFragment::class.java.simpleName}:Aliens",
            Gson().toJson(inputData)
        )
        inputList.removeIf { a -> a.key == inputData?.key }
        inputList.add(inputData!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDynamicDialogBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun onForm(formControl: FormControl?, modules: Modules?) {
        val formData = busData.data as GroupForm
        val params = formData.action?.serviceParamIDs?.split(",")
        val json = JSONObject()
        val encrypted = JSONObject()
        if (FieldValidationHelper.instance.validateFields(
                inputList = inputList,
                params = params!!,
                activity = requireActivity(),
                json = json,
                encrypted = encrypted,
                all = formData.allow
            )
        ) {
            val destination = formControl?.formID
            when (BaseClass.nonCaps(destination)) {
                BaseClass.nonCaps(FormNavigation.PAYMENT.name) -> {
                    confirm.onPay(
                        json = json, encrypted = encrypted,
                        inputList = inputList, module = modules,
                        action = formData.action, formControl = formControl
                    )
                    onDialog()
                }
                else -> {
                    confirm.validateModule(
                        jsonObject = json,
                        encrypted = encrypted,
                        modules = modules,
                        formControl = formControl,
                        action = formData.action
                    )
                    onDialog()
                }
            }
        }
    }

    override fun onDialog() {
        dialog?.dismiss()
    }


    override fun onDisplay(formControl: FormControl?, modules: Modules?) {
        AppLogger.instance.appLog("DISPLAY:form", Gson().toJson(formControl))
        if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("DISPLAY")) {
            AppLogger.instance.appLog("DIS", Gson().toJson(formControl))
            if (!busData.res?.display.isNullOrEmpty()) {
                val controller = MainDisplayController(this)
                controller.setData(DisplayData(busData.res?.display, formControl, modules))
                binding.dialogContainer.setController(controller)
            }
            if (BaseClass.nonCaps(formControl?.controlFormat) == BaseClass.nonCaps("JSON")) {
                if (!busData.res?.formField.isNullOrEmpty()) {
                    try {
                        val controller = MainDisplayController(this)
                        binding.dialogContainer.visibility = View.VISIBLE
                        // stopShimmer()
                        val list =
                            busData.res?.formField?.single { a -> a.controlID == formControl?.controlFormat }


                        val hashMaps = HashTypeConverter().from(list?.controlValue)

                        AppLogger.instance.appLog("DISPLAY:value", Gson().toJson(hashMaps))

                        controller.setData(
                            DisplayData(
                                hashMaps!!.toMutableList(),
                                formControl,
                                modules
                            )
                        )
                        binding.dialogContainer.setController(controller)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("JSON")) {
            busData.res?.let { setJsonFormatData(it, formControl, modules) }
        }
    }

    private fun setJsonFormatData(
        data: DynamicAPIResponse?,
        form: FormControl?,
        modules: Modules?
    ) {
        try {
            if (data != null) {
                AppLogger.instance.appLog("DYNAMIC:RESULT:DATA", Gson().toJson(data.resultsData))
                AppLogger.instance.appLog("DYNAMIC:FIELD:DATA", Gson().toJson(data.formField))

                val controller = MainDisplayController(this)
                if (!data.resultsData.isNullOrEmpty()) {
                    val list = data.resultsData?.single { a -> a.controlID == form?.controlID }
                    val hashMaps = HashTypeConverter().from(list?.controlValue)

                    controller.setData(DisplayData(hashMaps!!.toMutableList(), form, modules))
                    binding.dialogContainer.setController(controller)

                } else if (!data.formField.isNullOrEmpty()) {

                    binding.dialogContainer.visibility = View.VISIBLE
                    // val list = data.formField?.single { a -> a.controlID == form?.controlID }
                    val newList = data.formField?.find { a -> a.controlID == form?.controlID }
                    val hashMaps = HashTypeConverter().from(newList?.controlValue)

                    controller.setData(DisplayData(hashMaps!!.toMutableList(), form, modules))
                    binding.dialogContainer.setController(controller)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServerValue(formControl: FormControl?, view: TextView?) {
        AppLogger.instance.appLog(
            "${DynamicDialogFragment::class.java.simpleName} ServerForm",
            Gson().toJson(formControl)
        )
        AppLogger.instance.appLog(
            "${DynamicDialogFragment::class.java.simpleName} Form",
            Gson().toJson(busData.res)
        )
        try {
            if (busData.res != null) {
                if (!busData.res?.formField.isNullOrEmpty())
                    busData.res?.formField!!.forEach {
                        if (BaseClass.nonCaps(it.controlID) == BaseClass.nonCaps(formControl?.controlID)) {
                            view?.text = it.controlValue
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = it.controlValue,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory
                                )
                            )
                        }

                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDynamicDropDown(
        view: AutoCompleteTextView?,
        formControl: FormControl?,
        editTextList: MutableList<TextInputEditText>?
    ) {
        try {

            editTextList?.forEach {
                AppLogger.instance.appLog("DYNAMIC:EDIT:TAGS", it.tag.toString())
            }

            if (busData.res?.formField != null) {
                val data =
                    busData.res?.formField?.single { a -> a.controlID == formControl?.controlID }

                if (BaseClass.nonCaps(data?.controlID) == BaseClass.nonCaps("Packages")) {
                    val packages = InsuranceTypeConverter().from(data?.controlValue)
                    val adapter =
                        InsuranceAdapterItem(
                            requireContext(),
                            0, packages!!.toMutableList()
                        )
                    view?.setAdapter(adapter)
                    view?.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, p2, _ ->
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = adapter.getItem(p2)?.policyTerm,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory
                                )
                            )
                            val map = parameters(adapter.getItem(p2)!!)

                            if (editTextList!!.isNotEmpty())
                                for (s in map.entries) {
                                    editTextList.filter { a ->
                                        BaseClass.nonCaps(a.tag.toString()) == BaseClass.nonCaps(s.key)
                                    }.map { it.setText(s.value.toString()) }
                                }
                        }
                }
                else if (BaseClass.nonCaps(data?.controlID) == BaseClass.nonCaps("LOANS")) {
                    val packages = LoanTypeConverter().from(data?.controlValue)
                    val adapter =
                        LoanAdapterItem(
                            requireContext(),
                            0, packages!!.toMutableList()
                        )
                    view?.setAdapter(adapter)
                    view?.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, p2, _ ->
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = adapter.getItem(p2)?.productId,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory
                                )
                            )
                            val map = parameters(adapter.getItem(p2)!!)
                            if (editTextList!!.isNotEmpty())
                                for (s in map.entries) {
                                    editTextList.filter { a ->
                                        BaseClass.nonCaps(a.tag.toString()) == BaseClass.nonCaps(s.key)
                                    }.map { it.setText(s.value.toString()) }
                                }
                        }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private lateinit var confirm: Confirm

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DynamicDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DynamicDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun callback(confirm: Confirm, manager: FragmentManager) =
            DynamicDialogFragment().apply {
                this@Companion.confirm = confirm
                show(manager, this.tag)
            }
    }
}