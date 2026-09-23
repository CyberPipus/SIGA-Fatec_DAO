package siga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AlunoDAOBanco implements AlunoDAO {
    private final Map<String, Aluno> alunos = new HashMap<>();

    @Override
    public void inserir(Aluno aluno) {
        BancoSimulado.executar("INSERT INTO alunos (nome, matricula, media) VALUES ('" + aluno.getNome() + "', '" + aluno.getMatricula() + "', " + aluno.getMedia() + ")", aluno.toString());
        alunos.put(aluno.getMatricula(), aluno);
    }

    @Override
    public Optional<Aluno> buscarPorMatricula(String matricula) {
        return Optional.ofNullable(alunos.get(matricula));
    }

    @Override
    public List<Aluno> listarTodos() {
        BancoSimulado.consultar("SELECT * FROM alunos");
        return new ArrayList<>(alunos.values());
    }

    @Override
    public void atualizar(Aluno aluno) {
        BancoSimulado.executar("UPDATE alunos SET nome = '" + aluno.getNome() + "', media = " + aluno.getMedia() + " WHERE matricula = '" + aluno.getMatricula() + "'", aluno.toString());
        alunos.put(aluno.getMatricula(), aluno);
    }

    @Override
    public void remover(String matricula) {
        BancoSimulado.executar("DELETE FROM alunos WHERE matricula = '" + matricula + "'", "Removendo aluno com matrícula: " + matricula);
        alunos.remove(matricula);   
    } 
}