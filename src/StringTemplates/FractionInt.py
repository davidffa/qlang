

class FractionInt :

    def __init__(self, num, den):
        self.num = num
        self.den = den

    def __str__(self):
        return str(self.num) + '/' +  str(self.den)
    
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

