package adapters.saida;

import aplicacao.RepositorioMatriculas;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RepositorioEmArquivo implements RepositorioMatriculas {
    private final Path arquivo;

    public RepositorioEmArquivo(Path arquivo) {
        this.arquivo = arquivo;
    }

    public void salvar(String aluna) {
        List<String> alunas = listar();
        if (alunas.contains(aluna)) return;
        alunas.add(aluna);
        try {
            Files.createDirectories(arquivo.toAbsolutePath().getParent());
            Files.write(arquivo, alunas, StandardCharsets.UTF_8);
        } catch (IOException erro) {
            throw new UncheckedIOException(erro);
        }
    }

    public List<String> listar() {
        try {
            return Files.exists(arquivo)
                ? Files.readAllLines(arquivo, StandardCharsets.UTF_8)
                : new ArrayList<>();
        } catch (IOException erro) {
            throw new UncheckedIOException(erro);
        }
    }
}
