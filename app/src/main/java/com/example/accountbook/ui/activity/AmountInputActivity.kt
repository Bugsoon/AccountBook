package com.example.accountbook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.accountbook.R

class AmountInputActivity : AppCompatActivity() {
    private var amountStr = "0"
    private var expression = ""
    private var lastResult = 0.0
    private var hasDecimal = false
    private var recordType = "expense"

    private lateinit var tvAmount: TextView
    private lateinit var tvExpression: TextView
    private lateinit var tvType: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amount_input)

        recordType = intent.getStringExtra("type") ?: "expense"

        tvAmount = findViewById(R.id.tv_amount)
        tvExpression = findViewById(R.id.tv_expression)
        tvType = findViewById(R.id.tv_type)

        tvType.text = if (recordType == "expense") "支出" else "收入"

        findViewById<android.view.View>(R.id.toolbar).setOnClickListener { finish() }

        setupKeyboard()
    }

    private fun setupKeyboard() {
        val buttons = mapOf(
            R.id.btn_0 to "0", R.id.btn_1 to "1", R.id.btn_2 to "2",
            R.id.btn_3 to "3", R.id.btn_4 to "4", R.id.btn_5 to "5",
            R.id.btn_6 to "6", R.id.btn_7 to "7", R.id.btn_8 to "8",
            R.id.btn_9 to "9", R.id.btn_dot to "."
        )

        buttons.forEach { (id, value) ->
            findViewById<TextView>(id).setOnClickListener {
                onNumberInput(value)
            }
        }

        findViewById<TextView>(R.id.btn_plus).setOnClickListener {
            onOperator("+")
        }

        findViewById<TextView>(R.id.btn_delete).setOnClickListener {
            onDelete()
        }

        findViewById<TextView>(R.id.btn_confirm).setOnClickListener {
            onConfirm()
        }
    }

    private fun onNumberInput(number: String) {
        if (amountStr == "0" && number != ".") {
            amountStr = number
        } else if (number == ".") {
            if (!hasDecimal) {
                amountStr += "."
                hasDecimal = true
            }
        } else {
            amountStr += number
        }
        updateDisplay()
    }

    private fun onOperator(operator: String) {
        val currentAmount = amountStr.toDoubleOrNull() ?: 0.0
        if (expression.isEmpty()) {
            expression = "$amountStr $operator "
            lastResult = currentAmount
        } else {
            lastResult = calculateExpression(expression + amountStr)
            expression = "$lastResult $operator "
        }
        amountStr = "0"
        hasDecimal = false
        updateDisplay()
    }

    private fun onDelete() {
        if (amountStr.length > 1) {
            amountStr = amountStr.dropLast(1)
            if (!amountStr.contains(".")) hasDecimal = false
        } else {
            amountStr = "0"
            hasDecimal = false
        }
        updateDisplay()
    }

    private fun calculateExpression(expr: String): Double {
        return try {
            val parts = expr.trim().split(" ")
            if (parts.size < 3) return parts[0].toDoubleOrNull() ?: 0.0

            var result = parts[0].toDouble()
            var i = 1
            while (i < parts.size - 1) {
                val op = parts[i]
                val num = parts[i + 1].toDouble()
                when (op) {
                    "+" -> result += num
                    "-" -> result -= num
                }
                i += 2
            }
            result
        } catch (e: Exception) {
            0.0
        }
    }

    private fun updateDisplay() {
        tvAmount.text = amountStr
        tvExpression.text = expression + if (expression.isNotEmpty()) amountStr else ""
    }

    private fun onConfirm() {
        val finalAmount = if (expression.isNotEmpty()) {
            calculateExpression(expression + amountStr)
        } else {
            amountStr.toDoubleOrNull() ?: 0.0
        }

        if (finalAmount > 0) {
            val resultIntent = Intent()
            resultIntent.putExtra("amount", finalAmount)
            resultIntent.putExtra("type", recordType)
            setResult(RESULT_OK, resultIntent)
        }
        finish()
    }
}