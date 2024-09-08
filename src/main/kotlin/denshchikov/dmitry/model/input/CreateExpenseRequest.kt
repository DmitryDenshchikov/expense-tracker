package denshchikov.dmitry.model.input

import denshchikov.dmitry.model.Category
import java.math.BigDecimal

data class CreateExpenseRequest(
    val amount: BigDecimal,
    val category: Category,
    val description: String?,
    val date: Long
)
