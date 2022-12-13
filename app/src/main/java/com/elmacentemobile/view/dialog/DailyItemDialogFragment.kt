package com.elmacentemobile.view.dialog

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.elmacentemobile.R
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.getBitmap
import com.elmacentemobile.view.binding.colorsUrl
import com.elmacentemobile.view.dialog.tip.TipDialogMain
import com.elmacentemobile.view.model.StaticDataViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_Data = "data"

/**
 * A simple [Fragment] subclass.
 * Use the [DailyItemDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class DailyItemDialogFragment : DialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val color = MutableStateFlow(0)

    //private val image: MutableStateFlow<Bitmap>? = null
    var shareIntent = Intent(Intent.ACTION_SEND)
    private val viewModel: StaticDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_Data, DayTipData::class.java)
            } else it.getParcelable(ARG_Data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            val image = MutableStateFlow(BitmapFactory.decodeResource(resources, R.drawable.logo))
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val bitmap = getBitmap("${data?.bigImage}")
                    withContext(Dispatchers.Main) {
                        color.value = colorsUrl(bitmap)!!.rgb
                        image.value = bitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            setContent {
                if (data != null) {
                    TipDialogMain(
                        data = data!!,
                        appCallbacks = this@DailyItemDialogFragment,
                        color = color,
                        bitmap = image
                    )
                }
            }
        }
    }

    companion object {
        private var data: DayTipData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DailyItemDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(manager: FragmentManager, data: DayTipData) =
            DailyItemDialogFragment().apply {
                this@Companion.data = data
                show(manager, this.tag)
            }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes.windowAnimations = R.style.MyDialogAnimation
    }

    override fun onDialog() {
        dialog?.dismiss()
    }
}


@Parcelize
data class DayTipData(
    @field:SerializedName("BannerImg")
    @field:Expose
    val bannerImage: String?,
    @field:SerializedName("Title")
    @field:Expose
    val title: String?,
    @field:SerializedName("SmallImage")
    @field:Expose
    val smallImage: String?,
    @field:SerializedName("BigImage")
    @field:Expose
    val bigImage: String?,
    @field:SerializedName("ButtonText")
    @field:Expose
    val buttonText: String?,
    @field:SerializedName("TextData")
    @field:Expose
    val textData: String?
) : Parcelable


data class CarouselItem(
    @field:SerializedName("TextData")
    @field:Expose
    val textData: String?
)


class DayTipDataConverter {
    @TypeConverter
    fun from(data: String?): List<DayTipData>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<DayTipData>?>() {}.type
        return gsonBuilder.fromJson<List<DayTipData>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<DayTipData>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

val dataList = mutableListOf(
    DayTipData(
        bannerImage = "https://uat.craftsilicon.com//image//CentenaryApp//TipOfTheDay_fifa.jpg",
        title = "WORLD CUP MATCHES TODAY",
        buttonText = "Got It",
        smallImage = "",
        bigImage = "https://img.freepik.com/free-vector/gradient-world-footbal-championship-schedule-template_23-2149703072.jpg?w=2000",
        textData = "PGh0bWw+CiAgPGJvZHk+CiAgICA8aDE+Q2luZW1hIENsYXNzaWNzIE1vdmllIFJldmlld3M8L2gxPgogICAgPGgyPlJldmlldzogQmFza2V0YmFsbCBEb2cgKDIwMTgpPC9oMj4KICAgIDxwPjxpPjQgb3V0IG9mIDUgc3RhcnM8L2k+PC9wPgogICAgPHA+RnJvbSBkaXJlY3RvciA8Yj5WaWNraSBGbGVtaW5nPC9iPiBjb21lcyB0aGUgaGVhcnR3YXJtaW5nIHRhbGUgb2YgYSBib3kgbmFtZWQgUGV0ZSAoVHJlbnQgRHVnc29uKSBhbmQgaGlzIGRvZyBSb3ZlciAodm9pY2VkIGJ5IEJyaW5zb24gTHVtYmxlYnJ1bnQpLiBZb3UgbWF5IHRoaW5rIGEgYm95IGFuZCBoaXMgZG9nIGxlYXJuaW5nIHRoZSB0cnVlIHZhbHVlIG9mIGZyaWVuZHNoaXAgc291bmRzIGZhbWlsaWFyLCBidXQgYSBiaWcgdHdpc3Qgc2V0cyB0aGlzIGZsaWNrIGFwYXJ0OiBSb3ZlciBwbGF5cyBiYXNrZXRiYWxsLCBhbmQgaGUncyBkb2dnb25lIGdvb2QgYXQgaXQuPC9wPgogICAgPHA+V2hpbGUgaXQgbWF5IG5vdCBoYXZlIGJlZW4gbmVjZXNzYXJ5IHRvIGluY2x1ZGUgYWxsIDE1MCBtaW51dGVzIG9mIFJvdmVyJ3MgY2hhbXBpb25zaGlwIGdhbWUgaW4gcmVhbCB0aW1lLCBCYXNrZXRiYWxsIERvZyB3aWxsIGtlZXAgeW91ciBpbnRlcmVzdCBmb3IgdGhlIGVudGlyZXR5IG9mIGl0cyA0LWhvdXIgcnVudGltZSwgYW5kIHRoZSBlbmQgd2lsbCBoYXZlIGFueSBkb2cgbG92ZXIgaW4gdGVhcnMuIElmIHlvdSBsb3ZlIGJhc2tldGJhbGwgb3Igc3BvcnRzIHBldHMsIHRoaXMgaXMgdGhlIG1vdmllIGZvciB5b3UuPC9wPgogICAgPHA+RmluZCB0aGUgZnVsbCBjYXN0IGxpc3RpbmcgYXQgdGhlIEJhc2tldGJhbGwgRG9nIHdlYnNpdGUuPC9wPgogIDwvYm9keT4KPC9odG1sPg=="
    )
)

class TipItemConverter {
    @TypeConverter
    fun from(data: TipItem?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, TipItem::class.java)
    }

    @TypeConverter
    fun to(str: String?): TipItem? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, TipItem::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

class TipConverter {
    @TypeConverter
    fun from(data: DayTipData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, DayTipData::class.java)
    }

    @TypeConverter
    fun to(str: String?): DayTipData? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, DayTipData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

data class TipItem(
    @field:SerializedName("tip")
    @field:Expose
    val tip: Any?,
    @field:SerializedName("type")
    @field:Expose
    val type: TipTypeEnum
)

data class CarouselTip(
    @field:SerializedName("data")
    @field:Expose
    val data: String?,
    @field:SerializedName("banner")
    @field:Expose
    val banner: String?
)

class CarouselTipConverter {
    @TypeConverter
    fun from(data: String?): List<CarouselTip?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<CarouselTip?>?>() {}.type
        return gsonBuilder.fromJson<List<CarouselTip?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<CarouselTip?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

}

enum class TipTypeEnum {
    Url, Dialog
}



private fun parse(html: String): Spannable =
    (HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) as Spannable)