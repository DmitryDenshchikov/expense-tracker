package denshchikov.dmitry.model.input

import denshchikov.dmitry.model.Category
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateExpenseRequest(
    @field:Positive
    val amount: BigDecimal,
    val category: Category,
    val description: String?,
    val date: Long
)
