grammar pil;

program:
    (stat ';')* stat? EOF
    ;

stat:
      assignment
    | expr
    | writeStat
    | condStat
    | loopStat
    ;

writeStat:
    type=('write'|'writeln') expr (',' expr)*
    ;
    
block: (stat ';')* stat?;

condStat:
    'if' expr 'then' ifBlock=block elseifBlock* ('else' elseBlock=block)? 'end'
    ;

elseifBlock: 'elseif' expr 'then' block;

loopStat:
    'loop' A=block type=('until'|'while') expr 'do' B=block 'end'
    ;

assignment:
    Identifier ':=' expr
    ;

expr:
      op=('+'|'-') expr                         #ExprUnary
    | expr op=('*'|':'|'%') expr                #ExprBinary
    | expr op=('+'|'-') expr                    #ExprBinary
    | expr op=('='|'/='|'<='|'>='|'<'|'>') expr #ExprRel
    | 'not' expr                                #ExprNot
    | expr 'and' 'then' expr                    #ExprBoolAndThen
    | expr op='and' expr                        #ExprBoolOp
    | expr 'or' 'else' expr                     #ExprBoolOrElse
    | expr op=('xor'|'or') expr                 #ExprBoolOp
    | expr op='implies' expr                    #ExprBoolOp
    | '(' expr ')'                              #ExprParent
    | 'read' StringLiteral?                     #ExprRead
    | 'integer' '(' expr ')'                    #ExprConvertInteger
    | 'text' '(' expr ')'                       #ExprConvertText
    | Integer                                   #ExprInteger
    | Real                                      #ExprReal
    | StringLiteral                             #ExprString
    | Identifier                                #ExprIdentifier
    ;

fragment ESC: '\\' .;
Integer: [0-9]+;
Real: [0-9]+('.'[0-9]+)?;
Identifier: [a-zA-Z_][a-zA-Z0-9_]*;
StringLiteral: ('"' (ESC|.)*? '"') | ('\'' (ESC|.)*? '\'');
WS: [ \t\r\n]+ -> skip;
Comment: '--' .*? '\n' -> skip;
Error: .;
