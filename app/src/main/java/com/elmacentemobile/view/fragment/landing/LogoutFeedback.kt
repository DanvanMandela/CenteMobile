package com.elmacentemobile.view.fragment.landing

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.elmacentemobile.R
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.databinding.FragmentLogoutFeedbackBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.navigate
import com.elmacentemobile.view.dialog.InfoFragment
import com.elmacentemobile.view.dialog.MainDialogData
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LogoutFeedback.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LogoutFeedback : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val composite = CompositeDisposable()
    private lateinit var rate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentLogoutFeedbackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLogoutFeedbackBinding.inflate(inflater, container, false)
        setBinding()
        setData()
        return binding.root.rootView
        //return inflater.inflate(R.layout.fragment_logout_feedback, container, false)
    }

    //dd/MM/yyyy
    private fun getDateFromMilliSeconds(millisecond: Long, date_format: String): String {
        val simpleDateFormat = SimpleDateFormat(date_format, Locale.getDefault())
        return simpleDateFormat.format(millisecond)
        //textView.text = String.format("Date: %s", dateString)
    }


    fun setData() {
        baseViewModel.dataSource.setFeedbackTimer(1)
        val loginTime = widgetViewModel.storageDataSource.loginTime.value!!//1661523060000L
        val logoutTime = System.currentTimeMillis()
        val duration = ((System.currentTimeMillis() - loginTime))

        val dateFormat: String = "dd MMM yyyy HH:mm:ss"
        var dateFormatHours: String = "HH:mm:ss"
        binding.txtLoginTime.text = getDateFromMilliSeconds(loginTime, dateFormat)
        binding.txtLogoutTime.text = getDateFromMilliSeconds(logoutTime, dateFormat)
        binding.txtDuration.text = getTimeDifference(duration)

        //binding.txtLoginTime.setText(""+widgetViewModel.storageDataSource.loginTime)
        //binding.txtLogoutTime.setText(""+widgetViewModel.storageDataSource.loginTime)
        //binding.txtDuration.setText(""+widgetViewModel.storageDataSource.loginTime)
    }


    fun main(args: Array<String>) {

        val milliseconds: Long = 1000000

        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60

        println("$milliseconds Milliseconds = $minutes minutes and $seconds seconds.")
    }

    fun getTimeDifference(milliseconds: Long): String {
        var x = milliseconds;
        val h = x / (60 * 60 * 1000)
        x -= h * (60 * 60 * 1000)
        val m = x / (60 * 1000)
        x -= m * (60 * 1000)
        val s = x / 1000
        x -= s * 1000

        return "${formatDuration(h.toDouble())}:${formatDuration(m.toDouble())}:${formatDuration(s.toDouble())}"

    }

    private fun formatDuration(duration: Double): String {
        // fill the result to be of 2 characters, use 0 as padding char
        return duration.toInt().toString().padStart(2, '0')
    }

    override fun setBinding() {
        binding.callback = this
    }

    companion object {
        private lateinit var callbacks: AppCallbacks

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LogoutFeedback.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogoutFeedback().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun setData(appCallbacks: AppCallbacks) =
            LogoutFeedback().apply {
                this@Companion.callbacks = appCallbacks
            }
    }

    override fun rating_dismiss() {
        Handler(Looper.getMainLooper()).postDelayed({
            dialog?.dismiss()
            callbacks.onDialog()

        }, 200)
    }

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    private fun showError(data: MainDialogData) {
        navigate(baseViewModel.navigationData.navigateToAlertDialog(data))
    }


    override fun rating_submit() {
        //TODO call server to submit loading
        if (rating > 0) {
            val comment = binding.edtComment.text
            if (rating < 3 && comment?.length!! < 3) {//bad rating comment required, comment require more than 3 characters
                BaseClass.show_toast(activity, activity?.getString(R.string.add_comment))
            } else {
                createFeedback()
            }
        } else {
            BaseClass.show_toast(activity, activity?.getString(R.string.choose_rating))
        }

    }

    private fun createFeedback() {
        val json = JSONObject()
        json.put("COMMENT", binding.edtComment.text.toString())
        json.put("RATING", rate)
        try {
            setLoading(true)
            composite.add(
                baseViewModel.addComment(
                    json, requireContext()
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        try {
                            AppLogger.instance.appLog(
                                "Feedback", BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(it.response) != "ok") {
                                val resData = DynamicDataResponseTypeConverter().to(
                                    BaseClass.decryptLatest(
                                        it.response,
                                        baseViewModel.dataSource.deviceData.value!!.device,
                                        true,
                                        baseViewModel.dataSource.deviceData.value!!.run
                                    )
                                )
                                if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                    setLoading(false)
                                    ShowToast(requireContext(), resData!!.message)
                                    callbacks.onDialog()
                                    dialog?.dismiss()
                                } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                    InfoFragment.showDialog(this.childFragmentManager, this)
                                } else {
                                    setLoading(false)
                                    AppLogger.instance.appLog("Feedback", Gson().toJson(resData))
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        showError(
                                            MainDialogData(
                                                title = getString(R.string.error),
                                                message = resData?.message,
                                            )
                                        )
                                    }, 200)
                                }
                            }
                        } catch (e: Exception) {
                            setLoading(false)
                            e.printStackTrace()
                        }
                    }, {
                        setLoading(false)
                        it.printStackTrace()
                    })
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    var rating: Int = 0
    override fun rate_very_poor() {
        rating = 1
        override_images()
        BaseClass.animation_blow(activity, binding.imgVeryPoor)
        binding.imgVeryPoor.setImageResource(R.drawable.feedback_one_selected)
        rate = getString(R.string.very_poor)
    }

    override fun rate_poor() {
        rating = 2
        override_images()
        BaseClass.animation_blow(activity, binding.imgPoor)
        binding.imgPoor.setImageResource(R.drawable.feedback_two_selected)
        rate = getString(R.string.poor)
    }

    override fun rate_average() {
        rating = 3
        override_images()
        BaseClass.animation_blow(activity, binding.imgAverage)
        binding.imgAverage.setImageResource(R.drawable.feedback_three_selected)
        rate = getString(R.string.average)
    }

    override fun rate_good() {
        rating = 4
        override_images()
        BaseClass.animation_blow(activity, binding.imgGood)
        binding.imgGood.setImageResource(R.drawable.feedback_four_selected)
        rate = getString(R.string.good)
    }

    override fun rate_excellent() {
        rating = 5
        override_images()
        BaseClass.animation_blow(activity, binding.imgExcellent);
        binding.imgExcellent.setImageResource(R.drawable.feedback_five_selected)
        rate = getString(R.string.excelent)
    }

    fun override_images() {
        binding.materialCardComment.visibility = if (rating > 3) {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.imgVeryPoor.setImageResource(R.drawable.feedback_one)
        binding.imgPoor.setImageResource(R.drawable.feedback_two)
        binding.imgAverage.setImageResource(R.drawable.feedback_three)
        binding.imgGood.setImageResource(R.drawable.feedback_four)
        binding.imgExcellent.setImageResource(R.drawable.feedback_five)
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

    override fun timeOut() {
        dialog?.dismiss()
        callbacks.onDialog()
    }


}