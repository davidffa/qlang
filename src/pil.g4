grammar pil;

program:
    (stat ';')* EOF
    ;

stat:
      assignment
    | expr
    | writeStat
    | condStat
    | loopStat
    ;

writeStat:
    type=('write'|'writeln') expr
    ;
    
condStat:
    'if' expr 'then' stat ('elseif' stat)* ('else' stat)? 'end'
    ;

loopStat:
    'loop' type=('until'|'while') expr 'do' stat 'end'
    ;

assignment:
    Identifier ':=' expr
    ;

expr:
      op=('+'|'-') expr                             #ExprUnary
    | expr op=('*'|':'|'%') expr                    #ExprBinary
    | expr op=('+'|'-') expr                        #ExprBinary
    | expr op=('='|'/='|'<='|'>=' | '<' | '>') expr #ExprRel
    | expr ',' expr                                 #ExprConcat // TODO: A prioridade desta operação está correta?
    | 'not' expr                                    #ExprNot
    | expr op=('or'|'and'|'xor'|'and then'
           |'or else'|'implies') expr               #ExprBoolOp
    | '(' expr ')'                                  #ExprParent
    | 'read' StringLiteral                          #ExprRead
    | 'integer' expr                                #ExprConvertInteger
    | 'text' expr                                   #ExprConvertText
    | Integer                                       #ExprInteger
    | StringLiteral                                 #ExprString
    | Identifier                                    #ExprIdentifier
    ;


Integer: [0-9]+;
Identifier: [a-zA-Z_][a-zA-Z0-9_]*;
// TODO: As strings de PIL também podem ser 'python wise' ? como as da linguagem principal??
StringLiteral: ('"' ~["]* '"'); // | ('\'' ~[']* '\'');
WS: [ \t\r\n]+ -> skip;
Comment: '--' .*? '\n' -> skip;
Error: .;
