package org.swabs.core.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.util.SignatureWithPubkey

import java.security.KeyPairGenerator
import java.security.Security
import java.security.Signature
import java.security.spec.ECGenParameterSpec

class VerificationSpec extends AnyWordSpec with Matchers {
  "Verification#sigWithPubkey" must {
    Security.addProvider(new BouncyCastleProvider())
    val bouncyCastle = Security.getProvider("BC")

    "verify successfully" in {
      // arrange
      val curveName = "secp256k1"
      val keyPairGen = KeyPairGenerator.getInstance("EC", bouncyCastle)
      val ecSpec = new ECGenParameterSpec(curveName)
      keyPairGen.initialize(ecSpec)
      val keyPair = keyPairGen.generateKeyPair()

      val publicKey = keyPair.getPublic
      val publicKeyHex = Base64.encode(publicKey.getEncoded)
      val publicKeyString = new String(publicKeyHex, "UTF-8")

      val signer = Signature.getInstance("SHA256withECDSA", bouncyCastle)
      signer.initSign(keyPair.getPrivate)
      val signatureBytes = Base64.encode(signer.sign())
      val signature = new String(signatureBytes, "UTF-8")

      // act & assert
      SignatureWithPubkey.verify(signature, publicKeyString) mustBe true
    }
  }
}
