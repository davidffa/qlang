grammar qlang;

program:
    (stat ';')* EOF
    ;

stat:
      variableDeclaration
    | assignment
    | question
    | export
    | expr
    | printStat
    | codeStat
    ;

expr:
      'integer' expr        #ExprConvertInteger
    | 'text' expr           #ExprConvertText
    | 'read' StringLiteral  #ExprRead
    | expr '|' expr         #ExprPipe
    | 'execute' expr        #ExprExecute
    | 'new' Identifier      #ExprNew
    | '(' expr ')'          #ExprParen
    | Identifier            #ExprIdentifier
    | StringLiteral         #ExprString
    ;

question:
      holeQuestion
    | openQuestion
    | codeOpenQuestion
    | codeHoleQuestion
    | multiChoiceQuestion
    ;

holeQuestion:
    ;

openQuestion:
    'open' questionStat+ 'end'
    ;

codeOpenQuestion:
    ;

codeHoleQuestion:
    ;

multiChoiceQuestion:
    ;

questionStat:
    printStat
    ;

codeStat:
    'code' Identifier 'is' StringLiteral 'end'
    ;

printStat:
    type=('print'|'println') expr+
    ;

variableDeclaration:
    Identifier ':' type=('text' | 'integer' | 'question' | 'fraction' | 'code')
    ;

assignment:
      Identifier ':=' expr
      ;

export:
    'export' Identifier 'to' StringLiteral
    ;

Identifier: [a-zA-Z_][a-zA-Z0-9_.]*;
StringLiteral: ('"' ~["]* '"') | ('\'' ~[']* '\'');
Comment: '#' .*? '\n' -> skip;
WS: [ \t\r\n]+ -> skip;
Error: .;
