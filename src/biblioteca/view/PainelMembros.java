package biblioteca.view;

import biblioteca.controller.MembroController;
import biblioteca.model.Membro;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class PainelMembros extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MembroController controller = new MembroController();
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    public PainelMembros() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Nome", "E-mail", "Telefone", "Cadastrado em"}, 0) {
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
        JButton btnNovo = new JButton("Novo Membro");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar Lista");
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnAtualizar.addActionListener(e -> carregarTabela());

        carregarTabela();
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        for (Membro membro : controller.listar()) {
            modeloTabela.addRow(new Object[]{
                    membro.getId(),
                    membro.getNome(),
                    membro.getEmail(),
                    membro.getTelefone(),
                    membro.getDataCadastro().format(FORMATO_DATA)
            });
        }
    }

    private Membro obterSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        return controller.listar().stream()
                .filter(membro -> membro.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void editarSelecionado() {
        Membro selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um membro na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario(selecionado);
    }

    private void excluirSelecionado() {
        Membro selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um membro na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o membro \"" + selecionado.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.excluir(selecionado.getId());
            carregarTabela();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormulario(Membro membroExistente) {
        JTextField campoNome = new JTextField(membroExistente != null ? membroExistente.getNome() : "", 20);
        JTextField campoEmail = new JTextField(membroExistente != null ? membroExistente.getEmail() : "", 20);
        JTextField campoTelefone = new JTextField(membroExistente != null ? membroExistente.getTelefone() : "", 20);

        JPanel painel = new JPanel(new GridLayout(0, 1, 5, 5));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("E-mail:"));
        painel.add(campoEmail);
        painel.add(new JLabel("Telefone:"));
        painel.add(campoTelefone);

        String tituloJanela = membroExistente == null ? "Novo Membro" : "Editar Membro";
        int resultado = JOptionPane.showConfirmDialog(this, painel, tituloJanela,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            if (membroExistente == null) {
                controller.cadastrar(campoNome.getText(), campoEmail.getText(), campoTelefone.getText());
            } else {
                controller.atualizar(membroExistente.getId(), campoNome.getText(), campoEmail.getText(),
                        campoTelefone.getText(), membroExistente.getDataCadastro());
            }
            carregarTabela();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de validação", JOptionPane.ERROR_MESSAGE);
        }
    }
}
