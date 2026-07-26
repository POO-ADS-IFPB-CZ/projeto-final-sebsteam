package biblioteca.dao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Classe base para os DAOs que persistem dados em arquivos de texto.
 * Concentra a lógica repetida de criar a pasta/arquivo de dados e de ler e
 * escrever as linhas do arquivo, para que cada DAO concreto só precise se
 * preocupar em transformar suas entidades em linhas de texto e vice-versa.
 *
 * Cada linha do arquivo representa um registro, com os campos separados
 * por ";" (um formato similar a um CSV simplificado).
 */
public abstract class ArquivoDao {

    private static final String PASTA_DADOS = "data";

    private final Path caminhoArquivo;

    protected ArquivoDao(String nomeArquivo) {
        this.caminhoArquivo = Path.of(PASTA_DADOS, nomeArquivo);
        garantirQueArquivoExiste();
    }

    private void garantirQueArquivoExiste() {
        try {
            Path pasta = Path.of(PASTA_DADOS);
            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }
            if (!Files.exists(caminhoArquivo)) {
                Files.createFile(caminhoArquivo);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível preparar o arquivo de dados: " + caminhoArquivo, e);
        }
    }

    /**
     * Lê todas as linhas não vazias do arquivo de dados desta entidade.
     */
    protected List<String> lerLinhas() {
        try {
            return Files.readAllLines(caminhoArquivo).stream()
                    .filter(linha -> !linha.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler o arquivo de dados: " + caminhoArquivo, e);
        }
    }

    /**
     * Substitui todo o conteúdo do arquivo pelas linhas informadas.
     * É uma estratégia simples: a cada alteração, o arquivo inteiro é
     * reescrito com a lista atualizada de registros.
     */
    protected void escreverLinhas(List<String> linhas) {
        try {
            Files.write(caminhoArquivo, linhas);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível escrever no arquivo de dados: " + caminhoArquivo, e);
        }
    }

    /**
     * Calcula o próximo id disponível (maior id existente + 1).
     */
    protected int proximoId(List<Integer> idsExistentes) {
        return idsExistentes.stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
}
