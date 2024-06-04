import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {

    private Stack<Map<String, Type>> scopes;

    public SymbolTable() {
        scopes = new Stack<>();
        // para adicionar um global scope
        enterScope();
        declare("result.name", Type.TEXT);
        declare("result.grade", Type.FRACTION);
        declare("result.id", Type.TEXT);
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) { // assim garanto que não sai do global scope
            scopes.pop();
        }
    }

    public void declare(String name, Type type) {
        var currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            throw new SemanticException("Variable " + name + " already declared in this scope.");
        }
        currentScope.put(name, type);
    }

    public Type lookup(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            var scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        throw new SemanticException("Variable " + name + " não declarada.");
    }
}
