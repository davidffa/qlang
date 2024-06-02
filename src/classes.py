import copy
import random
from antlr4 import InputStream
from pilMain import RunCode
class Group:
    def __init__(self, name,children):
        #children is either a list of subGroups or a question
        self.name = name
        self.children = children
    def addChildren(self, child):
        if isinstance(self,Group):
            self.children.append(child)
    def getChildrenQuestion(self,questions=set()):
        #questions is a set of Objects of type Questions
        if isinstance(self,Question):
            return self
        elif not isinstance(self.children,Question):
            for q in self.children:
                if isinstance (q,Group):
                    questions.add(q.getChildrenQuestion(questions))
                elif isinstance(q,Question):
                    questions.add(q)
        else:
            return self.children
        return questions
    def getChild(self,name:str=None):
        if  isinstance(self.children,Question) or isinstance(self.children,Code) :
            return self.children
        for c in self.children:
            if isinstance(c,Group):
                # print(c)
                # print(c.name == name and (isinstance(c.children,Question) or isinstance(c.children,Code)))
                if c.name == name  and (isinstance(c.children,Question) or isinstance(c.children,Code)):
                    return c.children
                elif c.name == name:
                    return c
        return None
    def __str__(self):
        return self.name


class FractionInt :
    def __init__(self, num, den):
        self.num = num
        self.den = den

    def __percent__(self):
        return (self.num / self.den) * 100

    def __str__(self):
        return str(round(self.__percent__(),2)) + "%"

    def __add__(self, other):
        return FractionInt(self.num * other.den + other.num * self.den, self.den * other.den)

    def __sub__(self, other):
        return FractionInt(self.num * other.den - other.num * self.den, self.den * other.den)
    def __prod__(self,other):
        return FractionInt(self.num * other.num,self.den * other.den)
    def __reduce__(self):
        def gcd(a, b):
            while b:
                a, b = b, a % b
            return a
        g = gcd(self.num, self.den)
        return FractionInt(self.num // g, self.den // g)
    def setNum(self,num):
        self.num=num
        return self
    def setDen(self,den):
        self.den=den
        return self
    def __deepcopy__(self, memo):
        # Fractions are immutable, so return self
        return FractionInt(self.num, self.den)
class Result:
    def __init__(self, name="result",id = 0) :
        self.name = name
        self.id = id
        self.result = FractionInt(0,1)
        self.question ={}
    def setId(self,id):
        self.id=id
    def getId(self):
        return self.id
    def setName(self,name):
        self.name=name
    def getName(self):
        return self.name
    def addQuestion(self,name,question):
        self.question[question] = {'name':name,'grade':None} ;
    def get_result(self):
        tempScore= FractionInt(0,1)
        for q ,value in self.question.items():
            if q.autoGrading:
                v = q.Grade()
                tempScore = tempScore.__add__(v)
                self.question[q]['grade'] = v
            else:
                self.question[q]['grade'] = "Manual grading required"
        self.result=tempScore.setDen(tempScore.den)
        return self.result
    def export_file(self, name="result.txt"):
        self.get_result()
        print("Exporting file...")
        with open(name, "w") as file:
            file.write("User: "+self.name + "; ID: " + str(self.id) + "; Grade: " + str(self.result.setDen(self.result.den*len(self.question))) + "\n")
            for q,value in self.question.items():
                file.write(value['name'] + ":" + str(value['grade']) + "\n")
        print(f"File exported successfully, {self.name} got a grade of {self.result}")
class Question:
    def __init__(self):
        self.autoGrading = False
        self.answer=None
        self.score=FractionInt(0,1)
    def Grade(self):
        if not self.autoGrading:
            self.score = FractionInt(0,1)
        return self.score
    def Answer(self):
        self.answer=input(f"Answer the question:")
    def clone(self):
        return copy.deepcopy(self)
    def __str__(self):
        return "Esta é uma questão"



class Grading():
    def __init__(self):
        self.TotalScore=FractionInt(0,1)
    def getScore(self):
        return self.TotalScore
    def setScore(self,grade):
        self.TotalScore=grade
    def addToScore(self,value:FractionInt):
        self.TotalScore.add(value)
    def __str__(self):
        return str(self.TotalScore)

class HoleQuestion(Question):
    def __init__(self,print):
        # name is the identifier of the question
        # print is the text of the question or the subparts of more then one question,
        super().__init__()
        self.print=print
        self.autoGrading = True
    def Answer(self):
        for p in self.print:
            p.Answer()
        return self
    def Grade(self):
        for p in self.print:
            sectionG = p.Grade()
            if sectionG.num == 0 and self.score.num == 0:
                self.score.setNum(sectionG.num)
                self.score.setDen(sectionG.den*self.score.den)
                continue
            self.score = self.score.__add__(sectionG)
        return self.score
    def __str__(self):
        return "This is a hole question"

class OpenQuestion(Question):
    def __init__(self,prints):
        super().__init__()
        self.prints = prints
        self.answer=None
    def Answer(self):
        for p in self.prints:
            p.Answer()
        self.answer=input(f"Answer here:")
        return self
    def __str__(self):
        return "this is a opene question"

# Por completar:
class CodeOpenQuestion(Question):
    def __init__(self,print,code):
        super().__init__()
        self.print = print
        self.answer=None
        self.code = code
    def Answer(self):
        for p in self.print:
            p.Answer()
        print(self.code.execute())
        self.answer=input(f"Answer the question (PIL Code):\n")
        return self
    def __str__(self):
        return "this is a code-open question"
class CodeHoleQuestion(Question):
    def __init__(self,print,code,rules=None):
        # Pil can be either a  Pil object or the name of the file .pil
        # print is the text of the question or the subparts of more then one question
        super().__init__()
        self.autoGrading = True
        self.print = print
        self.grade=FractionInt(0,1)
        self.code = code # this is a code object
        self.rules=rules # if the list of rules is empty does that mean that the question has holes ?
    def Answer(self):
        for p in self.print:
            p.Answer()
        if len(self.code.elements) == 0 and self.rules is not None:
            removingHoles=[]
            for r in self.rules:
                removingHoles.append(r.getValue())
            codeTextHoled = self.code.createHoles(removingHoles)
            print(codeTextHoled)
        else:
            self.code.printCode()
        self.code.Answer()
        return self
    def Grade(self):
        g = self.code.Grade(self.rules)
        if isinstance(g, FractionInt) and g.num!=0 and g.den!=1:
            self.grade=g
        else:   
            self.grade = FractionInt(0,1)
        return self.grade
    def __str__(self):
        return "this is a code-hole question"

class MultipleChoiceQuestion(Question):
    def __init__(self,print,code,ops):
        # the print is list of Print objects
        # the ops is a list of choice objects each options is basically a value and that value can be the correct one based on the 
        super().__init__()
        self.code=code
        self.print=print
        self.options=ops
        self.autoGrading = True
        self.answer=None
        self.correctAnswer=None
        self.input=[] # this is the input for the code it acts as stdin

    def Grade(self):
        if self.correctAnswer is not None and self.answer is not None:
            for choice in self.options:
                if choice.Equals(self.answer) :
                    if self.answer == self.correctAnswer:
                        return FractionInt(1,1)
                    else:
                        return choice.getGrade() if choice.grade != None else FractionInt(0,1)

    def Answer(self):
        for p in self.print:
            p.Answer()
            for el in p.getElements():
                if isinstance(el,Element) and not (isinstance(el,Hole) or isinstance(el,Choice)):
                    self.input.append(el.getValue())
        print("Select one of the following options:\n")
        for i ,option in enumerate(self.options):
            print(f"{i+1}) {option}")
        pos =-1
        while(pos < 0 or pos>= len(self.options)):
            pos = int(input())-1
        self.answer = self.options[pos].getValue()
        resultCode= self.code.execute(self.input)
        self.correctAnswer= resultCode.lstrip().replace("\n"," ")
        return self
    def __str__(self):
        return "This is a multiple choice question"

class ComposedQuestion(Question):
    def __init__(self,grades,groups):
        super().__init__()
        assert(len(grades) == len(groups))
        self.autoGrading=True
        self.AllPossibleQuestions = {}
        self.answered={}
        self.grades=grades
        self.overAllG=FractionInt(0,1);
        for i in range(len(grades)):
            self.AllPossibleQuestions[i] =[i.clone() for i in list(groups[i].getChildrenQuestion())]
    def Answer(self):
        print("Composed Question\n")
        for indx,q in self.AllPossibleQuestions.items():
            print(f"\n---- Question {indx+1} ----\n")
            if isinstance(q,Question):
                q.Answer()
                self.answered[q]=None
            else:
                randomQ = random.choice(q)
                randomQ.Answer()
                self.answered[randomQ] = None
                self.AllPossibleQuestions[indx].remove(randomQ)
        return self
    def Grade(self):
        indx=0
        for g,p in self.answered.items():
            print(self.grades[indx])
            qG = g.Grade()
            print(qG)
            gradeQ=self.grades[indx].__prod__(qG)
            print(f"{g}, grade : {gradeQ}")
            self.answered[g] = gradeQ
            if gradeQ.num != 0 and self.overAllG.num == 0 :
                #para que o produto não seja sempre nulo
                self.overAllG.setNum(gradeQ.num).setDen(gradeQ.den)
            elif gradeQ.num!=0:
                self.overAllG = self.overAllG.__add__(gradeQ)
                print(f"Overall : {self.overAllG}")
            indx+=1
        return self.overAllG
    def __str__(self):
        return "This is a composed question!"

class Print():
    def __init__(self,text,elements=[],ln=False):
        # the text parameter is a list that represnts all the parts of the question's text
        # the elements list includes all the sub-elements as the components of the print statment
        # it is assumed that the elements and the text's list is already sorted
        self.elements=elements
        self.text=text
        self.ln= "\n" if ln else ""
        self.points= FractionInt(0,len(elements))
    def getElements(self):
        return self.elements
    def Answer(self):
        if len(self.elements) == 1 and isinstance(self.elements[0],Code) and len(self.text)==0:
            code = self.elements[0]
            code.printCode()
            return
        text=""
        for i in range(len(self.text)):
            text += self.text[i]
            if i < len(self.elements) and i >=0:
                if isinstance(self.elements[i],Hole):
                    text+="__"
                elif isinstance(self.elements[i],Element):
                    text+=str(self.elements[i].getValue())
        text+=self.ln
        print(text)
        for el in self.elements:
            if isinstance(el,Hole):
                el.Fill()
            elif isinstance(el,Choice):
                pass
    def Grade(self)-> FractionInt:
        numberOfCorrectAns=0
        for el in self.elements:
            if isinstance(el,Hole) and el.isCorrect():
                self.points.setNum(1)
                numberOfCorrectAns +=1
        self.points = self.points.__prod__(FractionInt(numberOfCorrectAns,1))
        return self.points
    def __str__(self):
        #concatenate the text and the sub-elements of the question
        for idx in range(len(self.text)):
            print(self.text[idx]+self.ln)
            if idx < len(self.elements) and idx >=0:
                print(self.elements)




class Element():
    def __init__(self,value=None):
        self.value=value
    def getValue(self):
        return self.value
    def setValue(self,value):
        self.value=value
    def __str__(self):
        return str(self.value)
class Rule(Element):
    def __init__(self,rule,grade,line=None):
        super().__init__(rule)
        # rule pode ser tanto um objecto element ou uma string para as de correspondência
        self.grade=grade
        self.line=line
    def getPoints(self):
        return self.grade if isinstance(self.grade,FractionInt) else FractionInt(self.grade,1)
    def __str__(self):
        return self.rule
class Hole(Element):
    def __init__(self,validAnswer,id=""):
        super().__init__(validAnswer)
        self.id = id
        self.answer=None
    def getId(self):
        return self.id
    def isAnswered(self):
        return False if self.answer is None else True
    def Fill(self):
        self.answer=input("Write your answer here:").strip()
    def isCorrect(self):
        return self.getValue() == self.answer
    def __str__(self):
        if self.isAnswered:
            return self.answer
        return "__"
class Choice(Element):
    def __init__(self, validAnswer:str,grade = None):
        # answer is a list with all the possible answers, correct is the index of the correct answer
        #grade is the possible grade that this specific option can take
        super().__init__(validAnswer)
        self.grade=grade
    def Equals(self,correct):
        if self.getValue() == correct:
            return True
        return False
    def getGrade(self):
        return self.grade
    def __str__(self):
        return self.getValue()



class Code():
    def __init__(self,pil,elements=[]):
        # the name is the identifier of the code
        # the pil is the programming language of the code or parts that are concatenated with the holes for certain types of questions
        # the elements is a list of objects of type
        self.pil = pil
        self.elements = elements
        self.file = isinstance(self.pil,str) and ".pil" in self.pil
    def getPIL(self):
        return self.pil
    def setElements(self,elements):
        self.elements=elements
    def Answer(self):
        if len(self.elements) > 0:
            for el in self.elements:
                if isinstance(el,Hole):
                    el.Fill()
    def Grade(self,rules=None):
        sc = FractionInt(0,1);
        code = self.getCode()
        if rules is not None :
            for r in rules:
                sc.setDen(sc.den + r.grade )
                identifier = r.getValue()
                for el in self.elements:
                    if isinstance(el,Hole) and identifier == el.id and el.isCorrect():
                        sc.setNum(sc.num + r.getPoints().num)
            sc.setDen(sc.den-1) if sc.den != 1 else sc # retira-se por causa da questão da indeterminação 0/0
            return sc
        for el in self.elements:
            sc.setDen(sc.den + 1)
            if isinstance(el,Hole) and el.isCorrect():
                sc.setNum(sc.num + 1)
        sc.setDen(sc.den-1) if sc.den != 1 else sc # retira-se por causa da questão da indeterminação 0/0
        if self.file:
            self.elements=[]
        return sc
    def getCode(self):
        pilText=""
        if self.file:
            with open(self.pil,"r") as file:
                pilText = file.read()
        else:
            for idx,p in enumerate(self.pil):
                pilText += p
                if idx < len(self.elements):
                    el = self.elements[idx]
                    if isinstance(el,Hole) and  not el.isAnswered():
                        pilText+="__"
                    elif isinstance(el,Hole):
                        pilText+=el.answer
        return pilText
    def printCode(self):
        print(self.getCode())
        return self
    def execute(self,stdin=[]):
        # execute the code pil code that is outputted by the code
        # the main function is the function that is called to execute the code in the pil language 
        PILCodeToRun = InputStream(self.getCode())
        return ''.join(RunCode(PILCodeToRun,stdin))
    def createHoles(self,holes):
        text = self.getCode()
        for h in holes:
            self.elements.append(Hole(h,h))
        for h in self.elements:
            text = text.strip().replace(h.id,"__")
        if not self.file:
            self.pil = text
        return text
    def __str__(self):
        return "este é um pedaço de código PIL"

# ----------------