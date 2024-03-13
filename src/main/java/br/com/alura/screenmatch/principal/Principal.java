package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=2d691d95";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por titulo
                    5 - Buscar série por Ator
                    6 - Buscar série por Avaliação
                    7 - Buscar por categoria
                    8 - Filtrar séries
                    9 - Buscar episódios por trecho
                    10 - Top 5 melhores episódios
                    11 - Buscar episódios a partir de um ano
                    0 - Sair                             
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisódiosPorSérie();
                    break;
                case 11:
                    buscarEpisodiosPordata();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        //Busca no MDB e salva no Banco
        try {
            DadosSerie dados = getDadosSerie();
            if (Objects.nonNull(dados.titulo())) {
                Serie serie = new Serie(dados);
                //dadosSeries.add(dados);
                repositorio.save(serie);
                System.out.println(dados);
            } else {
                System.out.println("Não foi possivel encontrar essa série!");
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro:" + e.getMessage());
        }
    }

    private DadosSerie getDadosSerie() throws IOException, InterruptedException {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome : ");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);


        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                String json = null;
                try {
                    json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo()
                            .replace(" ", "+") + "&season=" + i + API_KEY);

                    DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                    temporadas.add(dadosTemporada);
                } catch (Exception e) {
                    System.out.println("Ocorreu um erro:" + e.getMessage());
                }
            }
            temporadas.forEach(System.out::println);


            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada!! ='( ");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome : ");
        String nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série : " + serieBusca.get());
        } else {
            System.out.println("Série não encotrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome do autor para busca ?");
        String nomeAtor = leitura.nextLine();
        System.out.println("Avaliações de que valor ?");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEcontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou");
        seriesEcontradas.forEach(seri -> System.out.println(seri.getTitulo() +
                " - avaliação: " + seri.getAvaliacao()));

    }

    private void buscarTop5Series() {
        List<Serie> top5Series = repositorio.findTop5ByOrderByAvaliacaoDesc();
        top5Series.forEach(topseri -> System.out.println(topseri.getTitulo() +
                " - avaliação: " + topseri.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar série de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Série da Categoria : " + categoria);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do episódio para busca? ");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(episodio ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        episodio.getSerie().getTitulo(), episodio.getTemporada(),
                        episodio.getNumeroEpisodio(), episodio.getTitulo()));
    }

    private void topEpisódiosPorSérie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(episodio ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Avaliação %s\n",
                            episodio.getSerie().getTitulo(), episodio.getTemporada(),
                            episodio.getNumeroEpisodio(), episodio.getTitulo(), episodio.getAvaliacao()));
        }
    }
    private void buscarEpisodiosPordata(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento :");
            var anoLancamento= leitura.nextInt();
            leitura.nextLine();
            List<Episodio>episodiosAno=repositorio.episodiosPorSerieEAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
}
