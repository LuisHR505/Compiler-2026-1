package com.compiler.lexer;

import java.util.Set;

import com.compiler.lexer.regex.RegexParser;
import com.compiler.lexer.nfa.*;
import java.util.HashSet;

/**
 * NfaSimulator
 * ------------
 * This class provides functionality to simulate a Non-deterministic Finite Automaton (NFA)
 * on a given input string. It determines whether the input string is accepted by the NFA by processing
 * each character and tracking the set of possible states, including those reachable via epsilon (ε) transitions.
 *
 * Simulation steps:
 * - Initialize the set of current states with the ε-closure of the NFA's start state.
 * - For each character in the input, compute the next set of states by following transitions labeled with that character,
 *   and include all states reachable via ε-transitions from those states.
 * - After processing the input, check if any of the current states is a final (accepting) state.
 *
 * The class also provides a helper method to compute the ε-closure of a given state, which is the set of all states
 * reachable from the given state using only ε-transitions.
 */
/**
 * Simulator for running input strings on an NFA.
 */
public class NfaSimulator {
    /**
     * Default constructor for NfaSimulator.
     */
        public NfaSimulator() {
            // TODO: Implement constructor if needed
        }

    /**
     * Simulates the NFA on the given input string.
     * Starts at the NFA's start state and processes each character, following transitions and epsilon closures.
     * If any final state is reached after processing the input, the string is accepted.
     *
     * @param nfa The NFA to simulate.
     * @param input The input string to test.
     * @return True if the input is accepted by the NFA, false otherwise.
     */
    public boolean simulate(NFA nfa, String input) {
        /*
         Pseudocode:
         1. Initialize currentStates with epsilon-closure of NFA start state
         2. For each character in input:
              - For each state in currentStates:
                  - For each transition:
                      - If transition symbol matches character:
                          - Add epsilon-closure of destination state to nextStates
              - Set currentStates to nextStates
         3. After input, if any state in currentStates is final, return true
         4. Otherwise, return false
        */
        //Auxiliar set.
        Set<State> current_states = new HashSet<State>();
 
        //Adding in a set 'current states' a epsilon-closure of the initial state.
        addEpsilonClosure(nfa.getStartState(),current_states);

        //for each caracter in the input.
        for (int i=0; i<input.length();i++){
            char character = input.charAt(i); 
            Set<State> nexStates = new HashSet<State>();

            for (State estado : current_states){
                for (Transition trans : estado.transitions){

                    //if the transition matches with character.
                    if ( trans.symbol!=null && trans.symbol.charValue()==character){
                        State destino=trans.toState;
                        
                        //Getting epsilon-closure of the match character.
                        addEpsilonClosure(destino,nexStates); 
                    }
                }
            }
            current_states=nexStates;
        }

        //if some state in the set 'curren_states' is a final state then return true, in any other case
        //is false
        for (State estado : current_states){
            if (estado.isFinal) {
                return true;
            }
        }

        return false;
    }

    /**
     * Computes the epsilon-closure: all states reachable from 'start' using only epsilon (null) transitions.
     *
     * @param start The starting state.
     * @param closureSet The set to accumulate reachable states.
     */
    private void addEpsilonClosure(State start, Set<State> closureSet) {
        /*
         Pseudocode:
         If start not in closureSet:
             - Add start to closureSet
             - For each transition in start:
                 - If transition symbol is null:
                     - Recursively add epsilon-closure of destination state
        */

        //If start is already on closure Set then return.
        if( closureSet.contains(start) ){
            return;
        }
        
        //Adding start to closureSet
        closureSet.add(start);

        // For each transition in star state transitions.
        for ( Transition transition : start.transitions ) {
            if( transition.symbol == null ){
                //Here I found a epsilon transition. 
                //Recursive call
                addEpsilonClosure( transition.toState , closureSet);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Creo el automata a|b");
        
        RegexParser parser = new RegexParser();
        NFA automata=parser.parse("a|b");
        NfaSimulator simulator = new NfaSimulator();
        System.out.println("La cadena a del automata a|b ha sido: "+simulator.simulate(automata,"a"));

    }


}