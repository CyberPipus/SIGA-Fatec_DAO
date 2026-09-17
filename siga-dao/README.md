# SIGA — Atividade de Persistência e padrão DAO (código inicial)

**Técnicas de Programação II (TP2) · Aula 7** — CST em Desenvolvimento de Software Multiplataforma · Fatec de Porto Ferreira

Este é o **código inicial** da atividade prática da Aula 7. Ele contém, de forma **proposital**, comandos SQL misturados à regra de negócio. O programa compila e executa — o problema não é o funcionamento, e sim o acoplamento entre domínio e tecnologia de persistência.

## Estrutura do projeto

```
siga-dao/
└── src/
    └── siga/
        ├── Aluno.java             (entidade de domínio; pronta)
        ├── BancoSimulado.java     (simula o banco; representa a tecnologia)
        ├── ServicoMatricula.java  (contém os problemas a refatorar)
        └── Main.java              (demonstra os problemas em execução)
```

> O `BancoSimulado` existe apenas para o projeto rodar **sem** um servidor de banco instalado. Trate-o como se fosse o driver JDBC real: é a tecnologia da qual a regra de negócio não deveria depender. Não é necessário alterá-lo.

## Como compilar e executar

Pré-requisito: JDK 17 ou superior (`java -version` para verificar).

```bash
# 1. Compilar (a saída vai para a pasta "bin")
javac -d bin src/siga/*.java

# 2. Executar
java -cp bin siga.Main
```

## Os problemas propositais

| Local | Problema | Princípio violado |
|---|---|---|
| `ServicoMatricula.matricular` | Monta e executa SQL dentro do método que valida a matrícula. | **SRP** — dois motivos para mudar |
| `ServicoMatricula` | Depende diretamente da tecnologia de persistência. | **DIP** — depende de implementação, não de abstração |
| `matricular` e `gerarRelatorio` | O mesmo acesso a dados aparece duplicado. | **DRY** — não se repita |

Consequência prática: para testar a regra "a média não pode ser negativa", seria preciso ter um banco disponível. Testar um `if` exigindo infraestrutura é sinal de design acoplado.

## Sua tarefa

Siga as etapas da ficha de atividade prática:

1. **Identificar** o SQL misturado à regra de negócio e registrar, por escrito, as violações de SRP e DIP e a duplicação.
2. **Definir a interface `AlunoDAO`** com as operações do domínio: `inserir`, `buscarPorMatricula`, `listarTodos`, `atualizar` e `remover`. Use o vocabulário do domínio — sem `tabela`, `coluna` ou `INSERT` nos nomes.
3. **Implementar `AlunoDAOMemoria`**, guardando os alunos em um `Map<String, Aluno>` interno. (Opcionalmente, implemente também um `AlunoDAOBanco` que use o `BancoSimulado`.)
4. **Refatorar `ServicoMatricula`** para receber um `AlunoDAO` pelo construtor e remover todo o SQL da classe; ela deve conter apenas regra de negócio.
5. **Demonstrar a troca** de implementação do DAO no `Main`, sem alterar uma linha da regra de negócio.

## Critério de sucesso

Ao final: (a) a classe `ServicoMatricula` **não deve conter nenhum comando SQL**; (b) deve ser possível **trocar a implementação** do DAO passando outro objeto no construtor; e (c) deve ser possível **testar a regra da média sem banco de dados**, usando o DAO em memória.

## Padrão de entrega

Conforme a ficha de atividade prática: identificadores em português, um arquivo `.java` por classe pública, código formatado, entrega no repositório Git com README e commits descritivos. O uso de IA para gerar o código é proibido nesta atividade (ver seção 5.3 da ficha).
