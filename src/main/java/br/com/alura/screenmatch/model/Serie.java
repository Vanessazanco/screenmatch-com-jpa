package br.com.alura.screenmatch.model;

import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private String atores;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy ="serie",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie(){

    }
    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();

        this.avaliacao = dadosSerie.avaliacao() != null ?
                OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0)
                : 0.0;
        this.atores = dadosSerie.atores();
        this.genero = dadosSerie.genero() != null ? Categoria.fromString(dadosSerie.genero().split(",")[0].trim()) : Categoria.NAOLISTADA;
        this.poster = dadosSerie.poster();
        this.sinopse= dadosSerie.sinopse();//this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(ep -> ep.setSerie(this));
        this.episodios = episodios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return "Serie: " + titulo +
                ", Genero =" + genero +
                ", Temporadas =" + totalTemporadas +
                ", Avaliacao =" + avaliacao +
                ", Atores ='" + atores +
                ", Poster ='" + poster +
                ", Sinopse ='" + sinopse +
                ", Episodios ='" + episodios ;
    }
}

