package org.swabs.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64

import java.security.KeyFactory
import java.security.Security
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object SignatureWithPubkey {
  def verify(signature: String, pubKey: String): Boolean = {
    Security.addProvider(new BouncyCastleProvider())

    val provider  = Security.getProvider("BC")
    val sig       = Signature.getInstance("SHA256withECDSA", provider)
    val kf        = KeyFactory.getInstance("EC", provider)
    val keySpec   = new X509EncodedKeySpec(Base64.decode(pubKey))
    val publicKey = kf.generatePublic(keySpec)

    sig.initVerify(publicKey)
    sig.verify(Base64.decode(signature))
  }
}
