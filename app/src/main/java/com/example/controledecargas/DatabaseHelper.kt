package com.example.controledecargas

// ==============================================
// DatabaseHelper.kt — Gerencia o banco SQLite
// ==============================================

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "controle_cargas.db", null, 1) {

    companion object {
        const val TABELA = "produtos"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABELA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                categoria TEXT NOT NULL,
                quantidade INTEGER NOT NULL DEFAULT 0,
                unidade TEXT NOT NULL DEFAULT 'un',
                peso_kg REAL DEFAULT 0.0,
                localizacao TEXT,
                data_atualizacao TEXT DEFAULT (datetime('now','localtime'))
            )
        """.trimIndent())

        inserirDadosIniciais(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABELA")
        onCreate(db)
    }

    private fun inserirDadosIniciais(db: SQLiteDatabase) {
        val dados = listOf(
            arrayOf("Caixa de Parafusos M8",  "Fixadores",  "150", "cx",  "12.50", "A-01"),
            arrayOf("Bobina de Cabo Elétrico", "Elétrico",   "30",  "un",  "180.00","B-03"),
            arrayOf("Saco de Cimento 50kg",    "Construção", "80",  "sc",  "50.00", "C-02"),
            arrayOf("Tambor de Óleo Lubrif.",  "Químicos",   "20",  "tb",  "200.00","D-01"),
            arrayOf("Pallet de Tijolos",       "Construção", "15",  "pl",  "800.00","C-01"),
            arrayOf("Rolo de Mangueira 3/4\"", "Hidráulico", "45",  "un",  "22.00", "B-01"),
            arrayOf("Caixa de Pregos 18x27",   "Fixadores",  "200", "cx",  "5.00",  "A-02"),
            arrayOf("Lona Plástica 4x6m",      "Proteção",   "60",  "un",  "3.50",  "E-01")
        )
        dados.forEach { d ->
            val cv = ContentValues().apply {
                put("nome", d[0]); put("categoria", d[1])
                put("quantidade", d[2].toInt()); put("unidade", d[3])
                put("peso_kg", d[4].toDouble()); put("localizacao", d[5])
            }
            db.insert(TABELA, null, cv)
        }
    }

    // ---- LISTAR (com busca e ordenação) ----
    fun listarProdutos(busca: String = "", ordenacao: String = "categoria, nome"): List<Produto> {
        val lista = mutableListOf<Produto>()
        val db = readableDatabase
        val cursor = if (busca.isBlank()) {
            db.query(TABELA, null, null, null, null, null, ordenacao)
        } else {
            db.query(TABELA, null,
                "nome LIKE ? OR categoria LIKE ?",
                arrayOf("%$busca%", "%$busca%"),
                null, null, ordenacao)
        }
        cursor.use {
            while (it.moveToNext()) {
                lista.add(Produto(
                    id             = it.getInt(it.getColumnIndexOrThrow("id")),
                    nome           = it.getString(it.getColumnIndexOrThrow("nome")),
                    categoria      = it.getString(it.getColumnIndexOrThrow("categoria")),
                    quantidade     = it.getInt(it.getColumnIndexOrThrow("quantidade")),
                    unidade        = it.getString(it.getColumnIndexOrThrow("unidade")),
                    pesoKg         = it.getDouble(it.getColumnIndexOrThrow("peso_kg")),
                    localizacao    = it.getString(it.getColumnIndexOrThrow("localizacao")),
                    dataAtualizacao= it.getString(it.getColumnIndexOrThrow("data_atualizacao"))
                ))
            }
        }
        db.close()
        return lista
    }

    // ---- ADICIONAR ----
    fun adicionarProduto(p: Produto): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome", p.nome); put("categoria", p.categoria)
            put("quantidade", p.quantidade); put("unidade", p.unidade)
            put("peso_kg", p.pesoKg); put("localizacao", p.localizacao)
            put("data_atualizacao", dataAtual())
        }
        val id = db.insert(TABELA, null, cv)
        db.close()
        return id != -1L
    }

    // ---- ATUALIZAR ----
    fun atualizarProduto(p: Produto): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome", p.nome); put("categoria", p.categoria)
            put("quantidade", p.quantidade); put("unidade", p.unidade)
            put("peso_kg", p.pesoKg); put("localizacao", p.localizacao)
            put("data_atualizacao", dataAtual())
        }
        val rows = db.update(TABELA, cv, "id = ?", arrayOf(p.id.toString()))
        db.close()
        return rows > 0
    }

    // ---- DELETAR ----
    fun deletarProduto(id: Int): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABELA, "id = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    // ---- ESTATÍSTICAS para o Dashboard ----
    fun getEstatisticas(): Map<String, Any> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABELA", null)
        var total = 0; var zerados = 0; var criticos = 0; var baixos = 0; var normais = 0
        var pesoTotal = 0.0; var itensTotal = 0
        cursor.use {
            while (it.moveToNext()) {
                total++
                val qtd  = it.getInt(it.getColumnIndexOrThrow("quantidade"))
                val peso = it.getDouble(it.getColumnIndexOrThrow("peso_kg"))
                pesoTotal  += qtd * peso
                itensTotal += qtd
                when {
                    qtd == 0  -> zerados++
                    qtd <= 10 -> criticos++
                    qtd <= 30 -> baixos++
                    else      -> normais++
                }
            }
        }
        db.close()
        return mapOf(
            "total" to total, "zerados" to zerados, "criticos" to criticos,
            "baixos" to baixos, "normais" to normais,
            "pesoTotal" to pesoTotal, "itensTotal" to itensTotal
        )
    }

    private fun dataAtual(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
