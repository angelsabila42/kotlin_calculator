package com.github.tabithachebet.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.github.tabithachebet.myapplication.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    binding.working.append(view.text)
                    canAddDecimal = false
                }
            } else {
                binding.working.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            binding.working.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        binding.working.text = ""
        binding.display.text = "0"
    }

    fun backSpaceAction(view: View) {
        val length = binding.working.length()
        if (length > 0) {
            binding.working.text = binding.working.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        binding.display.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) {
            return ""
        }

        val timesDivisionResult = timesDivisionCalculate(digitsOperators) ?: return "Error"

        val result = addSubtractCalculate(timesDivisionResult)
        return DecimalFormat("#.#####").format(result)
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>? {
        val list = passedList.toMutableList()
        var i = 0
        while (i < list.size) {
            if (list[i] is Char && (list[i] == 'x' || list[i] == '/')) {
                val operator = list[i] as Char
                val prevDigit = list[i - 1] as Float
                val nextDigit = list[i + 1] as Float

                if (operator == '/' && nextDigit == 0f) {
                    return null // Division by zero
                }

                val result = if (operator == 'x') prevDigit * nextDigit else prevDigit / nextDigit
                list[i - 1] = result
                list.removeAt(i) // Remove operator
                list.removeAt(i) // Remove next digit
                i = 0 // Restart scan
            } else {
                i++
            }
        }
        return list
    }
    
    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        if (passedList.isEmpty()) return 0f
        var result = passedList[0] as Float
        for (i in 1 until passedList.size step 2) {
            val operator = passedList[i] as Char
            val nextDigit = passedList[i + 1] as Float
            when (operator) {
                '+' -> result += nextDigit
                '-' -> result -= nextDigit
            }
        }
        return result
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.working.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if(currentDigit.isNotEmpty()){
                    list.add(currentDigit.toFloat())
                }
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }
        return list
    }
}