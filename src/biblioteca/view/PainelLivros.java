package biblioteca.view;

import biblioteca.controller.AutorController;
import biblioteca.controller.LivroController;
import biblioteca.model.Autor;
import biblioteca.model.Livro;

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
import java.util.List;

public class PainelLivros extends JPanel {

    private final LivroController controller = new LivroController();
    private final AutorController autorController = new AutorController();
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    public PainelLivros() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Título", "ISBN", "Ano", "Autor", "Total", "Disponíveis"}, 0) {
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
        JButton btnNovo = new JButton("Novo Livro");
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
        for (Livro livro : controller.listar()) {
            modeloTabela.addRow(new Object[]{
                    livro.getId(),
                    livro.getTitulo(),
                    livro.getIsbn(),
                    livro.getAnoPublicacao(),
                    livro.getAutor().getNome(),
                    livro.getQuantidadeTotal(),
                    livro.getQuantidadeDisponivel()
            });
        }
    }

    private Livro obterSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        return controller.listar().stream()
                .filter(livro -> livro.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void editarSelecionado() {
        Livro selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario(selecionado);
    }

    private void excluirSelecionado() {
        Livro selecionado = obterSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o livro \"" + selecionado.getTitulo() + "\"?",
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

    private void abrirFormulario(Livro livroExistente) {
        List<Autor> autores = autorController.listar();
        if (autores.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cadastre ao menos um autor antes de cadastrar um livro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField campoTitulo = new JTextField(livroExistente != null ? livroExistente.getTitulo() : "", 20);
        JTextField campoIsbn = new JTextField(livroExistente != null ? livroExistente.getIsbn() : "", 20);
        JTextField campoAno = new JTextField(
                livroExistente != null ? String.valueOf(livroExistente.getAnoPublicacao()) : "", 20);
        JComboBox<Autor> comboAutor = new JComboBox<>(autores.toArray(new Autor[0]));
        JTextField campoQuantidade = new JTextField(
                livroExistente != null ? String.valueOf(livroExistente.getQuantidadeTotal()) : "", 20);

        if (livroExistente != null) {
            for (Autor autor : autores) {
                if (autor.getId() == livroExistente.getAutor().getId()) {
                    comboAutor.setSelectedItem(autor);
                    break;
                }
            }
        }

        JPanel painel = new JPanel(new GridLayout(0, 1, 5, 5));
        painel.add(new JLabel("Título:"));
        painel.add(campoTitulo);
        painel.add(new JLabel("ISBN:"));
        painel.add(campoIsbn);
        painel.add(new JLabel("Ano de publicação:"));
        painel.add(campoAno);
        painel.add(new JLabel("Autor:"));
        painel.add(comboAutor);
        painel.add(new JLabel("Quantidade de exemplares:"));
        painel.add(campoQuantidade);

        String tituloJanela = livroExistente == null ? "Novo Livro" : "Editar Livro";
        int resultado = JOptionPane.showConfirmDialog(this, painel, tituloJanela,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            int ano = Integer.parseInt(campoAno.getText().trim());
            int quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            Autor autorSelecionado = (Autor) comboAutor.getSelectedItem();

            if (livroExistente == null) {
                controller.cadastrar(campoTitulo.getText(), campoIsbn.getText(), ano, autorSelecionado, quantidade);
            } else {
                controller.atualizar(livroExistente.getId(), campoTitulo.getText(), campoIsbn.getText(),
                        ano, autorSelecionado, quantidade);
            }
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano e quantidade devem ser números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de validação", JOptionPane.ERROR_MESSAGE);
        }
    }
}
