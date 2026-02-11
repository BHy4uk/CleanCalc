package com.neche.cleancalc

import kotlin.math.roundToLong

data class EvaluationResult(val result: String?, val error: String? = null)

object CalculatorEngine {
    const val MAX_INPUT_LENGTH = 24
    private val operators = setOf('+', '-', '×', '÷')

    fun formatExpression(expression: String): String {
        if (expression.isBlank()) return "0"
        return expression
            .replace(Regex("""([+\-×÷])"""), " $1 ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    fun appendDigit(expression: String, digit: String): String {
        if (digit.length != 1 || !digit[0].isDigit()) return expression
        val currentSegment = lastNumberSegment(expression)
        if (currentSegment == "0") {
            val prefix = expression.dropLast(currentSegment.length)
            return prefix + digit
        }
        return expression + digit
    }

    fun appendDecimal(expression: String): String {
        val lastChar = expression.lastOrNull()
        val currentSegment = lastNumberSegment(expression)
        if (currentSegment.contains(".")) return expression
        if (expression.isBlank() || (lastChar != null && isOperator(lastChar))) {
            return expression + "0."
        }
        return expression + "."
    }

    fun appendOperator(expression: String, operator: String): String {
        if (operator.length != 1 || !operators.contains(operator[0])) return expression
        if (expression.isBlank()) return expression
        val lastChar = expression.last()
        return if (isOperator(lastChar)) {
            expression.dropLast(1) + operator
        } else {
            expression + operator
        }
    }

    fun deleteLast(expression: String): String {
        return if (expression.isNotEmpty()) expression.dropLast(1) else ""
    }

    fun isExpressionComplete(expression: String): Boolean {
        if (expression.isBlank()) return false
        val lastChar = expression.last()
        return !isOperator(lastChar) && lastChar != '.'
    }

    fun evaluate(expression: String): EvaluationResult {
        val sanitized = sanitizeExpression(expression)
        if (sanitized.isBlank()) return EvaluationResult(null)

        val tokenRegex = Regex("""(\d*\.\d+|\d+\.?\d*|[+\-×÷])""")
        val rawTokens = tokenRegex.findAll(sanitized).map { it.value }.toList()
        if (rawTokens.isEmpty()) return EvaluationResult(null)

        val tokens = rawTokens.map {
            when (it) {
                "×" -> "*"
                "÷" -> "/"
                else -> it
            }
        }

        if (!tokens.first().isNumberToken() || !tokens.last().isNumberToken()) {
            return EvaluationResult(null)
        }

        val firstPass = mutableListOf<String>()
        var index = 0
        while (index < tokens.size) {
            val token = tokens[index]
            if (token == "*" || token == "/") {
                val prevToken = firstPass.removeLastOrNull()
                val nextToken = tokens.getOrNull(index + 1)
                if (prevToken == null || nextToken == null || !prevToken.isNumberToken() || !nextToken.isNumberToken()) {
                    return EvaluationResult(null)
                }
                val prevValue = prevToken.toDouble()
                val nextValue = nextToken.toDouble()
                if (token == "/" && nextValue == 0.0) {
                    return EvaluationResult(null, "Cannot divide by zero")
                }
                val computed = if (token == "*") prevValue * nextValue else prevValue / nextValue
                if (!computed.isFinite()) return EvaluationResult(null, "Invalid input")
                firstPass.add(computed.toString())
                index += 2
            } else {
                firstPass.add(token)
                index += 1
            }
        }

        var total = firstPass.first().toDoubleOrNull() ?: return EvaluationResult(null)
        var i = 1
        while (i < firstPass.size) {
            val operator = firstPass[i]
            val nextToken = firstPass.getOrNull(i + 1) ?: return EvaluationResult(null)
            val nextValue = nextToken.toDoubleOrNull() ?: return EvaluationResult(null)
            when (operator) {
                "+" -> total += nextValue
                "-" -> total -= nextValue
            }
            i += 2
        }

        if (!total.isFinite()) return EvaluationResult(null, "Invalid input")
        return EvaluationResult(formatResult(total))
    }

    private fun sanitizeExpression(expression: String): String {
        var sanitized = expression.trim()
        while (sanitized.isNotBlank() && isOperator(sanitized.last())) {
            sanitized = sanitized.dropLast(1)
        }
        if (sanitized.endsWith('.')) {
            sanitized = sanitized.dropLast(1)
        }
        return sanitized
    }

    private fun formatResult(value: Double): String {
        val rounded = (value * 1e10).roundToLong() / 1e10
        var text = rounded.toString()
        if (text.contains(".")) {
            text = text.trimEnd('0').trimEnd('.')
        }
        return text
    }

    private fun lastNumberSegment(expression: String): String {
        return expression.split(Regex("""[+\-×÷]""")).lastOrNull() ?: ""
    }

    private fun isOperator(char: Char): Boolean = operators.contains(char)

    private fun String.isNumberToken(): Boolean = this.toDoubleOrNull() != null
}
