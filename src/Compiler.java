import org.stringtemplate.v4.*;

@SuppressWarnings("CheckReturnValue")

public class Compiler extends qlangBaseVisitor<ST> {
    private STGroup allTemplates;
    private SymbolTable symbolTable;
    private String programTemp;

    public Compiler() {
        allTemplates = new STGroupFile("PyTemplates.stg");
        symbolTable = new SymbolTable();
    }

    @Override
    public ST visitProgram(qlangParser.ProgramContext ctx) {
        symbolTable.enterScope();
        ST st = allTemplates.getInstanceOf("module");
        for (var stat : ctx.stat()) {
            st.add("stat", this.visit(stat));
        }

        symbolTable.exitScope();

        return st;
    }

    @Override
    public ST visitStat(qlangParser.StatContext ctx) {
        ST st = allTemplates.getInstanceOf("stats");
        ST get = this.visitChildren(ctx);
        ST stringT = get;
        st.add("stat", stringT == null ? "" : stringT.render());
        return st;
    }

    @Override
    public ST visitQuestion(qlangParser.QuestionContext ctx) {
        ST st = allTemplates.getInstanceOf("stats");
        st.add("stat", this.visitChildren(ctx));
        return st;
    }

    @Override
    public ST visitExprFrac(qlangParser.ExprFracContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        st.add("expr1", this.visit(ctx.Fraction()));
        return st;
    }

    @Override
    public ST visitExprBoolAndThen(qlangParser.ExprBoolAndThenContext ctx) {
        ST st = allTemplates.getInstanceOf("andExpr");
        st.add("expr1", this.visit(ctx.expr(0)));
        st.add("expr2", this.visit(ctx.expr(1)));
        return st;
    }

    @Override
    public ST visitExprLabel(qlangParser.ExprLabelContext ctx) {
        ST st = allTemplates.getInstanceOf("labelExpr");
        st.add("expr1", this.visit(ctx.Identifier()));
        return st;
    }

    @Override
    public ST visitExprIdentifier(qlangParser.ExprIdentifierContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        String variable = ctx.Identifier().getText();
        if (variable.contains("result")) {
            variable = variable.replace("result", "Result");
        }
        st.add("expr1", variable);
        return st;
    }

    @Override
    public ST visitExprRead(qlangParser.ExprReadContext ctx) {
        ST st = allTemplates.getInstanceOf("input");
        st.add("expr", ctx.StringLiteral().getText());
        return st;
    }

    @Override
    public ST visitExprParen(qlangParser.ExprParenContext ctx) {
        ST st = allTemplates.getInstanceOf("exprParenthesis");
        st.add("expr", this.visit(ctx.expr()));
        return st;
    }

    @Override
    public ST visitExprConcat(qlangParser.ExprConcatContext ctx) {
        ST st = allTemplates.getInstanceOf("exprConcat");
        String lhs = this.visit(ctx.expr(0)).render();
        String rhs = this.visit(ctx.expr(1)).render();
        st.add("expr1", lhs + "+");
        st.add("expr2", rhs);
        return st;
    }

    @Override
    public ST visitExprString(qlangParser.ExprStringContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        st.add("expr1", ctx.StringLiteral().getText());
        return st;
    }

    // @Override
    // public ST visitExprComp(qlangParser.ExprCompContext ctx) {
    // ST st = allTemplates.getInstanceOf("compExpr");
    // st.add("expr1", this.visit(ctx.expr(0)));
    // st.add("expr2", this.visit(ctx.expr(1)));
    // return st;
    // }

    @Override
    public ST visitExprPipe(qlangParser.ExprPipeContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        String text = "";
        text += ctx.expr().getText();
        st.add("expr1", text);
        programTemp = text;
        return st;
    }

    @Override
    public ST visitExprExecuteWithPipe(qlangParser.ExprExecuteWithPipeContext ctx) {
        ST st = allTemplates.getInstanceOf("CC_execute");
        String input = visit(ctx.expr()).render();
        String code = ctx.Identifier().getText();

        if (input == null || input.equals("")) {
            st.add("instance", code);
            st.add("stdin", "");
        } else {
            st.add("instance", code);
            st.add("stdin", "[" + input + "]");
        }
        return st;
    }

