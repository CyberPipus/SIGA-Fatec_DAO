package siga;

import java.util.ArrayList;
import java.util.List;

public class BancoSimulado {

    private static final List<String> LINHAS = new ArrayList<>();

    /** Simula a execução de um comando de escrita (INSERT, UPDATE, DELETE). */
    public static void executar(String sql, String linha) {
        System.out.println("[SQL] " + sql);
        LINHAS.add(linha);
    }

    /** Simula a execução de uma consulta, devolvendo as linhas gravadas. */
    public static List<String> consultar(String sql) {
        System.out.println("[SQL] " + sql);
        return new ArrayList<>(LINHAS);
    }
}
