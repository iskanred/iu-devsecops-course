package iu.devsecops.currenttimeserver.service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [TimeService::class]
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TimeServiceTest(
    private val timeService: TimeService
) {

    @Test
    fun `test time increase on refresh`() {
        val oldTime = timeService.moscowTime()
        Thread.sleep(1000)
        val newTime = timeService.moscowTime()

        assertTrue("time after refresh must be greater than time before") {
            newTime > oldTime
        }
    }
}
