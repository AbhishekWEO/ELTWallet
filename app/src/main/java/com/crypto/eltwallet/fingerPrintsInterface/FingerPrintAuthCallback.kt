package com.crypto.eltwallet.fingerPrintsInterface

import android.hardware.fingerprint.FingerprintManager

interface FingerPrintAuthCallback {

    /**
     * This method will notify the user whenever there is no finger print hardware found on the device.
     * Developer should use any other way of authenticating the user, like pin or password to authenticate the user.
     */
    fun onNoFingerPrintHardwareFound()

    /**
     * This method will execute whenever device supports finger print authentication but does not
     * have any finger print registered.
     * Developer should notify user to add finger prints in the settings by opening security settings
     * by using [FingerPrintUtils.openSecuritySettings].
     */
    fun onNoFingerPrintRegistered()

    /**
     * This method will be called if the device is running on android below API 23. As starting from the
     * API 23, android officially got the finger print hardware support, for below marshmallow devices
     * developer should authenticate user by other ways like pin, password etc.
     */
    fun onBelowMarshmallow()

    /**
     * This method will occur whenever  user authentication is successful.
     *
     * @param cryptoObject [FingerprintManager.CryptoObject] associated with the scanned finger print.
     */
    fun onAuthSuccess(cryptoObject: FingerprintManager.CryptoObject?)

    /**
     * This method will execute whenever any error occurs during the authentication.
     *
     * @param errorCode    Error code for the error occurred. These error code will be from [AuthErrorCodes].
     * @param errorMessage A human-readable error string that can be shown in UI
     * @see AuthErrorCodes
     */
    fun onAuthFailed(errorCode: Int, errorMessage: String?)
}