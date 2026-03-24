package com.example.prodesk;

public class ReservaModel {

    String nome, dataInicio, dataFim, horario, pagamento, status;
    int imagem;

    public ReservaModel(String nome, String dataInicio, String dataFim,
                        String horario, String pagamento, String status, int imagem) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horario = horario;
        this.pagamento = pagamento;
        this.status = status;
        this.imagem = imagem;
    }
}