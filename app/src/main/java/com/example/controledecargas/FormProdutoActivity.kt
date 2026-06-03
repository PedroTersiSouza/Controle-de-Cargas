package com.example.controledecargas

// ==============================================
// FormProdutoActivity.kt
// Tela única para ADICIONAR e EDITAR produto
// Se receber "id" > 0, está editando. Senão, está adicionando.
// ==============================================

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class FormProdutoActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var edtNome: EditText
    private lateinit var edtCategoria: EditText
    private lateinit var edtQuantidade: EditText
    private lateinit var edtUnidade: EditText
    private lateinit var edtPeso: EditText
    private lateinit var edtLocal: EditText
    private lateinit var btnSalvar: Button

    private var produtoId = 0
    private val editando get() = produtoId > 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_produto)

        db            = DatabaseHelper(this)
        edtNome       = findViewById(R.id.edtNome)
        edtCategoria  = findViewById(R.id.edtCategoria)
        edtQuantidade = findViewById(R.id.edtQuantidade)
        edtUnidade    = findViewById(R.id.edtUnidade)
        edtPeso       = findViewById(R.id.edtPeso)
        edtLocal      = findViewById(R.id.edtLocal)
        btnSalvar     = findViewById(R.id.btnSalvar)

        // Recebe dados se for edição
        produtoId = intent.getIntExtra("id", 0)
        if (editando) {
            supportActionBar?.title = "Editar Produto"
            edtNome.setText(intent.getStringExtra("nome"))
            edtCategoria.setText(intent.getStringExtra("categoria"))
            edtQuantidade.setText(intent.getIntExtra("quantidade", 0).toString())
            edtUnidade.setText(intent.getStringExtra("unidade"))
            edtPeso.setText(intent.getDoubleExtra("peso", 0.0).toString())
            edtLocal.setText(intent.getStringExtra("localizacao"))
            btnSalvar.text = "Salvar Alterações"
        } else {
            supportActionBar?.title = "Novo Produto"
            btnSalvar.text = "Cadastrar no Banco"
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSalvar.setOnClickListener { salvar() }
        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }
    }

    private fun salvar() {
        val nome  = edtNome.getText().toString().trim()
        val cat   = edtCategoria.getText().toString().trim()
        val qtdS  = edtQuantidade.getText().toString().trim()
        val unid  = edtUnidade.getText().toString().trim().ifEmpty { "un" }
        val pesoS = edtPeso.getText().toString().trim()
        val local = edtLocal.getText().toString().trim()

        if (nome.isEmpty() || cat.isEmpty() || qtdS.isEmpty()) {
            Snackbar.make(findViewById(R.id.formRoot),
                "Preencha nome, categoria e quantidade", Snackbar.LENGTH_SHORT).show()
            return
        }

        val produto = Produto(
            id          = produtoId,
            nome        = nome,
            categoria   = cat,
            quantidade  = qtdS.toInt(),
            unidade     = unid,
            pesoKg      = pesoS.toDoubleOrNull() ?: 0.0,
            localizacao = local
        )

        val ok = if (editando) db.atualizarProduto(produto) else db.adicionarProduto(produto)

        if (ok) {
            val msg = if (editando) "Produto atualizado!" else "Produto cadastrado!"
            Toast.makeText(this, "✅ $msg", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Snackbar.make(findViewById(R.id.formRoot), "Erro ao salvar", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
