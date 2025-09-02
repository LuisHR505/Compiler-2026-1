package com.compiler.lexer.regex;

import java.util.Stack;
import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;
import com.compiler.lexer.nfa.Transition;

/**
 * RegexParser
 * -----------
 * This class provides functionality to convert infix regular expressions into nondeterministic finite automata (NFA)
 * using Thompson's construction algorithm. It supports standard regex operators: concatenation (·), union (|),
 * Kleene star (*), optional (?), and plus (+). The conversion process uses the Shunting Yard algorithm to transform
 * infix regex into postfix notation, then builds the corresponding NFA.
 *
 * Features:
 * - Parses infix regular expressions and converts them to NFA.
 * - Supports regex operators: concatenation, union, Kleene star, optional, plus.
 * - Implements Thompson's construction rules for NFA generation.
 *
 * Example usage:
 * <pre>
 *     RegexParser parser = new RegexParser();
 *     NFA nfa = parser.parse("a(b|c)*");
 * </pre>
 */
/**
 * Parses regular expressions and constructs NFAs using Thompson's construction.
 */
public class RegexParser {
    /**
     * Default constructor for RegexParser.
     */
        public RegexParser() {
            // TODO: Implement constructor if needed
        }

    /**
     * Converts an infix regular expression to an NFA.
     *
     * @param infixRegex The regular expression in infix notation.
     * @return The constructed NFA.
     */
    public NFA parse(String infixRegex) {
    // Pseudocode: Convert infix to postfix, then build NFA from postfix
        String postRegex = ShuntingYard.toPostfix(infixRegex);
        
        NFA automata= buildNfaFromPostfix(postRegex);
        return automata;
    }

    /**
     * Builds an NFA from a postfix regular expression.
     *
     * @param postfixRegex The regular expression in postfix notation.
     * @return The constructed NFA.
     */
    private NFA buildNfaFromPostfix(String postfixRegex) {
    // // Pseudocode: For each char in postfix, handle operators and operands using a stack

        //Auxiliar stack
        Stack<NFA> pila = new Stack<>();
    
        for(int i=0;i<postfixRegex.length();i++){
            char caracter= postfixRegex.charAt(i);
            switch (caracter) {
                case '*':
                    handleKleeneStar(pila);
                    break;

                case '+':
                    handlePlus(pila);
                    break;

                case '?':
                    handleOptional(pila);
                    break;

                case '.':
                    handleConcatenation(pila);
                    break;

                case '|':
                    handleUnion(pila);
                    break;

                default:
                    NFA automata=createNfaForCharacter(caracter);
                    pila.push(automata);
                    break;
            }
        }
        return pila.pop();
        }

    /**
     * Handles the '?' operator (zero or one occurrence).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or one occurrence.
     * @param stack The NFA stack.
     */
    private void handleOptional(Stack<NFA> stack) {
    // Pseudocode: Pop NFA, create new start/end, add epsilon transitions for zero/one occurrence
         //This operator uses 1 NFA.
         NFA automata = stack.pop();

         //I need to create a new initial state and a new final state.
         State e_inicial = new State();
         State e_final = new State();

         //Creating a new NFA.
         NFA new_automata=new NFA(e_inicial, e_final);
          
         //Assign false to the isFinal flag.
         automata.getEndState().isFinal=false;

         //New transitions to the new initial state.
         Transition trans_inicial1 = new Transition(null, automata.getStartState());
         Transition trans_inicial2 = new Transition(null, e_final);

         //Transitions to the older final state.
         Transition trans_final1 = new Transition(null, e_final);

         //Adding transitions to each state.
         e_inicial.transitions.add(trans_inicial1);
         e_inicial.transitions.add(trans_inicial2);
         automata.getEndState().transitions.add(trans_final1);

         //Adding the new NFA to the stack.
         stack.push(new_automata);

    }

    /**
     * Handles the '+' operator (one or more occurrences).
     * Pops an NFA from the stack and creates a new NFA that accepts one or more occurrences.
     * @param stack The NFA stack.
     */
    private void handlePlus(Stack<NFA> stack) {
    // Pseudocode: Pop NFA, create new start/end, add transitions for one or more occurrence
        
        //This operator uses 1 NFA.
        NFA automata = stack.pop();

        //I need to create 2 new states, one is an initial state and the other one is a final state.
        State e_inicial = new State();
        State e_final = new State();

        //Create the new NFA.
        NFA new_automata=new NFA(e_inicial, e_final);

        //I need to create a new transition to new initial state. 
        Transition trans_inicial1 = new Transition(null, automata.getStartState());

        //I need to create new transitions to the older final state.
        Transition trans_final1 = new Transition(null, e_final);
        Transition trans_final2 = new Transition(null, automata.getStartState());

        //Add the new transitions to states.
        e_inicial.transitions.add(trans_inicial1);
        automata.getEndState().transitions.add(trans_final1);
        automata.getEndState().transitions.add(trans_final2);

        //Assign false to the isFinal flag.
        automata.getEndState().isFinal=false;

        //agregar el nuevo automata a la pila.
        stack.push(new_automata); 
    }
    
