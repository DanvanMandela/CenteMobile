package com.elmacentemobile.view.composable

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.elmacentemobile.R

private val VerticalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(x = 0f)
    override suspend fun onPreFling(available: Velocity) = available.copy(x = 0f)
}

private val HorizontalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(y = 0f)
    override suspend fun onPreFling(available: Velocity) = available.copy(y = 0f)
}

fun Modifier.disabledVerticalPointerInputScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(VerticalScrollConsumer) else this

fun Modifier.disabledHorizontalPointerInputScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(HorizontalScrollConsumer) else this


@Composable
fun CarouselImage(
    url: String, modifier: Modifier = Modifier
) {

    val context = LocalContext.current


    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    var drawable by remember { mutableStateOf<Drawable?>(null) }
    val sizeModifier = modifier
        .fillMaxWidth()
        .height(100.dp)
        .clip(shape = RoundedCornerShape(16.dp))


    DisposableEffect(url) {
        val glide = Glide.with(context)
        val target = object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                image = null
                drawable = placeholder
            }

            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                image = bitmap.asImageBitmap()
            }
        }
        glide.asBitmap()
            .centerCrop()
            .load(url)
            .placeholder(R.drawable.centenary_bank)
            .into(target)

        onDispose {
            image = null
            drawable = null
            glide.clear(target)
        }
    }


    val theImage = image
    val theDrawable = drawable
    if (theImage != null) {
        Column(
            modifier = sizeModifier
        ) {
            // Image is a pre-defined composable that lays out and draws a given [ImageBitmap].
            Image(
                bitmap = theImage,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }
    } else if (theDrawable != null) {
        Canvas(modifier = sizeModifier) {
            drawIntoCanvas { canvas ->
                theDrawable.draw(canvas.nativeCanvas)
            }
        }
    }
}
