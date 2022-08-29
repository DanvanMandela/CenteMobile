package com.craft.silicon.centemobile.view.fragment.auth.pin

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.FragmentChangePinBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.isOnline
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.fragment.levels.LevelOneFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.gson.Gson
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
 * Use the [ChangePinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChangePinFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val baseViewModel: BaseViewModel by viewModels()
    private val workViewModel: WorkerViewModel by viewModels()
    private val composite = CompositeDisposable()

    private lateinit var binding: FragmentChangePinBinding

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
        binding = FragmentChangePinBinding.inflate(inflater, container, false)
        setOnClick()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.toolbar.setNavigationOnClickListener {
            (requireActivity().onBackPressed())
        }

        binding.materialButton.setOnClickListener {
            if (requireActivity().isOnline()) {
                if (validateFields()) changePin()
            } else ShowToast(
                requireContext(),
                getString(R.string.no_connection),
                true
            )
        }
    }

    private fun changePin() {
        val json = JSONObject()
        val encrypted = JSONObject()
        encrypted.put("OLDPIN", BaseClass.newEncrypt(binding.pinEdit.text.toString()))
        encrypted.put("NEWPIN", BaseClass.newEncrypt(binding.editNewPin.text.toString()))
        encrypted.put("CONFIRMPIN", BaseClass.newEncrypt(binding.editConPin.text.toString()))



        composite.add(
            baseViewModel.changePin(json, encrypted, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setOnSuccess(it)
                }, {
                    setLoading(false)
                    showError(getString(R.string.something_))
                    it.printStackTrace()
                })
        )
    }

    private fun setLoading(b: Boolean) {
        if (b) LoadingFragment.show(this.childFragmentManager)
        else LoadingFragment.dismiss(this.childFragmentManager)
    }


    private fun setOnSuccess(res: DynamicResponse?) {
        try {
            AppLogger().appLog(
                "Change:PIN:Response",
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
                        "${LevelOneFragment::class.java.simpleName}:E:ChangePin",
                        Gson().toJson(moduleData)
                    )
                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                        setLoading(false)
                        baseViewModel.dataSource.setBio(false)
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
                        workViewModel.routeData(viewLifecycleOwner,
                            object : WorkStatus {
                                override fun workDone(b: Boolean) {
                                    if (b) {
                                        setLoading(false)
                                        changePin()
                                    }
                                }
                            })
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            showError(getString(R.string.something_))
            setLoading(false)
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


    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.pinEdit.text.toString())) {
            ShowToast(requireContext(), getString(R.string.old_pin_required), true)
            false
        } else if (TextUtils.isEmpty(binding.editNewPin.text.toString())) {
            ShowToast(requireContext(), getString(R.string.new_pin_required), true)
            false
        } else if (TextUtils.isEmpty(binding.editConPin.text.toString())) {
            ShowToast(requireContext(), getString(R.string.con_new_pin_required), true)
            false
        } else {
            if (binding.editNewPin.text.toString() != binding.editConPin.text.toString()) {
                ShowToast(requireContext(), getString(R.string.new_password_not_matching), true)
                false
            } else true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun navigateUp() {

        (requireActivity().onBackPressed())
    }
}