    /**
     * Creates an NFA for a single character.
     * @param c The character to create an NFA for.
     * @return The constructed NFA.
     */
    private NFA createNfaForCharacter(char c) {
    // Pseudocode: Create start/end state, add transition for character

        //I need to create a new start and final state.
        State e_initial = new State();
        State e_final = new State();
        e_final.isFinal=true;
        
        //create the new transition.
        Transition trans = new Transition(c, e_final);
        
        //I need to add the new transition.
        //e_initial.transitions.add(trans); borrar esto despues.
        e_initial.transitions.add(trans);

        //finally we create the new NFA and return.
        NFA automata_character = new NFA(e_initial, e_final);
        
        return automata_character;

    }

    /**
     * Handles the concatenation operator (·).
     * Pops two NFAs from the stack and connects them in sequence.
     * @param stack The NFA stack.
     */
    private void handleConcatenation(Stack<NFA> stack) {
    // Pseudocode: Pop two NFAs, connect end of first to start of second
        //This operator needs 2 NFA.
        NFA right = stack.pop();
        NFA left = stack.pop();

        //I create a new transition epsilon to initial b NFA.
        Transition trans_epsilon = new Transition(null, right.getStartState());

        //Add the new transition to left NFA end.
        left.getEndState().transitions.add(trans_epsilon);

        //Assign false to the older end state of a NFA.
        left.getEndState().isFinal=false;

        //Create the new NFA.
        NFA automata = new NFA(left.getStartState(),right.getEndState());

        stack.push(automata);

    }

    /**
     * Handles the Kleene star operator (*).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or more repetitions.
     * @param stack The NFA stack.
     */
    private void handleKleeneStar(Stack<NFA> stack) {
    // Pseudocode: Pop NFA, create new start/end, add transitions for zero or more repetitions

        //This operator uses 1 NFA.
        NFA a = stack.pop();

        //I need to create a new initial state and a new final state.
        State e_inicial = new State();
        State e_final = new State();

        //Creating a new NFA.
        NFA automata = new NFA(e_inicial, e_final);

        //Assign false to isFinal flag.
        a.getEndState().isFinal=false;

        //Creating new transitions to initial a and new final state.
        Transition trans_epsilon_inicial_a= new Transition(null, a.getStartState());
        Transition trans_epsilon_final_nuevo= new Transition(null, e_final);

        //Adding new transitions to the new initial state.
        e_inicial.transitions.add(trans_epsilon_inicial_a);
        e_inicial.transitions.add(trans_epsilon_final_nuevo);

        //Adding new transitions to the older final state.
        a.getEndState().transitions.add(trans_epsilon_inicial_a);
        a.getEndState().transitions.add(trans_epsilon_final_nuevo);

        //push the new NFA to the stack.
        stack.push(automata);
    }

    /**
     * Checks if a character is an operand (not an operator).
     * @param c The character to check.
     * @return True if the character is an operand, false if it is an operator.
     */
    private boolean isOperand(char c) {
    // Pseudocode: Return true if c is not an operator
        
        char[] operadores = {'|', '*', '?', '+', '(', ')', '.'};
        return !ShuntingYard.contains(operadores, c);
    }

    /**
     * Handles the union operator (|).
     * Pops two NFAs from the stack and creates a new NFA that accepts either.
     * <p>
     * Pseudocode: Pop two NFAs, create new start/end, add epsilon transitions for union
     * </p>
     * @param stack The NFA stack.
     */
    private void handleUnion(Stack<NFA> stack) {
        //This operator uses 2 NFA.
        NFA a = stack.pop();
        NFA b = stack.pop();

        //I need to create a new initial and final state.
        State s_initial = new State();
        State s_final = new State();

        //I need to create a new automata.
        NFA new_Automata=new NFA(s_initial, s_final);

        //I need to create 2 new transitions epsilon from e_initial to initial_a and initial_b
        Transition s_inital_a= new Transition(null, a.getStartState());
        Transition s_inital_b= new Transition(null, b.getStartState());

        //I need to create a new transition from final_b and final_a to new final state e_final.
        Transition a_b_final = new Transition(null, s_final);

        //I need to delete the final flag in the NFA a and b.
        a.getEndState().isFinal=false;
        b.getEndState().isFinal=false;

        //I need to add the new transitions to the states.
        new_Automata.getStartState().transitions.add(s_inital_a);
        new_Automata.getStartState().transitions.add(s_inital_b);

        a.getEndState().transitions.add(a_b_final);
        b.getEndState().transitions.add(a_b_final);

        //add the new NFA to the stack.
        stack.push(new_Automata);

    }

}