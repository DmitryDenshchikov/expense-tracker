package denshchikov.dmitry.mapper

import denshchikov.dmitry.model.domain.Expense
import denshchikov.dmitry.model.input.CreateExpenseRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
class ExpenseMapper {

    fun toDomain(request: CreateExpenseRequest): Expense = request.let {
        Expense(UUID.randomUUID(), it.amount, it.category, it.description, it.date)
    }

}