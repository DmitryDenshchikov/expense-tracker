package denshchikov.dmitry.controller

import denshchikov.dmitry.mapper.ExpenseMapper
import denshchikov.dmitry.model.Category.FOOD
import denshchikov.dmitry.model.Category.LEISURE
import denshchikov.dmitry.model.domain.Expense
import denshchikov.dmitry.model.input.CreateExpenseRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.util.*

@WebMvcTest(ExpenseController::class)
class ExpenseControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var expenseMapper: ExpenseMapper

    @MockBean
    lateinit var expenseStorage: MutableMap<UUID, Expense>

    @Nested
    inner class CreateExpense {

        @Test
        fun shouldCreateExpenseWhenAllDataPassed() {
            // Given
            val request = CreateExpenseRequest(
                BigDecimal("10.99"), FOOD, "Test description", 1725051828
            )

            val expenseId = UUID.randomUUID()
            val expense = Expense(
                expenseId, BigDecimal("10.99"), FOOD, "Test description", 1725051828
            )

            `when`(expenseMapper.toDomain(request)).thenReturn(expense)

            // When & Then
            mockMvc.post("/expenses") {
                contentType = APPLICATION_JSON
                content = """
                {
                  "amount": 10.99,
                  "category": "FOOD",
                  "description": "Test description",
                  "date": 1725051828
                }
                """.trimIndent()
            }.andExpect {
                status { isCreated() }
                header {
                    string(LOCATION, "http://localhost/expenses/${expenseId}")
                }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "id": $expenseId,
                          "amount": 10.99,
                          "category": "FOOD",
                          "description": "Test description",
                          "date": 1725051828
                        }
                        """.trimIndent()
                    )
                }
            }
        }

        @Test
        fun shouldCreateExpenseWhenNoDescription() {
            // Given
            val request = CreateExpenseRequest(
                BigDecimal("10.99"), FOOD, null, 1725051828
            )

            val expenseId = UUID.randomUUID()
            val expense = Expense(
                expenseId, BigDecimal("10.99"), FOOD, null, 1725051828
            )

            `when`(expenseMapper.toDomain(request)).thenReturn(expense)

            // When & Then
            mockMvc.post("/expenses") {
                contentType = APPLICATION_JSON
                content = """
                {
                  "amount": 10.99,
                  "category": "FOOD",
                  "date": 1725051828
                }
                """.trimIndent()
            }.andExpect {
                status { isCreated() }
                header {
                    string(LOCATION, "http://localhost/expenses/${expenseId}")
                }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "id": $expenseId,
                          "amount": 10.99,
                          "category": "FOOD",
                          "date": 1725051828
                        }
                        """.trimIndent()
                    )
                }
            }
        }

        @ParameterizedTest
        @MethodSource("denshchikov.dmitry.controller.ExpenseControllerTest#inappropriateContentTypes")
        fun shouldReturn415WhenInappropriateContentType(mediaType: MediaType) {
            // Given & When & Then
            mockMvc.post("/expenses") {
                contentType = mediaType
                content = """
                {
                  "amount": 10.99,
                  "category": "FOOD",
                  "description": "Test description",
                  "date": 1725051828
                }
                """.trimIndent()
            }.andExpect {
                status { isUnsupportedMediaType() }
            }
        }

        @Test
        fun shouldReturn400WhenNoAmount() {
            // Given & When & Then
            mockMvc.post("/expenses") {
                contentType = APPLICATION_JSON
                content = """
                {
                  "category": "FOOD",
                  "description": "Test description",
                  "date": 1725051828
                }
                """.trimIndent()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun shouldReturn400WhenNoCategory() {
            // Given & When & Then
            mockMvc.post("/expenses") {
                contentType = APPLICATION_JSON
                content = """
                {
                  "amount": 10.99,
                  "description": "Test description",
                  "date": 1725051828
                }
                """.trimIndent()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun shouldReturn400WhenNoDate() {
            // Given & When & Then
            mockMvc.post("/expenses") {
                contentType = APPLICATION_JSON
                content = """
                {
                  "amount": 10.99,
                  "category": "FOOD",
                  "description": "Test description",
                }
                """.trimIndent()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun shouldReturn400WhenAmountIsNegative() {
            // Given & When & Then
            mockMvc.post("/expenses") {
                contentType = APPLICATION_JSON
                content = """
                {
                  "amount": -10.99,
                  "category": "FOOD",
                  "description": "Test description",
                  "date": 1725051828
                }
                """.trimIndent()
            }.andExpect {
                status { isBadRequest() }
            }
        }

    }

    @Nested
    inner class GetExpense {

        @Test
        fun shouldReturnExpenseWhenExpenseExists() {
            // Given
            val expenseId = UUID.randomUUID()
            val expense = Expense(
                expenseId, BigDecimal("10.99"), FOOD, "Test description", 1725051828
            )

            `when`(expenseStorage.get(expenseId)).thenReturn(expense)

            // When & Then
            mockMvc.get("/expenses/${expenseId}") {
                contentType = APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        {
                          "id": $expenseId,
                          "amount": 10.99,
                          "category": "FOOD",
                          "date": 1725051828
                        }
                        """.trimIndent()
                    )
                }
            }
        }

        @Test
        fun shouldReturn404WhenNoExpenseFound() {
            // Given
            val expenseId = UUID.randomUUID()

            `when`(expenseStorage.get(expenseId)).thenReturn(null)

            // When & Then
            mockMvc.get("/expenses/${expenseId}") {
                contentType = APPLICATION_JSON
            }.andExpect {
                status { isNotFound() }
            }
        }

    }

    @Nested
    inner class GetExpenses {

        @Test
        fun shouldReturnExpensesWhenExpensesExist() {
            // Given
            val firstExpenseId = UUID.randomUUID()
            val firstExpense = Expense(
                firstExpenseId, BigDecimal("10.99"), FOOD, "First test description", 1725051828
            )

            val secondExpenseId = UUID.randomUUID()
            val secondExpense = Expense(
                secondExpenseId, BigDecimal("99.99"), LEISURE, "Second test description", 1735051828
            )

            `when`(expenseStorage.values).thenReturn(mutableListOf(firstExpense, secondExpense))

            // When & Then
            mockMvc.get("/expenses") {
                contentType = APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        [
                            {
                                "amount": 10.99,
                                "category": "FOOD",
                                "description": "First test description",
                                "date": 1725051828
                            },
                            {
                                "amount": 99.99,
                                "category": "LEISURE",
                                "description": "Second test description",
                                "date": 1735051828
                            }
                        ]
                        """.trimIndent()
                    )
                }
            }
        }

        @Test
        fun shouldReturnEmptyListWhenNoExpensesFound() {
            // Given
            `when`(expenseStorage.values).thenReturn(mutableListOf())

            // When & Then
            mockMvc.get("/expenses") {
                contentType = APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(
                        """
                        []
                        """.trimIndent()
                    )
                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun inappropriateContentTypes() = listOf(TEXT_PLAIN, APPLICATION_XML, ALL)
    }

}