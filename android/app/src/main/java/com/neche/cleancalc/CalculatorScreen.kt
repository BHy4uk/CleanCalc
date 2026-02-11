package com.neche.cleancalc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val displayResult = state.error ?: state.result

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.32f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = CalculatorEngine.formatExpression(state.expression),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = displayResult,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier
                .weight(0.68f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton("sin", { viewModel.onUnary(UnaryOperation.SIN) }, modifier = Modifier.weight(1f))
                CalculatorButton("cos", { viewModel.onUnary(UnaryOperation.COS) }, modifier = Modifier.weight(1f))
                CalculatorButton("tan", { viewModel.onUnary(UnaryOperation.TAN) }, modifier = Modifier.weight(1f))
                CalculatorButton("ln", { viewModel.onUnary(UnaryOperation.LN) }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton("log", { viewModel.onUnary(UnaryOperation.LOG10) }, modifier = Modifier.weight(1f))
                CalculatorButton("√", { viewModel.onUnary(UnaryOperation.SQRT) }, modifier = Modifier.weight(1f))
                CalculatorButton("x²", { viewModel.onUnary(UnaryOperation.SQUARE) }, modifier = Modifier.weight(1f))
                CalculatorButton("^", { viewModel.onOperator("^") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton("π", viewModel::onPi, modifier = Modifier.weight(1f))
                CalculatorButton("e", viewModel::onEuler, modifier = Modifier.weight(1f))
                CalculatorButton("%", { viewModel.onUnary(UnaryOperation.PERCENT) }, modifier = Modifier.weight(1f))
                CalculatorButton("1/x", { viewModel.onUnary(UnaryOperation.RECIPROCAL) }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(
                    label = "C",
                    onClick = viewModel::onClear,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                CalculatorButton(
                    label = "⌫",
                    onClick = viewModel::onDelete,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                CalculatorButton(
                    label = "÷",
                    onClick = { viewModel.onOperator("÷") },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                CalculatorButton(
                    label = "×",
                    onClick = { viewModel.onOperator("×") },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("7", { viewModel.onDigit("7") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton("8", { viewModel.onDigit("8") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton("9", { viewModel.onDigit("9") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton(
                    label = "-",
                    onClick = { viewModel.onOperator("-") },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("4", { viewModel.onDigit("4") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton("5", { viewModel.onDigit("5") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton("6", { viewModel.onDigit("6") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton(
                    label = "+",
                    onClick = { viewModel.onOperator("+") },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("1", { viewModel.onDigit("1") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton("2", { viewModel.onDigit("2") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton("3", { viewModel.onDigit("3") }, modifier = Modifier.fillMaxWidth().weight(1f))
                CalculatorButton(
                    label = "=",
                    onClick = viewModel::onEquals,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(
                    label = "0",
                    onClick = { viewModel.onDigit("0") },
                    modifier = Modifier.fillMaxWidth().weight(2f)
                )
                CalculatorButton(
                    label = ".",
                    onClick = viewModel::onDecimal,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
