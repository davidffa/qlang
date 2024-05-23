import ast

from antlr4 import *

from pilParser import pilParser
from pilVisitor import pilVisitor
from SymbolTable import SymbolTable, SymbolType


class RuntimeError:
    def __init__(self, message):
        self.message = message
        self.consumed = False

    def __str__(self):
        return self.message

class SemanticException(Exception):
    def __init__(self, error_message, output):
        self.error_message = error_message
        self.output = output

    def __str__(self):
        return self.error_message


class Interpreter(pilVisitor):
    def __init__(self, input=[]):
        self.symbolTable = SymbolTable()
        self.last_error = None
        self.errored = False

        self.input = input
        # Array of strings containing the results of write(ln) calls
        self.output = []

    def visitProgram(self, ctx:pilParser.ProgramContext):
        self.visitChildren(ctx)

        return self.output

    def visitStat(self, ctx:pilParser.StatContext):
        return self.visitChildren(ctx)

    def visitBlock(self, ctx:pilParser.BlockContext):
        # Enter block, construct a new scope
        self.symbolTable = SymbolTable(self.symbolTable)

        for stat in ctx.stat():
            self.visit(stat)

        # Exit block, destroy the current scope
        self.symbolTable = self.symbolTable.parent

    def visitWriteStat(self, ctx:pilParser.WriteStatContext):
        eol = ""

        if ctx.type_.text == "writeln":
            eol = "\n"

        result = ""

        for expr in ctx.expr():
            result += str(self.visit(expr))

        if self.errored:
            return

        self.output.append(f"{result}{eol}")

    def visitCondStat(self, ctx:pilParser.CondStatContext):
        res = self.visit(ctx.expr())

        if type(res) == RuntimeError:
            if not res.consumed:
                self.visit(ctx.ifBlock)
                res.consumed = True
            return None

        if type(res) != bool:
            raise SemanticException("ERROR: expr in condition is not a boolean expression!", self.output)

        if res:
            self.visit(ctx.ifBlock)
            return True

        for alternative in ctx.elseifBlock():
            if self.visitElseifBlock(alternative):
                return True

        if ctx.elseBlock is not None:
            self.visit(ctx.elseBlock)


    def visitElseifBlock(self, ctx:pilParser.ElseifBlockContext):
        res = self.visit(ctx.expr())

        if type(res) == RuntimeError:
            if not res.consumed:
                self.visit(ctx.ifBlock)
                res.consumed = True
                return True
            return False

        if type(res) != bool:
            raise SemanticException("ERROR: expr in condition is not a boolean expression!", self.output)

        if res:
            self.visit(ctx.block())
            return True

    def visitLoopStat(self, ctx:pilParser.LoopStatContext):
        first = False

        if ctx.A is not None:
            first = True
            self.visit(ctx.A)

        loop_type = ctx.type_.text

        if loop_type != "while" and loop_type != "until":
            raise SemanticException(f"ERROR: Unknown loop type {loop_type}", self.output)

        while True:
            res = self.visit(ctx.expr())

            if type(res) != bool:
                raise SemanticException(f"ERROR: Expected condition and received {type(res)} inside a loop condition", self.output)

            if loop_type == "while" and not res:
                break
            elif loop_type == "until" and res:
                break

            if ctx.A is not None and not first:
                self.visit(ctx.A)

            if ctx.B is not None:
                self.visit(ctx.B)

            first = False

    def visitAssignment(self, ctx:pilParser.AssignmentContext):
        identifier = ctx.Identifier().getText()

        if identifier == "error":
            raise SemanticException("ERROR: Cannot reassign the global error variable", self.output)

        value = self.visit(ctx.expr())

        if self.errored:
            return

        if type(value) == int:
            sym_type = SymbolType.INTEGER
        elif type(value) == float:
            sym_type = SymbolType.REAL
        elif type(value) == str:
            sym_type = SymbolType.TEXT
        elif type(value) == bool:
            sym_type = SymbolType.BOOLEAN
        elif type(value) == RuntimeError:
            sym_type = SymbolType.ERROR
        else:
            raise Exception(f"ERROR: Unrecognized symbol type. Value: {value}")

        # As PIL only defines assignments, not variable declarations, I will not implement variable shadowing

        if not self.symbolTable.replace(identifier, sym_type, value):
            self.symbolTable.put(identifier, sym_type, value)

    def visitExprIdentifier(self, ctx:pilParser.ExprIdentifierContext):
        identifier = ctx.Identifier().getText()

        if identifier == "error":
            self.errored = False
            return self.last_error

        value = self.symbolTable.get(identifier)

        if value is not None:
            return value[1]

        raise SemanticException(f"ERROR: Undeclared identifier {identifier}", self.output)

    def visitExprRead(self, ctx:pilParser.ExprReadContext):
        if len(self.input) > 0:
            return self.input.pop(0)

        prompt = ""

        if ctx.StringLiteral() is not None:
            prompt = ast.literal_eval(ctx.StringLiteral().getText())

        return input(prompt)

    def visitExprString(self, ctx:pilParser.ExprStringContext):
        return ast.literal_eval(ctx.StringLiteral().getText())

    def visitExprParent(self, ctx:pilParser.ExprParentContext):
        return self.visit(ctx.expr())

    def visitExprNot(self, ctx:pilParser.ExprNotContext):
        res = self.visit(ctx.expr())
        
        if type(res) != bool:
            raise SemanticException(f"ERROR: Trying to perform a boolean operation (not) in {type(res)}", self.output)

        return not self.visit(ctx.expr())

    def visitExprBinary(self, ctx:pilParser.ExprBinaryContext):
        lhs = self.visit(ctx.expr(0))
        rhs = self.visit(ctx.expr(1))

        if (type(lhs) != float and type(lhs) != int) or (type(rhs) != float and type(rhs) != int):
            raise SemanticException(f"ERROR: Trying to perform a binary op between {type(lhs)} and {type(rhs)}", self.output)
        
        op = ctx.op.text

        if op == "+":
            return lhs + rhs
        elif op == "-":
            return lhs - rhs
        elif op == "*":
            return lhs * rhs
        elif op == ":":
            if rhs == 0:
                self.last_error = RuntimeError("division by zero")
                self.errored = True
                return None

            return lhs / rhs
        elif op == "%":
            if rhs == 0:
                self.last_error = RuntimeError("division by zero")
                self.errored = True
                return None

            return lhs % rhs
        else:
            raise Exception(f"ERROR: Operation {op} not implemented!")

    def visitExprRel(self, ctx:pilParser.ExprRelContext):
        op = ctx.op.text

        lhs = self.visit(ctx.expr(0))
        rhs = self.visit(ctx.expr(1))

        if type(lhs) != type(rhs) and (type(lhs) == str or type(rhs) == str):
            raise SemanticException(f"ERROR: Cannot perform relational operation between {type(lhs)} and {type(rhs)}", self.output)

        if op == "=":
            return lhs == rhs
        elif op == "/=":
            return lhs != rhs
        elif op == "<=":
            return lhs <= rhs
        elif op == ">=":
            return lhs >= rhs
        elif op == "<":
            return lhs < rhs
        elif op == ">":
            return lhs > rhs
        else:
            raise Exception(f"ERROR: Relational operator {op} not implemented")

    def visitExprUnary(self, ctx:pilParser.ExprUnaryContext):
        child = self.visit(ctx.expr())

        if type(child) != int and type(child) != float:
            raise SemanticException(f"ERROR: Trying to perform unary op to a {type(child)}", self.output)

        op = ctx.op.text

        if op == "+":
            return child
        elif op == "-":
            return -child
        else:
            raise Exception(f"ERROR: Unary operation {op} not implemented!")

    def visitExprBoolAndThen(self, ctx:pilParser.ExprBoolAndThenContext):
        lhs = self.visit(ctx.expr(0))

        if type(lhs) != bool:
            raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(lhs)}", self.output)

        if not lhs:
            return False

        rhs = self.visit(ctx.expr(1))
        if type(rhs) != bool:
            raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(rhs)}", self.output)

        return rhs

    def visitExprBoolOrElse(self, ctx:pilParser.ExprBoolOrElseContext):
        lhs = self.visit(ctx.expr(0))

        if type(lhs) != bool:
            raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(lhs)}", self.output)

        if lhs:
            return True

        rhs = self.visit(ctx.expr(1))
        if type(rhs) != bool:
            raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(rhs)}", self.output)

        return rhs

    def visitExprBoolOp(self, ctx:pilParser.ExprBoolOpContext):
        lhs = self.visit(ctx.expr(0))

        if type(lhs) != bool:
            raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(lhs)}", self.output)

        op = ctx.op.text

        if op == "or":
            if lhs:
                return True

            rhs = self.visit(ctx.expr(1))
            if type(rhs) != bool:
                raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(rhs)}", self.output)

            return rhs
        elif op == "and":
            if not lhs:
                return False

            rhs = self.visit(ctx.expr(1))
            if type(rhs) != bool:
                raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(rhs)}", self.output)

            return rhs
        elif op == "xor":
            rhs = self.visit(ctx.expr(1))
            if type(rhs) != bool:
                raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(rhs)}", self.output)

            return lhs != rhs
        elif op == "implies":
            if not lhs:
                return True

            rhs = self.visit(ctx.expr(1))
            if type(rhs) != bool:
                raise SemanticException(f"ERROR: Cannot perform a boolean operation with {type(rhs)}", self.output)

            return rhs

    def visitExprReal(self, ctx:pilParser.ExprRealContext):
        return float(ctx.Real().getText())

    def visitExprConvertInteger(self, ctx:pilParser.ExprConvertIntegerContext):
        value = self.visit(ctx.expr())
        try:
            return int(value)
        except:
            self.errored = True
            self.last_error = RuntimeError(f"invalid conversion integer({value})")

    def visitExprInteger(self, ctx:pilParser.ExprIntegerContext):
        return int(ctx.Integer().getText())

    def visitExprConvertText(self, ctx:pilParser.ExprConvertTextContext):
        return str(self.visit(ctx.expr()))
