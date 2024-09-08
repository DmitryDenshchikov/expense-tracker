package denshchikov.dmitry.config

import denshchikov.dmitry.model.domain.Expense
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

@Configuration
class AppConfig {

    @Bean
    fun expenseStorage(): MutableMap<UUID, Expense> {
        return hashMapOf()
    }

}