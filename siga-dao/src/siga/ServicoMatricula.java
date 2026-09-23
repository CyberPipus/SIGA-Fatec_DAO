package siga;

import java.util.List;

public class ServicoMatricula {

    private final AlunoDAO alunoDAO;

    public ServicoMatricula(AlunoDAO alunoDAO) {
        this.alunoDAO = alunoDAO;
    }

    public void matricular(Aluno aluno) {
        if (aluno.getMedia() < 0 || aluno.getMedia() > 10) {
            throw new IllegalArgumentException("Média inválida: " + aluno.getMedia());
        }

        alunoDAO.inserir(aluno);
    }

    public void gerarRelatorio() {
        List<Aluno> alunos = alunoDAO.listarTodos();

        System.out.println("=== Relatório de Alunos ===");
        for (Aluno aluno : alunos) {
            System.out.println(aluno);
        }
    }
}