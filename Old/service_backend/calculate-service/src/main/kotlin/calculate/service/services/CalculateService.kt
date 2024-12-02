package org.example.calculate.service.services

import net.objecthunter.exp4j.ExpressionBuilder

class CalculateService {

    fun calculaFuncao(messageBody: String): MessageService {
        return try {
            val expression = ExpressionBuilder(messageBody).build()
            val result = expression.evaluate()
            val response = "$messageBody = $result"
            MessageService("calcula_funcao", response)
        } catch (e: Exception) {
            MessageService("calcula_funcao", "Erro ao calcular a expressão: ${e.message}")
        }
    }
}