package biblioteca.util;

import biblioteca.controller.AutorController;
import biblioteca.controller.EmprestimoController;
import biblioteca.controller.LivroController;
import biblioteca.controller.MembroController;
import biblioteca.model.Autor;
import biblioteca.model.Livro;
import biblioteca.model.Membro;

import java.util.List;

/**
 * Popula o sistema com alguns dados de exemplo na primeira execução (quando
 * ainda não existe nenhum autor cadastrado), só para a aplicação não abrir
 * com todas as telas vazias. Em execuções seguintes, como os arquivos em
 * data / já existem, este passo é ignorado.
 */
public final class DadosIniciais {

    private DadosIniciais() {
    }

    public static void carregarSeVazio() {
        AutorController autorController = new AutorController();
        if (!autorController.listar().isEmpty()) {
            return;
        }

        autorController.cadastrar("Machado de Assis", "Brasileira");
        autorController.cadastrar("Clarice Lispector", "Brasileira");
        autorController.cadastrar("J.K. Rowling", "Britânica");
        List<Autor> autores = autorController.listar();

        LivroController livroController = new LivroController();
        livroController.cadastrar("Dom Casmurro", "978-8535910663", 1899, autores.get(0), 3);
        livroController.cadastrar("A Hora da Estrela", "978-8532501436", 1977, autores.get(1), 2);
        livroController.cadastrar("Harry Potter e a Pedra Filosofal", "978-8532530783", 1997, autores.get(2), 4);
        List<Livro> livros = livroController.listar();

        MembroController membroController = new MembroController();
        membroController.cadastrar("Ana Souza", "ana.souza@email.com", "(87) 99999-1111");
        membroController.cadastrar("Bruno Lima", "bruno.lima@email.com", "(87) 99999-2222");
        List<Membro> membros = membroController.listar();

        EmprestimoController emprestimoController = new EmprestimoController();
        emprestimoController.registrarEmprestimo(livros.get(0), membros.get(0), 7);
    }
}
