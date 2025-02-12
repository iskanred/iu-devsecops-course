package iu.devsecops.currenttimeserver.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import iu.devsecops.currenttimeserver.service.TimeService
import iu.devsecops.currenttimeserver.service.VisitsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/")
@Suppress("unused")
class TimeController(
    private val timeService: TimeService,
    private val visitsService: VisitsService
) {

    /**
     * @return text string that contains current time in "Europe/Moscow" timezone
     */
    @GetMapping
    fun time(): String = runCatching {
        visitsService.incrementVisits()
        timeService.moscowTime()
    }.onSuccess {
        logger.info { "GET /" }
    }.onFailure {
        logger.error { it }
    }.getOrThrow()

    /**
     * @return text string that contains number of visits of "/" endpoint by different users
     */
    @GetMapping("/visits")
    fun visitsCounter(): String = runCatching {
        visitsService.getVisits()
    }.onSuccess {
        logger.info { "GET /visits" }
    }.onFailure {
        logger.error { it }
    }.getOrThrow()

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
