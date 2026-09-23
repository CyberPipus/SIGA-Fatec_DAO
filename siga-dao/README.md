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
R: O SRP é violado em "ServivcoMatricula" quando quem precisa alterar a regra da média é, possivelmente, um professor ou um coordenador do curso, talvez aumentando a quantidade de notas ou aplicando pesos. Porém quem altera a tabela dos dados, dentro do SGBD do negócio, é o setor de TI ou um subsetor deste destinado à gestão dos dados dos alunos. Isto força dois atores distintos a aplicarem mudanças de natureza independente entre si dentro do mesmo método.
Já o DIP é comprometido pela dependência de "ServicoMatricula" ao "BancoSimulado", definindo que um módulo de alto nível está dependente de um de baixo nível enquanto que ambos deveriam estar dependentes de uma abstração. Aqui também há um problema advindo desta dependência: a chamada dos dois métodos estáticos de "BancoSimulado" por "ServicoMatricula" que são resolvidos durante a compilação do código. Isso significa que não temos um objeto a ser trocado, implicando ausência de um polimorfismo e inexistência de um ponto de substituição, o que enrijece o acoplamento a ponto de não ser possível testar a regra da média sem que a classe participe. Qualquer teste do "if" da média arrastaria a persistência junto pois não é possível interceptá-la.
Por fim, em "ServicoMatricula" ocorre a duplicação de acessos ao "BancoSimulado": um pelo método "matricular" e outro pelo "gerarRelatorio". Caso a tabela mude o nome de uma de suas colunas no BD precisamos editar ambos os métodos em "ServicoMatricula", ou seja, a duplicação não é uma cópia literal de ambos os métodos, um insere dados e o outro consulta estes nas mesmas colunas da mesmíssima tabela, mas sim do conhecimento de nomes de ambos estar espalhado. Tal condição impede a busca textual de encontrar os dois. Isso é um cheiro com nome: "Shotgun Surgery".

2. **Definir a interface `AlunoDAO`** com as operações do domínio: `inserir`, `buscarPorMatricula`, `listarTodos`, `atualizar` e `remover`. Use o vocabulário do domínio — sem `tabela`, `coluna` ou `INSERT` nos nomes.
R: Definimos a interface "AlunoDAO" com os métodos "inserir", "buscarPorMatricula", "listarTodos", "atualizar" e "remover", a qual declara o conjunto de acesso aos dados dos alunos no sistema sem definir onde está sua persistência. Nomear os métodos com o vocabulário do domínio é importante para evitar comprometer o caráter abstrato da interface ao declarar uma tabela com "inserirNaTabela", uma vez que a Etapa 3 define que a classe "AlunoDAOMemoria" salva os alunos em um "Map".
Então temos "inserir", "atualizar" e "remover", métodos que não retornam nada pois somente precisam executar suas ações. Enquanto que "buscarPorMatricula" precisa receber uma matrícula do tipo "String" para retornar "Optional<Aluno>"  e, por fim, "listarTodos" retorna uma lista com todos os dados salvos no momento do tipo "Aluno". Também mudamos de linhas de texto que não permitiam acessar as informações contidas nela como dados, forçando quem precisasse utilizar de uma informação, digamos média, como um valor numérico do tipo "double" a localizar o trecho, recortá-lo e convertê-lo para "double". Isso denomina-se "parsing" e fragiliza o código pois basta alguém mudar o formato do "toString()" da classe "Aluno" para que todo recorte quebre. "List<Aluno>" agora permite que todas as informações nela sejam acessíveis como dados, por exemplo com "aluno.getMedia()" devolvendo a média desejada já como um "double". Tudo porque o código inicial convertia objetos em texto muito cedo, obrigando quem recebia a desfazer a conversão.
Além do mais, utilizar "List<Aluno>" no lugar de "Aluno[]" é justificado pois o uso de um vetor em API pública tem um tamanho fixo e obriga quem implementa a converter. Como utilizaremos um "Map" em "AlunoDAOMemoria" para salvar os dados, os valores sairão como coleção e precisariam virar um vetor forçadamente, complicando quem receber pois não conseguirá iterar com a comodidade das coleções, recomendado por Bloch com o Item 28: "Prefer lists to arrays". 
Assim como "Optional<Aluno>" substituir "Aluno buscarPorMatricula (String matricula)" se justifica para evitar a devolução de um valor "null" quando não encontrar um resultado e causar um "NullPointerException" longe da causa pois o chamador esquece de verificar a referência nula, denominada "erro de um bilhão de dólares" por Tony Hoare numa palestra em 2009.
O tipo declara que o resultado pode não existir e o compilador obriga quem chama a resolver a situação, recomendado por Bloch no Item 55: "Return optionals judiciously" para métodos de busca os quais podem não encontrar nada.
Por fim, como a interface não executa nada, a comprovação é o próprio arquivo: cinco assinaturas nela, nenhum termo de SQL evidente nos nomes e nada para indicar onde os dados são salvos.

