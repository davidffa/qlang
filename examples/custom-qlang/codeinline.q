
code PIL.Example1 is
"[
   -- PIL code from here
   n := integer(read "Number: "); -- type conversion: type(expression)
   write "Number ",n, " is ";
   if n % 2 = 0 then -- = is the comparison operator (as in math)
      writeln "even"
   else
      writeln "odd"
   end;
   -- PIL code to here
]" 
end;

c: code; # code type variable
c := new PIL.Example1;
res: text;
res := execute c; # standard output stored in res
res := "10" | execute c; # uses "10" as standard input
execute c; # output goes to standard output


code PIL.Example2 is 
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

c: code; 
c := new PIL.Example2;
res: text;
res := execute c; 
res := "10" | execute c;
execute c; 

code Example3 is # PIL is not required (type names are the programmers choice)
"<
  name := read "Nome: ";
  writeln name
>"
end;

c1: code; # code type variable
c1 := new Example3;
res: text;
res := execute c1; # standard output stored in res
res := "10" | execute c1; # uses "10" as standard input
execute c1; # output goes to standard output


code PIL.Example4 is 
"{
   n := integer("Number: "); -- read [<prompt>]
   write "Number ",n, " is ";
   if }" l1->"n % 2 = 0" "< then
      writeln "even"
   >" l2->"else" "[
      writeln "odd"
   end;
   ]"
end;

c: code; 
c := new PIL.Example4;
res: text;
res := execute c; 
res := "10" | execute c;
execute c; 

export result to "result.txt";