package com.example.lemacdatalab.datos

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Seguridad {

    private const val ITERACIONES = 10_000
    private const val LARGO_BITS = 256

    fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun calcularHash(contrasena: String, sal: String): String {
        val especificacion = PBEKeySpec(
            contrasena.toCharArray(),
            Base64.decode(sal, Base64.NO_WRAP),
            ITERACIONES,
            LARGO_BITS
        )
        val hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            .generateSecret(especificacion)
            .encoded
        especificacion.clearPassword()
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun coincide(contrasena: String, sal: String, hashGuardado: String): Boolean {
        val calculado = calcularHash(contrasena, sal).toByteArray()
        return MessageDigest.isEqual(calculado, hashGuardado.toByteArray())
    }
}