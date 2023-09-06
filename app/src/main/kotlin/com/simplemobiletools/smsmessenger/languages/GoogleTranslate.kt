import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Provides functionality to amend certain translations.
 */
object TranslationFixer {

    fun applyCorrections(originalText: String, sourceLang: String = "ka"): String =
        if (sourceLang == "ka") originalText.replace("საქმარისი", "ანგარიში") else originalText
}

/**
 * Handles translation operations using Google Translate.
 */
class GoogleTranslate(private val context: Context) {

    companion object {
        private const val KEY_ALIAS = "GoogleTranslateKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
        if (!containsAlias(KEY_ALIAS)) generateSecretKey()
    }

    fun translate(text: String, sourceLang: String?, targetLang: String?): String {
        if (sourceLang == null || targetLang == null || sourceLang == targetLang) return text

        return try {
            val service = TranslateOptions.newBuilder().setApiKey(retrieveApiKey()).build().service
            val correctedText = TranslationFixer.applyCorrections(text, sourceLang)
            val translation = service.translate(
                correctedText,
                Translate.TranslateOption.sourceLanguage(sourceLang),
                Translate.TranslateOption.targetLanguage(targetLang)
            )
            translation.translatedText
        } catch (e: Exception) {
            e.printStackTrace()
            text
        }
    }

    fun saveApiKey(apiKey: String) {
        val encryptedDataWithIv = encrypt(apiKey.toByteArray(Charset.forName("UTF-8")))
        context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE).edit().apply {
            putString("encryptedData", Base64.encodeToString(encryptedDataWithIv.first, Base64.DEFAULT))
            putString("iv", Base64.encodeToString(encryptedDataWithIv.second, Base64.DEFAULT))
            apply()
        }
    }

    fun retrieveApiKey(): String {
        val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
        val encryptedData = sharedPrefs.getString("encryptedData", null)?.let { Base64.decode(it, Base64.DEFAULT) }
        val iv = sharedPrefs.getString("iv", null)?.let { Base64.decode(it, Base64.DEFAULT) }

        return if (encryptedData != null && iv != null) decrypt(encryptedData, iv) else ""
    }

    private fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey())
        }
        return cipher.doFinal(data) to cipher.iv
    }

    private fun decrypt(data: ByteArray, iv: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv))
        }
        return String(cipher.doFinal(data), Charset.forName("UTF-8"))
    }

    private fun generateSecretKey() {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()

        KeyGenerator.getInstance("AES", ANDROID_KEYSTORE).apply {
            init(keyGenParameterSpec)
            generateKey()
        }
    }

    private fun getSecretKey() = keyStore.getKey(KEY_ALIAS, null) as SecretKey
}
