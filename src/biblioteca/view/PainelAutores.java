package biblioteca.view;

import biblioteca.controller.AutorController;
import biblioteca.model.Autor;

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

public class PainelAutores extends JPanel {

    private final AutorController controller = new AutorController();
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    public PainelAutores() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Nacionalidade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo = new JButton("Novo Autor");
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
        for (Autor autor : controller.listar()) {
            modeloTabela.addRow(new Object[]{autor.getId(), autor.getNome(), autor.getNacionalidade()});
        }
    }

    private Autor obterSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        return controller.listar().stream()
                .filter(autor -> autor.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void editarSelecionado() {
        Autor selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um autor na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario(selecionado);
    }

    private void excluirSelecionado() {
        Autor selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um autor na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o autor \"" + selecionado.getNome() + "\"?",
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

    private void abrirFormulario(Autor autorExistente) {
        JTextField campoNome = new JTextField(autorExistente != null ? autorExistente.getNome() : "", 20);
        JTextField campoNacionalidade = new JTextField(autorExistente != null ? autorExistente.getNacionalidade() : "", 20);

        JPanel painel = new JPanel(new GridLayout(0, 1, 5, 5));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("Nacionalidade:"));
        painel.add(campoNacionalidade);

        String tituloJanela = autorExistente == null ? "Novo Autor" : "Editar Autor";
        int resultado = JOptionPane.showConfirmDialog(this, painel, tituloJanela,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            if (autorExistente == null) {
                controller.cadastrar(campoNome.getText(), campoNacionalidade.getText());
            } else {
                controller.atualizar(autorExistente.getId(), campoNome.getText(), campoNacionalidade.getText());
            }
            carregarTabela();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de validação", JOptionPane.ERROR_MESSAGE);
        }
    }
}
