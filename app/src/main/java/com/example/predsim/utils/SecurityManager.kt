package com.example.predsim.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecurityManager private constructor(private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun storeSecureString(key: String, value: String) {
        encryptedPreferences.edit().putString(key, value).apply()
    }

    fun getSecureString(key: String, defaultValue: String = ""): String {
        return encryptedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun clearSecureStorage() {
        encryptedPreferences.edit().clear().apply()
    }

    fun encryptData(plaintext: String): ByteArray {
        val cipher = getCipher(Cipher.ENCRYPT_MODE)
        return cipher.doFinal(plaintext.toByteArray())
    }

    fun decryptData(ciphertext: ByteArray): String {
        val cipher = getCipher(Cipher.DECRYPT_MODE)
        return String(cipher.doFinal(ciphertext))
    }

    fun storeAuthToken(token: String) {
        storeSecureString(KEY_AUTH_TOKEN, token)
    }

    fun getAuthToken(): String? {
        val token = getSecureString(KEY_AUTH_TOKEN)
        return if (token.isNotEmpty()) token else null
    }

    fun clearAuthToken() {
        encryptedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    fun isUserAuthenticated(): Boolean {
        return getAuthToken() != null
    }

    private fun getCipher(mode: Int): Cipher {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        // Get or generate the secret key
        val key = if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } else {
            generateSecretKey()
        }

        return Cipher.getInstance(TRANSFORMATION).apply {
            init(mode, key)
        }
    }

    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "PredSimSecretKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ENCRYPTED_PREFS_FILE_NAME = "encrypted_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"

        @Volatile
        private var instance: SecurityManager? = null

        fun getInstance(context: Context): SecurityManager {
            return instance ?: synchronized(this) {
                instance ?: SecurityManager(context.applicationContext).also { instance = it }
            }
        }
    }

    class SecurityException(message: String) : Exception(message)

    interface AuthenticationCallback {
        fun onAuthenticationSuccess(token: String)
        fun onAuthenticationFailure(error: String)
    }

    fun validateCredentials(username: String, password: String): Boolean {
        // TODO: Implement actual credential validation
        // This is a placeholder implementation
        return username.isNotEmpty() && password.length >= 8
    }

    fun hashPassword(password: String): String {
        // TODO: Implement proper password hashing
        // This is a placeholder implementation
        return password.hashCode().toString()
    }

    fun generateSessionToken(): String {
        // TODO: Implement proper session token generation
        // This is a placeholder implementation
        return java.util.UUID.randomUUID().toString()
    }

    fun validateSessionToken(token: String): Boolean {
        // TODO: Implement proper session token validation
        // This is a placeholder implementation
        return token.isNotEmpty() && token == getAuthToken()
    }
}
