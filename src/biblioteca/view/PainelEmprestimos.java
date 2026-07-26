package biblioteca.view;

import biblioteca.controller.EmprestimoController;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Membro;
import biblioteca.model.StatusEmprestimo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PainelEmprestimos extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmprestimoController controller = new EmprestimoController();
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    public PainelEmprestimos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{
                "ID", "Livro", "Membro", "Empréstimo", "Devolução prevista", "Devolução real", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo = new JButton("Novo Empréstimo");
        JButton btnDevolver = new JButton("Registrar Devolução");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar Lista");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnDevolver);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> abrirFormularioNovoEmprestimo());
        btnDevolver.addActionListener(e -> registrarDevolucao());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnAtualizar.addActionListener(e -> carregarTabela());

        carregarTabela();
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        for (Emprestimo emprestimo : controller.listar()) {
            String statusTexto;
            if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
                statusTexto = "Devolvido";
            } else if (emprestimo.isAtrasado()) {
                statusTexto = "Atrasado";
            } else {
                statusTexto = "Ativo";
            }

            modeloTabela.addRow(new Object[]{
                    emprestimo.getId(),
                    emprestimo.getLivro().getTitulo(),
                    emprestimo.getMembro().getNome(),
                    emprestimo.getDataEmprestimo().format(FORMATO_DATA),
                    emprestimo.getDataDevolucaoPrevista().format(FORMATO_DATA),
                    emprestimo.getDataDevolucaoReal() != null ? emprestimo.getDataDevolucaoReal().format(FORMATO_DATA) : "-",
                    statusTexto
            });
        }
    }

    private Emprestimo obterSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        return controller.listar().stream()
                .filter(emprestimo -> emprestimo.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void registrarDevolucao() {
        Emprestimo selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controller.devolverLivro(selecionado);
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        Emprestimo selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este registro de empréstimo?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }
        controller.excluir(selecionado.getId());
        carregarTabela();
    }

    private void abrirFormularioNovoEmprestimo() {
        List<Livro> livrosDisponiveis = controller.listarLivrosDisponiveis();
        List<Membro> membros = controller.listarMembros();

        if (livrosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há livros disponíveis para empréstimo no momento.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (membros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre ao menos um membro antes de registrar um empréstimo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Livro> comboLivro = new JComboBox<>(livrosDisponiveis.toArray(new Livro[0]));
        JComboBox<Membro> comboMembro = new JComboBox<>(membros.toArray(new Membro[0]));
        JTextField campoDias = new JTextField("7", 20);

        JPanel painel = new JPanel(new GridLayout(0, 1, 5, 5));
        painel.add(new JLabel("Livro:"));
        painel.add(comboLivro);
        painel.add(new JLabel("Membro:"));
        painel.add(comboMembro);
        painel.add(new JLabel("Prazo para devolução (dias):"));
        painel.add(campoDias);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Novo Empréstimo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            int dias = Integer.parseInt(campoDias.getText().trim());
            Livro livroSelecionado = (Livro) comboLivro.getSelectedItem();
            Membro membroSelecionado = (Membro) comboMembro.getSelectedItem();
            controller.registrarEmprestimo(livroSelecionado, membroSelecionado, dias);
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um número válido de dias.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
