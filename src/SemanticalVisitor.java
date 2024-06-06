// Generated from qlang.g4 by ANTLR 4.13.1

class SemanticException extends RuntimeException {
    public SemanticException(String message) {
        super(message);
    }
}

@SuppressWarnings("CheckReturnValue")
public class SemanticalVisitor extends qlangBaseVisitor<Type> {
    private SymbolTable symbolTable = new SymbolTable();

    @Override
    public Type visitProgram(qlangParser.ProgramContext ctx) {
        symbolTable.enterScope(); // assim entra
        Type res = visitChildren(ctx);
        symbolTable.exitScope(); // Sair do scope depois de processar o programa
        return res;
    }

    @Override
    public Type visitStat(qlangParser.StatContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitQuestion(qlangParser.QuestionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitExprFrac(qlangParser.ExprFracContext ctx) {
        return Type.FRACTION;
    }

    @Override
    public Type visitExprBoolAndThen(qlangParser.ExprBoolAndThenContext ctx) {
        Type lhs = visit(ctx.expr(0));
        Type rhs = visit(ctx.expr(1));

        if (lhs != Type.BOOLEAN || rhs != Type.BOOLEAN)
            throw new SemanticException("Bool expression expects boolean operands");

        return Type.BOOLEAN;
    }

    @Override
    public Type visitExprLabel(qlangParser.ExprLabelContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitExprIdentifier(qlangParser.ExprIdentifierContext ctx) {
        String varName = ctx.Identifier().getText();
        return symbolTable.lookup(varName); // Só para verificar se a variável foi declarada
    }

    @Override
    public Type visitExprRead(qlangParser.ExprReadContext ctx) {
        return Type.TEXT;
    }

    @Override
    public Type visitExprParen(qlangParser.ExprParenContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitExprConcat(qlangParser.ExprConcatContext ctx) {
        Type lhs = visit(ctx.expr(0));
        Type rhs = visit(ctx.expr(1));

        if (lhs != rhs) {
            throw new SemanticException("Cannot concat the types " + lhs + " and " + rhs);
        }

        return lhs;
    }

    @Override
    public Type visitExprString(qlangParser.ExprStringContext ctx) {
        return Type.TEXT;
    }

    @Override
    public Type visitExprPipe(qlangParser.ExprPipeContext ctx) {
        Type lhs = visit(ctx.expr());

        if (lhs != Type.TEXT)
            throw new SemanticException("Invalid pipe arguments");
        return Type.TEXT;
    }

    @Override
    public Type visitExprExecuteWithPipe(qlangParser.ExprExecuteWithPipeContext ctx) {
        Type lhs = visit(ctx.expr());
        if (lhs != Type.TEXT)
            throw new SemanticException("Invalid execute with pipe arguments");

        return Type.TEXT;
    }

    @Override
    public Type visitExprNot(qlangParser.ExprNotContext ctx) {
        if (visit(ctx.expr()) != Type.BOOLEAN)
            throw new SemanticException("Invalid type");

        return Type.BOOLEAN;
    }

    @Override
    public Type visitExprBoolOrElse(qlangParser.ExprBoolOrElseContext ctx) {
        Type lhs = visit(ctx.expr(0));
        Type rhs = visit(ctx.expr(1));

        if (lhs != Type.BOOLEAN || rhs != Type.BOOLEAN)
            throw new SemanticException("Bool expression expects boolean operands");

        return Type.BOOLEAN;
    }

    @Override
    public Type visitExprBinary(qlangParser.ExprBinaryContext ctx) {
        Type lhs = visit(ctx.expr(0));
        Type rhs = visit(ctx.expr(1));

        if ((lhs != Type.INTEGER && lhs != Type.FRACTION) || (rhs != Type.INTEGER && rhs != Type.FRACTION))
            throw new SemanticException("Type error");

        return lhs;
    }

    @Override
    public Type visitExprCast(qlangParser.ExprCastContext ctx) {
        if (ctx.type.getText().equals("text")) {
            return Type.TEXT;
        }
        return Type.INTEGER;
    }

    @Override
    public Type visitExprRel(qlangParser.ExprRelContext ctx) {
        Type lhs = visit(ctx.expr(0));
        Type rhs = visit(ctx.expr(1));

        if ((lhs != Type.INTEGER && lhs != Type.FRACTION) || (rhs != Type.INTEGER && rhs != Type.FRACTION))
            throw new SemanticException("Relational expression expects integer/fraction operands");

        return Type.BOOLEAN;
    }

    @Override
    public Type visitExprUnary(qlangParser.ExprUnaryContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitExprExecute(qlangParser.ExprExecuteContext ctx) {
        if (ctx.expr() != null) {
            Type grade = visit(ctx.expr());

            if (grade != Type.FRACTION && grade != Type.INTEGER) {
                throw new SemanticException("Invalid execute grade");
            }
        }

        Type rhs = symbolTable.lookup(ctx.Identifier().getText());

        if (rhs == Type.QUESTION)
            return Type.FRACTION;
        else if (rhs == Type.CODE) {
            if (ctx.expr() != null)
                throw new SemanticException("Cannot put a grade in code execution!");
            return Type.TEXT;
        }

        throw new SemanticException("Invalid operand in execute");
    }

    @Override
    public Type visitExprNew(qlangParser.ExprNewContext ctx) {
        return symbolTable.lookup(ctx.Identifier().getText());
    }

    @Override
    public Type visitExprBoolOp(qlangParser.ExprBoolOpContext ctx) {
        Type lhs = visit(ctx.expr(0));
        Type rhs = visit(ctx.expr(1));

        if (lhs != Type.BOOLEAN || rhs != Type.BOOLEAN)
            throw new SemanticException("Bool expression expects boolean operands");

        return Type.BOOLEAN;
    }

    @Override
    public Type visitExprInteger(qlangParser.ExprIntegerContext ctx) {
        return Type.INTEGER;
    }

    @Override
    public Type visitHoleQuestion(qlangParser.HoleQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        visitChildren(ctx);
        return Type.QUESTION;
    }

    @Override
    public Type visitHoleQuestionStatement(qlangParser.HoleQuestionStatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitHoleQuestionBlock(qlangParser.HoleQuestionBlockContext ctx) {
        symbolTable.enterScope();
        visitChildren(ctx);
        symbolTable.exitScope();

        return null;
    }

    @Override
    public Type visitOpenQuestion(qlangParser.OpenQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        return visitChildren(ctx);
    }

    @Override
    public Type visitCodeOpenQuestion(qlangParser.CodeOpenQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        return visitChildren(ctx);
    }

    @Override
    public Type visitCodeHoleQuestion(qlangParser.CodeHoleQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        return visitChildren(ctx);
    }

    @Override
    public Type visitMultiChoiceQuestion(qlangParser.MultiChoiceQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        return visitChildren(ctx);
    }

    @Override
    public Type visitCodeOutputQuestion(qlangParser.CodeOutputQuestionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitImportStat(qlangParser.ImportStatContext ctx) {
        visitChildren(ctx);
        return Type.CODE;
    }

    @Override
    public Type visitChoiceStat(qlangParser.ChoiceStatContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitChoiceStatBlock(qlangParser.ChoiceStatBlockContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitCodeinline(qlangParser.CodeinlineContext ctx) {
        symbolTable.declare(ctx.Identifier().getText(), Type.CODE);
        visitChildren(ctx);
        return Type.CODE;
    }

    @Override
    public Type visitComposed(qlangParser.ComposedContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        return visitChildren(ctx);
    }

    @Override
    public Type visitComposedStatement(qlangParser.ComposedStatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitComposedBlock(qlangParser.ComposedBlockContext ctx) {
        symbolTable.enterScope();
        visitChildren(ctx);
        symbolTable.exitScope();

        return null;
    }

    @Override
    public Type visitBlock(qlangParser.BlockContext ctx) {
        symbolTable.enterScope();
        Type res = visitChildren(ctx);
        symbolTable.exitScope();
        return res;
    }

    @Override
    public Type visitCondStat(qlangParser.CondStatContext ctx) {
        Type cond = visit(ctx.expr());

        if (cond != Type.BOOLEAN)
            throw new SemanticException("Condition expression is not a boolean");

        ctx.block().forEach(this::visit);
        ctx.elseifBlock().forEach(this::visit);

        return null;
    }

    @Override
    public Type visitElseifBlock(qlangParser.ElseifBlockContext ctx) {
        Type cond = visit(ctx.expr());

        if (cond != Type.BOOLEAN)
            throw new SemanticException("Condition expression is not a boolean");

        visit(ctx.block());

        return null;
    }

    @Override
    public Type visitPrintStatBlock(qlangParser.PrintStatBlockContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitPrintStat(qlangParser.PrintStatContext ctx) {
        Type type = visit(ctx.expr());

        if (type == Type.QUESTION)
            throw new SemanticException("Cannot print question. Maybe you meant to execute it.");

        return null;
    }

    @Override
    public Type visitLoopStat(qlangParser.LoopStatContext ctx) {
        ctx.block().forEach(this::visit);

        if (visit(ctx.expr()) != Type.BOOLEAN)
            throw new SemanticException("Loop condition must be a boolean");

        return null;
    }

    @Override
    public Type visitVariableDeclaration(qlangParser.VariableDeclarationContext ctx) {
        String varName = ctx.Identifier().getText();
        String varType = ctx.type.getText();

        Type type = switch (varType) {
            case "text" -> Type.TEXT;
            case "integer" -> Type.INTEGER;
            case "question" -> Type.QUESTION;
            case "fraction" -> Type.FRACTION;
            case "code" -> Type.CODE;
            default -> throw new UnsupportedOperationException();
        };

        symbolTable.declare(varName, type);
        return type;
    }

    @Override
    public Type visitAssignment(qlangParser.AssignmentContext ctx) {
        Type declared = symbolTable.lookup(ctx.Identifier().getText());

        if (declared != visit(ctx.expr()))
            throw new SemanticException("Invalid assignment type");

        return null;
    }

    @Override
    public Type visitHole(qlangParser.HoleContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitGradeRule(qlangParser.GradeRuleContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitExport(qlangParser.ExportContext ctx) {
        return visitChildren(ctx);
    }
}
