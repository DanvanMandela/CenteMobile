package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentOnTheLandingBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnTheLandingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OnTheLandingFragment : Fragment(), AppCallbacks, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentOnTheLandingBinding

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
        binding = FragmentOnTheLandingBinding.inflate(inflater, container, false)
        setOnClick()
        setBinding()
        setName()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)

    }

    private fun setName() {
        val builder = SpannableStringBuilder()
        val ab = getString(R.string.welcome_to_)
        val abSpannable = SpannableString(ab)
        abSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.dar_color_one)),
            0,
            ab.length,
            0
        )
        builder.append(abSpannable)
        val a = getString(R.string.cente)
        val aSpannable = SpannableString(a)

        aSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.app_blue_dark)),
            0,
            a.length,
            0
        )
        builder.append(aSpannable)

        val b = getString(R.string.on_)
        val bSpannable = SpannableString(b)
        bSpannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_app
                )
            ),
            0, b.length, 0
        )
        builder.append(bSpannable)

        val c = getString(R.string.the)
        val cSpannable = SpannableString(c)
        cSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.app_blue_dark)),
            0,
            c.length,
            0
        )
        builder.append(cSpannable)

        val d = getString(R.string.go)
        val dSpannable = SpannableString(d)
        dSpannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_app
                )
            ), 0, d.length, 0
        )
        builder.append(dSpannable)

        binding.textView.setText(builder, TextView.BufferType.SPANNABLE)
        builder.clear()
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnTheLandingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnTheLandingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = OnTheLandingFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(view: View?) {
        if (view == binding.materialButton) {
            pagerData?.onNext(1)
        }
    }
}