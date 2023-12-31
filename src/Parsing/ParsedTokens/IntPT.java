package Parsing.ParsedTokens;

import Lexing.Token;

public class IntPT extends ParsedToken{
    public IntPT(Token t){
        super(t);
    }
    public ParsedTokenType getParsedType(){
        return ParsedTokenType.VALUE;
    }
    public String toString(){
        return token.getValue();
    }
}
