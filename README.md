# Tema **QLang**, grupo **qlang-q01**
-----

## Constituição dos grupos e participação individual global

| NMec | Nome | Participação |
|:---:|:---|:---:|
| 112610 | DAVID FILIPE FERREIRA AMORIM | 23.0% |
| 112841 | FRANCISCA FERREIRA PEDRO SILVA | 20.0% |
| 114646 | FRANCISCO BASTOS ALBERGARIA | 14.0% |
| 113207 | GUILHERME ALEXANDRE QUINTÃO JANEIRO AMARAL | 20.0% |
| 114990 | HENRIQUE MIGUEL LOPES DE FREITAS | 23.0% |

## Relatório

### Características Mínimas

#### Linguagem Principal

- [x] Instrução para definir perguntas dos tipos: hole, open, code−open, code−hole
- [x] Instrução para definir código (em linha ou importado de um ficheiro externo)
- [x] Os tipo de dados inteiro, texto, e fração. Este último corresponde a uma fração inteira.
- [x] Aceita expressões aritméticas standard para os tipos de dados numéricos, bem como a operação de concatenação de texto.
- [x] Instrução de escrita no standard output (com e sem mudança de linha no fim).
- [x] Instrução de leitura de texto a partir do standard input.
- [x] Operadores de conversão entre tipos de dados (text e int)
- [x] Instrução para executar código ou perguntas.
- [x] Instrução para exportar resultados do questionário.
- [x] Verificação semântica do sistema de tipos.

#### Linguagem secundária (PIL)

- [x] A definição de expressões booleanas (predicados) contendo, pelo menos relações de ordem e operadores booleanos (conjunção, disjunção, etc.).
- [x] Instrução condicional
- [x] Instrução de iteração (while & until)

### Características Desejáveis

#### Linguagem Principal

- [x] Permitir a definição de expressões booleanas (predicados) contendo, pelo menos relações de ordem e operadores booleanos.
- [x] Instrução condicional (operando sobre expressões booleanas).
- [x] Instrução iterativa (operando sobre expressões booleanas).
- [x] Perguntas dos tipos: multi−choice e code−output.
- [x] A definição de buracos em código definido em linha.
- [ ] Definição de pergunta composta (que pode incluir a execução de uma ou mais perguntas).
    - [x] A gramática aceita composed questions
    - [x] O código de suporte (classes em python) suporta parcialmente composed questions (apenas a Q1 do p3.q)
    - A geração de código para composed questions não foi feito, devido à falta de tempo

### Características Avançadas

#### Linguagem secundária

- [x] Implementação do estado de "error" quando ocorre um RuntimeError (ex: divisão por zero, conversão inválida de um texto para int)


### Considerações de implementação

#### Linguagem Principal (QLang)

- As perguntas do tipo code-open, como estão definidas nos exemplos são "automatic grading", ou seja, executaríamos ambos os códigos do import e a resposta do utilizador. No entanto, no caso de código que contenha "reads", levantou-se a pergunta de quem introduziria esses inputs (por exemplo, fazer vários test-cases definidos na gramática com pares input/output). Portanto nesse tipo de questões decidimos implementar como "manual grading"
- Não existia qualquer exemplo de questões do tipo `code-output`, portanto definimos que as questões desse tipo teriam um import de código PIL e seriam de automatic grading. Assim, o código PIL do import vai ser mostrado ao utilizador e após a resposta do utilizador, o código do import irá ser executado e o output vai ser comparado com o que o utilizador escreveu, validando assim a sua resposta.

#### Linguagem Secundária

- Na definição da gramática, decidimos torná-la o mais genérica possível, por isso, sintaticamente podemos ter uma adição entre strings e números, booleans e números etc. Por isso, todos esses erros foram resolvidos semanticamente, no interpretador.
- O visitor da linguagem secundária pode lançar uma exceção (SemanticException), caso detecte um erro semântico, devolvendo uma mensagem de erro.
- Na ocorrência de runtime errors (ex: divisões por zero, conversão inválida de texto para int) o interpretador entra num estado de erro excecional, em que as instruções não são executadas, até que a variável global "error" seja consumida. No entanto a expressão vai ser executada (para podermos ver no visitor se o error foi consumido ou não)
    - Exemplo (supondo que está em error state): `writeln 1+3;`. Não vai ser nada impresso no stdout, mas a expressão 1+3 vai ser executada.
- O visitor da linguagem secundária pode lançar uma exceção genérica (Exception) caso entre num estado _unreachable_ isto é, por exemplo, se alterarmos a gramática para aceitar um novo operador binário e ainda não o tivermos implementado nesse visitor, o visitor vai lançar essa exceção a dizer que esse novo operador não está implementado.
- Variáveis / Escopos
    - As variáveis não estão ligadas diretamente a um tipo, isto é, podemos ter uma variável que seja atribuído um inteiro e posteriormente ser atribuido ao mesmo identificador uma string.
    - Foi implementada uma [Tabela de Símbolos](./src/SymbolTable.py), para auxiliar a gestão de variáveis e escopos.
    - Quando se entra num bloco (por ex: conjunto de statements delimitado por then...end numa instrução condicional) é criada uma nova instância da tabela de símbolos que contém um atributo parent para a tabela de símbolos "pai", quando se sai do bloco essa tabela é destruída, e a tabela atual passa a ser a parent da tabela destruída.
    - Quando é feita uma atribuição dentro de um bloco, se o símbolo já existir na tabela ou numa tabela "parent", o valor dessa variável é alterado, caso contrário, é criado um novo símbolo na tabela correspondente ao bloco atual.


## Contribuições

- Gramática Principal (qlang)
    - Francisca Silva
    - Henrique Freitas

- Análise Semântica (qlang) [Ver Pull Request](https://github.com/detiuaveiro/qlang-q01/pull/8)
    - Francisco Albergaria
    - David Amorim

- Código (classes) de suporte aos StringTemplates (compilação à mão) [Ver Pull Request](https://github.com/detiuaveiro/qlang-q01/pull/6)
    - Henrique Freitas

- Geração de código (StringTemplates e Visitor de geração de código)
    - Guilherme Amaral
    - Henrique Freitas
    - Francisca Silva

- Gramática da linguagem secundária (PIL) [Ver Pull Request](https://github.com/detiuaveiro/qlang-q01/pull/1)
    - David Amorim

- Interpretador da linguagem secundária (PIL) [Ver Pull Request](https://github.com/detiuaveiro/qlang-q01/pull/1)
    - David Amorim

- Exemplos extra de programas PIL [exemplos](./examples/custom)
    - David Amorim

- Exemplos extra de programas QLang [exemplos qlang](./examples/custom-qlang)
    - Francisca Silva

- Exemplos erros semânticos QLang [exemplo](./examples/semantic-tester.q)
    - Henrique Freitas
