package com.compiler.lexer.regex;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Utility class for regular expression parsing using the Shunting Yard
 * algorithm.
 * <p>
 * Provides methods to preprocess regular expressions by inserting explicit
 * concatenation operators, and to convert infix regular expressions to postfix
 * notation for easier parsing and NFA construction.
 */
/**
 * Utility class for regular expression parsing using the Shunting Yard
 * algorithm.
 */
public class ShuntingYard {

    /**
     * Default constructor for ShuntingYard.
     */
    public ShuntingYard() {
        // TODO: Implement constructor if needed
    }

    /**
     * Look up for an element, if it is int the array returns true, in other case return false.
     * @param arr character array.
     * @param character character to find in the array.
     * @return true if character is in it, false in other case.
     */
    public static boolean contains(char[] arr, char caracter){
        for(char car:arr){
            if(car==caracter){  
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts the explicit concatenation operator ('.') into the regular
     * expression according to standard rules. This makes implicit
     * concatenations explicit, simplifying later parsing.
     *
     * @param regex Input regular expression (may have implicit concatenation).
     * @return Regular expression with explicit concatenation operators.
     */
    public static String insertConcatenationOperator(String regex) {
        // /*
        //     Pseudocode:
        //     For each character in regex:
        //         - Append current character to output
        //         - If not at end of string:
        //                 - Check if current and next character form an implicit concatenation
        //                 - If so, append '.' to output
        //     Return output as string
        //  */

        //string to return at the end.
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < regex.length(); i++) {
            
            //concat the current char to the str.
            str=str.append(regex.charAt(i));
            
            //condition of loop exit.
            if (i==regex.length()-1) {
                break;
            }else{
                //auxiliar variables.
                char current = regex.charAt(i);
                char next = regex.charAt(i+1);

                //cases of implicit concatetion.

                //case 1.

                if (isOperand(current) && isOperand(next)) {
                    str.append('.');
                
                //case 2
                }else if (isOperand(current) && next=='(') {
                    str.append('.');
                
                //case 3
                }else if(current==')' && isOperand(next)){
                    str.append('.');

                //case 4
                }else if(isUnary(current) && isOperand(next)){
                    str.append('.');
                
                //case 5
                }else if (current==')'&&next=='(') {
                    str.append('.');
                }
            }
        }
        return str.toString();

    }

    /**
     * Auxiliar Function to know if a character is a operator unary or not.
     * 
     * @param c The character to compare with *?+
     * @return true if c is unary, false in any other case.
     */
    public static boolean isUnary( char c ){
        if ( isOperand(c) ){
            return false;
        }
        switch (c) {
            case '*':
                return true;
            case '?':
                return true;
            case '+':
                return true;
            default:
                return false;
    }   }

    /**
     * Determines if the given character is an operand (not an operator or
     * parenthesis).
     *
     * @param c Character to evaluate.
     * @return true if it is an operand, false otherwise.
     */
    private static boolean isOperand(char c) {
        /*
        Pseudocode:
        Return true if c is not one of: '|', '*', '?', '+', '(', ')', '·'
         */

        char[] operadores = {'|', '*', '?', '+', '(', ')', '.'};
        if (contains(operadores, c)) {
            return false;
        }else{
            return true;
        }
    }

    /**
     * Converts an infix regular expression to postfix notation using the
     * Shunting Yard algorithm. This is useful for constructing NFAs from
     * regular expressions.
     *
     * @param infixRegex Regular expression in infix notation.
     * @return Regular expression in postfix notation.
     */
    public static String toPostfix(String infixRegex) {
        /*
        Pseudocode:
        1. Define operator precedence map
        2. Preprocess regex to insert explicit concatenation operators
        3. For each character in regex:
            - If operand: append to output
            - If '(': push to stack
            - If ')': pop operators to output until '(' is found
            - If operator: pop operators with higher/equal precedence, then push current operator
        4. After loop, pop remaining operators to output
        5. Return output as string
        */
        Map<Character, Integer> operators = new HashMap<>();
        operators.put('?',5);
        operators.put('*', 4);
        operators.put('+', 3);
        operators.put('.', 2);
        operators.put('|', 1);
        operators.put('(', 0);
        operators.put(')', 0);

        //Auxiliar Variables
        StringBuilder str = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        
        //explicit concatenation of infixRegex
        String str_concat= insertConcatenationOperator(infixRegex);

        for (int i = 0; i < str_concat.length(); i++){
            char current=str_concat.charAt(i);

            
            if(isOperand(current)){
                //case1
                str.append(current);

            }else if(current=='('){
                //case 2
                stack.push(current);
            
                
            }else if(current==')'){
                //case3
                //pop until '(' appears.
                while ((!stack.isEmpty()) && stack.peek()!='('){
                    str.append(stack.pop());
                    
                }
                stack.pop(); // this is necesary to delete the '(' remaining character.
                
                
            }else if(!isOperand(current)){
                //case4

                while ( (!stack.isEmpty()) && (operators.get(current)<=operators.get(stack.peek()))  ) {
                    //while the stack is not empty aand the current operartor has a minor priority.
                    str.append(stack.pop());
                }

                //finally we add the operator to the stack
                stack.push(current);
            }  
        }

        //we need to pop all the elements remaining in the stack to the str.
        while (!stack.isEmpty()){
            str.append(stack.pop());            
        }

        return str.toString();

    }

    public static void main(String[] args) {
        System.out.println("Pruebas locales para los metodos de esta clase.");
        System.out.println("Prueba para la notacion posfija");
        System.out.println("resultado: "+toPostfix("(a|b)*(c)+"));
        System.out.println("resultado: "+toPostfix("(a|b)*(c)"));
        System.out.println("resultado: "+toPostfix("abc*(a+b)"));
        System.out.println("resultado: "+toPostfix("(a)(a)"));
        System.out.println("resultado: "+toPostfix("((a|b|c)*d+)|e"));
        System.out.println("resultado: "+toPostfix("(a|b)*abb(a|b)*"));
        System.out.println("resultado: "+toPostfix("((a|b)+)|(def)*"));
        

    }
}