3. **Implementar `AlunoDAOMemoria`**, guardando os alunos em um `Map<String, Aluno>` interno. (Opcionalmente, implemente também um `AlunoDAOBanco` que use o `BancoSimulado`.)
R: Definimos a classe "AlunoDAOMemoria" que implementa a interface "AlunoDAO" e salva os dados num "Map<String, Aluno>" implementado por um "HashMap", tendo como sua chave a matrícula de cada aluno.
"Nela traduzimos as cinco operações da interface em chamadas ao "Map": "inserir" e "atualizar" receberam a chamada "put" e "remover" recebeu "remove".
"buscarPorMatricula" recebeu "get" com "Optional", para que ao "get" devolver um "null" diretamente por não encontrar a matrícula requerida, "Optional.ofNullable" o encapsula e retorna um objeto "Optional" vazio para expressar a ausência e, na presença, produz um "Optional" com o aluno encontrado.
E "listarTodos" recebeu "values()" com a particularidade de devolver uma "Collection" que tem o papel de ser uma visão viva do "Map". A fim de evitar o vazamento de referência da estrutura interna pela visão, "new ArrayList<>(alunos.values())" assume o papel de criar uma cópia independente."
A necessidade de produzir uma cópia independente da visão viva se justifica por ela negar alterações externas não autorizadas no "Map", como um "dao.listarTodos().clear()", apagando todos os alunos do DAO sem passar pelo método "remover", sem controle e, muito possivelmente, sem ninguém constatar até o relatório sair vazio. O DAO oferece "remover" especificamente para que uma remoção passe por ele e devolver a coleção viva criaria um segundo caminho para alterar fora da interface "AlunoDAO".
Além do mais, "inserir" e "atualizar" apresentam corpos idênticos mas não podem estar unidos na interface por: 1 - criar algo é fundamentalmente diferente de se alterar algo - se unir ambos conceitos em "salvar" ou "salvarOuAtualizar", por exemplo, enfraquece a expressividade da interface e oculta a intenção da camada de negócio; 2 - em uma persistência concreta (SQL, JDBC, JPA/Hibernate, MongoDB) ambas as operações se distinguem quanto aos comandos no motor do banco. "inserir" executa "INSERT INTO alunos...." que cria um registro se não houver um ou uma exceção de chave primária caso já tenha um, enquanto "atualizar" executa "UPDATE alunos SET ... WHERE matricula = ..." que atualiza as colunas e o banco retorna que uma linha foi alterada se o aluno já estiver registrado, ou uma exceção de "Entidade não encontrada" se não estiver registrado.
Por fim, "AlunoDAOMemoria" funciona sem banco de dados nem conexão com um nem SQL nem servidor. Isso permite exercitar a regra da média sem infraestrutura, ou seja, direto na memória RAM e evidenciando o item (c) do critério de sucesso.

4. **Refatorar `ServicoMatricula`** para receber um `AlunoDAO` pelo construtor e remover todo o SQL da classe; ela deve conter apenas regra de negócio.

5. **Demonstrar a troca** de implementação do DAO no `Main`, sem alterar uma linha da regra de negócio.

## Critério de sucesso

Ao final: (a) a classe `ServicoMatricula` **não deve conter nenhum comando SQL**; (b) deve ser possível **trocar a implementação** do DAO passando outro objeto no construtor; e (c) deve ser possível **testar a regra da média sem banco de dados**, usando o DAO em memória.

## Padrão de entrega

Conforme a ficha de atividade prática: identificadores em português, um arquivo `.java` por classe pública, código formatado, entrega no repositório Git com README e commits descritivos. O uso de IA para gerar o código é proibido nesta atividade (ver seção 5.3 da ficha).
