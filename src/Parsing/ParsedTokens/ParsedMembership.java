package Parsing.ParsedTokens;

import Lexing.Token;

public class ParsedMembership extends ParsedToken{
    ParsedVariable object;
    ParsedToken member;


    public ParsedMembership(Token t, ParsedVariable object, ParsedToken member) {
        super(t);
        this.object = object;
        this.member = member;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(object.toString());
        sb.append(token.getValue());
        sb.append(member.toString());
        return sb.toString();
    }

    public ParsedTokenType getParsedType(){
        if (member.getParsedType() == ParsedTokenType.FUNCTION_CALL)
            return ParsedTokenType.MEMBERSHIP_FUNCTION_CALL;
        if (member.getParsedType() == ParsedTokenType.VARIABLE)
            return ParsedTokenType.MEMBERSHIP_VARIABLE;
        return null;
    }
}
