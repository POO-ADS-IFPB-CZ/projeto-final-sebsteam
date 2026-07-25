package biblioteca.dao;

import biblioteca.model.Autor;
import biblioteca.model.Livro;
import java.util.ArrayList;
import java.util.List;

public class LivroDao extends ArquivoDao implements Dao<Livro> {
    private final AutorDao autorDao = new AutorDao();

    public LivroDao() {
        super("livros.txt");
    }

    @Override
    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        for (String linha : lerLinhas()) {
            String[] campos = linha.split(";", -1);
            Autor autor = autorDao.buscarPorId(Integer.parseInt(campos[4]));
            if (autor == null) {
                continue; // autor não encontrado: ignora registro inconsistente
            }
            livros.add(new Livro(
                    Integer.parseInt(campos[0]),
                    campos[1],
                    campos[2],
                    Integer.parseInt(campos[3]),
                    autor,
                    Integer.parseInt(campos[5]),
                    Integer.parseInt(campos[6])
            ));
        }
        return livros;
    }

    @Override
    public Livro buscarPorId(int id) {
        return listarTodos().stream()
                .filter(livro -> livro.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void inserir(Livro livro) {
        List<Livro> livros = listarTodos();
        livro.setId(proximoId(livros.stream().map(Livro::getId).toList()));
        livros.add(livro);
        salvarTodos(livros);
    }

    @Override
    public void atualizar(Livro livroAtualizado) {
        List<Livro> livros = listarTodos();
        for (int i = 0; i < livros.size(); i++) {
            if (livros.get(i).getId() == livroAtualizado.getId()) {
                livros.set(i, livroAtualizado);
                break;
            }
        }
        salvarTodos(livros);
    }

    @Override
    public void excluir(int id) {
        List<Livro> livros = listarTodos();
        livros.removeIf(livro -> livro.getId() == id);
        salvarTodos(livros);
    }

    private void salvarTodos(List<Livro> livros) {
        List<String> linhas = new ArrayList<>();
        for (Livro livro : livros) {
            linhas.add(String.join(";",
                    String.valueOf(livro.getId()),
                    livro.getTitulo(),
                    livro.getIsbn(),
                    String.valueOf(livro.getAnoPublicacao()),
                    String.valueOf(livro.getAutor().getId()),
                    String.valueOf(livro.getQuantidadeTotal()),
                    String.valueOf(livro.getQuantidadeDisponivel())
            ));
        }
        escreverLinhas(linhas);
    }
}
