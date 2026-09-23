package siga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AlunoDAOMemoria implements AlunoDAO {
    private final Map<String, Aluno> alunos = new HashMap<>();

    @Override
    public void inserir(Aluno aluno) {
        alunos.put(aluno.getMatricula(), aluno);
    }

    @Override
    public Optional<Aluno> buscarPorMatricula(String matricula) {
        return Optional.ofNullable(alunos.get(matricula));
    }

    @Override
    public List<Aluno> listarTodos() {
        return new ArrayList<>(alunos.values());
    }

    @Override
    public void atualizar(Aluno aluno) {
        alunos.put(aluno.getMatricula(), aluno);
    }

    @Override
    public void remover(String matricula) {
        alunos.remove(matricula);
    } 
}
