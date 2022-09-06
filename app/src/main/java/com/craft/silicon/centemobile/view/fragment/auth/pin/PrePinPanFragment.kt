package com.craft.silicon.centemobile.view.fragment.auth.pin

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.FragmentPrePinOTPBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.TextHelper
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrePinPanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class PrePinPanFragment : Fragment(), AppCallbacks, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPrePinOTPBinding

    private val baseViewModel: BaseViewModel by viewModels()
    private val workerViewModel: WorkerViewModel by viewModels()
    private val composite = CompositeDisposable()


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
        binding = FragmentPrePinOTPBinding.inflate(inflater, container, false)
        settToolbar()
        setTextWatchers()
        setOnClick()
        return binding.root.rootView
    }


    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
    }

    private fun settToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            pagerData?.onBack(0)
        }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.editAccountNumber.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_account_number), true
            )
            false
        } else if (TextUtils.isEmpty(binding.editATM.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_atm_number), true
            )
            false
        } else if (TextUtils.isEmpty(binding.editATMPin.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_atm_pin), true
            )
            false
        } else {
            if (binding.editAccountNumber.length() < 8) {
                ShowToast(
                    requireContext(),
                    getString(R.string.account_number_invalid), true
                )
                false
            } else if (binding.editATM.length() < 12) {
                ShowToast(
                    requireContext(),
                    getString(R.string.atm_number_invalid), true
                )
                false
            } else if (binding.editATMPin.length() < 4) {
                ShowToast(
                    requireContext(),
                    getString(R.string.atm_pin_invalid), true
                )
                false
            } else true
        }
    }


    private fun setTextWatchers() {
        binding.editATM.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!TextHelper.isInputCorrect(
                        editable,
                        TextHelper.CARD_NUMBER_TOTAL_SYMBOLS,
                        TextHelper.CARD_NUMBER_DIVIDER_MODULO,
                        TextHelper.CARD_NUMBER_DIVIDER
                    )
                ) {
                    editable.replace(
                        0, editable.length,
                        TextHelper.concatString(
                            TextHelper.getDigitArray(
                                editable,
                                TextHelper.CARD_NUMBER_TOTAL_DIGITS
                            ),
                            TextHelper.CARD_NUMBER_DIVIDER_POSITION, TextHelper.CARD_NUMBER_DIVIDER
                        )
                    )
                }
            }
        })
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrePinPanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrePinPanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = PrePinPanFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.materialButton) {
            if (validateFields()) resetPin()
        }
    }

    private fun showError(string: String) {
        AlertDialogFragment.newInstance(
            DialogData(
                R.string.error,
                string,
                R.drawable.warning_app
            ), childFragmentManager
        )
    }

    private fun setLoading(b: Boolean) {
        if (b) LoadingFragment.show(this.childFragmentManager)
        else LoadingFragment.dismiss(this.childFragmentManager)
    }

    private fun resetPin() {
        setLoading(true)
        val jsonObject = JSONObject()
        val encrypted = JSONObject()
        try {
            jsonObject.put(
                "BANKACCOUNTID", Objects
                    .requireNonNull(binding.editAccountNumber.text).toString()
            )
            jsonObject.put(
                "MOBILENUMBER", PreForgotFragment.mobile
            )
            jsonObject.put("OTPKEY", PreForgotFragment.otp)
            encrypted.put(
                "CARDNUMBER", BaseClass.newEncrypt(
                    Objects.requireNonNull(binding.editATM.text)
                        .toString().replace("-", "")
                )
            )
            encrypted.put(
                "CARDPIN", BaseClass
                    .newEncrypt(Objects.requireNonNull(binding.editATMPin.text).toString())
            )
            composite.add(
                baseViewModel.pinResetPre(jsonObject, encrypted, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ res: DynamicResponse? ->
                        setOnSuccess(res)
                    }, { obj: Throwable -> obj.printStackTrace() })
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setOnSuccess(res: DynamicResponse?) {
        try {
            AppLogger().appLog(
                "RESET:PIN:Response",
                BaseClass.decryptLatest(
                    res!!.response,
                    baseViewModel.dataSource.deviceData.value!!.device,
                    true,
                    baseViewModel.dataSource.deviceData.value!!.run
                )
            )
            if (BaseClass.nonCaps(res.response) != StatusEnum.ERROR.type) {
                try {
                    val moduleData = DynamicDataResponseTypeConverter().to(
                        BaseClass.decryptLatest(
                            res.response,
                            baseViewModel.dataSource.deviceData.value!!.device,
                            true,
                            baseViewModel.dataSource.deviceData.value!!.run
                        )
                    )
                    AppLogger.instance.appLog(
                        "${PrePinPanFragment::class.java.simpleName}:E:ResetPin",
                        Gson().toJson(moduleData)
                    )
                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                        setLoading(false)
                        SuccessDialogFragment.showDialog(
                            DialogData(
                                title = R.string.success,
                                subTitle = moduleData?.message!!,
                                R.drawable.success
                            ),
                            requireActivity().supportFragmentManager, this
                        )
                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.FAILED.type) {
                        setLoading(false)
                        showError(moduleData?.message!!)
                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.TOKEN.type) {
                        workerViewModel.routeData(viewLifecycleOwner,
                            object : WorkStatus {
                                override fun workDone(b: Boolean) {
                                    if (b) {
                                        setLoading(false)
                                        resetPin()
                                    }
                                }
                            })
                    } else {
                        setLoading(false)
                        showError(getString(R.string.something_))
                    }

                } catch (e: Exception) {
                    setLoading(false)
                    showError(getString(R.string.something_))
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            showError(getString(R.string.something_))
            setLoading(false)
        }
    }


    override fun navigateUp() {
        (requireActivity()).onBackPressed()
    }
}