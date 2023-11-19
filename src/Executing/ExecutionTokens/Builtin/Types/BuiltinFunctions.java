package Executing.ExecutionTokens.Builtin.Types;

import Executing.Executor;

import java.util.LinkedList;

public class BuiltinFunctions {
    public static void createFunctions(){
        FunctionType print = new FunctionType("print", new SourceFunction(){
            public ObjectType execute(LinkedList<ObjectType> args){
                ObjectType arg = args.get(0);
                args.removeFirst();
                StringType resultObj = ((StringType)arg.getMember("__str__").call(args));
                System.out.println(resultObj.getValue());
                return resultObj;
            }
        });
        Executor.setVariable("print", print);

        FunctionType abs = new FunctionType("abs", new SourceFunction(){
            public ObjectType execute(LinkedList<ObjectType> args){
                return args.get(0).getMember("__class__").getMember("__abs__").call(args.get(0));
            }
        });

        Executor.setVariable("abs", abs);

    }
}
