package iu.devsecops.currenttimeserver.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.name

@Service
class VisitsService {

    @PostConstruct
    @Suppress("unused")
    fun createVisitsFile() {
        try {
            Files.createDirectories(visitsDirPath)
            Files.createFile(visitsFilePath)
            Files.writeString(visitsFilePath, "0")
        } catch (_: FileAlreadyExistsException) {
            logger.info { "File 'visits' already exists" }
        }
    }

    fun incrementVisits() {
        val visitsCounter = Files.readString(visitsFilePath).toInt()
        val line = (visitsCounter + 1).toString()
        Files.writeString(visitsFilePath, line)
        logger.info { "Visits counter has been incremented to $line" }
    }

    fun getVisits(): String = Files.readString(visitsFilePath)

    companion object {
        private val logger = KotlinLogging.logger { }
        private val visitsDirPath = Paths.get("visits_dir")
        private val visitsFilePath = Paths.get("${visitsDirPath.name}/visits")
    }
}
