package com.example.controledecargas

// ==============================================
// Produto.kt — Data class do modelo
// ==============================================

data class Produto(
    val id: Int = 0,
    val nome: String = "",
    val categoria: String = "",
    val quantidade: Int = 0,
    val unidade: String = "un",
    val pesoKg: Double = 0.0,
    val localizacao: String = "",
    val dataAtualizacao: String = ""
) {
    fun getStatus(): String = when {
        quantidade == 0  -> "ZERADO"
        quantidade <= 10 -> "CRÍTICO"
        quantidade <= 30 -> "BAIXO"
        else             -> "NORMAL"
    }
}
