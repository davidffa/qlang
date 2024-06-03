q: question; 

open OpenQuestion is
  println "Defina a estrutura de dados lista ligada.";
  print "Responda com atenção" "!"
end; 

q := new OpenQuestion;
execute q; 

export result to "result.txt";
