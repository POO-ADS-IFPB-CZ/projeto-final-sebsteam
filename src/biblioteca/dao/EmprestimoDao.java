package biblioteca.dao;

import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Membro;
import biblioteca.model.StatusEmprestimo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDao extends ArquivoDao implements Dao<Emprestimo> {

    private final LivroDao livroDao = new LivroDao();
    private final MembroDao membroDao = new MembroDao();

    public EmprestimoDao() {
        super("emprestimos.txt");
    }

    @Override
    public List<Emprestimo> listarTodos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        for (String linha : lerLinhas()) {
            String[] c = linha.split(";", -1);

            Livro livro = livroDao.buscarPorId(Integer.parseInt(c[1]));
            Membro membro = membroDao.buscarPorId(Integer.parseInt(c[2]));
            if (livro == null || membro == null) {
                continue; // livro ou membro não encontrado: ignora registro inconsistente
            }

            LocalDate dataDevolucaoReal = c[5].isBlank() ? null : LocalDate.parse(c[5]);

            emprestimos.add(new Emprestimo(
                    Integer.parseInt(c[0]),
                    livro,
                    membro,
                    LocalDate.parse(c[3]),
                    LocalDate.parse(c[4]),
                    dataDevolucaoReal,
                    StatusEmprestimo.valueOf(c[6])
            ));
        }
        return emprestimos;
    }

    @Override
    public Emprestimo buscarPorId(int id) {
        return listarTodos().stream()
                .filter(emprestimo -> emprestimo.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void inserir(Emprestimo emprestimo) {
        List<Emprestimo> emprestimos = listarTodos();
        emprestimo.setId(proximoId(emprestimos.stream().map(Emprestimo::getId).toList()));
        emprestimos.add(emprestimo);
        salvarTodos(emprestimos);
    }

    @Override
    public void atualizar(Emprestimo emprestimoAtualizado) {
        List<Emprestimo> emprestimos = listarTodos();
        for (int i = 0; i < emprestimos.size(); i++) {
            if (emprestimos.get(i).getId() == emprestimoAtualizado.getId()) {
                emprestimos.set(i, emprestimoAtualizado);
                break;
            }
        }
        salvarTodos(emprestimos);
    }

    @Override
    public void excluir(int id) {
        List<Emprestimo> emprestimos = listarTodos();
        emprestimos.removeIf(emprestimo -> emprestimo.getId() == id);
        salvarTodos(emprestimos);
    }

    private void salvarTodos(List<Emprestimo> emprestimos) {
        List<String> linhas = new ArrayList<>();
        for (Emprestimo emprestimo : emprestimos) {
            linhas.add(String.join(";",
                    String.valueOf(emprestimo.getId()),
                    String.valueOf(emprestimo.getLivro().getId()),
                    String.valueOf(emprestimo.getMembro().getId()),
                    emprestimo.getDataEmprestimo().toString(),
                    emprestimo.getDataDevolucaoPrevista().toString(),
                    emprestimo.getDataDevolucaoReal() != null ? emprestimo.getDataDevolucaoReal().toString() : "",
                    emprestimo.getStatus().name()
            ));
        }
        escreverLinhas(linhas);
    }
}
