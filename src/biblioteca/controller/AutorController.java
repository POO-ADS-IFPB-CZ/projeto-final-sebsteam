package biblioteca.controller;

import biblioteca.dao.AutorDao;
import biblioteca.dao.LivroDao;
import biblioteca.model.Autor;

import java.util.List;

/**
 * Controller de Autor: faz a ponte entre a View (telas Swing) e o DAO.
 * É aqui que ficam as validações e as regras de negócio - a View nunca
 * fala diretamente com o AutorDao.
 */
public class AutorController {

    private final AutorDao autorDao = new AutorDao();
    private final LivroDao livroDao = new LivroDao();

    public List<Autor> listar() {
        return autorDao.listarTodos();
    }

    public void cadastrar(String nome, String nacionalidade) {
        validar(nome, nacionalidade);
        autorDao.inserir(new Autor(0, nome.trim(), nacionalidade.trim()));
    }

    public void atualizar(int id, String nome, String nacionalidade) {
        validar(nome, nacionalidade);
        autorDao.atualizar(new Autor(id, nome.trim(), nacionalidade.trim()));
    }

    public void excluir(int id) {
        boolean possuiLivros = livroDao.listarTodos().stream()
                .anyMatch(livro -> livro.getAutor().getId() == id);
        if (possuiLivros) {
            throw new IllegalStateException(
                    "Não é possível excluir: este autor possui livro(s) cadastrado(s).");
        }
        autorDao.excluir(id);
    }

    private void validar(String nome, String nacionalidade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do autor é obrigatório.");
        }
        if (nacionalidade == null || nacionalidade.isBlank()) {
            throw new IllegalArgumentException("A nacionalidade é obrigatória.");
        }
    }
}
