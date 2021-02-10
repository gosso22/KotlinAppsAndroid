package com.kassim.calculator_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.kassim.calculator_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var oper: String = ""
    private var doneOper: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val rooView = binding.root

        setContentView(rooView)
    }

    fun buNumberEvent(view: View) {

        if (binding.showNumbersAndOps.text.toString() == "0" || doneOper) {
            binding.showNumbersAndOps.text.clear()
            doneOper = false
        }
        var numberOfButtonSelected: String = binding.showNumbersAndOps.text.toString()

        when (view.id) {
            binding.bu0.id -> {
                numberOfButtonSelected += "0"
            }

            binding.bu1.id -> {
                numberOfButtonSelected += "1"
            }

            binding.bu2.id -> {
                numberOfButtonSelected += "2"
            }

            binding.bu3.id -> {
                numberOfButtonSelected += "3"
            }

            binding.bu4.id -> {
                numberOfButtonSelected += "4"
            }

            binding.bu5.id -> {
                numberOfButtonSelected += "5"
            }

            binding.bu6.id -> {
                numberOfButtonSelected += "6"
            }

            binding.bu7.id -> {
                numberOfButtonSelected += "7"
            }

            binding.bu8.id -> {
                numberOfButtonSelected += "8"
            }

            binding.bu9.id -> {
                numberOfButtonSelected += "9"
            }

            binding.buDot.id -> {
                if (!numberOfButtonSelected.contains("."))
                    numberOfButtonSelected += "."
            }

            binding.buPlusMinus.id -> {
                if (numberOfButtonSelected.contains("-")) {
                     numberOfButtonSelected = numberOfButtonSelected.substring(1 until numberOfButtonSelected.length)
                } else {
                    numberOfButtonSelected = "-$numberOfButtonSelected"
                }
            }
        }

        binding.showNumbersAndOps.setText(numberOfButtonSelected)


    }

    fun buOpEvent(view: View) {

        var numberForOps: String =  binding.showNumbersAndOps.text.toString()
        doneOper = false

        when(view.id) {

            binding.buDiv.id -> {
                val asciiDiv = 247
                numberForOps += " ${asciiDiv.toChar()} "
                oper = "${asciiDiv.toChar()}"
            }

            binding.buMul.id -> {

                numberForOps += " x "
                oper = "x"

            }

            binding.buMinus.id -> {

                numberForOps += " - "
                oper = "-"

            }

            binding.buPlus.id -> {

                numberForOps += " + "
                oper = "+"

            }

        }

        binding.showNumbersAndOps.setText(numberForOps)

    }

    fun evaluationEvent(view: View) {

        val evaluationExpr: String = binding.showNumbersAndOps.text.toString()
        var resultOfOp: Double = 0.00
        var resultToDisplay: String = ""

        if (evaluationExpr.contains(oper)) {
            val operands = evaluationExpr.split(" $oper ")
            if (oper == "%") {
                resultOfOp = percentage(operands[0].replace("%", "").toDouble())
            } else {
                val leftHSD = operands[0].toDouble()
                val rightHSD = operands[1].toDouble()
                resultOfOp = operateOn(leftHSD, rightHSD, oper)
            }
            doneOper = true
        }

        if ((resultOfOp % 1) == 0.0 ) {
            resultToDisplay = resultOfOp.toInt().toString()
        } else {
            resultToDisplay = resultOfOp.toString()
        }

        binding.showNumbersAndOps.setText(resultToDisplay)

    }

    private fun operateOn(leftHSD: Double, rightHSD: Double, oper: String): Double {

        var results: Double = 0.00
        if (oper == (247.toChar().toString())) {

           results = leftHSD / rightHSD

        } else if (oper == "x") {

            results = leftHSD * rightHSD

        } else if (oper == "-") {

            results = leftHSD - rightHSD

        } else if (oper == "+") {

            results = leftHSD + rightHSD

        }

        return results
    }

    fun clearNumbers(view: View) {

        binding.showNumbersAndOps.text.clear()
        binding.showNumbersAndOps.setText("0")
    }

    fun calculatePercentage(view: View) {

        var value: String = binding.showNumbersAndOps.text.toString()
        value += "%"
        oper = "%"

        binding.showNumbersAndOps.setText(value)

    }

    fun percentage(number: Double): Double {
        return number / 100
    }

}