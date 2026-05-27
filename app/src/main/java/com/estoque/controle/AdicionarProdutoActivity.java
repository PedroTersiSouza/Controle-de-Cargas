package com.estoque.controle;

// ==============================================
// AdicionarProdutoActivity.java — versão SQLite
// ==============================================

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AdicionarProdutoActivity extends AppCompatActivity {

    private EditText edtNome, edtCategoria, edtQuantidade, edtUnidade, edtPeso, edtLocal;
    private Button btnSalvar, btnCancelar;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar);

        db = new DatabaseHelper(this);

        edtNome       = findViewById(R.id.edtNome);
        edtCategoria  = findViewById(R.id.edtCategoria);
        edtQuantidade = findViewById(R.id.edtQuantidade);
        edtUnidade    = findViewById(R.id.edtUnidade);
        edtPeso       = findViewById(R.id.edtPeso);
        edtLocal      = findViewById(R.id.edtLocal);
        btnSalvar     = findViewById(R.id.btnSalvar);
        btnCancelar   = findViewById(R.id.btnCancelar);

        btnSalvar.setOnClickListener(v -> salvar());
        btnCancelar.setOnClickListener(v -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Novo Produto");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void salvar() {
        String nome  = edtNome.getText().toString().trim();
        String cat   = edtCategoria.getText().toString().trim();
        String qtdS  = edtQuantidade.getText().toString().trim();
        String unid  = edtUnidade.getText().toString().trim();
        String pesoS = edtPeso.getText().toString().trim();
        String local = edtLocal.getText().toString().trim();

        if (nome.isEmpty() || cat.isEmpty() || qtdS.isEmpty()) {
            Toast.makeText(this, "Preencha nome, categoria e quantidade", Toast.LENGTH_SHORT).show();
            return;
        }

        int qtd     = Integer.parseInt(qtdS);
        double peso = pesoS.isEmpty() ? 0 : Double.parseDouble(pesoS);
        if (unid.isEmpty()) unid = "un";

        boolean ok = db.adicionarProduto(nome, cat, qtd, unid, peso, local);
        if (ok) {
            Toast.makeText(this, "✅ Produto cadastrado!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "❌ Erro ao cadastrar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
