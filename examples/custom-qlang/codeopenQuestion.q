q: question; 

code-open Question.Code1 is
   uses code from "even-odd.pil" end;
   println "Implemente um programa que : ";
   println "pedindo um número inteiro do utilizador com o texto 'Number: ', escreva na consola se este é par (even) ou ímpar (odd).";
   println "E que pedindo um número inteiro do utilizador com o texto 'Number 2'";
   print "escreva na consola se este é par  (even) ou ímpar (odd)" "."
end; 

q := new Question.Code1;
g := execute q; 

code-open Question.Code2 is
   println "Implemente um programa que : ";
   println "pedindo um número inteiro do utilizador com o texto 'Number: ', escreva na consola se este é par (even) ou ímpar (odd).";
   println "E que pedindo um número inteiro do utilizador com o texto 'Number 2'";
   print "escreva na consola se este é par  (even) ou ímpar (odd)" ".";
   uses code from "even-odd.pil" end
end; 

q := new Question.Code2;
g := execute q; 

export result to "result.txt";