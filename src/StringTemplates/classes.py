from os import read




class FractionInt :

    def __init__(self, num, den):
        self.num = num
        self.den = den

    def __percent__(self):
        return self.num / self.den * 100

    def __str__(self):
        return str(self.__percent__()) + "%"

    def __add__(self, other):
        return FractionInt(self.num * other.den + other.num * self.den, self.den * other.den)

    def __sub__(self, other):
        return FractionInt(self.num * other.den - other.num * self.den, self.den * other.den)

    def __reduce__(self):
        def gcd(a, b):
            while b:
                a, b = b, a % b
            return a
        g = gcd(self.num, self.den)
        return FractionInt(self.num // g, self.den // g)
    

class Result:

    def __init__(self, name="User", id=0) :
        self.name = name
        self.id = id
        self.result = 0
        

    def add_result(self, result):
        self.result += result

    def get_result(self):
        return self.result

    def export_file(self, name="result.txt"):
        with open(name, "w") as file:
            file.write("User: "+self.name + ";\nID: " + str(self.id) + ";\n Grade: " + str(self.result) + "\n")
        file.close()

class Question:
    def __init__(self, name):
        self.name = name
    def __str__(self):
        return self.name


class GroupQuestion:
    def __init__(self,name,parent=None, questions=[],QuestionClass=Question):
        self.name= name
        self.parent= parent
        self.questions = [QuestionClass(question) for question in questions]

    def getGroups(self,name):
        for group in self.groups:
            if group == name:
                return group
        return None

    def getQuestion(self,question):
        for q in self.questions:
            if q == question:
                return q
        return None

    def addQuestion(self, question):
        self.questions.append(question)

    def addGroup(self, group):
        self.questions.append(group)




class Answer :
    def __init__(self, answerCorrect):
        self.answer = answerCorrect
        self.response=None
        self.correct=None
    def fillAnswer(self):
        self.response = read()
        if self.correct==None and self.response == self.answer:
            self.correct=True
        self.correct=False
    def isCorrect(self):
        return self.correct
    def __str__(self):
        return self.answer

class HoleQuestion(Question):
    def __init__(self, name,question,answers=[]):
        super().__init__(name)
        self.questions = question
        self.answers = [Answer(i) for i in answers]
        self.autograding = True
        self.grade = 0
    def answerQuestion(self):
        for answer in self.answers:
            answer.fillAnswer()
    def gradeQuestion(self):
        self.grade= FractionInt(sum([1 if answer.isCorrect() else 0 for answer in self.answers]),len(self.answers))
        Result.add_result(self.grade)
        return self.grade
    def __str__(self):
        return '___'.join([self.questions])

class OpenQuestion(Question):

    def __init__(self, name,question):
        super().__init__(name)
        self.questions = question
        self.answer=None
        self.autograding = False
        self.grade = -1
    def getQuestions(self):
        return '\n'.join(self.questions)

    def answerQuestion(self):
        self.answer=read()

    def gradeQuestion(self):
        while self.grade<0 and self.grade > 100:
            self.grade = FractionInt(int(read()),100)

    def __str__(self):
        return '\n'.join([self.questions])
    
class CodeOpenQuestion(Question):
    def __init__(self, name,question):
        super().__init__(name)
        self.questions = question
        self.answer=None
        self.autograding = True
        self.grade = -1
    

