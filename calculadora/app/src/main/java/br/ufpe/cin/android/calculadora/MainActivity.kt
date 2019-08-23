package br.ufpe.cin.android.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_info.setText(savedInstanceState?.getCharSequence("result"))
        text_calc.setText(savedInstanceState?.getCharSequence("expression"))

        btn_0.setOnClickListener(this)
        btn_1.setOnClickListener(this)
        btn_2.setOnClickListener(this)
        btn_3.setOnClickListener(this)
        btn_4.setOnClickListener(this)
        btn_5.setOnClickListener(this)
        btn_6.setOnClickListener(this)
        btn_7.setOnClickListener(this)
        btn_8.setOnClickListener(this)
        btn_9.setOnClickListener(this)
        btn_Add.setOnClickListener(this)
        btn_Divide.setOnClickListener(this)
        btn_Multiply.setOnClickListener(this)
        btn_Power.setOnClickListener(this)
        btn_Subtract.setOnClickListener(this)
        btn_LParen.setOnClickListener(this)
        btn_RParen.setOnClickListener(this)
        btn_Dot.setOnClickListener(this)
        btn_Equal.setOnClickListener(this)
        btn_Clear.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_Add, btn_Subtract, btn_Multiply, btn_Power, btn_Dot, btn_RParen, btn_LParen, btn_Divide -> {
                val button = p0 as Button
                val expression = text_calc.text.toString().plus(button.text)
                text_calc.setText(expression)
            }
            btn_Clear -> {
                text_calc.text.clear()
                text_info.setText("")
            }
            btn_Equal -> {
                val expression = text_calc.text.toString()
                val result = eval(expression)
                if (result === -0.0) {
                    Toast.makeText(this, "Expressão inválida!", Toast.LENGTH_LONG).show()
                } else {
                    text_info.setText(result.toString())
                    text_calc.text.clear()
                }

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putString("expression",text_calc.text.toString())
        outState.putString("result", text_info.text.toString())
        super.onSaveInstanceState(outState, outPersistentState)
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) {
                    Toast.makeText(this@MainActivity, "Expressão inválida!", Toast.LENGTH_LONG).show()
                    text_calc.text.clear()
                }
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        Toast.makeText(this@MainActivity, "Expressão inválida!", Toast.LENGTH_LONG).show()
                        text_calc.text.clear()
                        return -0.0
//                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    Toast.makeText(this@MainActivity, "Expressão inválida!", Toast.LENGTH_LONG).show()
                    text_calc.text.clear()
                    return -0.0
//                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }



}
