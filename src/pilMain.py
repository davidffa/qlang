"""
Python program used for testing PIL language
"""


import sys

from antlr4 import *

from Interpreter import Interpreter, SemanticException
from pilLexer import pilLexer
from pilParser import pilParser
def RunCode(stream,stdin=None):
    visitor0 = Interpreter(stdin)
    lexer = pilLexer(stream)
    stream = CommonTokenStream(lexer)
    parser = pilParser(stream)
    tree = parser.program()
    stdout=[]

    if parser.getNumberOfSyntaxErrors() == 0:
        try:
            result = visitor0.visit(tree)
            for out in result:
                stdout.append(out)
        except SemanticException as e:
            result = e.output

            for out in result:
                print(out, end='')

            print("[PILMain] Caught semantic exception!", file=sys.stderr)
            print(e.error_message, file=sys.stderr)

            # Exit with status 1 (only in this main, do not use this in the main compiler)
            exit(1)

        except Exception as e:
            print("[PILMain] INTERPRETER ERROR!", file=sys.stderr)
            print(e, file=sys.stderr)
            exit(1)
        return stdout

def main(argv):
    RunCode(FileStream(argv[1]))

if __name__ == '__main__':
    main(sys.argv)
