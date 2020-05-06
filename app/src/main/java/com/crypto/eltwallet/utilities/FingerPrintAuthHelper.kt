package com.crypto.eltwallet.utilities

import android.annotation.TargetApi
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.crypto.eltwallet.fingerPrintsInterface.FingerPrintAuthCallback
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey



class FingerPrintAuthHelper(var mContext: Context, var mCallback: FingerPrintAuthCallback) {
    companion object
    {
        private val KEY_NAME = UUID.randomUUID().toString()

        //error messages
        private val ERROR_FAILED_TO_GENERATE_KEY = "Failed to generate secrete key for authentication."
        private val ERROR_FAILED_TO_INIT_CHIPPER = "Failed to generate cipher key for authentication."

        /**
         * Get the [FingerPrintAuthHelper]
         *
         * @param context  instance of the caller.
         * @param callback [FingerPrintAuthCallback] to get notify whenever authentication success/fails.
         * @return [FingerPrintAuthHelper]
         */
        fun getHelper(@NonNull context: Context?, @NonNull callback: FingerPrintAuthCallback?): FingerPrintAuthHelper? {
            requireNotNull(context) { "Context cannot be null." }
            requireNotNull(callback) { "FingerPrintAuthCallback cannot be null." }
            return FingerPrintAuthHelper(context, callback)
        }
    }

    private var mKeyStore: KeyStore? = null
    private var mCipher: Cipher? = null

    /**
     * Instance of the caller class.
     */
    //private var mContext: Context? = null

    /**
     * [FingerPrintAuthCallback] to notify the parent caller about the authentication status.
     */
    //private var mCallback: FingerPrintAuthCallback? = null

    /**
     * [CancellationSignal] for finger print authentication.
     */
    private var mCancellationSignal: CancellationSignal? = null

    /**
     * Boolean to know if the finger print scanning is currently enabled.
     */
    private var isScanning = false

    /**
     * Private constructor.
     */
    /*private fun FingerPrintAuthHelper(@NonNull context: Context, @NonNull callback: FingerPrintAuthCallback) {
        mCallback = callback
        mContext = context
    }*/

    /**
     * Private constructor.
     */
    private fun FingerPrintAuthHelper() {
        throw RuntimeException("Use getHelper() to initialize FingerPrintAuthHelper.")
    }

    /**
     * Check if the finger print hardware is available.
     *
     * @param context instance of the caller.
     * @return true if finger print authentication is supported.
     */
    private fun checkFingerPrintAvailability(@NonNull context: Context): Boolean { // Check if we're running on Android 6.0 (M) or higher
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Fingerprint API only available on from Android 6.0 (M)
            val fingerprintManager: FingerprintManagerCompat =
                FingerprintManagerCompat.from(context)
            if (!fingerprintManager.isHardwareDetected()) { // Device doesn't support fingerprint authentication
                mCallback!!.onNoFingerPrintHardwareFound()
                return false
            } else if (!fingerprintManager.hasEnrolledFingerprints()) { // User hasn't enrolled any fingerprints to authenticate with
                mCallback!!.onNoFingerPrintRegistered()
                return false
            }
            true
        } else {
            mCallback!!.onBelowMarshmallow()
            false
        }
    }

    /**
     * Generate authentication key.
     *
     * @return true if the key generated successfully.
     */
    @TargetApi(23)
    private fun generateKey(): Boolean {
        mKeyStore = null
        val keyGenerator: KeyGenerator
        //Get the instance of the key store.
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
        } catch (e: NoSuchAlgorithmException) {
            return false
        } catch (e: NoSuchProviderException) {
            return false
        } catch (e: KeyStoreException) {
            return false
        }
        //generate key.
        try
        {
            mKeyStore!!.load(null)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or
                            KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
                    )
                    .build()
            )
            keyGenerator.generateKey()
            return true
        } catch (e: NoSuchAlgorithmException) {
            return false
        } catch (e: InvalidAlgorithmParameterException) {
            return false
        } catch (e: CertificateException) {
            return false
        } catch (e: IOException) {
            return false
        }
    }

    /**
     * Initialize the cipher.
     *
     * @return true if the initialization is successful.
     */
    @TargetApi(23)
    private fun cipherInit(): Boolean
    {
        val isKeyGenerated = generateKey()
        if (!isKeyGenerated)
        {
            mCallback!!.onAuthFailed(
                AuthErrorCodes.NON_RECOVERABLE_ERROR,
                ERROR_FAILED_TO_GENERATE_KEY)
            return false
        }
        mCipher = try {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } catch (e: NoSuchAlgorithmException) {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_GENERATE_KEY)
            return false
        } catch (e: NoSuchPaddingException) {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_GENERATE_KEY)
            return false
        }
        try
        {
            mKeyStore!!.load(null)
            val key =
                mKeyStore!!.getKey(KEY_NAME, null) as SecretKey
            mCipher!!.init(Cipher.ENCRYPT_MODE, key)
            return true
        }
        catch (e: KeyPermanentlyInvalidatedException)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        }
        catch (e: KeyStoreException) {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        }
        catch (e: CertificateException)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        }
        catch (e: UnrecoverableKeyException)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        } catch (e: IOException)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        } catch (e: NoSuchAlgorithmException)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        } catch (e: InvalidKeyException)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
            return false
        }
    }

    @TargetApi(23)
    @Nullable
    private fun getCryptoObject(): FingerprintManager.CryptoObject? {
        return if (cipherInit()) FingerprintManager.CryptoObject(mCipher) else null
    }

    /**
     * Start the finger print authentication by enabling the finger print sensor.
     * Note: Use this function in the onResume() of the activity/fragment. Never forget to call [.stopAuth]
     * in onPause() of the activity/fragment.
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun startAuth()
    {
        if (isScanning) stopAuth()
        //check if the device supports the finger print hardware?
        if (!checkFingerPrintAvailability(mContext!!)) return
        val fingerprintManager = mContext!!.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        val cryptoObject = getCryptoObject()
        if (cryptoObject == null)
        {
            mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER)
        }
        else
        {
            mCancellationSignal = CancellationSignal()
            fingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0,
                object : FingerprintManager.AuthenticationCallback()
                {
                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence)
                    {
                        mCallback!!.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, errString.toString())
                    }

                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence)
                    {
                        mCallback!!.onAuthFailed(AuthErrorCodes.RECOVERABLE_ERROR, helpString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        mCallback!!.onAuthFailed(AuthErrorCodes.CANNOT_RECOGNIZE_ERROR, null)
                    }

                    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                        mCallback!!.onAuthSuccess(result.cryptoObject)
                    }
                }, null
            )
        }
    }

    /**
     * Stop the finger print authentication.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun stopAuth() {
        if (mCancellationSignal != null) {
            isScanning = true
            mCancellationSignal!!.cancel()
            mCancellationSignal = null
        }
    }

    /**
     * @return true if currently listening the for the finger print.
     */
    fun isScanning(): Boolean {
        return isScanning
    }

}