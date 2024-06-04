import java.io.FileWriter;
import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.stringtemplate.v4.ST;

public class qlangMain {
    public static void main(String[] args) {
        try {
            // create a CharStream that reads from standard input:
            CharStream input = CharStreams.fromFileName(args[0]);
            // create a lexer that feeds off of input CharStream:
            qlangLexer lexer = new qlangLexer(input);
            // create a buffer of tokens pulled from the lexer:
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // create a parser that feeds off the tokens buffer:
            qlangParser parser = new qlangParser(tokens);
            // replace error listener:
            //parser.removeErrorListeners(); // remove ConsoleErrorListener
            //parser.addErrorListener(new ErrorHandlingListener());
            // begin parsing at program rule:
            ParseTree tree = parser.program();
            if (parser.getNumberOfSyntaxErrors() == 0) {
                // print LISP-style tree:
                // System.out.println(tree.toStringTree(parser));
                SemanticalVisitor sv = new SemanticalVisitor();
                sv.visit(tree);

                Compiler visitor0 = new Compiler();
                ST st = visitor0.visit(tree);

                FileWriter fw = new FileWriter(args[1]);
                fw.write(st.render());
                fw.close();
            }
        }
        catch (SemanticException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch(RecognitionException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
