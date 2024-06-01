from classes import *

def __main__():
    name:str=""
    id:int=None
    result = Result()
    result.setName(input("Nome:"))
    id = int(input("ID:"))
    result.setId(id)
    Print(["Nome: "," ID: "],[Element(name),Element(id)]).Answer()
    text="""-- PIL code from here
    n := integer(read "Number: "); -- type conversion: type(expression)
    write "Number ",n, " is ";
    if n % 2 = 0 then -- = is the comparison operator (as in math)
       writeln "even"
    else
       writeln "odd"
    end;
    -- PIL code to here"""
    pil = Group("PIL",[Group("Example1",Code(text))])
    c = pil.getChild("Example1")
    
    p = Print(["Complete o seguinte código."],[],True)
    r1 = Rule("n % 2 = 0",10)
    r2 = Rule("else",5,2)
    algorithm = Group("Algorithm",[Group("Code1",CodeHoleQuestion([p],c,[r1,r2]))])
    al = algorithm.getChild("Code1")
    
    al.Answer()
    result.addQuestion("Code1",al)
    
    
    
    result.export_file("result-p2.txt")
if __name__ == "__main__":
   __main__()