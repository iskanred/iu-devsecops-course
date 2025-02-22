package iu.devsecops.currenttimeserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CurrentTimeServerApplication

fun main(args: Array<String>) {
    runApplication<CurrentTimeServerApplication>(*args)
}
