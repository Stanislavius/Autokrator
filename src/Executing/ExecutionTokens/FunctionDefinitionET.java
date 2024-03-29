package Executing.ExecutionTokens;
import Executing.Types.*;
import Executing.Executor;
import Lexing.Token;

import java.util.LinkedList;

public class FunctionDefinitionET extends ExecutionToken{
    LinkedList<String> args = new LinkedList<String>();
    LinkedList<ExecutionToken> toDo;
    String name;
    public FunctionDefinitionET(Token t, String name, LinkedList<String> args, LinkedList<ExecutionToken> toDo){
        super(t);
        this.args = args;
        this.toDo = toDo;
        this.name = name;
    }

    public ObjectType execute() throws ExecutionException {
        Executor.logger.info("Define custom function " + name);
        FunctionType newFunction = new FunctionType(name, new CustomFunctionType(this));
        Executor.setVariable(name, newFunction);
        return newFunction;
    }

    public static void addFun(String funName, FunctionDefinitionET fun){
        Executor.setVariable(funName, new FunctionType(funName, new CustomFunctionType(fun)));
    }

    public ObjectType execute(LinkedList<ObjectType> funcArgs) throws ExecutionException {
        if (args.size() != funcArgs.size()) {
            ErrorType et = new ErrorType("WrongNumberOfArguments");
            throw new ExecutionException(et);
        }
        Executor.addToStack(this);
        for (int i = 0; i < args.size(); ++i){
            Executor.setVariable(args.get(i), funcArgs.get(i));
        }
        for (int i = 0; i < toDo.size(); ++i){
            if (toDo.get(i).getClass() == FunctionDefinitionET.class)
                ((FunctionDefinitionET) toDo.get(i)).replaceOuterVariableIfHasAny();
        }
        ObjectType result = new VoidType();
        for (int i = 0; i < toDo.size(); ++i){
            result = toDo.get(i).execute();
        }
        Executor.removeFromStack();
        return result;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("def");
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        for(int i = 0; i < args.size(); ++i){
            sb.append(args.get(i).toString());
            if(i != args.size() - 1)
                sb.append(", ");
        }
        sb.append(")");
        sb.append("\n");
        for(int i = 0; i < toDo.size(); ++i){
            sb.append("\t");
            sb.append(toDo.get(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public void replaceOuterVariableIfHasAny(LinkedList<String> args) throws ExecutionException {
        for(int i = 0; i < toDo.size(); ++i){
            toDo.get(i).replaceOuterVariableIfHasAny(args);
        }
    }

    public void replaceOuterVariableIfHasAny() throws ExecutionException {
        for(int i = 0; i < toDo.size(); ++i){
            toDo.get(i).replaceOuterVariableIfHasAny(this.args);
        }
    }
}
