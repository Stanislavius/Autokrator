package Executing.Types;

import Executing.Executor;

import java.util.LinkedList;

public class FloatType extends ObjectType {
    Float value;
    static ClassType type;
    public static void createType()
    {
        type = new ClassType();
        type.setMember("__name__", new StringType("float"));

        type.setMember("__call__", new FunctionType("__call__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                ObjectType val1 = args.get(0);
                switch (val1.getType().toString()) {
                    case "int":
                        IntType it = (IntType) val1;
                        return new FloatType((float) it.getInt());
                    case "str":
                        StringType sT = (StringType) val1;
                        return new FloatType(Float.parseFloat(sT.getValue()));
                    case "float":
                        return val1;
                    default:
                        return val1.getMember("__float__").call();
                }
            }
        }
        ));


        type.setMember("__add__", new FunctionType("__add__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                float v1 = ((FloatType)(args.get(0))).getFloat();
                ObjectType val2 = args.get(1);
                if (val2.getType().toString().equals("int")) {
                    int v2 = ((IntType) (val2)).getInt();
                    return new FloatType(v1+v2);
                }
                if (val2.getType().toString().equals("float")) {
                    float v2 = ((FloatType) (val2)).getFloat();
                    return new FloatType(v1+v2);
                }
                return VoidType.voidObject;
            }
        }
        ));

        type.setMember("__pos__", new FunctionType("__pos__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                return args.get(0);
            }
        }
        ));

        type.setMember("__abs__", new FunctionType("__str__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args){
                float v1 = ((FloatType)(args.get(0))).getFloat();
                return new FloatType(Math.abs(v1));
            }
        }
        ));

        type.setMember("__sub__", new FunctionType("__sub__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                float v1 = ((FloatType)(args.get(0))).getFloat();
                ObjectType val2 = args.get(1);
                if (val2.getType().toString().equals("int")) {
                    int v2 = ((IntType) (val2)).getInt();
                    return new FloatType(v1-v2);
                }
                if (val2.getType().toString().equals("float")) {
                    float v2 = ((FloatType) (val2)).getFloat();
                    return new FloatType(v1-v2);
                }
                return VoidType.voidObject;
            }
        }
        ));

        type.setMember("__mul__", new FunctionType("__mul__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                float v1 = ((FloatType)(args.get(0))).getFloat();
                ObjectType val2 = args.get(1);
                if (val2.getType().toString().equals("int")) {
                    int v2 = ((IntType) (val2)).getInt();
                    return new FloatType(v1*v2);
                }
                if (val2.getType().toString().equals("float")) {
                    float v2 = ((FloatType) (val2)).getFloat();
                    return new FloatType(v1*v2);
                }
                return VoidType.voidObject;
            }
        }
        ));

        type.setMember("__div__", new FunctionType("__div__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                float v1 = ((FloatType)(args.get(0))).getFloat();
                ObjectType val2 = args.get(1);
                if (val2.getType().toString().equals("int")) {
                    int v2 = ((IntType) (val2)).getInt();
                    if (v2 == 0) {
                        ErrorType error = new ErrorType("DivisionByZero");
                        throw new ExecutionException(error);
                    }
                    return new FloatType(v1/v2);
                }
                if (val2.getType().toString().equals("float")) {
                    float v2 = ((FloatType) (val2)).getFloat();
                    if (v2 == 0.0) {
                        ErrorType error = new ErrorType("DivisionByZero");
                        throw new ExecutionException(error);
                    }
                    return new FloatType(v1/v2);
                }
                return VoidType.voidObject;
            }
        }
        ));

        type.setMember("__neg__", new FunctionType("__neg__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args){
                float v1 = ((FloatType)(args.get(0))).getFloat();
                return new FloatType(-v1);
            }
        }
        ));

        type.setMember("__str__", new FunctionType("__str__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args){
                float v1 = ((FloatType)(args.get(0))).getFloat();
                return new StringType(v1+"");
            }
        }
        ));

        type.setMember("__eq__", new FunctionType("__eq__", new SourceFunctionType(){
            public ObjectType execute(LinkedList<ObjectType> args) throws ExecutionException {
                float v1 = ((FloatType)(args.get(0))).getFloat();
                ObjectType val2 = args.get(1);
                if (val2.getType().toString().equals("int")) {
                    int v2 = ((IntType) (val2)).getInt();
                    if (v1 == v2)
                        return new IntType(1);
                    else
                        return new IntType(0);

                }
                if (val2.getType().toString().equals("float")) {
                    float v2 = ((FloatType) (val2)).getFloat();
                    if (v1 == v2)
                        return new IntType(1);
                    else
                        return new IntType(0);
                }

                if (val2.getType().toString().equals("str")) {
                    return new IntType(0);
                }
                if (val2.getType().toString().equals("list")) {
                    return new IntType(0);
                }
                if (val2.getType().toString().equals("dict")) {
                    return new IntType(0);
                }
                if (val2.getType().toString().equals("type")) {
                    return new IntType(0);
                }
                LinkedList<ObjectType> revArgs = new LinkedList<ObjectType>();
                if (val2.getMember("__class__").contains("__eq__")) {
                    revArgs.add(val2);
                    revArgs.add(args.get(0));
                    return val2.getMember("__eq__").call(revArgs);
                }
                else{
                    return new IntType(0);
                }
            }
        }
        ));

        Executor.setVariable("float", type);
    }
    public FloatType(Float value) {
        this.setMember("__class__", type);
        this.value = value;
    }

    public float getFloat(){
        return value;
    }
    public String toString(){
        return "" + value;
    }

    public boolean equals(IntType other){
        return this.getFloat() == other.getInt();
    }

    public boolean equals(FloatType other){
        return this.getFloat() == other.getFloat();
    }

    public boolean equals(Object other){
        if (other.getClass() == IntType.class)
            return this.equals((IntType) other);
        else{
            if (other.getClass() == FloatType.class)
                return this.equals((FloatType) other);
            else
                return false;
        }
    }

    public int hashCode(){
        return value.hashCode();
    }
}
