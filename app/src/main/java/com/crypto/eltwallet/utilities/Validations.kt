package com.crypto.eltwallet.utilities

import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.crypto.eltwallet.R
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Matcher
import java.util.regex.Pattern

class Validations {

    companion object
    {

        lateinit var pattern: Pattern
        lateinit var matcher: Matcher

        fun validateForSignUp(userName: String, email: String, password: String, rePassword: String, edtMobile: String, linearLayout: LinearLayout): Boolean {

            if (userName.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_username, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (email.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_email, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (!email.trim { it <= ' ' }.matches(Constants.EMAIL_PATREN.toRegex())) {

                Snackbar.make(linearLayout, R.string.enter_your_correct_email, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (edtMobile.toString().trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_mobileno, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (edtMobile.toString().length < 9) {

                Snackbar.make(linearLayout, R.string.enter_atleast_your_mobileno, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (password.toString().trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_password, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (password.toString().trim { it <= ' ' }.length < 8) {

                Snackbar.make(linearLayout, R.string.atleast_eight, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (!validate(password.toString())) {

                Snackbar.make(linearLayout, R.string.enter_well_pattern, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (rePassword.toString().trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_password, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (password.toString().trim { it <= ' ' } != rePassword.toString().trim { it <= ' ' }) {

                Snackbar.make(linearLayout, R.string.make_sure_you_enter_same_password, Snackbar.LENGTH_LONG).show()

                return false
            }
            else
            {
                return true
            }

//        if (!isNetworkConnected(context)) {
//            showNoInternetConnection(context)
//            return false
//        } else {
//            return true
//        }

        }


        fun validateForLogin(userName: String, password: String, linearLayout: LinearLayout): Boolean {

            if (userName.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_username, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (password.toString().trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_password, Snackbar.LENGTH_LONG).show()

                return false
            }
//            else if (password.toString().trim { it <= ' ' }.length < 8) {
//
//                Snackbar.make(linearLayout, R.string.atleast_eight, Snackbar.LENGTH_LONG).show()
//
//                return false
//            }
//            else if (!validate(password.toString())) {
//
//                Snackbar.make(linearLayout, R.string.enter_well_pattern, Snackbar.LENGTH_LONG).show()
//
//                return false
//            }
            else
            {
                return true
            }

//        if (!isNetworkConnected(context)) {
//            showNoInternetConnection(context)
//            return false
//        } else {
//            return true
//        }

        }


        fun validateForImportWallet(input_walletname: String, input_walletaddress: String, input_walletpvtkey : String, linearLayout: RelativeLayout): Boolean {

            if (input_walletname.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_wallet_address, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (input_walletaddress.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_wallet_Private_key, Snackbar.LENGTH_LONG).show()

                return false
            }
            else if (input_walletpvtkey.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_new_wallet_name, Snackbar.LENGTH_LONG).show()

                return false
            }

            else
            {
                return true
            }

//        if (!isNetworkConnected(context)) {
//            showNoInternetConnection(context)
//            return false
//        } else {
//            return true
//        }

        }


        fun validate(password: String): Boolean {

            pattern = Pattern.compile(Constants.PASSWORD_PATREN)
            matcher = pattern.matcher(password)
            return matcher.matches()

        }


        fun validateForSend(input_to: String, input_from: String, input_amount: String, linearLayout: RelativeLayout): Boolean {

            if (input_to.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_to_address, Snackbar.LENGTH_LONG).show()

                return false
            } else if (input_from.toString().trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_from_address, Snackbar.LENGTH_LONG)
                    .show()

                return false
            } else if (input_amount.toString().trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_elt_amount, Snackbar.LENGTH_LONG).show()

                return false
            } else {
                return true
            }
        }

    }

}