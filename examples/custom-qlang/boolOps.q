x: integer;
y: integer;

x := 1;
y := 3;

if x = 1 and y = 3 then
    println "Should print";
end;

if x = 1 xor y = 3 then
    println "Should not print";
else
    println "Should print";
end;

if x = 1 implies 2 = 2 then
    println "Should print";
else
    println "Should not print";
end;


if 1 = 2 or 2 = 3 then
    println "Should not print";
elseif 1 = 1 and 2 /= 2 then
    println "Should not print";
elseif 2 = 2 xor 2 /= 2 then
    println "Should print";
else
    println "Should not print";
end;
