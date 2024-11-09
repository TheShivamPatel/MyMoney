package com.piikeup.consumer.home.activity

import android.graphics.drawable.Drawable


data class TabItem(
    val title: String,
    val icon: Drawable? = null,
    val isSelected: Boolean? = null
)