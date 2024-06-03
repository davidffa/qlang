multi-choice Algorithm.Cond1 is 
  uses code PIL.Example1 end; 
  println PIL.Example1;
  println "Dada a entrada " "10"|program ", este programa vai escrever no standard output:"; 
  choice "123456789\n" end;
  choice 2/10,'246810' end;
  choice "2468" end
end; 

multi-choice Algorithm.Cond2 is
    println PIL.Example1;
    println "Dada a entrada " "10"|program ", este programa vai escrever no standard output:"; 
    uses code PIL.Example1 end; 
    choice "123456789\n" end;
    choice 2/10,'246810' end;
    choice "2468" end
end;

export result to "result.txt";