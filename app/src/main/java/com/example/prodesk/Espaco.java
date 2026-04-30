package com.example.prodesk;

public class Espaco {

    private String id;
    private String nome;
    private String descricao;
    private String preco;
    private String imagem;
    private String donoNome;
    private double latitude;
    private double longitude;

    public Espaco() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPreco() { return preco; }
    public void setPreco(String preco) { this.preco = preco; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public String getDonoNome() { return donoNome; }
    public void setDonoNome(String donoNome) { this.donoNome = donoNome; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}