package siga;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== SIGA - Atividade de Persistência e DAO ===\n");
        System.out.println("=== Demonstração da persistência em memória ===");
        ServicoMatricula servico = new ServicoMatricula(new AlunoDAOMemoria());

        servico.matricular(new Aluno("Maria Silva", "2026001", 8.5));
        servico.matricular(new Aluno("João Souza",  "2026002", 6.0));
        System.out.println();

        servico.gerarRelatorio();

        // A regra de negócio funciona: média inválida é rejeitada.
        System.out.println();
        try {
            servico.matricular(new Aluno("Teste Inválido", "2026003", -1));
        } catch (IllegalArgumentException e) {
            System.out.println("Regra de negócio funcionou: " + e.getMessage());
        }

        System.out.println("\n=== Demonstração da persistência em banco de dados (simulado) ===");
        ServicoMatricula servicoBanco = new ServicoMatricula(new AlunoDAOBanco());

        servicoBanco.matricular(new Aluno("Ana Pereira", "2026004", 9.0));
        servicoBanco.matricular(new Aluno("Carlos Lima", "2026005", 7.5));
        System.out.println();

        servicoBanco.gerarRelatorio();

        System.out.println();
        try {
            servicoBanco.matricular(new Aluno("Teste Inválido", "2026006", 11));
        } catch (IllegalArgumentException e) {
            System.out.println("Regra de negócio funcionou: " + e.getMessage());
        }
    }
}
