grammar qlang;

program: (stat ';')* stat? EOF;

stat:
 	  variableDeclaration
	| assignment
	| question
	| loopStat
	| printStat
	| codeinline
	| composed
	| export
	| expr;

question:
	holeQuestion
	| openQuestion
	| codeOpenQuestion
	| codeHoleQuestion
	| multiChoiceQuestion
	| codeOutputQuestion;

expr:
   op=('+'|'-') expr                            #ExprUnary
	| expr '|' expr								#ExprPipe
    | expr op=('*'|':'|'%') expr                #ExprBinary
    | expr op=('+'|'-') expr                    #ExprBinary
    | expr op=('='|'/='|'<='|'>='|'<'|'>') expr #ExprRel
    | 'not' expr                                #ExprNot
    | expr 'and' 'then' expr                    #ExprBoolAndThen
    | expr op='and' expr                        #ExprBoolOp
    | expr 'or' 'else' expr                     #ExprBoolOrElse
    | expr op=('xor'|'or') expr                 #ExprBoolOp
    | expr op='implies' expr                    #ExprBoolOp
	|type=('integer'|'text') '(' expr')' 		#ExprCast
	| 'read' StringLiteral						#ExprRead
	| '|' Identifier							#ExprLabel
	| expr expr		    						#ExprConcat
	| expr ',' expr								#ExprComp
	| 'execute' expr							#ExprExecute
	| 'new' Identifier							#ExprNew
	| '(' expr ')'								#ExprParen
	| Identifier								#ExprIdentifier
	| StringLiteral								#ExprString
	| Fraction									#ExprFrac	
	| Integer									#ExprInteger			
	;

holeQuestion: 'hole' Identifier 'is' holeQuestionBlock 'end';
holeQuestionStatement: printStat (hole StringLiteral*)+;
holeQuestionBlock:  (printStat ';')* holeQuestionStatement (';' (holeQuestionStatement|printStat))* ';'?;

openQuestion: 'open' Identifier 'is' printStatBlock 'end';

codeOpenQuestion:
	'code-open' Identifier 'is' ((printStatBlock importStat)|(importStat printStatBlock)) 'end';

codeHoleQuestion:
	'code-hole' Identifier 'is' ((printStatBlock importStat)|(importStat printStatBlock)) 'end';

multiChoiceQuestion:
	'multi-choice' Identifier 'is' ((printStatBlock importStat)|(importStat printStatBlock)) choiceStatBlock 'end';

codeOutputQuestion: 'open' printStatBlock 'end';

codeStat: 'code' Identifier 'is' StringLiteral+ 'end';

//Import
importStat: 'uses' 'code' ((Identifier)| 'from' StringLiteral) (gradeRule ';')* (gradeRule)? 'end' ';'?;
//-------

//Choice
choiceStat: 'choice' (Fraction ',')? StringLiteral 'end';
choiceStatBlock: choiceStat (';' (choiceStat))* ';'?;
//-------


codeinline: 'code' Identifier 'is' (VerbatimString|expr|hole)+ 'end';

composed: 'composed' Identifier 'is' composedBlock 'end';
composedStatement:  (expr  |variableDeclaration |assignment|condStat);
composedBlock: composedStatement (';' (composedStatement))* ';'?;

block: (stat ';')* stat?;
condStat:
    'if' expr 'then' ifBlock=block elseifBlock* ('else' elseBlock=block)? 'end'
    ;
elseifBlock: 'elseif' expr 'then' block;

printStatBlock: printStat (';' (printStat))* ';'?;

printStat: type = ('print' | 'println') expr;

loopStat:
    'loop' A=block type=('until'|'while') expr 'do' B=block 'end'
    ;

variableDeclaration:
	Identifier ':' type = (
		'text'
		| 'integer'
		| 'question'
		| 'fraction'
		| 'code'
	);

assignment: Identifier ':=' expr;

hole: Identifier '->' StringLiteral;

gradeRule:
	Integer ',' ((StringLiteral (('line' | 'lines') Integer)?) | Identifier);

export: 'export' Identifier 'to' StringLiteral;

Integer: [0-9]+;
Identifier: [a-zA-Z_][a-zA-Z0-9_.]*;
VerbatimString: '"' [{<[] .*? [\]>}] '"';
fragment ESC: '\\' .;
StringLiteral: ('"' (.|ESC)*? '"') | ('\'' (.|ESC)*? '\'');
Fraction: [0-9]+ '/' [0-9]+;
Comment: '#' .*? '\n' -> skip;
WS: [ \t\r\n]+ -> skip;
Error: .;
