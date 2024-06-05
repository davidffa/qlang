from classes import *
def main():

    Question_q1 = Group("Question", [Group("q1", HoleQuestionClass([Print(["A atribuição de valor em PIL usa o operador ","."], [Hole(":=")],True)]))])
    _q = Question_q1.getChild("q1")
    _g = _q.Answer().Grade()
    OpenQuestion = Group("OpenQuestion", OpenQuestionClass([Print(["Defina a estrutura de dados lista ligada."], [],True)]))
    _q = OpenQuestion.getChild()
    _q.Answer()
    Question_Code1 = Group("Question", [Group("Code1", CodeOpenQuestionClass([Print(["Implemente um programa que, pedindo um número inteiro do utilizador com o texto 'Number: ', escreva na consola se este é par (even) ou ímpar (odd)."], [],True)],Code("../examples/even-odd.pil",[])))])
    _q = Question_Code1.getChild("Code1")
    _g = _q.Answer().Grade()
    Result.export_file("result.txt")

if __name__ == "__main__":
    main()