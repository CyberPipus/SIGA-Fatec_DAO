package siga;

import java.util.List;
import java.util.Optional;

public interface AlunoDAO {
    void inserir(Aluno aluno);
    Optional<Aluno> buscarPorMatricula(String matricula);
    List<Aluno> listarTodos();
    void atualizar(Aluno aluno);
    void remover(String matricula);
}