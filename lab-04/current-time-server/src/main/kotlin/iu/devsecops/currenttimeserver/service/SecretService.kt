package iu.devsecops.currenttimeserver.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils


@Service
class SecretService {

    @Value("#{environment.SECRET}")
    private lateinit var secret: String

    fun showHashedSecret(): String = DigestUtils.md5DigestAsHex(secret.toByteArray())
}
