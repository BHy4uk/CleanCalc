package com.neche.cleancalc

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val error: String? = null,
    val justEvaluated: Boolean = false
)

class CalculatorViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_EXPRESSION = "expression"
        private const val KEY_RESULT = "result"
        private const val KEY_ERROR = "error"
        private const val KEY_JUST_EVALUATED = "justEvaluated"
    }

    private val _uiState = MutableStateFlow(loadState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: String) {
        val current = _uiState.value
        val base = if (current.justEvaluated) "" else current.expression
        if (base.length >= CalculatorEngine.MAX_INPUT_LENGTH) return
        val expression = CalculatorEngine.appendDigit(base, digit)
        updateExpression(expression, justEvaluated = false)
    }

    fun onDecimal() {
        val current = _uiState.value
        val base = if (current.justEvaluated) "" else current.expression
        if (base.length >= CalculatorEngine.MAX_INPUT_LENGTH) return
        val expression = CalculatorEngine.appendDecimal(base)
        updateExpression(expression, justEvaluated = false)
    }

    fun onOperator(operator: String) {
        val current = _uiState.value
        val base = when {
            current.expression.isNotBlank() -> current.expression
            current.justEvaluated -> current.result
            else -> ""
        }
        val expression = CalculatorEngine.appendOperator(base, operator)
        updateExpression(expression, justEvaluated = false)
    }

    fun onClear() {
        updateState(CalculatorUiState())
    }

    fun onDelete() {
        val expression = CalculatorEngine.deleteLast(_uiState.value.expression)
        updateExpression(expression, justEvaluated = false)
    }

    fun onEquals() {
        val current = _uiState.value
        if (current.expression.isBlank()) return
        val evaluation = CalculatorEngine.evaluate(current.expression)
        if (evaluation.error != null) {
            updateState(current.copy(error = evaluation.error, justEvaluated = false))
            return
        }
        if (evaluation.result != null) {
            updateState(
                current.copy(
                    expression = evaluation.result,
                    result = evaluation.result,
                    error = null,
                    justEvaluated = true
                )
            )
        }
    }

    private fun updateExpression(expression: String, justEvaluated: Boolean) {
        val current = _uiState.value
        val preview = calculatePreview(expression, current.result)
        updateState(
            current.copy(
                expression = expression,
                result = preview.result,
                error = preview.error,
                justEvaluated = justEvaluated
            )
        )
    }

    private fun calculatePreview(expression: String, fallbackResult: String): PreviewResult {
        if (expression.isBlank()) {
            return PreviewResult("0", null)
        }
        if (!CalculatorEngine.isExpressionComplete(expression)) {
            return PreviewResult(fallbackResult, null)
        }
        val evaluation = CalculatorEngine.evaluate(expression)
        return when {
            evaluation.error != null -> PreviewResult(fallbackResult, evaluation.error)
            evaluation.result != null -> PreviewResult(evaluation.result, null)
            else -> PreviewResult(fallbackResult, null)
        }
    }

    private fun updateState(state: CalculatorUiState) {
        _uiState.value = state
        savedStateHandle[KEY_EXPRESSION] = state.expression
        savedStateHandle[KEY_RESULT] = state.result
        savedStateHandle[KEY_ERROR] = state.error
        savedStateHandle[KEY_JUST_EVALUATED] = state.justEvaluated
    }

    private fun loadState(): CalculatorUiState {
        val expression = savedStateHandle.get<String>(KEY_EXPRESSION) ?: ""
        val result = savedStateHandle.get<String>(KEY_RESULT) ?: "0"
        val error = savedStateHandle.get<String>(KEY_ERROR)
        val justEvaluated = savedStateHandle.get<Boolean>(KEY_JUST_EVALUATED) ?: false
        return CalculatorUiState(expression, result, error, justEvaluated)
    }

    private data class PreviewResult(val result: String, val error: String?)
}
