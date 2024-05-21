code PIL.Example1 is # default is code of PIL type (the only one to be implemented)
"[
   -- PIL code from here
   n := integer(read);
   i := 1;
   loop until i = n do
      if i % 2 = 0 then
         write i
      end;
      i := i + 1
   end
   -- PIL code to here
]"
end;

code-hole Algorithm.Code1 is
   println "Complete o seguinte código.";
   uses code PIL.Example1
      10,"n % 2 = 0"; 
      5,"else" line 2 
   end;
end; 

execute new Algorithm.Code1;

code PIL.Example2 is # include hole definitions (identified by labels)
"{
   n := integer("Number: "); -- read [<prompt>]
   write "Number ",n, " is ";
   if }" l1->"n % 2 = 0" "{ then
      writeln "even"
   }" l2->"else" "{
      writeln "odd"
   end;
}"
end;

code-hole Algorithm.Code2 is
   println "Complete o seguinte código.";
   uses code PIL.Example2
      10,l1; 
      5,l2 
   end;
end; 

execute new Algorithm.Code2;

code-hole Algorithm.Code3 is
    uses code PIL.Example2
        10, "write" line 1;
        10, l1;
        5, l2
    end;
    println "Complete o código acima."
end;

execute new Algorithm.Code3;

export result to "result.txt";