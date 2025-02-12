package iu.devsecops.currenttimeserver.controller

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkBeans
import io.mockk.every
import iu.devsecops.currenttimeserver.service.TimeService
import iu.devsecops.currenttimeserver.service.VisitsService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@WebMvcTest(controllers = [TimeController::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@MockkBeans(
    MockkBean(TimeService::class),
    MockkBean(VisitsService::class),
)
class TimeControllerTest(
    private val mockMvc: MockMvc,
    private val timeService: TimeService,
    private val visitsService: VisitsService
) {

    @Test
    fun `test time page available and has correct text`() {
        every { timeService.moscowTime() } returns MOCK_TIME_VALUE
        every { visitsService.incrementVisits() } returns Unit

        val requestPerform = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/")
        )

        requestPerform
            .andExpect(
                MockMvcResultMatchers.status().isOk
            )
            .andExpect(
                MockMvcResultMatchers.content().string(MOCK_TIME_VALUE)
            )
    }

    companion object {
        private const val MOCK_TIME_VALUE = "00:00:00"
    }
}
