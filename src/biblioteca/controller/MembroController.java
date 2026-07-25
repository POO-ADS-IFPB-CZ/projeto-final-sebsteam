package biblioteca.controller;

import biblioteca.dao.EmprestimoDao;
import biblioteca.dao.MembroDao;
import biblioteca.model.Membro;
import biblioteca.model.StatusEmprestimo;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller de Membro: validações e regras de negócio relacionadas aos
 * usuários da biblioteca.
 */
public class MembroController {

    private final MembroDao membroDao = new MembroDao();
    private final EmprestimoDao emprestimoDao = new EmprestimoDao();

    public List<Membro> listar() {
        return membroDao.listarTodos();
    }

    public void cadastrar(String nome, String email, String telefone) {
        validar(nome, email);
        Membro membro = new Membro(0, nome.trim(), email.trim(), telefone == null ? "" : telefone.trim(), LocalDate.now());
        membroDao.inserir(membro);
    }

    public void atualizar(int id, String nome, String email, String telefone, LocalDate dataCadastro) {
        validar(nome, email);
        Membro membro = new Membro(id, nome.trim(), email.trim(), telefone == null ? "" : telefone.trim(), dataCadastro);
        membroDao.atualizar(membro);
    }

    public void excluir(int id) {
        boolean possuiEmprestimoAtivo = emprestimoDao.listarTodos().stream()
                .anyMatch(emp -> emp.getMembro().getId() == id && emp.getStatus() == StatusEmprestimo.ATIVO);
        if (possuiEmprestimoAtivo) {
            throw new IllegalStateException(
                    "Não é possível excluir: este membro possui empréstimo(s) ativo(s).");
        }
        membroDao.excluir(id);
    }

    private void validar(String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do membro é obrigatório.");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
    }
}
