package biblioteca.controller;

import biblioteca.dao.EmprestimoDao;
import biblioteca.dao.LivroDao;
import biblioteca.dao.MembroDao;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Membro;
import biblioteca.model.StatusEmprestimo;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller de Empréstimo: concentra a regra de negócio mais importante do
 * sistema - registrar um empréstimo e registrar uma devolução, mantendo a
 * quantidade disponível de cada livro sempre coerente.
 */
public class EmprestimoController {

    private final EmprestimoDao emprestimoDao = new EmprestimoDao();
    private final LivroDao livroDao = new LivroDao();
    private final MembroDao membroDao = new MembroDao();

    public List<Emprestimo> listar() {
        return emprestimoDao.listarTodos();
    }

    public List<Livro> listarLivrosDisponiveis() {
        return livroDao.listarTodos().stream()
                .filter(livro -> livro.getQuantidadeDisponivel() > 0)
                .toList();
    }

    public List<Membro> listarMembros() {
        return membroDao.listarTodos();
    }

    public void registrarEmprestimo(Livro livro, Membro membro, int diasParaDevolucao) {
        if (livro == null) {
            throw new IllegalArgumentException("Selecione um livro.");
        }
        if (membro == null) {
            throw new IllegalArgumentException("Selecione um membro.");
        }
        if (diasParaDevolucao <= 0) {
            throw new IllegalArgumentException("O prazo para devolução deve ser maior que zero.");
        }
        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new IllegalStateException("Não há exemplares disponíveis deste livro.");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setMembro(membro);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(diasParaDevolucao));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimoDao.inserir(emprestimo);

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroDao.atualizar(livro);
    }

    public void devolverLivro(Emprestimo emprestimo) {
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new IllegalStateException("Este empréstimo já foi devolvido.");
        }

        emprestimo.setDataDevolucaoReal(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimoDao.atualizar(emprestimo);

        Livro livro = livroDao.buscarPorId(emprestimo.getLivro().getId());
        if (livro != null) {
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
            livroDao.atualizar(livro);
        }
    }

    public void excluir(int id) {
        emprestimoDao.excluir(id);
    }
}
