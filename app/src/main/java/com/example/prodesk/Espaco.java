package com.example.prodesk;

public class Espaco {

    private String nome;
    private String descricao;
    private String preco;

    public Espaco(String nome, String descricao, String preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPreco() {
        return preco;
    }
}