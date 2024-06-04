q: question; # question type variable
g: fraction; # fraction type variable (an integer fraction)

# ; is a (required) statement separator

# define a question (no execution involved)
# question types: multi-choice, hole, open, code-hole, code-open, code-output
hole Question.q1 is
  println "A atribuição de valor em PIL usa o operador " ans->":=" "."
  # the text preceded with a label (ans in this example) is omitted from the user
  # (and provides information for automatic grading).
end; # automatic grading

g := new Question.q1; # has to throw an error fraction type not compatible with question type
q := execute q1; # execute question returns error because q1 doesnt exist in this scope and return type is not compatible with question
# executing a question impacts on overall (result) questionnaire grading
# g is the grade of this q execution

open OpenQuestion is
  println "Defina a estrutura de dados lista ligada."
end; # manual grading

q := new OpenQuestion;
execute q; # execute question stored in variable q (result would be: undefined)

code-open Question.Code1 is
   uses code from "even-odd.pil" end;
   println "Implemente um programa que, pedindo um número inteiro do utilizador com o texto 'Number: ', escreva na consola se este é par (even) ou ímpar (odd).";
end; # automatic grading

q := new Question.Code1;
g := execute q; # execute question stored in variable q


if q /= 1 then # error, question type not compatible with boolean type
    println "Question.Code1 failed!";
elseif g /= 2 then
    println "Question.Code1 failed!";
else
    println Question.Code1;
end;


export result to "result.txt"; # export (to a file) current questionnaire results
# export command can be executed anywhere!
