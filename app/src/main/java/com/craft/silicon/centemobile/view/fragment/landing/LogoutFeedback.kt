package com.craft.silicon.centemobile.view.fragment.landing

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
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentLogoutFeedbackBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.navigate
import com.craft.silicon.centemobile.view.binding.setImageRes
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

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
    private fun getDateFromMilliSeconds(millisecond: Long, date_format:String):String{
        val simpleDateFormat = SimpleDateFormat(date_format)
        val dateString = simpleDateFormat.format(millisecond)
        return dateString
        //textView.text = String.format("Date: %s", dateString)
    }


    fun setData(){
        val login_time  = widgetViewModel.storageDataSource.loginTime.value!!;//1661523060000L
        val logout_time = System.currentTimeMillis()
        val duration    = ((System.currentTimeMillis() - login_time));

        val date_format: String       = "dd MMM yyyy HH:mm:ss"
        var date_format_hours: String = "HH:mm:ss"
        binding.txtLoginTime.text     = getDateFromMilliSeconds(login_time, date_format)
        binding.txtLogoutTime.text    = getDateFromMilliSeconds(logout_time, date_format)
        binding.txtDuration.text      = getTimeDifference(duration)

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

    fun getTimeDifference(milliseconds:Long):String{
        var x = milliseconds;
        var h = x / (60*60*1000)
        x -= h * (60 * 60 * 1000)
        var m = x / (60*1000)
        x -= m * (60 * 1000)
        var s = x / 1000
        x -= s * 1000

        return "${formatDuration(h.toDouble())}:${formatDuration(m.toDouble())}:${formatDuration(s.toDouble())}"

    }

    fun formatDuration(duration: Double): String {
        // fill the result to be of 2 characters, use 0 as padding char
        return duration.toInt().toString().padStart(2, '0')
    }

    override fun setBinding() {
        binding.callback=this
    }

    companion object {
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
    }

    override fun rating_dismiss() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigate(widgetViewModel!!.navigation().navigateLanding())

        }, 500)
    }

    override fun rating_submit() {
        //widgetViewModel.storageDataSource.clearDevice();
        //TODO call server to submit loading
        Handler(Looper.getMainLooper()).postDelayed({
            navigate(widgetViewModel!!.navigation().navigateLanding())
        }, 500)
    }

    override fun rate_very_poor() {
        override_images()
        BaseClass.animation_blow(activity, binding.imgVeryPoor)
        binding.imgVeryPoor.setImageResource(R.drawable.feedback_one_selected)
    }

    override fun rate_poor() {
        override_images()
        BaseClass.animation_blow(activity, binding.imgPoor)
        binding.imgPoor.setImageResource(R.drawable.feedback_two_selected)
    }

    override fun rate_average() {
        override_images()
        BaseClass.animation_blow(activity, binding.imgAverage);
        binding.imgAverage.setImageResource(R.drawable.feedback_three_selected)
    }

    override fun rate_good() {
        override_images()
        BaseClass.animation_blow(activity, binding.imgGood);
        binding.imgGood.setImageResource(R.drawable.feedback_four_selected)
    }

    override fun rate_excellent() {
        override_images()
        BaseClass.animation_blow(activity, binding.imgExcellent);
        binding.imgExcellent.setImageResource(R.drawable.feedback_five_selected)
    }

    fun override_images(){
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


}