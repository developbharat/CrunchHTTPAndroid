package com.developbharat.crunchhttp.ui.screens.start

import com.developbharat.crunchhttp.fragment.ClientDevice

data class StartState(
    val clientDevice: ClientDevice? = null,
    val isInProgress: Boolean = false,
)