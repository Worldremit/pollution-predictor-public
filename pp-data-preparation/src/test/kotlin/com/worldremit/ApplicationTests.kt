package com.worldremit

import com.worldremit.util.core.AssumeConnection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@AssumeConnection(uris = ["tcp://localhost:9092", "tcp://localhost:8081"])
@TestPropertySource(properties = ["generator.enabled=false"])
class ApplicationTests {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoads() {
        assertThat(applicationContext).isNotNull
    }

}
