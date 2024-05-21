q: question; 


hole Question.q1 is
	println "Responda à seguinte questão:";
	println "A atribuição de valor em PIL usa o operador " ans->":=" "."
end; 

q := new Question.q1;
g := execute q; 

hole Question.q2 is
	println "Responda à seguinte questão:";
	println "A instrução de separação em PIL usa o operador " ans->";" " podendo ser omitida na última instrução."
end; 

q := new Question.q2;
g := execute q; 

hole Question.q3 is
	println "Responda à seguinte questão:";
	println "A instrução de separação em PIL usa o operador " ans->";" ;
	print "podendo ser onitida na última instrução."
end; 

q := new Question.q3;
g := execute q; 

hole Question.q4 is
	println "Responda à seguinte questão:";
	println "A instrução de separação em PIL usa o operador " ans->";" ;
	print "podendo ser onitida na última instrução.";
	println "A atribuição de valor em PIL usa o operador " ans->":=" "."
end; 

q := new Question.q4;
g := execute q; 

export result to "result.txt";
