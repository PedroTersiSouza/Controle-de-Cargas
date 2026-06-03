package com.example.controledecargas

// ==============================================
// MainActivity.kt — Tela principal com busca e ordenação
// ==============================================

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ProdutoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtBusca: EditText
    private lateinit var txtTotal: TextView
    private lateinit var txtVazio: TextView
    private lateinit var rootView: View

    private var ordenacaoAtual = "categoria, nome"
    private var buscaAtual = ""
    private var ultimoProdutoDeletado: Produto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db           = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        edtBusca     = findViewById(R.id.edtBusca)
        txtTotal     = findViewById(R.id.txtTotal)
        txtVazio     = findViewById(R.id.txtVazio)
        rootView     = findViewById(R.id.rootLayout)

        // Adapter com lambdas Kotlin
        adapter = ProdutoAdapter(mutableListOf(),
            onEditar  = { produto -> abrirEditar(produto) },
            onDeletar = { produto -> confirmarDelete(produto) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // FAB — Novo Produto
        findViewById<FloatingActionButton>(R.id.fabAdicionar).setOnClickListener {
            startActivity(Intent(this, FormProdutoActivity::class.java))
        }

        // Botão Dashboard
        findViewById<Button>(R.id.btnDashboard).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        // Campo de busca
        edtBusca.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                buscaAtual = s.toString()
                carregarProdutos()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Botão de ordenação
        findViewById<ImageButton>(R.id.btnOrdenar).setOnClickListener { mostrarMenuOrdenacao(it) }

        carregarProdutos()
    }

    override fun onResume() {
        super.onResume()
        carregarProdutos()
    }

    private fun carregarProdutos() {
        val lista = db.listarProdutos(buscaAtual, ordenacaoAtual)
        if (lista.isEmpty()) {
            txtVazio.visibility      = View.VISIBLE
            recyclerView.visibility  = View.GONE
            txtTotal.text = "Nenhum produto encontrado"
        } else {
            txtVazio.visibility      = View.GONE
            recyclerView.visibility  = View.VISIBLE
            adapter.atualizarLista(lista)
            txtTotal.text = "${lista.size} produto(s) no estoque"
        }
    }

    private fun abrirEditar(produto: Produto) {
        val intent = Intent(this, FormProdutoActivity::class.java).apply {
            putExtra("id",          produto.id)
            putExtra("nome",        produto.nome)
            putExtra("categoria",   produto.categoria)
            putExtra("quantidade",  produto.quantidade)
            putExtra("unidade",     produto.unidade)
            putExtra("peso",        produto.pesoKg)
            putExtra("localizacao", produto.localizacao)
        }
        startActivity(intent)
    }

    private fun confirmarDelete(produto: Produto) {
        AlertDialog.Builder(this)
            .setTitle("Remover produto")
            .setMessage("Deseja remover \"${produto.nome}\" do estoque?")
            .setPositiveButton("Remover") { _, _ ->
                if (db.deletarProduto(produto.id)) {
                    ultimoProdutoDeletado = produto
                    carregarProdutos()
                    // Snackbar com botão DESFAZER
                    Snackbar.make(rootView, "\"${produto.nome}\" removido", Snackbar.LENGTH_LONG)
                        .setAction("DESFAZER") {
                            ultimoProdutoDeletado?.let { p ->
                                db.adicionarProduto(p)
                                carregarProdutos()
                                Snackbar.make(rootView, "Produto restaurado!", Snackbar.LENGTH_SHORT).show()
                            }
                        }.show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarMenuOrdenacao(view: View) {
        val opcoes = arrayOf("Nome (A-Z)", "Categoria", "Menor quantidade", "Maior quantidade")
        val ordenacoes = arrayOf("nome", "categoria, nome", "quantidade, nome", "quantidade DESC, nome")

        AlertDialog.Builder(this)
            .setTitle("Ordenar por")
            .setItems(opcoes) { _, which ->
                ordenacaoAtual = ordenacoes[which]
                carregarProdutos()
            }.show()
    }
}
