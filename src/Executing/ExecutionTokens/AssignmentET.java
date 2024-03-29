package Executing.ExecutionTokens;

import Executing.Types.ExecutionException;
import Executing.Types.ObjectType;
import Executing.Types.VoidType;
import Executing.Executor;
import Lexing.Token;

import java.util.LinkedList;

public class AssignmentET extends ExecutionToken {
    ExecutionToken assignTo;
    ExecutionToken value;

    public AssignmentET(Token t, ExecutionToken assignTo, ExecutionToken value) {
        super(t);
        this.assignTo = assignTo;
        this.value = value;
    }

    public ObjectType execute() throws ExecutionException {
        if(assignTo.getClass().equals(VariableET.class)){
            String name = assignTo.getToken().getValue();
            ObjectType val = value.execute();
            Executor.setVariable(name, val);
            Executor.logger.info("Assign " + name + " = " + val);
        }

        if (assignTo.getClass().equals(MemberET.class)){
            MemberET met = (MemberET)  assignTo;
            ObjectType object = met.executeObject();
            ObjectType val = value.execute();
            object.setMember(met.getNameMember(), val);
            Executor.logger.info("Assign " + met.toString() + " = " + val);
        }

        if(assignTo.getClass().equals(ItemET.class)){
            ((ItemET) assignTo).executeSetItem(value.execute());
            Executor.logger.info("Assign " + assignTo.toString() + " = " + value.toString());
        }

        return new VoidType();
    }

    public String toString(){
        return assignTo.toString() + "=" + value.toString();
    }

    public void replaceOuterVariableIfHasAny(LinkedList<String> args) throws ExecutionException {
        value.replaceOuterVariableIfHasAny(args);
    }
}
