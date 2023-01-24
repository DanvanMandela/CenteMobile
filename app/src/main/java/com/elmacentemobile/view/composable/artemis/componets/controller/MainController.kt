package com.elmacentemobile.view.composable.artemis.componets.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.elmacentemobile.view.ep.data.DynamicData
import com.elmacentemobile.view.ep.data.GroupForm
import com.elmacentemobile.view.ep.data.GroupModule
import kotlinx.coroutines.launch

@Composable
fun MainController(data: DynamicData) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        scope.launch {
            when (data) {
                is GroupForm -> {}
                is GroupModule -> {}
            }
        }
    }
}