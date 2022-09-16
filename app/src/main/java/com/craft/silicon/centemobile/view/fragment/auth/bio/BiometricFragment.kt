package com.craft.silicon.centemobile.view.fragment.auth.bio

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.converter.IVData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.pref.CryptoManager
import com.craft.silicon.centemobile.data.source.pref.EncryptedData
import com.craft.silicon.centemobile.databinding.FragmentBiometricBinding
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.level.FalconHeavyActivity
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.fragment.auth.bio.util.BiometricAuthListener
import com.craft.silicon.centemobile.view.fragment.auth.bio.util.BiometricUtil
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.crypto.Cipher


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BiometricFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BiometricFragment : BottomSheetDialogFragment(), AppCallbacks, View.OnClickListener,
    BioInterface,
    BiometricAuthListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var cryptographyManager: CryptoManager
    private lateinit var secretKeyName: String
    private lateinit var binding: FragmentBiometricBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private var state = false
    private var pin: String? = null

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
        binding = FragmentBiometricBinding.inflate(inflater, container, false)
        setBinding()
        setViewModel()
        setOnClick()
        setCrypto()

        setBioMetric()
        return binding.root.rootView
    }

    private fun setBioMetric() {

    }

    override fun setViewModel() {
        val sData = widgetViewModel.storageDataSource.bio.asLiveData()
        sData.observe(viewLifecycleOwner) {
            if (it != null) {
                state = it
                if (state) {
                    binding.enableBio.text = getString(R.string.disable_touch_id_login)
                    binding.toggleGroup.check(R.id.enableBio)
                } else {
                    binding.enableBio.text = getString(R.string.enable_touch_id_login)
                    binding.toggleGroup.uncheck(R.id.enableBio)
                }
            }
        }
    }

    private fun setCrypto() {
        cryptographyManager = CryptoManager()
        secretKeyName = getString(R.string.secret_key_name)
    }

    override fun setOnClick() {
        binding.enableBio.setOnClickListener(this)
    }


    private fun processData(cryptoObject: BiometricPrompt.CryptoObject?) {
        if (!state) {
            val encryptedData = cryptographyManager.encryptData(pin!!, cryptoObject?.cipher!!)
            enableBio(encryptedData)
        } else {
            val s = widgetViewModel.storageDataSource.iv.value
            val data = cryptographyManager.decryptData(s!!.cipherText, cryptoObject?.cipher!!)
            disableBio(data)
        }

    }

    private fun disableBio(data: String) {
        if (data == pin) {
            widgetViewModel.storageDataSource.setBio(false)
            SuccessDialogFragment.showDialog(
                DialogData(
                    title = R.string.success,
                    subTitle = getString(R.string.fingerprint_disabled),
                    R.drawable.success
                ),
                childFragmentManager, this
            )
        } else ShowToast(requireContext(), getString(R.string.invalid_pin), true)
    }

    private fun enableBio(data: EncryptedData) {
        widgetViewModel.storageDataSource.setIv(
            IVData(
                iv = data.initializationVector,
                cipherText = data.ciphertext
            )
        )
        widgetViewModel.storageDataSource.setBio(true)
        SuccessDialogFragment.showDialog(
            DialogData(
                title = R.string.success,
                subTitle = getString(R.string.fingerprint_enabled),
                R.drawable.success
            ),
            childFragmentManager, this
        )
    }


    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.callback = this
        if (form != null && module != null) {
            binding.data = GroupForm(
                module = module!!,
                form = form
            )
        }


    }

    override fun navigateUp() {
        requireActivity().onBackPressed()
    }

    companion object {
        private var form: MutableList<FormControl>? = null
        private var module: Modules? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BiometricFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BiometricFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun setData(
            form: MutableList<FormControl>?,
            module: Modules?
        ) =
            BiometricFragment().apply {
                this@Companion.form = form
                this@Companion.module = module
            }

        @JvmStatic
        fun show(
            form: MutableList<FormControl>?,
            module: Modules?, manager: FragmentManager
        ) =
            BiometricFragment().apply {
                this@Companion.form = form
                this@Companion.module = module

                show(manager, this.tag)
            }

    }

    override fun onPin(pin: String) {
        this.pin = pin
//        authenticateTo()
        if (state) {
            val data = widgetViewModel.storageDataSource.iv.value
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName,
                data!!.iv
            )
            showBiometricOption(cipher)
        } else {
            val cipher =
                cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            showBiometricOption(cipher)
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.enableBio) {
            if ((BiometricUtil.isBiometricReady(requireContext()))) {
                EnableBiometricFragment.showDialog(
                    this,
                    this.childFragmentManager
                )
                if (state) {
                    binding.toggleGroup.check(R.id.enableBio)
                } else {
                    binding.toggleGroup.uncheck(R.id.enableBio)
                }
            } else ShowToast(requireContext(), getString(R.string.device_no_biometric_enable), true)
        }
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        processData(result.cryptoObject)
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
        ShowToast(requireContext(), errorMessage)
    }

    private fun showBiometricOption(cipher: Cipher) {
        BiometricUtil.showBiometricPrompt(
            "${getString(R.string.app_name)} ${getString(R.string.auth_)}",
            getString(R.string.use_password),
            getString(R.string.auth_finger),
            requireActivity() as FalconHeavyActivity,
            this,
            BiometricPrompt.CryptoObject(cipher)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { data ->
                val behaviour = BottomSheetBehavior.from(data)
                setupFullHeight(data)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.setDraggable(false)
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}

interface BioInterface {
    fun onPin(pin: String)
}