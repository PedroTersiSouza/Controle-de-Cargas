package com.example.controledecargas

// ==============================================
// DashboardActivity.kt — Tela de resumo do estoque
// ==============================================

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.title = "Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = DatabaseHelper(this)
        val stats = db.getEstatisticas()

        findViewById<TextView>(R.id.txtTotalProdutos).text =
            stats["total"].toString()
        findViewById<TextView>(R.id.txtTotalItens).text =
            stats["itensTotal"].toString()
        findViewById<TextView>(R.id.txtPesoTotal).text =
            String.format("%.1f kg", stats["pesoTotal"] as Double)
        findViewById<TextView>(R.id.txtNormais).text =
            stats["normais"].toString()
        findViewById<TextView>(R.id.txtBaixos).text =
            stats["baixos"].toString()
        findViewById<TextView>(R.id.txtCriticos).text =
            stats["criticos"].toString()
        findViewById<TextView>(R.id.txtZerados).text =
            stats["zerados"].toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
