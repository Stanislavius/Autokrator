package Parsing.ParsedTokens;

import Lexing.Token;

import java.util.LinkedList;

public class DictPT extends ParsedToken{
    LinkedList<ParsedToken> keys;
    LinkedList<ParsedToken> values;
    public DictPT(Token t, LinkedList<ParsedToken> keys, LinkedList<ParsedToken> values){
        super(t);
        this.values = values;
        this.keys = keys;
    }
    public ParsedTokenType getParsedType(){
        return ParsedTokenType.VALUE;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i = 0; i < keys.size(); ++i){
            sb.append(keys.get(i).toString());
            sb.append(":");
            sb.append(values.get(i).toString());
            if (i != keys.size() - 1)
                sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    public LinkedList<ParsedToken> getValues(){return values;}

    public LinkedList<ParsedToken> getKeys(){return keys;}
}
