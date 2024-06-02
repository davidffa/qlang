from classes import *

def __main__():
    q:Question
    g:FractionInt= None
    result = Result()
    result.setName(input("Nome:"))
    id = int(input("ID:"))
    result.setId(id)
    
    
    h1 = Hole(":=")
    h2 = Hole(":")
    p = Print(["A atribuição de valor em PIL usa o operador ","e o operador de tipo","."], [h1,h2],True)

    group = Group("Question",[Group("q1",HoleQuestion([p]))])
    q = group.getChild("q1")
    q.Answer()
    
    result.addQuestion("q1",q)


    pOpen= Print(["Defina a estruutra de dados lista ligada."])
    open=Group("Open", OpenQuestion([pOpen]))
    q = open.getChild("Open")
    q.Answer()

    result.addQuestion("Open",q)

    pCOpen= Print(["Implemente um programa que, pedindo um número inteiro do utilizador com o texto 'Number: ', escreva na consola se este é par (even) ou ímpar (odd)."])
    codeSec= Code("../examples/even-odd.pil")

    group.addChildren(Group("Code1",CodeOpenQuestion([pCOpen],codeSec)))

    q = group.getChild("Code1")
    q.Answer()
    
    result.addQuestion("Code1",q)
    
    result.export_file()

if __name__ == "__main__":
    __main__()