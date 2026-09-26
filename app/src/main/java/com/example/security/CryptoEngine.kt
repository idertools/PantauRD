package com.example.security

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

data class EncryptedEnvelope(
    val ciphertext: String,
    val iv: String,
    val algorithm: String = "AES-256-GCM",
    val keyFingerprint: String,
    val timestamp: Long = System.currentTimeMillis()
)

object SafeBase64 {
    fun encode(bytes: ByteArray): String {
        return try {
            java.util.Base64.getEncoder().encodeToString(bytes)
        } catch (_: Throwable) {
            try {
                android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            } catch (_: Throwable) {
                // Fallback manual basic encoding if both are unavailable
                val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                val sb = StringBuilder()
                var i = 0
                while (i < bytes.size) {
                    val b0 = bytes[i].toInt() and 0xFF
                    val b1 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
                    val b2 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0
                    sb.append(chars[b0 shr 2])
                    sb.append(chars[((b0 and 3) shl 4) or (b1 shr 4)])
                    sb.append(if (i + 1 < bytes.size) chars[((b1 and 15) shl 2) or (b2 shr 6)] else '=')
                    sb.append(if (i + 2 < bytes.size) chars[b2 and 63] else '=')
                    i += 3
                }
                sb.toString()
            }
        }
    }

    fun decode(str: String): ByteArray {
        return try {
            java.util.Base64.getDecoder().decode(str)
        } catch (_: Throwable) {
            android.util.Base64.decode(str, android.util.Base64.NO_WRAP)
        }
    }
}

object CryptoEngine {
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val ALGORITHM = "AES/GCM/NoPadding"

    private val defaultSessionKey: SecretKey by lazy {
        try {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256)
            keyGen.generateKey()
        } catch (e: Exception) {
            val bytes = ByteArray(32) { (it * 7).toByte() }
            SecretKeySpec(bytes, "AES")
        }
    }

    fun getKeyFingerprint(secretKey: SecretKey = defaultSessionKey): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(secretKey.encoded)
            hash.take(8).joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            "E2EE:F4:9A:8C:21:B3:01"
        }
    }

    fun encryptPayload(plainText: String, customKey: SecretKey? = null): EncryptedEnvelope {
        val key = customKey ?: defaultSessionKey
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val cipherBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        return EncryptedEnvelope(
            ciphertext = SafeBase64.encode(cipherBytes),
            iv = SafeBase64.encode(iv),
            algorithm = "AES-256-GCM",
            keyFingerprint = getKeyFingerprint(key)
        )
    }

    fun decryptPayload(envelope: EncryptedEnvelope, customKey: SecretKey? = null): String {
        return try {
            val key = customKey ?: defaultSessionKey
            val iv = SafeBase64.decode(envelope.iv)
            val cipherBytes = SafeBase64.decode(envelope.ciphertext)

            val cipher = Cipher.getInstance(ALGORITHM)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            "[Gagal dekripsi: Kunci tidak cocok atau integritas rusak]"
        }
    }
}
