import org.stringtemplate.v4.*;

@SuppressWarnings("CheckReturnValue")

public class Compiler extends qlangBaseVisitor<ST> {
   private STGroup allTemplates;

   public Compiler(){
      allTemplates = new STGroupFile("PyTemplates.stg");
   }
   @Override public ST visitProgram(qlangParser.ProgramContext ctx) {
      ST st = allTemplates.getInstanceOf("module");
      st.add("name", "qlang");
      
      for (var stat : ctx.stat()) {
         st.add("stat", this.visit(stat));
      }

      return st;
   }

   @Override public ST visitStat(qlangParser.StatContext ctx) {
      ST st = allTemplates.getInstanceOf("stat");
      st.add("stat", this.visitChildren(ctx));
      return st;
   }

   @Override public ST visitQuestion(qlangParser.QuestionContext ctx) {
      ST st = allTemplates.getInstanceOf("stat");
      st.add("stat", this.visitChildren(ctx));
      return st;
   }

   @Override public ST visitExprFrac(qlangParser.ExprFracContext ctx) {
      ST st = allTemplates.getInstanceOf("returnData");
      st.add("expr1", this.visit(ctx.Fraction()));
      return st;
   }

   @Override public ST visitExprBoolAndThen(qlangParser.ExprBoolAndThenContext ctx) {
      ST st = allTemplates.getInstanceOf("andExpr");
      st.add("expr1", this.visit(ctx.expr(0)));
      st.add("expr2", this.visit(ctx.expr(1)));
      return st;
   }

   @Override public ST visitExprLabel(qlangParser.ExprLabelContext ctx) {
      ST st = allTemplates.getInstanceOf("labelExpr");
      st.add("expr1", this.visit(ctx.Identifier()));
      return st;
   }

   @Override public ST visitExprIdentifier(qlangParser.ExprIdentifierContext ctx) {
      ST st = allTemplates.getInstanceOf("returnData");
      st.add("expr1", this.visit(ctx.Identifier()));
      return st;
   }

   @Override public ST visitExprRead(qlangParser.ExprReadContext ctx) {
      ST st = allTemplates.getInstanceOf("input");
      st.add("expr", this.visit(ctx.StringLiteral()));
      return st;
   }

   @Override public ST visitExprParen(qlangParser.ExprParenContext ctx) {
      ST st = allTemplates.getInstanceOf("exprParenthesis");
      st.add("expr", this.visit(ctx.expr()));
      return st;
   }

   @Override public ST visitExprConcat(qlangParser.ExprConcatContext ctx) {
      ST st = allTemplates.getInstanceOf("exprConcat");
      st.add("expr1", this.visit(ctx.expr(0)));
      st.add("expr2", this.visit(ctx.expr(1)));
      return st;
   }

   @Override public ST visitExprString(qlangParser.ExprStringContext ctx) {
      ST st = allTemplates.getInstanceOf("stringConvert");
      st.add("expr", this.visit(ctx));
      return st;
   }

   @Override public ST visitExprComp(qlangParser.ExprCompContext ctx) {
      ST st = allTemplates.getInstanceOf("compExpr");
      st.add("expr1", this.visit(ctx.expr(0)));
      st.add("expr2", this.visit(ctx.expr(1)));      
      return st;
   }

   @Override public ST visitExprPipe(qlangParser.ExprPipeContext ctx) {
      ST st = allTemplates.getInstanceOf("pipeExpr");
      st.add("expr1", this.visit(ctx.expr(0)));
      st.add("expr2", this.visit(ctx.expr(1)));      
      return st;
   }

   @Override public ST visitExprNot(qlangParser.ExprNotContext ctx) {
      ST st = allTemplates.getInstanceOf("notExpr");
      st.add("expr1", this.visit(ctx.expr()));
      return st;
   }

   @Override public ST visitExprBoolOrElse(qlangParser.ExprBoolOrElseContext ctx) {
      ST st = allTemplates.getInstanceOf("orExpr");
      st.add("expr1", this.visit(ctx.expr(0)));
      st.add("expr2", this.visit(ctx.expr(1)));
      return st;
   }

