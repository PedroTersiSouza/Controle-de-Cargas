package com.estoque.controle;

// ==============================================
// DatabaseHelper.java
// Gerencia o banco de dados SQLite local
// Substitui completamente o ApiHelper.java
// ==============================================

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_NOME    = "controle_cargas.db";
    private static final int    BANCO_VERSAO  = 1;
    private static final String TABELA        = "produtos";

    public DatabaseHelper(Context context) {
        super(context, BANCO_NOME, null, BANCO_VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Cria a tabela
        String sql = "CREATE TABLE " + TABELA + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "categoria TEXT NOT NULL," +
                "quantidade INTEGER NOT NULL DEFAULT 0," +
                "unidade TEXT NOT NULL DEFAULT 'un'," +
                "peso_kg REAL DEFAULT 0.0," +
                "localizacao TEXT," +
                "data_atualizacao TEXT DEFAULT (datetime('now','localtime'))" +
                ")";
        db.execSQL(sql);

        // Insere dados iniciais de exemplo
        inserirDadosIniciais(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    private void inserirDadosIniciais(SQLiteDatabase db) {
        String[][] dados = {
                {"Caixa de Parafusos M8",   "Fixadores",   "150", "cx",  "12.50", "A-01"},
                {"Bobina de Cabo Elétrico",  "Elétrico",    "30",  "un",  "180.00","B-03"},
                {"Saco de Cimento 50kg",     "Construção",  "80",  "sc",  "50.00", "C-02"},
                {"Tambor de Óleo Lubrif.",   "Químicos",    "20",  "tb",  "200.00","D-01"},
                {"Pallet de Tijolos",        "Construção",  "15",  "pl",  "800.00","C-01"},
                {"Rolo de Mangueira 3/4\"",  "Hidráulico",  "45",  "un",  "22.00", "B-01"},
                {"Caixa de Pregos 18x27",    "Fixadores",   "200", "cx",  "5.00",  "A-02"},
                {"Lona Plástica 4x6m",       "Proteção",    "60",  "un",  "3.50",  "E-01"}
        };

        for (String[] d : dados) {
            ContentValues cv = new ContentValues();
            cv.put("nome",        d[0]);
            cv.put("categoria",   d[1]);
            cv.put("quantidade",  Integer.parseInt(d[2]));
            cv.put("unidade",     d[3]);
            cv.put("peso_kg",     Double.parseDouble(d[4]));
            cv.put("localizacao", d[5]);
            db.insert(TABELA, null, cv);
        }
    }

    // ---- LISTAR TODOS OS PRODUTOS ----
    public List<Produto> listarProdutos() {
        List<Produto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA, null, null, null, null, null, "categoria, nome");

        if (cursor.moveToFirst()) {
            do {
                Produto p = new Produto(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                        cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantidade")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unidade")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("peso_kg")),
                        cursor.getString(cursor.getColumnIndexOrThrow("localizacao")),
                        cursor.getString(cursor.getColumnIndexOrThrow("data_atualizacao"))
                );
                lista.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // ---- ATUALIZAR QUANTIDADE ----
    public boolean atualizarQuantidade(int id, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantidade", quantidade);
        cv.put("data_atualizacao", obterDataAtual());
        int rows = db.update(TABELA, cv, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // ---- ADICIONAR PRODUTO ----
    public boolean adicionarProduto(String nome, String categoria, int quantidade,
                                     String unidade, double peso, String local) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nome",        nome);
        cv.put("categoria",   categoria);
        cv.put("quantidade",  quantidade);
        cv.put("unidade",     unidade);
        cv.put("peso_kg",     peso);
        cv.put("localizacao", local);
        cv.put("data_atualizacao", obterDataAtual());
        long id = db.insert(TABELA, null, cv);
        db.close();
        return id != -1;
    }

    // ---- DELETAR PRODUTO ----
    public boolean deletarProduto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABELA, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // ---- DATA ATUAL ----
    private String obterDataAtual() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}
