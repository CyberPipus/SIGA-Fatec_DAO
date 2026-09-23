package siga;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== SIGA - Atividade de Persistência e DAO (código inicial) ===\n");

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
    }
}
