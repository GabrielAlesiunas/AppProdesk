package com.example.prodesk;

public class Cartao {

    public String id;
    public String numero;
    public String nome;
    public String validade;

    public Cartao() {}

    public Cartao(String id, String numero, String nome, String validade) {
        this.id = id;
        this.numero = numero;
        this.nome = nome;
        this.validade = validade;
    }
}