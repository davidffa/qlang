from classes import *

def __main__():
    # name:str=""
    # id:int=None
    result=Result()
    # result.setName(input("Nome:"))
    # id = int(input("ID:"))
    
    # Print(["Nome: "," ID: "],[Element(name),Element(id)]).Answer()
    text=""" -- PIL code from here
            n := integer(read);
            i := 1;
            loop until i = n do
                if i % 2 = 0 then
                    write i
                end;
                i := i + 1
            end
            -- PIL code to here
            """


    pil = Group("PIL",[Group("Example1",Code(text))])
    c = pil.getChild("Example1")

    c1 = Choice("123456789\\n")
    c2 = Choice("246810",FractionInt(2,10))
    c3 = Choice("2468")
    codeP = Print([],[c],True)
    p = Print(["Dada a entrada "," este programa vai escrever no standard output:"],[Element(10)],True)
    al = Group("Algorithm",[Group("Cond1",MultipleChoiceQuestion([p,codeP],c,[c1,c2,c3]))])
    cond1 = al.getChild("Cond1").clone()
    # cond1.Answer()
   
    # result.addQuestion("Cond1",cond1)
    
    text2=["""n := integer("Number: "); -- read [<prompt>]
   write "Number ",n, " is ";
   if ""","""then
      writeln "even"\n""","""\nwriteln "odd"
   end;"""]
    h1 = Hole("n % 2 = 0","l1")
    h2 = Hole("else","l2")
    code1=Code(text2,[h1,h2])
    p1=Print(["Complete o seguinte código."],[],)
    rule1 = Rule("l1",10)
    rule2 = Rule("l2",5)
    pil.addChildren(Group("Example2",code1))
    al.addChildren(Group("Code1",CodeHoleQuestion([p1],code1,[rule1,rule2])))
    codeQuestion = al.getChild("Code1").clone()
    # codeQuestion.Answer()

    # result.addQuestion("Code1",codeQuestion)
    
    hole1= Hole(":=")
    com = Print(["A atribuição de valor em PIL usa o operador","."],[hole1],True)
    question= Group("Question",[Group("q1",HoleQuestion([com]))])
    q1 = question.getChild("q1").clone()
    # q1.Answer()

    # result.addQuestion("q1",q1)

    #     text3="""name := read "Nome: ";
    #   writeln name"""
    #     code2 = Group("Example3",Code(text3))
        
    #     c1 = code2.getChild("Example3")
    #     res:str = c1.execute()
    #     res = c1.execute(["10"])
    #     print(c1.execute())

    evenFile = Code("../examples/even-odd.pil")
    r1 = Rule("n % 2 = 0",10)
    r2 = Rule("else",5,2)
    al.addChildren(Group("Code2",CodeHoleQuestion([p1],evenFile,[r1,r2])))
    code2 = al.getChild("Code2").clone()
    # code2.Answer()
    
    # result.addQuestion("Code2",code2)


    q2G = Group("Q2",ComposedQuestion([FractionInt(1,2),FractionInt(1,4),FractionInt(1,4)],[al,al,al]))

    q2 = q2G.getChild("Q2")
    
    q2.Answer()
    
    result.addQuestion("Q2",q2)


    result.export_file("result-p3.txt")
if __name__ == "__main__":
    __main__()