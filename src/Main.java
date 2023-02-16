import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<String, Boolean> vars = new HashMap<>();
        vars.put("a",true);
        vars.put("b",true);
        vars.put("c",false);
        vars.put("d",false);
        Element element =transform("NAND(XOR(a,b), AND(a,c), NOR(a,d), NAND(b,c), NOT(XOR(b,d)), OR(c,d)))");
        evaluate(element.operators,getValues(vars,element.operands));

    }
    static class Element {
        Stack<String> operands = new Stack<>();
        Stack<String> operators = new Stack<>();

        public Element(Stack<String> operators, Stack<String> operands) {
            this.operands = operands;
            this.operators = operators;
        }
    }

    enum Operator{
        AND,
        OR,
        XOR,
        NAND,
        NOR,
        NOT;

        public static Operator  getOperator(String value){
            for (Operator operator:Operator.values()){
                if(operator.name().equals(value)){
                    return operator;
                }
            }
            return null;
        }
    }

    public static boolean eval(String operator , List<Boolean> booleans ){
        return switch (Operator.valueOf(operator)){
            case AND -> booleans.stream().reduce(Boolean::logicalAnd).orElse(false);
            case OR -> booleans.stream().reduce(Boolean::logicalOr).orElse(false);
            case XOR -> booleans.stream().reduce(Boolean::logicalXor).orElse(false);
            case NAND -> !booleans.stream().reduce(Boolean::logicalAnd).orElse(false);
            case NOR -> !booleans.stream().reduce(Boolean::logicalOr).orElse(false);
            case NOT -> booleans.size()>0?booleans.get(0):false;
        };
    }
    public static Element transform (String expression){
                  Stack<String> operands= new Stack<>();     
                  Stack<String> operators= new Stack<>();    
        expression=expression.replaceAll("\\s", "");
        int i=0,j=1;
        String exp;
        while(i<expression.length()){
            exp=expression.substring(i,j);
            if(exp.equals("(") || exp.equals(")") || exp.equals(",") || Objects.nonNull(Operator.getOperator(exp))){
                i=j;
                j++;
                operators.push(exp);
            }else if(expression.charAt(j)==')'|| expression.charAt(j)==','){
                i=j;
                j++;
                operands.push(exp);
            }else {
                 j++;
            }

        }
                 return new Element(operators,operands);
    }
    public static Stack<Boolean> getValues(Map<String, Boolean> map, Stack<String> operands){
        Stack<Boolean> values = new Stack<>();
        for (String operand: operands ){
            values.push(map.get(operand));
        }
        return values;
    }
    public static void evaluate (Stack<String> operators, Stack<Boolean> operands){
        List<Boolean> list= new ArrayList<>();
        List<Boolean> subList= Collections.synchronizedList(new ArrayList<>());
        int index=0;
        String operator, nextOperator;
        while (operators.size()>1){
            operator=operators.pop();
            nextOperator= operators.get(operators.size()-1);
            if(operator.equals(")")) {
                index = list.size();
                if(nextOperator.equals(","))      {
                    list.add(operands.pop());
                }
            }
            else if (operator.equals(",") && !nextOperator.equals(")")) {
                        list.add(operands.pop());
            } else if (operator.equals("(")) {
                               subList= list.subList(index, list.size());
                               list=list.subList(0, index);
                               list.add(eval(nextOperator,subList));
                               index=0;
            }

        }
        if(list.size()==1){
            System.out.println(list.get(0));
        }
        else {
            System.out.println("ERROR §§§!!!");
        }
    }

}