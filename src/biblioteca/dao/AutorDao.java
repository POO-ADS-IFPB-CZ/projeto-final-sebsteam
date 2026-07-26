package biblioteca.dao;

import biblioteca.model.Autor;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável por ler e escrever os dados de {@link Autor} no arquivo
 * data/autores.txt. Formato de cada linha: id;nome;nacionalidade
 */
public class AutorDao extends ArquivoDao implements Dao<Autor> {

    public AutorDao() {
        super("autores.txt");
    }

    @Override
    public List<Autor> listarTodos() {
        List<Autor> autores = new ArrayList<>();
        for (String linha : lerLinhas()) {
            String[] campos = linha.split(";", -1);
            autores.add(new Autor(
                    Integer.parseInt(campos[0]),
                    campos[1],
                    campos[2]
            ));
        }
        return autores;
    }

    @Override
    public Autor buscarPorId(int id) {
        return listarTodos().stream()
                .filter(autor -> autor.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void inserir(Autor autor) {
        List<Autor> autores = listarTodos();
        autor.setId(proximoId(autores.stream().map(Autor::getId).toList()));
        autores.add(autor);
        salvarTodos(autores);
    }

    @Override
    public void atualizar(Autor autorAtualizado) {
        List<Autor> autores = listarTodos();
        for (int i = 0; i < autores.size(); i++) {
            if (autores.get(i).getId() == autorAtualizado.getId()) {
                autores.set(i, autorAtualizado);
                break;
            }
        }
        salvarTodos(autores);
    }

    @Override
    public void excluir(int id) {
        List<Autor> autores = listarTodos();
        autores.removeIf(autor -> autor.getId() == id);
        salvarTodos(autores);
    }

    private void salvarTodos(List<Autor> autores) {
        List<String> linhas = new ArrayList<>();
        for (Autor autor : autores) {
            linhas.add(String.join(";",
                    String.valueOf(autor.getId()),
                    autor.getNome(),
                    autor.getNacionalidade()
            ));
        }
        escreverLinhas(linhas);
    }
}
