import enum


class SymbolType(enum.Enum):
    INTEGER = 0
    REAL = 1
    BOOLEAN = 2
    TEXT = 3
    ERROR = 4

class SymbolTable:
    def __init__(self, parent=None):
        self.parent = parent
        # identifier <-> (Type, Value)
        self.table = {}

    def get(self, symbol: str) -> (SymbolType, any):
        if symbol in self.table:
            return self.table[symbol]

        if self.parent is None:
            return None

        return self.parent.get(symbol)

    def replace(self, symbol: str, new_type: SymbolType, new_value: any):
        if symbol in self.table:
            self.table[symbol] = (new_type, new_value)
            return True

        if self.parent is None:
            return False

        return self.parent.replace(symbol, new_type, new_value)

    def put(self, symbol: str, sym_type: SymbolType, value: any):
        self.table[symbol] = (sym_type, value)
