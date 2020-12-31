package com.kernel.finch.utils

fun consume(callback: () -> Unit) = true.also { callback() }