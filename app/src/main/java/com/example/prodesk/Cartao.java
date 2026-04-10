package com.example.prodesk;

public class Cartao {

    String numero;
    String nome;

    public Cartao(String numero, String nome) {
        this.numero = numero;
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }
}