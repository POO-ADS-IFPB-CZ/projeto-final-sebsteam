package biblioteca.dao;

import biblioteca.model.Membro;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembroDao extends ArquivoDao implements Dao<Membro> {

    public MembroDao() {
        super("membros.txt");
    }

    @Override
    public List<Membro> listarTodos() {
        List<Membro> membros = new ArrayList<>();
        for (String linha : lerLinhas()) {
            String[] campos = linha.split(";", -1);
            membros.add(new Membro(
                    Integer.parseInt(campos[0]),
                    campos[1],
                    campos[2],
                    campos[3],
                    LocalDate.parse(campos[4])
            ));
        }
        return membros;
    }

    @Override
    public Membro buscarPorId(int id) {
        return listarTodos().stream()
                .filter(membro -> membro.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void inserir(Membro membro) {
        List<Membro> membros = listarTodos();
        membro.setId(proximoId(membros.stream().map(Membro::getId).toList()));
        membros.add(membro);
        salvarTodos(membros);
    }

    @Override
    public void atualizar(Membro membroAtualizado) {
        List<Membro> membros = listarTodos();
        for (int i = 0; i < membros.size(); i++) {
            if (membros.get(i).getId() == membroAtualizado.getId()) {
                membros.set(i, membroAtualizado);
                break;
            }
        }
        salvarTodos(membros);
    }

    @Override
    public void excluir(int id) {
        List<Membro> membros = listarTodos();
        membros.removeIf(membro -> membro.getId() == id);
        salvarTodos(membros);
    }

    private void salvarTodos(List<Membro> membros) {
        List<String> linhas = new ArrayList<>();
        for (Membro membro : membros) {
            linhas.add(String.join(";",
                    String.valueOf(membro.getId()),
                    membro.getNome(),
                    membro.getEmail(),
                    membro.getTelefone(),
                    membro.getDataCadastro().toString()
            ));
        }
        escreverLinhas(linhas);
    }
}
