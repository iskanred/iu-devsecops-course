package iu.devsecops.currenttimeserver.service

import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class TimeService {

    /**
     * @return current time in "Europe/Moscow" timezone
     */
    fun moscowTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return ZonedDateTime.now(ZoneId.of("Europe/Moscow")).format(formatter)
    }
}
