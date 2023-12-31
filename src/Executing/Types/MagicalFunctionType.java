package Executing.Types;

import java.util.Arrays;
import java.util.LinkedList;

public class MagicalFunctionType implements WrapperInterface {
    final static LinkedList<String> names = new LinkedList<>(Arrays.asList(
            "__add__",
            "__sub__",
            "__mul__"));
    String name;

    public MagicalFunctionType(String name){
        this.name = name;
    }
    public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
        ObjectType arg = args.get(0);
        args.removeFirst();
        return arg.getMember(name).call(args);
    }

    public static void createdMagicalFunctions(){
    }
}
