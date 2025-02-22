package iu.devsecops.currenttimeserver.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import iu.devsecops.currenttimeserver.service.SecretService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/secret")
@Suppress("unused")
class SecretController(
    private val secretService: SecretService
) {

    @GetMapping
    fun showSecret(): String = runCatching {
        "MD5 hash of secret is: ${secretService.showHashedSecret()}"
    }.onSuccess {
        logger.info { "GET /secret" }
    }.onFailure {
        logger.error { it }
    }.getOrThrow()

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
