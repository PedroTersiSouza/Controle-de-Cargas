package com.estoque.controle;

// ==============================================
// MainActivity.java — versão SQLite
// ==============================================

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProdutoAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ProdutoAdapter adapter;
    private List<Produto> listaProdutos = new ArrayList<>();
    private TextView txtVazio;
    private TextView txtTotal;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db           = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        txtVazio     = findViewById(R.id.txtVazio);
        txtTotal     = findViewById(R.id.txtTotal);

        // Remove o ProgressBar pois SQLite é instantâneo
        View progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) progressBar.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdutoAdapter(this, listaProdutos, this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnAdicionar).setOnClickListener(v ->
                startActivity(new Intent(this, AdicionarProdutoActivity.class)));

        findViewById(R.id.btnAtualizar).setOnClickListener(v -> carregarProdutos());

        carregarProdutos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProdutos();
    }

    private void carregarProdutos() {
        List<Produto> produtos = db.listarProdutos();

        if (produtos.isEmpty()) {
            txtVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtVazio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.atualizarLista(produtos);
            txtTotal.setText("Total: " + produtos.size() + " produto(s) no estoque");
        }
    }

    @Override
    public void onEditar(Produto produto) {
        Intent intent = new Intent(this, EditarProdutoActivity.class);
        intent.putExtra("id",          produto.getId());
        intent.putExtra("nome",        produto.getNome());
        intent.putExtra("categoria",   produto.getCategoria());
        intent.putExtra("quantidade",  produto.getQuantidade());
        intent.putExtra("unidade",     produto.getUnidade());
        intent.putExtra("peso",        produto.getPesoKg());
        intent.putExtra("localizacao", produto.getLocalizacao());
        startActivity(intent);
    }

    @Override
    public void onDeletar(Produto produto) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja remover \"" + produto.getNome() + "\" do estoque?")
                .setPositiveButton("Remover", (dialog, which) -> {
                    boolean ok = db.deletarProduto(produto.getId());
                    if (ok) {
                        Toast.makeText(this, "✅ Produto removido!", Toast.LENGTH_SHORT).show();
                        carregarProdutos();
                    } else {
                        Toast.makeText(this, "❌ Erro ao remover", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
