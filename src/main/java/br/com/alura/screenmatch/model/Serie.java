package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jdk.jfr.Category;

import java.util.OptionalDouble;

public class Serie {
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private String atores;
    private Categoria genero;
    private String poster;
    private String sinopse;

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.atores = dadosSerie.atores();
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse();
    }
}

