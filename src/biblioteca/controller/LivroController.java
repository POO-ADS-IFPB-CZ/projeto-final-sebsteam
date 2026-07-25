package biblioteca.controller;

import biblioteca.dao.EmprestimoDao;
import biblioteca.dao.LivroDao;
import biblioteca.model.Autor;
import biblioteca.model.Livro;
import biblioteca.model.StatusEmprestimo;

import java.util.List;

/**
 * Controller de Livro: validações e regras de negócio relacionadas ao
 * acervo, como manter a quantidade disponível coerente com a quantidade
 * total e impedir a exclusão de livros com empréstimos ativos.
 */
public class LivroController {

    private final LivroDao livroDao = new LivroDao();
    private final EmprestimoDao emprestimoDao = new EmprestimoDao();

    public List<Livro> listar() {
        return livroDao.listarTodos();
    }

    public void cadastrar(String titulo, String isbn, int anoPublicacao, Autor autor, int quantidadeTotal) {
        validarDadosBasicos(titulo, anoPublicacao, autor, quantidadeTotal);

        Livro livro = new Livro();
        livro.setTitulo(titulo.trim());
        livro.setIsbn(isbn == null ? "" : isbn.trim());
        livro.setAnoPublicacao(anoPublicacao);
        livro.setAutor(autor);
        livro.setQuantidadeTotal(quantidadeTotal);
        livro.setQuantidadeDisponivel(quantidadeTotal);
        livroDao.inserir(livro);
    }

    /**
     * Atualiza um livro existente. A quantidade disponível é recalculada a
     * partir da nova quantidade total, preservando o número de exemplares
     * que já estão emprestados no momento da edição.
     */
    public void atualizar(int id, String titulo, String isbn, int anoPublicacao, Autor autor,
                           int novaQuantidadeTotal) {
        validarDadosBasicos(titulo, anoPublicacao, autor, novaQuantidadeTotal);

        Livro livro = livroDao.buscarPorId(id);
        if (livro == null) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }

        int exemplaresEmprestados = livro.getQuantidadeTotal() - livro.getQuantidadeDisponivel();
        int novaQuantidadeDisponivel = novaQuantidadeTotal - exemplaresEmprestados;
        if (novaQuantidadeDisponivel < 0) {
            throw new IllegalArgumentException(
                    "A nova quantidade total não pode ser menor que a quantidade já emprestada ("
                            + exemplaresEmprestados + ").");
        }

        livro.setTitulo(titulo.trim());
        livro.setIsbn(isbn == null ? "" : isbn.trim());
        livro.setAnoPublicacao(anoPublicacao);
        livro.setAutor(autor);
        livro.setQuantidadeTotal(novaQuantidadeTotal);
        livro.setQuantidadeDisponivel(novaQuantidadeDisponivel);
        livroDao.atualizar(livro);
    }

    public void excluir(int id) {
        boolean possuiEmprestimoAtivo = emprestimoDao.listarTodos().stream()
                .anyMatch(emp -> emp.getLivro().getId() == id && emp.getStatus() == StatusEmprestimo.ATIVO);
        if (possuiEmprestimoAtivo) {
            throw new IllegalStateException(
                    "Não é possível excluir: este livro possui empréstimo(s) ativo(s).");
        }
        livroDao.excluir(id);
    }

    private void validarDadosBasicos(String titulo, int anoPublicacao, Autor autor, int quantidadeTotal) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título do livro é obrigatório.");
        }
        if (autor == null) {
            throw new IllegalArgumentException("Selecione um autor.");
        }
        if (anoPublicacao < 0 || anoPublicacao > 2100) {
            throw new IllegalArgumentException("Informe um ano de publicação válido.");
        }
        if (quantidadeTotal < 0) {
            throw new IllegalArgumentException("A quantidade de exemplares não pode ser negativa.");
        }
    }
}
