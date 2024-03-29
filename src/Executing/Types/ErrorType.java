package Executing.Types;

import Executing.Executor;

import java.util.LinkedList;

public class ErrorType extends ObjectType {
    static ClassType type;
    public static void createType()
    {
        type = new ClassType();
        type.setMember("__name__", new StringType("error"));
        type.setMember("__str__", new FunctionType("__str__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args){
                ErrorType error = ((ErrorType)(args.get(0)));
                return new StringType(error.toString());
            }
        }
        ));

        Executor.setVariable("error", type);
    }
    public ErrorType() {
        this.setMember("__class__", type);
        this.setMember("message", new StringType("error"));
        this.setMember("__name__", new StringType("error"));
    }

    public ErrorType(String name, String message){
        this.setMember("__class__", type);
        this.setMember("message", new StringType(message));
        this.setMember("__name__", new StringType(name));
    }

    public ErrorType(StringType message){
        this.setMember("__class__", type);
        this.setMember("message", message);
        this.setMember("__name__", new StringType("error"));
    }

    public ErrorType(String message){
        this.setMember("__class__", type);
        this.setMember("message", new StringType(message));
        this.setMember("__name__", new StringType("error"));
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.getMember("message").toString());
            if (this.contains("line")) {
                sb.append(": ");
                sb.append((this.getMember("line").toString()));
                if (this.contains("position")) {
                    sb.append(" ");
                    sb.append((this.getMember("position").toString()));
                }
            }
            return sb.toString();
        }
        catch (ExecutionException e){
            return "ERROR IN toString method of error, should not happen under any circumstances";
        }
    }

    public void setMessage(String message){
        this.setMember("message", new StringType(message));
    }

    public void setLine(int line){
        this.setMember("line", new IntType(line));
    }

    public void setPosition(int position){
        this.setMember("position", new IntType(position));
    }

    public String getMessage() throws ExecutionException {
        return this.getMember("message").toString();
    }

    public int getLineNum() throws ExecutionException {
        return ((IntType) (this.getMember("line"))).getInt();
    }

    public boolean hasLineNum() throws ExecutionException {
        return this.members.containsKey("line");
    }

    public ObjectType execute() {
        return this;
    }
}
