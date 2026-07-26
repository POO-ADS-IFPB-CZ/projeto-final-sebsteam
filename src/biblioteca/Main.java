package biblioteca;

import biblioteca.util.DadosIniciais;
import biblioteca.view.TelaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação. Execute esta classe (botão ▶ do IntelliJ)
 * para abrir o sistema.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se não conseguir aplicar o look and feel do sistema operacional,
            // a aplicação continua normalmente com o visual padrão do Swing.
        }

        DadosIniciais.carregarSeVazio();

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal telaPrincipal = new TelaPrincipal();
            telaPrincipal.setVisible(true);
        });
    }
}
