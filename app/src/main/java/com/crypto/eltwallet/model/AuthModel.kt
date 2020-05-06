package com.crypto.eltwallet.model

import com.crypto.eltwallet.R

enum class AuthModel private constructor(val titleResId: Int, val layoutResId: Int) {
    RED(R.string.one, R.layout.fragment_login),
    BLUE(R.string.two, R.layout.fragment_signup)
}
