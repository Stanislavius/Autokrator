package Executing.ExecutionTokens;

import Executing.ExecutionTokens.Builtin.Types.ObjectType;
import Executing.ExecutionTokens.Builtin.Types.VoidType;
import Lexing.Token;

import java.util.LinkedList;

public class MemberExecutionToken extends ExecutionToken {
    ExecutionToken object;
    ExecutionToken member;
    public MemberExecutionToken(Token token, ExecutionToken object, ExecutionToken member) {
        super(token);
        this.object = object;
        this.member = member;
    }

    public ObjectType execute(){
        ObjectType result = object.execute();
        return result.getMember(member.getToken().getValue());
    }

    public String getNameMember(){
        return member.getToken().getValue();
    }
    public ObjectType executeObject(){
        return object.execute();
    }

    public ExecutionToken getObject(){
        return object;
    }

    public ExecutionToken getMember(){
        return member;
    }
}