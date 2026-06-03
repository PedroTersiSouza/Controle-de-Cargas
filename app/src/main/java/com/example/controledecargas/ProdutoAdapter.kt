package com.example.controledecargas

// ==============================================
// ProdutoAdapter.kt — Adapter do RecyclerView
// ==============================================

import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ProdutoAdapter(
    private var lista: MutableList<Produto>,
    private val onEditar: (Produto) -> Unit,
    private val onDeletar: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome:        TextView = view.findViewById(R.id.txtNome)
        val txtCategoria:   TextView = view.findViewById(R.id.txtCategoria)
        val txtQuantidade:  TextView = view.findViewById(R.id.txtQuantidade)
        val txtStatus:      TextView = view.findViewById(R.id.txtStatus)
        val txtLocalizacao: TextView = view.findViewById(R.id.txtLocalizacao)
        val txtPeso:        TextView = view.findViewById(R.id.txtPeso)
        val txtData:        TextView = view.findViewById(R.id.txtData)
        val btnEditar:      Button   = view.findViewById(R.id.btnEditar)
        val btnDeletar:     Button   = view.findViewById(R.id.btnDeletar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = lista[position]

        holder.txtNome.text        = p.nome
        holder.txtCategoria.text   = p.categoria
        holder.txtLocalizacao.text = "📦 ${p.localizacao}"
        holder.txtPeso.text        = "${p.pesoKg} kg/un"
        holder.txtQuantidade.text  = "${p.quantidade} ${p.unidade}"
        holder.txtData.text        = "Atualizado: ${p.dataAtualizacao.take(16)}"

        val (bgColor, txtColor) = when (p.getStatus()) {
            "ZERADO"  -> "#9E9E9E" to "#9E9E9E"
            "CRÍTICO" -> "#F44336" to "#F44336"
            "BAIXO"   -> "#FF9800" to "#FF9800"
            else      -> "#4CAF50" to "#4CAF50"
        }
        holder.txtStatus.text = p.getStatus()
        holder.txtStatus.setBackgroundColor(Color.parseColor(bgColor))
        holder.txtQuantidade.setTextColor(Color.parseColor(txtColor))

        holder.btnEditar.setOnClickListener  { onEditar(p) }
        holder.btnDeletar.setOnClickListener { onDeletar(p) }
    }

    override fun getItemCount() = lista.size

    fun atualizarLista(novaLista: List<Produto>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }
}
