package denshchikov.dmitry.controller

import denshchikov.dmitry.mapper.ExpenseMapper
import denshchikov.dmitry.model.domain.Expense
import denshchikov.dmitry.model.input.CreateExpenseRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/expenses")
class ExpenseController(
    val expenseMapper: ExpenseMapper,
    val expenseStorage: MutableMap<UUID, Expense>
) {

    @PostMapping
    fun createTask(@Valid @RequestBody request: CreateExpenseRequest): ResponseEntity<Expense> {
        val domain = expenseMapper.toDomain(request)
        expenseStorage[domain.id] = domain

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(domain.id)
            .toUri()

        return ResponseEntity.created(location).body(domain)
    }

    @GetMapping
    fun getAllTasks() = expenseStorage.values

    @GetMapping("/{id}")
    fun getTask(@PathVariable id: UUID): ResponseEntity<Expense> {
        val expense = expenseStorage[id]
        return if (expense == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(expense)
        }
    }

}