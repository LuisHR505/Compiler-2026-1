package com.compiler.lexer.nfa;

import java.util.List;
import java.util.LinkedList;

/**
 * Represents a state in a Non-deterministic Finite Automaton (NFA).
 * Each state has a unique identifier, a list of transitions to other states,
 * and a flag indicating whether it is a final (accepting) state.
 *
 * <p>
 * Fields:
 * <ul>
 *   <li>{@code id} - Unique identifier for the state.</li>
 *   <li>{@code transitions} - List of transitions from this state to others.</li>
 *   <li>{@code isFinal} - Indicates if this state is an accepting state.</li>
 * </ul>
 *
 *
 * <p>
 * The {@code nextId} static field is used to assign unique IDs to each state.
 * </p>
 */
public class State {
    private static int nextId = 0;
    /**
     * Unique identifier for this state.
     */
    public final int id;

    /**
     * List of transitions from this state to other states.
     */
    public List<Transition> transitions;

    /**
     * Indicates if this state is a final (accepting) state.
     */
    public boolean isFinal;

    /**
     * Constructs a new state with a unique identifier and no transitions.
     * The state is not final by default.
     */
    public State() {
        this.id=nextId+1;   //id del ulitmo agregado al automata +1.
        nextId=nextId+1;
        this.transitions=new LinkedList<>(); //por defecto el estado no tiene transiciones.
        this.isFinal=false; //por defecto se asume que no será final.
    }

    /**
     * Checks if this state is a final (accepting) state.
     * @return true if this state is final, false otherwise
     */
    public boolean isFinal() {
        return this.isFinal;
    }

    /**
     * Returns the states reachable from this state via epsilon transitions (symbol == null).
     * @return a list of states reachable by epsilon transitions
     */
    public List<State> getEpsilonTransitions() {
        // Pseudocode: Iterate over transitions, if symbol is null, add to result list
        //lista auxiliar
        List<State> c_epsilon = new LinkedList<>(); 
        for(Transition transicion: this.transitions){ //por cada transicion en las transiciones del estado.
            if (transicion.symbol==null){
                c_epsilon.add(transicion.toState);
            }
        }
        return c_epsilon;
    }
    

    /**
     * Returns the states reachable from this state via a transition with the given symbol.
     * @param symbol the symbol for the transition
     * @return a list of states reachable by the given symbol
     */
    public List<State> getTransitions(char symbol) {
        // Pseudocode: Iterate over transitions, if symbol matches, add to result list.
        //lista auxiliar
        List<State> res = new LinkedList<>(); 
        for(Transition transicion: this.transitions){ //por cada transicion en las transiciones del estado.
            
            if (transicion.symbol!=null && transicion.symbol.charValue()==symbol){
                res.add(transicion.toState);
            }
        }
        return res; 
    }
}