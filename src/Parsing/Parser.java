package Parsing;

import Lexing.Token;
import Lexing.TokenType;
import Parsing.ParsedTokens.*;

import java.beans.Expression;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Parser {
    static ParsingLogger logger = new ParsingLogger();
    boolean logging;
    public Parser(){
        logging = true;
    }

    public Parser(boolean logging){
        this.logging = logging;
    }


    static final HashSet<String> SECOND_PRIORITY = new HashSet<String>(Arrays.asList("+", "-"));
    static final HashSet<String> FIRST_PRIORITY = new HashSet<String>(Arrays.asList("*", "/"));
    static final HashSet<String> COMPARISON_OPERATIONS = new HashSet<String>(Arrays.asList("==", "<", ">"));
    public LinkedList<ParsedAbstractStatement> parse (LinkedList<Token> tokens) throws ParsingException{
        LinkedList<LinkedList<Token>> lines = divideByLines(tokens);
        LinkedList<ParsedAbstractStatement> parsedLines = new LinkedList<ParsedAbstractStatement>();
        try {
            logger.info("Start parsing");
            for(int i = 0; i < lines.size(); ++i){
                if (logging) {
                    logger.info("Start parsing " + i + " line \r\n"+logger.tokensToString(lines.get(i)));
                }
                parsedLines.add(parseLine(lines.get(i)));
                if (logging) {
                    logger.info(i + " line is parsed \r\n" + parsedLines.getLast().toString());
                }
            }
            parsedLines = processBlocks(parsedLines);
        }
        catch (ParsingException e){
            System.out.println(e);
        }
        return parsedLines;
    }

    public LinkedList<LinkedList<Token>> divideByLines(LinkedList<Token> tokens){
        LinkedList<LinkedList<Token>> lines = new LinkedList<LinkedList<Token>>();
        LinkedList<Token> line = new LinkedList<Token>();
        for (Token token : tokens) {
            if (token.getType().equals(TokenType.NEWLINE)) {
                lines.add(line);
                line = new LinkedList<Token>();
            } else
                line.add(token);
        }
        return lines;
    }

    public ParsedAbstractStatement parseLine(LinkedList<Token> line) throws ParsingException{ //call it to parse whole line
        int indent = 0;
        ParsedAbstractStatement statement;
        while (indent < line.size() && line.get(indent).getType() == TokenType.INDENTATION) //count and remove indentation
            indent++;
        for(int i = 0; i < indent; ++i)
            line.removeFirst(); // checked indent and removed it from list of tokens
        if (line.get(0).getType() == TokenType.BLOCKWORD) {
            return parseLineWithBlockword(indent, line);
        }
        else{
            //if not BLOCKWORD then expression or assignment
            if (line.get(1).getType() == TokenType.ASSIGNMENT){
                return new ParsedAssigmentStatement(line.get(1),
                        indent,
                        new ParsedVariable(line.get(0)),
                        parseExpressionTokens(new LinkedList<Token>(line.subList(2, line.size()))));
            }
            else //then expression
                return new ParsedStatement(indent, parseExpressionTokens(line));
        }
    }

    public ParsedAbstractStatement parseLineWithBlockword(int indent, LinkedList<Token> line) throws ParsingException {
        if (line.get(0).getValue().equals("if")){
            return new ParsedConditionalStatement(line.get(0),
                    indent,
                    parseExpressionTokens(new LinkedList<Token>(line.subList(1, line.size()))));
        }
        if (line.get(0).getValue().equals("elif")){
            return new ParsedConditionalStatement(line.get(0),
                    indent,
                    parseExpressionTokens(new LinkedList<Token>(line.subList(1, line.size()))));
        }
        if (line.get(0).getValue().equals("else")){
            return new ParsedConditionalStatement(line.get(0),
                    indent);
        }
        if (line.get(0).getValue().equals("while")){
            return new ParsedConditionalStatement(line.get(0),
                    indent,
                    parseExpressionTokens(new LinkedList<Token>(line.subList(1, line.size()))));
        }
        if (line.get(0).getValue().equals("def")){
            return new ParsedFunctionDefinition(line.get(0),
                    indent, line.get(1),
                    parseArgsTokens(new LinkedList<Token>(line.subList(2, line.size()))));
        }
        line.removeFirst();
        // rest is processed as expression if needed
        return null;
    }

    public LinkedList<ParsedVariable> parseArgsTokens(LinkedList<Token> line) throws ParsingException {
        LinkedList<ParsedVariable> args = new LinkedList<ParsedVariable>();
        for (int i = 1; i < line.size()-1; ++i) {
            if (line.get(i).getType() == TokenType.PARENTHESIS) {

            }
            if (line.get(i).getType() == TokenType.SEPARATOR) {

            } else {
                args.add(new ParsedVariable(line.get(i)));
            }
        }
        // can process exceptions here
        return args;
    }

    public ParsedToken parseExpressionTokens(LinkedList<Token> line) throws ParsingException {
        LinkedList<ParsedToken> parsedLine = new LinkedList<ParsedToken>();
        for(int i = 0; i < line.size(); ++i){
            parsedLine.add(new ParsedToken(line.get(i)));
        }
        for(int i = 0; i < parsedLine.size(); ++i){
            if (parsedLine.get(i).getType() == TokenType.VARIABLE){
                parsedLine.set(i, new ParsedVariable(parsedLine.get(i).getToken()));
            }
            if (parsedLine.get(i).getType() == TokenType.INT){
                parsedLine.set(i, new ParsedIntToken(parsedLine.get(i).getToken()));
            }
            if (parsedLine.get(i).getType() == TokenType.FLOAT){
                parsedLine.set(i, new ParsedIntToken(parsedLine.get(i).getToken()));
            }
        } // we can do that much for now
        return parseExpression(parsedLine);
    }

    public ParsedFunctionCall parseFunctionCall(LinkedList<ParsedToken> line) throws ParsingException {
        Token functionToken = line.get(0).getToken();
        LinkedList<ParsedToken> args = new LinkedList<ParsedToken>();
        LinkedList<ParsedToken> arg = new LinkedList<ParsedToken>();
        for (int i = 2; i < line.size()-1; ++i) {
            if (line.get(i).getType() == TokenType.SEPARATOR) {
                args.add(parseExpression(arg));
                arg = new LinkedList<ParsedToken>();
            } else {
                arg.add(line.get(i));
            }
        }
        args.add(parseExpression(arg));
        return new ParsedFunctionCall(functionToken, args);
    }

    public ParsedToken divideByOperands(LinkedList<ParsedToken> operands, HashSet<String> operations) throws ParsingException {
        ParsedToken result = null;
        for (int i = 0; i < operands.size(); ++i) {
            if (operations.contains(operands.get(i).getValue()) && operands.get(i).getParsedType() == ParsedTokenType.UNPARSED) {
                LinkedList<ParsedToken> right = new LinkedList<ParsedToken>(operands.subList(i + 1, operands.size()));
                if (i != 0) {
                    LinkedList<ParsedToken> left = new LinkedList<ParsedToken>(operands.subList(0, i));
                    return new ParsedBinaryExpression(operands.get(i).getToken(), parseExpression(left), parseExpression(right));
                }
                else{
                    return new ParsedUnaryExpression(operands.get(i).getToken(), parseExpression(right));
                }
            }
        }
        return null;
    }

    public ParsedToken parseExpression(LinkedList<ParsedToken> line) throws ParsingException {
        Iterator iter = line.iterator();
        int balance = 0;
        int start = -1;
        boolean func_call = false;
        LinkedList<ParsedToken> operands = new LinkedList<ParsedToken>();
        LinkedList<ParsedToken> operand = new LinkedList<ParsedToken>();
        if (line.get(0).getType() == TokenType.PARENTHESIS && line.getLast().getType() == TokenType.PARENTHESIS){
            line.removeFirst();
            line.removeLast();
        }
        if (line.isEmpty()) {
            return null;
        }
        if (line.size() == 1) {
            return line.getFirst();
        }
        //checks
        boolean process_parentheses = false;
        while (iter.hasNext()){
            ParsedToken current = (ParsedToken)iter.next();
            Token curToken = current.getToken();
            switch (curToken.getType()){
                case PARENTHESIS:
                    process_parentheses = true;
                    if (curToken.getValue().equals("(")){
                        balance++;
                    }
                    else{
                        balance--;
                    }
                    operand.add(current);
                    if (balance == 0){
                        if (func_call){
                            operands.add(parseFunctionCall(operand));
                            operand = new LinkedList<ParsedToken>();
                        }
                        else{
                            operand.removeLast();
                            operand.removeFirst();
                            ParsedToken result = parseExpression(operand);
                            if (result != null)
                                operands.add(result);
                            operand = new LinkedList<ParsedToken>();
                        }
                        process_parentheses = false;
                    }
                    break;
                case FUNCTION:
                    process_parentheses = true;
                    if (balance == 0) {
                        func_call = true;
                        operand.add(current);
                    }
                    break;
                default:
                    if (process_parentheses)
                        operand.add(current);
                    else
                        operands.add(current);
            }
        }
        ParsedToken result = null;
        result = divideByOperands(operands, SECOND_PRIORITY);
        if (result != null) return result;
        result = divideByOperands(operands, FIRST_PRIORITY);
        if (result != null) return result;
        result = divideByOperands(operands, COMPARISON_OPERATIONS);
        if (result != null) return result;
        return operands.get(0);
    }

    public LinkedList<ParsedAbstractStatement> processBlocks(LinkedList<ParsedAbstractStatement> tokens){
        int lowerIndent = 0;
        for (ParsedAbstractStatement curLine: tokens){
            if (curLine.getIndentationLevel() > lowerIndent)
                lowerIndent = curLine.getIndentationLevel();
        }
        ParsedBlock block = null;
        while (lowerIndent != 0) {
            ParsedStatementWithBlock head = null;
            Iterator iter = tokens.iterator();
            while (iter.hasNext()) {
                ParsedAbstractStatement curLine = (ParsedAbstractStatement) iter.next();
                if (curLine.getType() == TokenType.BLOCKWORD && curLine.getIndentationLevel() == lowerIndent - 1) {
                    if (head == null) {
                        head = (ParsedStatementWithBlock)curLine;
                        block = new ParsedBlock(curLine.getToken());
                    }
                    else {
                        head.setToDo(block);
                        head = (ParsedStatementWithBlock)curLine;
                        block = null;
                    }
                }
                else {
                    if (head != null) {
                        if (curLine.getIndentationLevel() == lowerIndent) {
                            block.addStatement(curLine);
                            iter.remove();
                        }
                        else{
                            head.setToDo(block);
                            head = null;
                            block = null;
                        }
                    }
                }
            }

            if (block != null) {
                head.setToDo(block);
                head = null;
                block = null;
            }

            //now finding if elif else
            iter = tokens.iterator();
            ParsedConditionalStatement head1 = null;
            while (iter.hasNext()) {
                ParsedAbstractStatement curLine = (ParsedAbstractStatement) iter.next();
                if (curLine.getType() == TokenType.BLOCKWORD && curLine.getIndentationLevel() == lowerIndent - 1) {
                    if (curLine.getValue().equals("if") || curLine.getValue().equals("while"))
                        head1 = (ParsedConditionalStatement) curLine;
                    if (curLine.getValue().equals("elif")) {
                        iter.remove();
                        ((ParsedConditionalStatement)head1).append((ParsedConditionalStatement)curLine);
                    }
                    if (curLine.getValue().equals("else")) {
                        iter.remove();
                        ((ParsedConditionalStatement)head1).append((ParsedConditionalStatement)curLine);
                        head = null;
                    }

                }

            }
            lowerIndent--;
        }
        return tokens;
    }
}
