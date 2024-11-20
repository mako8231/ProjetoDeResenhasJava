import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

class Filme {
    private String titulo;
    private LocalDate dataLancamento;
    private List<Integer> avaliacoes;

    public Filme(String titulo, LocalDate dataLancamento) {
        this.titulo = titulo;
        this.dataLancamento = dataLancamento;
        this.avaliacoes = new ArrayList<>();
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void adicionarAvaliacao(int nota) {
        if (nota >= 1 && nota <= 5) {
            avaliacoes.add(nota);
        } else {
            throw new IllegalArgumentException("Nota deve ser entre 1 e 5.");
        }
    }

    public double getMediaAvaliacoes() {
        return avaliacoes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public List<Integer> getAvaliacoes() {
        return avaliacoes;
    }
}

class Usuario {
    private String nome;
    private Map<Filme, String> reviews; // Filme e a crítica escrita

    public Usuario(String nome) {
        this.nome = nome;
        this.reviews = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public int getTotalReviews() {
        return reviews.size();
    }

    public String getRotulo() {
        int totalReviews = getTotalReviews();
        if (totalReviews == 0) return "Consumidor";
        else if (totalReviews > 0 && totalReviews <= 3) return "Telespectador";
        else return "Crítico";
    }

    public boolean escreverReview(Filme filme, int nota, String critica) {
        if (reviews.containsKey(filme)) {
            System.out.println("Usuário já fez uma crítica para este filme.");
            return false;
        }
        filme.adicionarAvaliacao(nota);
        reviews.put(filme, critica);
        return true;
    }
}

public class SistemaDeResenhas {
    private List<Usuario> usuarios;
    private List<Filme> filmes;

    public SistemaDeResenhas() {
        this.usuarios = new ArrayList<>();
        this.filmes = new ArrayList<>();
    }

    public Usuario cadastrarUsuario(String nome) {
        Usuario usuario = new Usuario(nome);
        usuarios.add(usuario);
        return usuario;
    }

    public Filme cadastrarFilme(String titulo, LocalDate dataLancamento) {
        Filme filme = new Filme(titulo, dataLancamento);
        filmes.add(filme);
        return filme;
    }

    public List<Usuario> getPrincipaisCriticos() {
        return usuarios.stream()
                .sorted(Comparator.comparing(Usuario::getRotulo).thenComparing(Usuario::getTotalReviews).reversed())
                .collect(Collectors.toList());
    }

    public List<Filme> getTop5Filmes() {
        return filmes.stream()
                .sorted(Comparator.comparingDouble(Filme::getMediaAvaliacoes).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public double getMediaPorPeriodo(LocalDate inicio, LocalDate fim) {
        return filmes.stream()
                .filter(filme -> filme.getDataLancamento().isAfter(inicio.minusDays(1)) && filme.getDataLancamento().isBefore(fim.plusDays(1)))
                .flatMapToDouble(filme -> filme.getAvaliacoes().stream().mapToDouble(Integer::doubleValue))
                .average()
                .orElse(0.0);
    }

    public double getMediaDeFilme(String titulo) {
        return filmes.stream()
                .filter(filme -> filme.getTitulo().equalsIgnoreCase(titulo))
                .findFirst()
                .map(Filme::getMediaAvaliacoes)
                .orElse(0.0);
    }

    public static void main(String[] args) {
        SistemaDeResenhas sistema = new SistemaDeResenhas();

        // Cadastro de filmes e usuários
        Usuario usuario1 = sistema.cadastrarUsuario("João");
        Usuario usuario2 = sistema.cadastrarUsuario("Maria");
        Usuario usuario3 = sistema.cadastrarUsuario("Pedro");

        Filme filme1 = sistema.cadastrarFilme("Filme A", LocalDate.of(2020, 1, 1));
        Filme filme2 = sistema.cadastrarFilme("Filme B", LocalDate.of(2021, 6, 15));
        Filme filme3 = sistema.cadastrarFilme("Filme C", LocalDate.of(2019, 12, 25));

        // Adicionar reviews
        usuario1.escreverReview(filme1, 5, "Ótimo filme!");
        usuario2.escreverReview(filme2, 4, "Muito bom!");
        usuario3.escreverReview(filme3, 3, "Mediano.");

        // Exibir principais críticos
        System.out.println("Principais críticos:");
        sistema.getPrincipaisCriticos().forEach(u -> System.out.println(u.getNome() + " - " + u.getRotulo()));

        // Top 5 filmes mais bem avaliados
        System.out.println("\nTop 5 filmes mais bem avaliados:");
        sistema.getTop5Filmes().forEach(f -> System.out.println(f.getTitulo() + " - Média: " + f.getMediaAvaliacoes()));

        // Média de notas em um período
        System.out.println("\nMédia de notas de filmes lançados entre 2020 e 2022: " +
                sistema.getMediaPorPeriodo(LocalDate.of(2020, 1, 1), LocalDate.of(2022, 12, 31)));

        // Média de notas de um filme específico
        System.out.println("\nMédia de notas do filme 'Filme A': " + sistema.getMediaDeFilme("Filme A"));
    }
}