   @Override public ST visitExprBinary(qlangParser.ExprBinaryContext ctx) {
      ST st = allTemplates.getInstanceOf("exprBinary");
      st.add("value1", this.visit(ctx.expr(0)));
      st.add("op", ctx.op.getText());
      st.add("value2", this.visit(ctx.expr(1)));
      return st;
   }

   @Override public ST visitExprCast(qlangParser.ExprCastContext ctx) {
      if(ctx.type.getText().equals("integer")){
         ST st = allTemplates.getInstanceOf("intConvert");
         st.add("expr", this.visit(ctx.expr()));
         return st;
      } else if(ctx.type.getText().equals("text")){
         ST st = allTemplates.getInstanceOf("stringConvert");
         st.add("expr", this.visit(ctx.expr()));
         return st;
      } 
      ST res = null; 
      return res;
   }

   @Override public ST visitExprRel(qlangParser.ExprRelContext ctx) {
      ST st = allTemplates.getInstanceOf("relacionalExpr");
      st.add("var1", this.visit(ctx.expr(0)));
      st.add("condition", ctx.op.getText());
      st.add("var2", this.visit(ctx.expr(1)));
      return st;
   }

   @Override public ST visitExprUnary(qlangParser.ExprUnaryContext ctx) {
      ST st = allTemplates.getInstanceOf("exprUnary");
      st.add("value1", this.visit(ctx.expr()));
      st.add("op", ctx.op.getText());
      return st;
   }

   @Override public ST visitExprExecute(qlangParser.ExprExecuteContext ctx) {
      ST st = allTemplates.getInstanceOf("RunQuestion");
      st.add("question", this.visit(ctx.expr()));
      return st;
   }

   @Override public ST visitExprNew(qlangParser.ExprNewContext ctx) {
      ST st = allTemplates.getInstanceOf("returnData");
      st.add("expr1", this.visit(ctx.Identifier()));
      return st;
   }

