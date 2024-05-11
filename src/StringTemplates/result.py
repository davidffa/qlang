

class result :

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


