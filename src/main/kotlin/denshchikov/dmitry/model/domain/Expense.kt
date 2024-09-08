package denshchikov.dmitry.model.domain

import denshchikov.dmitry.model.Category
import java.math.BigDecimal
import java.util.UUID

data class Expense(
    val id: UUID,
    val amount: BigDecimal,
    val category: Category,
    val description: String?,
    val date: Long
)
