package com.estoque.controle;

// ==============================================
// EditarProdutoActivity.java — versão SQLite
// ==============================================

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class EditarProdutoActivity extends AppCompatActivity {

    private int produtoId;
    private EditText edtQuantidade;
    private TextView txtInfo;
    private Button btnSalvar, btnCancelar;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        db = new DatabaseHelper(this);

        produtoId    = getIntent().getIntExtra("id", 0);
        String nome  = getIntent().getStringExtra("nome");
        String cat   = getIntent().getStringExtra("categoria");
        int qtd      = getIntent().getIntExtra("quantidade", 0);
        String unid  = getIntent().getStringExtra("unidade");
        double peso  = getIntent().getDoubleExtra("peso", 0);
        String local = getIntent().getStringExtra("localizacao");

        edtQuantidade = findViewById(R.id.edtQuantidade);
        txtInfo       = findViewById(R.id.txtInfo);
        btnSalvar     = findViewById(R.id.btnSalvar);
        btnCancelar   = findViewById(R.id.btnCancelar);

        txtInfo.setText(
                "📦 " + nome + "\n" +
                "Categoria: " + cat + "\n" +
                "Localização: " + local + "\n" +
                "Peso: " + peso + " kg/un\n" +
                "Unidade: " + unid
        );
        edtQuantidade.setText(String.valueOf(qtd));
        edtQuantidade.selectAll();

        btnSalvar.setOnClickListener(v -> salvar());
        btnCancelar.setOnClickListener(v -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Editar Estoque");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void salvar() {
        String qtdStr = edtQuantidade.getText().toString().trim();

        if (qtdStr.isEmpty()) {
            Toast.makeText(this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
            return;
        }

        int novaQtd = Integer.parseInt(qtdStr);
        if (novaQtd < 0) {
            Toast.makeText(this, "Quantidade não pode ser negativa", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = db.atualizarQuantidade(produtoId, novaQtd);
        if (ok) {
            Toast.makeText(this, "✅ Quantidade atualizada!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "❌ Erro ao atualizar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
