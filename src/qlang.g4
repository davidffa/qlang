grammar qlang;

program: (stat ';')* stat? EOF;

stat:
 	  variableDeclaration
	| assignment
	| question
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
	'integer' expr			# ExprConvertInteger
	| 'text' expr			# ExprConvertText
	| 'read' StringLiteral	# ExprRead
	| '|' Identifier		# ExprLabel
	| expr '|' expr			# ExprPipe
	| expr expr		    	# ExprConcat
	| expr ',' expr			# ExprComp
	| 'execute' expr		# ExprExecute
	| 'new' Identifier		# ExprNew
	| expr op=('='|'/='|'<='|'>=' | '<' | '>') expr #ExprRel
	| '(' expr ')'			# ExprParen
	| Identifier			# ExprIdentifier
	| StringLiteral			# ExprString
	| Fraction				# ExprFrac	
	| Integer				# ExprInteger			
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