    @Override
    public ST visitExprNot(qlangParser.ExprNotContext ctx) {
        ST st = allTemplates.getInstanceOf("notExpr");
        st.add("expr1", this.visit(ctx.expr()));
        return st;
    }

    @Override
    public ST visitExprBoolOrElse(qlangParser.ExprBoolOrElseContext ctx) {
        ST st = allTemplates.getInstanceOf("orExpr");
        st.add("expr1", this.visit(ctx.expr(0)));
        st.add("expr2", this.visit(ctx.expr(1)));
        return st;
    }

    @Override
    public ST visitExprBinary(qlangParser.ExprBinaryContext ctx) {
        ST st = allTemplates.getInstanceOf("exprBinary");
        st.add("value1", this.visit(ctx.expr(0)));
        String op = ctx.op.getText().replace(":", "/");
        st.add("op", op);
        st.add("value2", this.visit(ctx.expr(1)));
        return st;
    }

    @Override
    public ST visitExprCast(qlangParser.ExprCastContext ctx) {
        if (ctx.type.getText().equals("integer")) {
            ST st = allTemplates.getInstanceOf("intConvert");
            st.add("expr", this.visit(ctx.expr()).render());
            return st;
        } else if (ctx.type.getText().equals("text")) {
            ST st = allTemplates.getInstanceOf("stringConvert");
            st.add("expr", this.visit(ctx.expr()).render());
            return st;
        }
        ST res = null;
        return res;
    }

    @Override
    public ST visitExprRel(qlangParser.ExprRelContext ctx) {
        ST st = allTemplates.getInstanceOf("relacionalExpr");
        st.add("var1", this.visit(ctx.expr(0)));
        st.add("condition", ctx.op.getText().replaceAll("^=$", "==").replaceAll("/=", "!="));
        st.add("var2", this.visit(ctx.expr(1)));
        return st;
    }

    @Override
    public ST visitExprUnary(qlangParser.ExprUnaryContext ctx) {
        ST st = allTemplates.getInstanceOf("exprUnary");
        st.add("value1", this.visit(ctx.expr()));
        st.add("op", ctx.op.getText());
        return st;
    }

    @Override
    public ST visitExprExecute(qlangParser.ExprExecuteContext ctx) {
        ST st = allTemplates.getInstanceOf("RunQuestion");
        Type type = symbolTable.lookup(ctx.Identifier().getText());
        String variable = ctx.Identifier().getText().replace(".", "_");
        if (type == Type.CODE) {
            st = allTemplates.getInstanceOf("CC_execute");
            st.add("instance", variable);
            st.add("stdin", "");

        } else if (type == Type.QUESTION) {
            String[] vars = variable.split("_");
            for (int i = 1; i < vars.length; i++) {
                variable = variable + ".getChild('" + vars[i] + "')";
            }
            st.add("question", variable);
        }
        if (ctx.getParent() instanceof qlangParser.StatContext) {
            ST p = allTemplates.getInstanceOf("println");
            p.add("expr", st.render());
            st = p;
        }

        return st;
    }

    @Override
    public ST visitExprNew(qlangParser.ExprNewContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        ST getC = allTemplates.getInstanceOf("GroupC_getChildren");
        String id = ctx.Identifier().getText();
        String[] subId = id.trim().split("\\.");
        String getChild = id.replace(".", "_");
        if (subId.length > 1) {
            for (int i = 1; i < subId.length; i++) {
                getC.add("instance", getChild);
                getC.add("name", subId[i]);
                getChild = getC.render();

            }
        } else {
            getChild = id + ".getChild()";
        }
        st.add("expr1", getChild);
        return st;
    }

    @Override
    public ST visitExprBoolOp(qlangParser.ExprBoolOpContext ctx) {
        ST res = null;
        switch (ctx.op.getText()) {
            case "and": {
                ST st = allTemplates.getInstanceOf("andExpr");
                st.add("expr1", this.visit(ctx.expr(0)));
                st.add("expr2", this.visit(ctx.expr(1)));
                return st;
            }
            case "or": {
                ST st = allTemplates.getInstanceOf("orExpr");
                st.add("expr1", this.visit(ctx.expr(0)));
                st.add("expr2", this.visit(ctx.expr(1)));
                return st;
            }
            case "xor": {
                ST st = allTemplates.getInstanceOf("xorExpr");
                st.add("expr1", this.visit(ctx.expr(0)));
                st.add("expr2", this.visit(ctx.expr(1)));
                return st;
            }
            case "implies": {
                ST st = allTemplates.getInstanceOf("impliesExpr");
                st.add("expr1", this.visit(ctx.expr(0)));
                st.add("expr2", this.visit(ctx.expr(1)));
                return st;
            }
        }
        return res;
    }

