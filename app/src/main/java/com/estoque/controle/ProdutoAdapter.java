package com.estoque.controle;

// ==============================================
// ProdutoAdapter.java
// Adapter do RecyclerView - monta cada card do estoque
// ==============================================

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onEditar(Produto produto);
        void onDeletar(Produto produto);
    }

    private Context context;
    private List<Produto> lista;
    private OnItemClickListener listener;

    public ProdutoAdapter(Context context, List<Produto> lista, OnItemClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produto p = lista.get(position);

        holder.txtNome.setText(p.getNome());
        holder.txtCategoria.setText(p.getCategoria());
        holder.txtLocalizacao.setText("📦 " + p.getLocalizacao());
        holder.txtPeso.setText(p.getPesoKg() + " kg/un");
        holder.txtQuantidade.setText(p.getQuantidade() + " " + p.getUnidade());

        // Cor do status de quantidade
        String status = p.getStatusQuantidade();
        holder.txtStatus.setText(status);
        switch (status) {
            case "ZERADO":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#F44336"));
                holder.txtQuantidade.setTextColor(Color.parseColor("#F44336"));
                break;
            case "CRÍTICO":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#FF5722"));
                holder.txtQuantidade.setTextColor(Color.parseColor("#FF5722"));
                break;
            case "BAIXO":
                holder.txtStatus.setBackgroundColor(Color.parseColor("#FF9800"));
                holder.txtQuantidade.setTextColor(Color.parseColor("#FF9800"));
                break;
            default:
                holder.txtStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
                holder.txtQuantidade.setTextColor(Color.parseColor("#4CAF50"));
                break;
        }

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(p));
        holder.btnDeletar.setOnClickListener(v -> listener.onDeletar(p));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void atualizarLista(List<Produto> novaLista) {
        lista.clear();
        lista.addAll(novaLista);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtCategoria, txtQuantidade, txtStatus, txtLocalizacao, txtPeso;
        Button btnEditar, btnDeletar;

        ViewHolder(View itemView) {
            super(itemView);
            txtNome        = itemView.findViewById(R.id.txtNome);
            txtCategoria   = itemView.findViewById(R.id.txtCategoria);
            txtQuantidade  = itemView.findViewById(R.id.txtQuantidade);
            txtStatus      = itemView.findViewById(R.id.txtStatus);
            txtLocalizacao = itemView.findViewById(R.id.txtLocalizacao);
            txtPeso        = itemView.findViewById(R.id.txtPeso);
            btnEditar      = itemView.findViewById(R.id.btnEditar);
            btnDeletar     = itemView.findViewById(R.id.btnDeletar);
        }
    }
}
