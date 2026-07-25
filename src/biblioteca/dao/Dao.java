package biblioteca.dao;

import java.util.List;

/**
 * Contrato padrão de CRUD (Create-Read-Update-Delete) que todo DAO
 * (Data Access Object) da aplicação deve seguir.
 *
 * @param <T> tipo da entidade manipulada pelo DAO
 */
public interface Dao<T> {

    List<T> listarTodos();

    T buscarPorId(int id);

    void inserir(T entidade);

    void atualizar(T entidade);

    void excluir(int id);
}
