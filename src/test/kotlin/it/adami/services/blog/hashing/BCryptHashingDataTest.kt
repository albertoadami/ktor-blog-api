package it.adami.services.blog.hashing

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class BCryptHashingDataTest: WordSpec({

    "BCryptHashingData" should {

        "encrypt a string and return true if the plain string is the same" {

            val password = "test"

            val encryptedPassword = BCryptHashingData.encrypt(password)

            BCryptHashingData.verify(password, encryptedPassword) shouldBe true

        }

        "encrypt a string and return false if the plain string is different from the original" {

            val password = "test"

            val encryptedPassword = BCryptHashingData.encrypt(password)

            BCryptHashingData.verify("$password-wrong", encryptedPassword) shouldBe false
        }


    }

})