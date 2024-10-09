package com.elmacentemobile.view.composable.shot

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Create a State of screenshot of composable that is used with that is kept on each recomposition.
 * @param delayInMillis delay before each screenshot
 * if [ScreenshotState.liveScreenshotFlow] is collected.
 */
@Composable
fun rememberScreenshotState(delayInMillis: Long = 200) = remember {
    ScreenshotState(delayInMillis)
}

/**
 * State of screenshot of composable that is used with.
 * @param timeInMillis delay before each screenshot if [liveScreenshotFlow] is collected.
 */
class ScreenshotState internal constructor(
    private val timeInMillis: Long = 200
) {
    val imageState = mutableStateOf<ImageResult>(ImageResult.Initial)

    val bitmapState = mutableStateOf<Bitmap?>(null)

    internal var callback: (() -> Unit)? = null

    /**
     * Captures current state of Composable inside [ScreenshotBox]
     */
    fun capture() {
        callback?.invoke()
    }

    val liveScreenshotFlow = flow {
        while (true) {
            callback?.invoke()
            delay(timeInMillis)
            bitmapState.value?.let {
                emit(it)
            }

        }
    }.map {
       it
    }.flowOn(Dispatchers.Default)


    val bitmap: Bitmap?
        get() = bitmapState.value

    val imageBitmap: ImageBitmap?
        get() = bitmap?.asImageBitmap()


}

sealed class ImageResult {
    object Initial : ImageResult()
    data class Error(val exception: Exception) : ImageResult()
    data class Success(val data: Bitmap) : ImageResult()
}

interface Capture {
    fun capture(bitmap: Bitmap) {
        throw Exception("Not implemented")
    }
}

/**
 * A composable that gets screenshot of Composable that is in [content].
 * @param screenshotState state of screenshot that contains [Bitmap].
 * @param content Composable that will be captured to bitmap on action or periodically.
 */
@Composable
fun ScreenshotBox(
    modifier: Modifier = Modifier,
    screenshotState: ScreenshotState,
    content: @Composable () -> Unit,
) {
    val view: View = LocalView.current

    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    DisposableEffect(Unit) {
        screenshotState.callback = {
            composableBounds?.let { bounds ->
                if (bounds.width == 0f || bounds.height == 0f) return@let

                view.screenshot(bounds) { imageResult: ImageResult ->
                    screenshotState.imageState.value = imageResult

                    if (imageResult is ImageResult.Success) {
                        screenshotState.bitmapState.value = imageResult.data
                    }
                }
            }
        }

        onDispose {
            val bmp = screenshotState.bitmapState.value
            bmp?.apply {
                if (!isRecycled) {
                    recycle()
                }
            }
            screenshotState.bitmapState.value = null
            screenshotState.callback = null
        }
    }

    Box(modifier = modifier
        .onGloballyPositioned {
            composableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.boundsInWindow()
            } else {
                it.boundsInRoot()
            }
        }
    ) {
        content()
    }
}

