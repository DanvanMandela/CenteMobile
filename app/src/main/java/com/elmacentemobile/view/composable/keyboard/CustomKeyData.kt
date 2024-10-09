package com.elmacentemobile.view.composable.keyboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elmacentemobile.R

data class CustomKeyData(
    var str: String,
    var type: KeyFunctionEnum,
    var icon: ImageVector? = null
)

enum class KeyFunctionEnum {
    Pop, Push, Finish, Space, Clear
}


val numericKey = mutableListOf(
    CustomKeyData(str = "1", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "2", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "3", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "4", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "5", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "6", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "7", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "8", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "9", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "0", type = KeyFunctionEnum.Push),
    CustomKeyData(
        str = "Delete",
        type = KeyFunctionEnum.Pop,
        Icons.Default.Backspace
    ),
    CustomKeyData(
        str = "Done",
        type = KeyFunctionEnum.Finish,
        Icons.Default.Done
    )
)

val alphaNumericKey = mutableListOf(
    CustomKeyData(str = "1", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "2", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "3", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "4", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "5", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "6", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "7", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "8", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "9", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "0", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "q", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "w", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "e", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "r", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "t", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "y", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "u", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "i", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "o", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "p", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "a", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "s", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "d", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "f", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "g", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "h", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "j", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "k", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "l", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "z", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "x", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "c", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "v", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "b", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "n", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "m", type = KeyFunctionEnum.Push),
    CustomKeyData(str = "", type = KeyFunctionEnum.Space),
    CustomKeyData(
        str = "", type = KeyFunctionEnum.Space
    ),
    CustomKeyData(
        str = "Delete",
        type = KeyFunctionEnum.Pop,
        Icons.Outlined.Backspace
    ),
    CustomKeyData(
        str = "Done",
        type = KeyFunctionEnum.Finish,
        Icons.Outlined.Done
    )
)

@Composable
fun CustomKeyItem(
    data: CustomKeyData,
    onClick: (option: CustomKeyData) -> Unit = {}
) {
    if (data.type != KeyFunctionEnum.Space)
        Card(
            elevation = 0.dp,
            backgroundColor = colorResource(id = R.color.ghost_white),
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onClick(data)
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                IconButton(
                    onClick = {
                        onClick(data)
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .matchParentSize(),
                ) {
                    if (data.icon != null)
                        Icon(
                            imageVector = data.icon!!,
                            contentDescription = data.str
                        )
                    else Text(
                        text = data.str,
                        fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                        style = Typography().h6,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
}

@Preview
@Composable
fun CustomKeyItemPreview() {
    CustomKeyItem(data = numericKey[0]) {}
}
