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
     * Busca un elemento dentro de un arreglo si esta regresa True, si no esta regresa False.
     * @param arr arreglo de caracteres.
     * @return true o false si el caracter esta dentro del arreglo o no.
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
        // TODO: Implement insertConcatenationOperator
        /*
            Pseudocode:
            For each character in regex:
                - Append current character to output
                - If not at end of string:
                        - Check if current and next character form an implicit concatenation
                        - If so, append '.' to output
            Return output as string
         */
        String regex_new ="";
        char[] operadores = {'*','|','+','.',')'}; //arreglo para guardar los caracteres que no necesitan una concatenacion
        int apuntador=0; //apuntador para poder ver el siguiente elemento.
        char[] regex_arr = regex.toCharArray(); // arreglo con chars de la cadena.
        
        for (char character :regex_arr ) {
            if (apuntador<regex_arr.length-1) {
                if (character=='('|| character=='.' || character== '|') { //operadores que no necesitan una concatenacion despues de escribirse
                    regex_new=regex_new+character;
                    
                }else{
                    regex_new=regex_new+character;
                    if (!contains(operadores,regex_arr[apuntador+1])){
                        regex_new=regex_new+'.';
                    }
                }    
               apuntador+=1; // para evitar llegar al final del array y romper el algoritmo.
            }else{
                regex_new=regex_new+character; //para el ultimo caracter.
            }
            
        }
        System.out.println("cadena con caracteres de concatenacion: "+regex_new);

        return regex_new;


        //throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Determines if the given character is an operand (not an operator or
     * parenthesis).
     *
     * @param c Character to evaluate.
     * @return true if it is an operand, false otherwise.
     */
    private static boolean isOperand(char c) {
        // TODO: Implement isOperand
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
        //throw new UnsupportedOperationException("Not implemented");
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
        // TODO: Implement toPostfix
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
            Map<Character, Integer> operadores = new HashMap<>();
            operadores.put('*', 4);
            operadores.put('+', 3);
            operadores.put('.', 2);
            operadores.put('|', 1);
            operadores.put('(', 0); // revisar si es necesario estos dos casos.
            operadores.put(')', 0);

            //obtener la concatenacion explicita.
            String regex=insertConcatenationOperator(infixRegex);

            //variables auxiliares
            String salida="";
            Deque<Character> pila = new ArrayDeque<>();


            //por cada elemento en la regex
            char [] regex_arr=regex.toCharArray();
            for (char caracter : regex_arr) {
                if (isOperand(caracter)){
                    salida=salida+caracter;

                }else if (caracter=='(') {
                    pila.push(caracter);
                    
                }else if(caracter==')'){
                    while (pila.peek()!='(') {
                        salida=salida+pila.pop();
                    }
                    pila.pop(); //para eliminar el '('

                }else if(!isOperand(caracter)){
                    //valor del caracter operador en el diccionario
                    int valor=operadores.get(caracter);
                    while (!pila.isEmpty() && operadores.get(pila.peek())>=valor){
                        salida=salida+pila.pop();
                    }
                    pila.push(caracter); //agregamos el nuevo operador.
                }   
            }
            //concatenamos todo lo que reste de la pila de operadores.
            while (!pila.isEmpty()) {
                salida=salida+pila.pop();
            }

            return salida;

        //throw new UnsupportedOperationException("Not implemented");
    }

    public static void main(String[] args) {
        System.out.println("Pruebas locales para los metodos de esta clase.");
        //System.out.println("prueba para imprimir los caracteres");
        // insertConcatenationOperator("abcd");
        // insertConcatenationOperator("(abcd)");
        // insertConcatenationOperator("(ab)cd");
        // insertConcatenationOperator("(ab)*cd");
        // insertConcatenationOperator("(a.b)*cd");
        // insertConcatenationOperator("(a.b)*c+d|c");

        System.out.println("Prueba para la notacion posfija");
        System.out.println("resultado: "+toPostfix("(a|b)*(c)+"));
        System.out.println("resultado: "+toPostfix("(a|b)*(c)"));
        System.out.println("resultado: "+toPostfix("abc*(a+b)"));
        System.out.println("resultado: "+toPostfix("(a)(a)"));
        System.out.println("resultado: "+toPostfix("((a|b|c)*d+)|e"));
        System.out.println("resultado: "+toPostfix("(a|b)*abb(a|b)*"));
        

    }
}
