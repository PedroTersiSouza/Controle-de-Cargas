package com.estoque.controle;

// ==============================================
// MODEL - Produto.java
// Representa um produto do estoque
// ==============================================

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private int quantidade;
    private String unidade;
    private double pesoKg;
    private String localizacao;
    private String dataAtualizacao;

    public Produto() {}

    public Produto(int id, String nome, String categoria, int quantidade,
                   String unidade, double pesoKg, String localizacao, String dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.pesoKg = pesoKg;
        this.localizacao = localizacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(String dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    // Retorna cor de status baseado na quantidade
    public String getStatusQuantidade() {
        if (quantidade == 0) return "ZERADO";
        if (quantidade <= 10) return "CRÍTICO";
        if (quantidade <= 30) return "BAIXO";
        return "NORMAL";
    }
}