    @Override
    public ST visitExprInteger(qlangParser.ExprIntegerContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        st.add("expr1", ctx.Integer().getText());
        return st;
    }

    @Override
    public ST visitHoleQuestion(qlangParser.HoleQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());
        ST st = allTemplates.getInstanceOf("InstanciateGroup");
        String[] parts = ctx.Identifier().getText().split("\\.");
        st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
        st.add("groupName", parts[0]);
        ST[] childGroups = new ST[parts.length - 1];
        for (int i = parts.length - 1; i > 0; i--) {
            if (i == parts.length - 1) {
                ST fin = allTemplates.getInstanceOf("childQuestion");
                fin.add("name", parts[i]);
                fin.add("child", visit(ctx.holeQuestionBlock()).render());
                childGroups[i - 1] = fin;
            } else {
                ST middle = allTemplates.getInstanceOf("childGroup");
                middle.add("name", parts[i]);
                middle.add("child", childGroups[i]);
                childGroups[i - 1] = middle;
            }
        }
        st.add("childGroups", childGroups[0]);
        return st;
    }

    @Override
    public ST visitHoleQuestionBlock(qlangParser.HoleQuestionBlockContext ctx) {
        symbolTable.enterScope();

        ST st = allTemplates.getInstanceOf("HoleQuestion");
        for (qlangParser.PrintStatContext ps : ctx.printStat()) {
            st.add("print", visit(ps).render());
        }
        for (qlangParser.HoleQuestionStatementContext hs : ctx.holeQuestionStatement()) {
            st.add("print", visit(hs).render());
        }

        symbolTable.exitScope();

        return st;
    }

    @Override
    public ST visitHoleQuestionStatement(qlangParser.HoleQuestionStatementContext ctx) {
        ST st = allTemplates.getInstanceOf("printObject");

        st.add("printStat", visit(ctx.printStat()).render());
        for (var print : ctx.StringLiteral()) {
            st.add("printStat", print.getText());
        }

        for (var hole : ctx.hole()) {
            st.add("hole", visit(hole));
        }
        return st;

    }

    @Override
    public ST visitOpenQuestion(qlangParser.OpenQuestionContext ctx) {
        // a verificar
        symbolTable.declareQuestion(ctx.Identifier().getText());

        String[] parts = ctx.Identifier().getText().split("\\.");
        if (parts.length > 1) {
            ST st = allTemplates.getInstanceOf("InstanciateGroup");
            st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
            st.add("groupName", parts[0]);
            ST[] childGroups = new ST[parts.length - 1];
            if (parts.length - 1 > 0) {
                for (int i = parts.length - 1; i > 0; i--) {
                    if (i == parts.length - 1) {
                        ST fin = allTemplates.getInstanceOf("OpenChildQuestion");
                        fin.add("name", parts[i]);
                        fin.add("child", visit(ctx.printStatBlock()).render());
                        childGroups[i - 1] = fin;
                    } else {
                        ST middle = allTemplates.getInstanceOf("childGroup");
                        middle.add("name", parts[i]);
                        middle.add("child", childGroups[i]);
                        childGroups[i - 1] = middle;
                    }
                }
                st.add("childGroups", childGroups[0]);
            }
            return st;
        } else {
            ST st = allTemplates.getInstanceOf("InstanciateGroupUnique");
            st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
            st.add("groupName", parts[0]);
            st.add("childGroups", "OpenQuestionClass([" + this.visit(ctx.printStatBlock()).render() + "])");
            return st;
        }

    }

    @Override
    public ST visitCodeOpenQuestion(qlangParser.CodeOpenQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());

        ST st = allTemplates.getInstanceOf("InstanciateGroup");
        String[] parts = ctx.Identifier().getText().split("\\.");
        st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
        st.add("groupName", parts[0]);
        ST[] childGroups = new ST[parts.length - 1];
        for (int i = parts.length - 1; i > 0; i--) {
            if (i == parts.length - 1) {
                ST fin = allTemplates.getInstanceOf("childQuestion");
                fin.add("name", parts[i]);
                ST question = allTemplates.getInstanceOf("CodeOpenQuestion");
                question.add("var1", this.visit(ctx.printStatBlock()));
                question.add("var2", this.visit(ctx.importStat()));
                fin.add("child", question);
                childGroups[i - 1] = fin;
            } else {
                ST middle = allTemplates.getInstanceOf("childGroup");
                middle.add("name", parts[i]);
                middle.add("child", childGroups[i]);
                childGroups[i - 1] = middle;
            }
        }
        st.add("childGroups", childGroups[0]);
        return st;
    }

    @Override
    public ST visitCodeHoleQuestion(qlangParser.CodeHoleQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());

        ST st = allTemplates.getInstanceOf("InstanciateGroup");
        String[] parts = ctx.Identifier().getText().split("\\.");
        st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
        st.add("groupName", parts[0]);
        ST[] childGroups = new ST[parts.length - 1];
        for (int i = parts.length - 1; i > 0; i--) {
            if (i == parts.length - 1) {
                ST fin = allTemplates.getInstanceOf("childQuestion");
                fin.add("name", parts[i]);
                ST question = allTemplates.getInstanceOf("CodeHoleQuestion");
                question.add("var1", this.visit(ctx.printStatBlock()));
                question.add("var2", this.visit(ctx.importStat()));
                fin.add("child", question);
                childGroups[i - 1] = fin;
            } else {
                ST middle = allTemplates.getInstanceOf("childGroup");
                middle.add("name", parts[i]);
                middle.add("child", childGroups[i]);
                childGroups[i - 1] = middle;
            }
        }
        st.add("childGroups", childGroups[0]);
        return st;
    }

    @Override
    public ST visitMultiChoiceQuestion(qlangParser.MultiChoiceQuestionContext ctx) {
        symbolTable.declareQuestion(ctx.Identifier().getText());

        ST st = allTemplates.getInstanceOf("InstanciateGroup");
        String[] parts = ctx.Identifier().getText().split("\\.");
        st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
        st.add("groupName", parts[0]);
        ST[] childGroups = new ST[parts.length - 1];
        for (int i = parts.length - 1; i > 0; i--) {
            if (i == parts.length - 1) {
                ST fin = allTemplates.getInstanceOf("childQuestion");
                fin.add("name", parts[i]);
                ST question = allTemplates.getInstanceOf("MultiChoiceQuestion");
                question.add("var1", this.visit(ctx.printStatBlock()));
                question.add("var2", this.visit(ctx.importStat()));
                question.add("var3", this.visit(ctx.choiceStatBlock()));
                fin.add("child", question);
                childGroups[i - 1] = fin;
            } else {
                ST middle = allTemplates.getInstanceOf("childGroup");
                middle.add("name", parts[i]);
                middle.add("child", childGroups[i]);
                childGroups[i - 1] = middle;
            }
        }
        st.add("childGroups", childGroups[0]);
        return st;
    }

    @Override
    public ST visitCodeOutputQuestion(qlangParser.CodeOutputQuestionContext ctx) {
        ST res = null;
        return visitChildren(ctx);
        // return res;
    }

    @Override
    public ST visitImportStat(qlangParser.ImportStatContext ctx) {
        ST st = allTemplates.getInstanceOf("CodeNormal");
        if (ctx.StringLiteral() != null) {
            st.add("text", ctx.StringLiteral().getText());
        }
        if (ctx.Identifier() != null) {
            st = allTemplates.getInstanceOf("CodeGetChild");
            String text = ctx.Identifier().getText().replace(".", "_");
            String[] code = text.split("_");
            for (int i = 1; i < code.length; i++) {
                text = text + ".getChild(" + '"' + code[i] + '"' + ")";
            }
            st.add("text", text);
        }
        for (var rule : ctx.gradeRule()) {
            st.add("hole", this.visit(rule));
        }
        return st;
    }

    @Override
    public ST visitChoiceStat(qlangParser.ChoiceStatContext ctx) {
        ST st = allTemplates.getInstanceOf("choice");
        if (ctx.Fraction() != null) {
            String[] frac = ctx.Fraction().getText().split("/");
            if (frac.length == 1) {
                st.add("expr2", "," + frac[0]);
            } else {
                st.add("expr2", ",FractionInt(" + frac[0] + "," + frac[1] + ")");
            }

        }

        st.add("expr1", ctx.StringLiteral().getText());
        return st;
    }

    @Override
    public ST visitChoiceStatBlock(qlangParser.ChoiceStatBlockContext ctx) {
        ST st = allTemplates.getInstanceOf("list");

        for (var stat : ctx.choiceStat()) {
            st.add("stat", this.visit(stat).render());
        }

        return st;
    }

    @Override
    public ST visitCodeinline(qlangParser.CodeinlineContext ctx) {
        ST st = allTemplates.getInstanceOf("InstanciateGroup");
        String[] parts = ctx.Identifier().getText().split("\\.");
        if (parts.length == 1) {
            st = allTemplates.getInstanceOf("InstanciateGroupUnique");
            st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
            st.add("groupName", parts[0]);
            ST question = allTemplates.getInstanceOf("Code");
            if (ctx.VerbatimString() != null) {
                for (var vb : ctx.VerbatimString()) {
                    String section = vb.getText();
                    question.add("text", "\"\"\"" + section.substring(2, section.length() - 2) + "\"\"\"");
                }
            }
            if (ctx.hole() != null) {
                for (var h : ctx.hole()) {
                    question.add("hole", visit(h).render());
                }
            }
            st.add("childGroups", question);
        } else {
            st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
            st.add("groupName", parts[0]);
            ST[] childGroups = new ST[parts.length - 1];
            for (int i = parts.length - 1; i > 0; i--) {
                if (i == parts.length - 1) {
                    ST fin = allTemplates.getInstanceOf("childQuestion");
                    fin.add("name", parts[i]);
                    ST question = allTemplates.getInstanceOf("Code");
                    if (ctx.VerbatimString() != null) {
                        for (var vb : ctx.VerbatimString()) {
                            String section = vb.getText();
                            question.add("text", "\"\"\"" + section.substring(2, section.length() - 2) + "\"\"\"");
                        }
                    }
                    if (ctx.hole() != null) {
                        for (var h : ctx.hole()) {
                            question.add("hole", visit(h).render());
                        }
                    }
                    fin.add("child", question);
                    childGroups[i - 1] = fin;
                } else {
                    ST middle = allTemplates.getInstanceOf("childGroup");
                    middle.add("name", parts[i]);
                    middle.add("child", childGroups[i]);
                    childGroups[i - 1] = middle;
                }
            }
            if (childGroups.length > 0) {
                st.add("childGroups", childGroups[0]);
            }
        }

        return st;
    }

    @Override
    public ST visitComposed(qlangParser.ComposedContext ctx) {
        ST st = allTemplates.getInstanceOf("InstanciateGroup");
        String[] parts = ctx.Identifier().getText().split("\\.");
        st.add("varGroup", ctx.Identifier().getText().replace(".", "_"));
        st.add("groupName", parts[0]);
        ST[] childGroups = new ST[parts.length - 1];
        for (int i = parts.length - 1; i > 0; i--) {
            if (i == parts.length - 1) {
                ST fin = allTemplates.getInstanceOf("childGroup");
                fin.add("name", parts[i]);
                fin.add("child", this.visit(ctx.composedBlock()));
                childGroups[i - 1] = fin;
            } else {
                ST middle = allTemplates.getInstanceOf("childGroup");
                middle.add("name", parts[i]);
                middle.add("child", childGroups[i]);
                childGroups[i - 1] = middle;
            }
        }
        st.add("childGroups", childGroups[0]);
        return st;
    }

    @Override
    public ST visitComposedStatement(qlangParser.ComposedStatementContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        st.add("expr1", this.visit(ctx.expr()).render());
        st.add("expr1", this.visit(ctx.variableDeclaration()).render());
        st.add("expr1", this.visit(ctx.assignment()).render());
        st.add("expr1", this.visit(ctx.condStat()).render());
        return st;
    }

    @Override
    public ST visitComposedBlock(qlangParser.ComposedBlockContext ctx) {
        symbolTable.enterScope();

        ST st = allTemplates.getInstanceOf("block");

        for (var stat : ctx.composedStatement()) {
            st.add("stat", this.visit(stat));
        }

        symbolTable.exitScope();

        return st;
    }

    @Override
    public ST visitBlock(qlangParser.BlockContext ctx) {
        symbolTable.enterScope();

        ST st = allTemplates.getInstanceOf("block");

        for (var stat : ctx.stat()) {
            st.add("stat", this.visit(stat));
        }

        symbolTable.exitScope();

        return st;
    }

    @Override
    public ST visitCondStat(qlangParser.CondStatContext ctx) {
        ST st = allTemplates.getInstanceOf("ifExpr");
        st.add("relacionalExpr", this.visit(ctx.expr()));
        st.add("expr", this.visit(ctx.ifBlock));

        for (var block : ctx.elseifBlock()) {
            st.add("expr2", this.visit(block).render());
        }

        if (ctx.elseBlock != null) {
            ST elseST = allTemplates.getInstanceOf("elseExpr");
            elseST.add("expr", this.visit(ctx.elseBlock).render());
            st.add("expr2", elseST.render());
        }
        return st;
    }

    @Override
    public ST visitElseifBlock(qlangParser.ElseifBlockContext ctx) {
        ST st = allTemplates.getInstanceOf("elifExpr");
        st.add("relacionalExpr", this.visit(ctx.expr()));
        st.add("expr", this.visit(ctx.block()));
        return st;
    }

    @Override
    public ST visitPrintStatBlock(qlangParser.PrintStatBlockContext ctx) {
        ST st = allTemplates.getInstanceOf("printObject");

        for (var stat : ctx.printStat()) {
            String v =this.visit(stat).render();
            System.out.println(v);
            st.add("printStat", v);
        }

        st.add("hole", programTemp.equals("") ? "" : "Element(" + programTemp + ")");
        return st;
    }

    @Override
    public ST visitPrintStat(qlangParser.PrintStatContext ctx) {
        ST st = allTemplates.getInstanceOf("returnData");
        boolean ln = ctx.type.getText().equals("print");
        st.add("expr1", this.visit(ctx.expr()));

        if (ctx.getParent() instanceof qlangParser.StatContext) {
            st = allTemplates.getInstanceOf(ln ? "print" : "println");
            st.add("expr", visit(ctx.expr()));
        }

        return st;

    }

    @Override
    public ST visitLoopStat(qlangParser.LoopStatContext ctx) {
        return visitChildren(ctx);
        // return res;
    }

    @Override
    public ST visitVariableDeclaration(qlangParser.VariableDeclarationContext ctx) {
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
        return null;
    }

    @Override
    public ST visitAssignment(qlangParser.AssignmentContext ctx) {
        ST st = allTemplates.getInstanceOf("assign");
        String variable = ctx.Identifier().getText();
        if (variable.equals("result.name")) {
            variable = "Result.name";
        }
        st.add("var", variable);
        if (ctx.expr() instanceof qlangParser.ExprExecuteContext)
            st.add("expr", this.visit(ctx.expr()).render());
        else
            st.add("expr", this.visit(ctx.expr()).render());
        return st;
    }

    @Override
    public ST visitHole(qlangParser.HoleContext ctx) {
        ST st = allTemplates.getInstanceOf("instantiate");
        st.add("class", "Hole");
        st.add("expr", ctx.StringLiteral().getText());
        return st;

    }

    @Override
    public ST visitGradeRule(qlangParser.GradeRuleContext ctx) {
        ST st = allTemplates.getInstanceOf("Rule");
        if (ctx.StringLiteral() != null) {
            String hole = ctx.StringLiteral().getText();
            st.add("expr1", hole.substring(1, hole.length() - 1));
        } else {
            // identifier
            String hole = ctx.Identifier().getText();
            st.add("expr1", hole);
        }

        for (var integer : ctx.Integer()) {
            st.add("expr2", integer.getText());
        }
        return st;
    }

    @Override
    public ST visitExport(qlangParser.ExportContext ctx) {
        ST st = allTemplates.getInstanceOf("RC_export");
        st.add("instance", "Result");
        st.add("file", ctx.StringLiteral().getText());
        return st;
    }
}