   @Override public ST visitExprBoolOp(qlangParser.ExprBoolOpContext ctx) {
      ST res = null;
      switch(ctx.op.getText()){
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

   @Override public ST visitExprInteger(qlangParser.ExprIntegerContext ctx) {
      ST st = allTemplates.getInstanceOf("intConvert");
      st.add("expr", this.visit(ctx));
      return st;
   }

   @Override public ST visitHoleQuestion(qlangParser.HoleQuestionContext ctx) {
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx.holeQuestionBlock())+"])";
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitHoleQuestionStatement(qlangParser.HoleQuestionStatementContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitHoleQuestionBlock(qlangParser.HoleQuestionBlockContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitOpenQuestion(qlangParser.OpenQuestionContext ctx) {
      //a verificar
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx.printStatBlock())+"])";
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitCodeOpenQuestion(qlangParser.CodeOpenQuestionContext ctx) {
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx.printStatBlock())+"])";
            content += "Group("+parts[i]+",["+this.visit(ctx.importStat())+"])";
            
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitCodeHoleQuestion(qlangParser.CodeHoleQuestionContext ctx) {
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx.printStatBlock())+"])";
            content += "Group("+parts[i]+",["+this.visit(ctx.importStat())+"])";
            
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitMultiChoiceQuestion(qlangParser.MultiChoiceQuestionContext ctx) {
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx.printStatBlock())+"])";
            content += "Group("+parts[i]+",["+this.visit(ctx.importStat())+"])";
            content += "Group("+parts[i]+",["+this.visit(ctx.choiceStatBlock())+"])";            
            
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitCodeOutputQuestion(qlangParser.CodeOutputQuestionContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitCodeStat(qlangParser.CodeStatContext ctx) {
      ST st = allTemplates.getInstanceOf("instantiate");
      st.add("expr", this.visit(ctx.Identifier()));
      st.add("class", ctx.getTokens(0));
      return st;
   }

   @Override public ST visitImportStat(qlangParser.ImportStatContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitChoiceStat(qlangParser.ChoiceStatContext ctx) {
      ST st = allTemplates.getInstanceOf("choice");
      st.add("expr1", this.visit(ctx.Fraction()));
      st.add("expr2", this.visit(ctx.StringLiteral()));
      return st;
   }

   @Override public ST visitChoiceStatBlock(qlangParser.ChoiceStatBlockContext ctx) {
      ST st = allTemplates.getInstanceOf("block");
           
      for (var stat : ctx.choiceStat()) {
         st.add("stat", this.visit(stat));
      }

      return st;
   }

   @Override public ST visitCodeinline(qlangParser.CodeinlineContext ctx) {
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx)+"])";
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitComposed(qlangParser.ComposedContext ctx) {
      ST st= allTemplates.getInstanceOf("InstanciateGroup");
      String[] parts=ctx.Identifier().getText().split("\\.");
      String content = "";
      for(int i=1;i<parts.length;i++){
         if (i+1<parts.length) {
            content += "]Group("+parts[i]+", [";
         }         
         if (i+1>=parts.length) {
            content += "Group("+parts[i]+",["+this.visit(ctx.composedBlock())+"])";
         }
      }
      st.add("vargroup", parts[0]);
      st.add("groupName", parts[0]);
      st.add("childGroups", content);
      return st;
   }

   @Override public ST visitComposedStatement(qlangParser.ComposedStatementContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitComposedBlock(qlangParser.ComposedBlockContext ctx) {
      ST st = allTemplates.getInstanceOf("block");
           
      for (var stat : ctx.composedStatement()) {
         st.add("stat", this.visit(stat));
      }

      return st;
   }

   @Override public ST visitBlock(qlangParser.BlockContext ctx) {
      ST st = allTemplates.getInstanceOf("block");
           
      for (var stat : ctx.stat()) {
         st.add("stat", this.visit(stat));
      }

      return st;
   }

   @Override public ST visitCondStat(qlangParser.CondStatContext ctx) {
      ST st = allTemplates.getInstanceOf("ifExpr");
      st.add("relacionalExpr", this.visit(ctx.expr()));
      st.add("expr", this.visit(ctx.block(0)));
      for (var block : ctx.elseifBlock()) {
         st.add("elif", this.visit(block));
      }
      if(ctx.block().size()>1){
         st.add("else", this.visit(ctx.block(1)));
      }
      return st;
   }

   @Override public ST visitElseifBlock(qlangParser.ElseifBlockContext ctx) {
      ST st = allTemplates.getInstanceOf("elifExpr");
      st.add("relacionalExpr", this.visit(ctx.expr()));
      st.add("expr", this.visit(ctx.block()));
      return st;
   }

   @Override public ST visitPrintStatBlock(qlangParser.PrintStatBlockContext ctx) {
      ST st = allTemplates.getInstanceOf("block");
           
      for (var stat : ctx.printStat()) {
         st.add("stat", this.visit(stat));
      }

      return st;
   }

   @Override public ST visitPrintStat(qlangParser.PrintStatContext ctx) {
      ST st = allTemplates.getInstanceOf("instantiate");
      st.add("expr", this.visit(ctx.expr()));
      st.add("class", "Print");
      return st;
   
   }

   @Override public ST visitLoopStat(qlangParser.LoopStatContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitVariableDeclaration(qlangParser.VariableDeclarationContext ctx) {
      ST st = allTemplates.getInstanceOf("instantiate");
      st.add("expr", this.visit(ctx.Identifier()));
      st.add("class", ctx.getTokens(0));
      return st;
   }

   @Override public ST visitAssignment(qlangParser.AssignmentContext ctx) {
      ST st = allTemplates.getInstanceOf("impliesExpr");
      st.add("var", this.visit(ctx.Identifier()));
      st.add("expr", this.visit(ctx.expr()));
      return st;
   }

   @Override public ST visitHole(qlangParser.HoleContext ctx) {
      ST st = allTemplates.getInstanceOf("instantiate");
      st.add("class", this.visit(ctx.Identifier()));
      st.add("expr", this.visit(ctx.StringLiteral()));
      return st;

   }

   @Override public ST visitGradeRule(qlangParser.GradeRuleContext ctx) {
      ST res = null;
      return visitChildren(ctx);
      //return res;
   }

   @Override public ST visitExport(qlangParser.ExportContext ctx) {
      ST st = allTemplates.getInstanceOf("RC_export");
      st.add("file", this.visit(ctx.StringLiteral()));
      st.add("instance", this.visit(ctx.Identifier()));
      return st;
   }
}
