package biblioteca.view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        super("Sistema de Gerenciamento de Biblioteca");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 550));
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Autores", new PainelAutores());
        abas.addTab("Livros", new PainelLivros());
        abas.addTab("Membros", new PainelMembros());
        abas.addTab("Empréstimos", new PainelEmprestimos());

        add(abas);
    }
}
