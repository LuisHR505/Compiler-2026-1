package com.compiler.lexer.nfa;

import java.util.List;

/**
 * Represents a Non-deterministic Finite Automaton (NFA) with a start and end state.
 * <p>
 * An NFA is used in lexical analysis to model regular expressions and pattern matching.
 * This class encapsulates the start and end states of the automaton.
 */

public class NFA {
    /**
     * The initial (start) state of the NFA.
     */
    public final State startState;

    /**
     * The final (accepting) state of the NFA.
     */
    public final State endState;
    
    /*
     * The set of states of the NFA.
     */
    public List<State> setState;

    /**
     * Constructs a new NFA with the given start and end states.
     * @param start The initial state.
     * @param end The final (accepting) state.
     */
    public NFA(State start, State end) {
        this.startState=start;
        this.endState=end;
    }

    /**
     * Returns the initial (start) state of the NFA.
     * @return the start state
     */
    public State getStartState() {
        return this.startState;
    }

    /**
     * Returns the initial (start) state of the NFA.
     * @return the start state
     */
    public State getEndState() {
        return this.endState;
    }


    /*
     * Metodo concatenacion.
     * Regresa el automata resultante de concatenar dos automatas,
     * nota se concatenaran en el orden de los parametros.
     * NFA(A),NFA(B) -> NFA(AB)
     * 
     * Precondiciones: dos automatas NFA en el orden en el que se 
     * quieren concatenar.
     * 
     * Postcondiciones: Un NFA resultante de la concatenacion de dos 
     * Automatas.
     */
    public NFA concatenar(NFA izq, NFA der){

        /*
         * Pasos para realizar el automata.
         * 1.- Creo el nuevo automata
         *      1.1.- Defino como startState al estado inicial de izq.
         *      1.2.- Defino como endState al estado final de der.
         * 2.- Realizo cambios a las transiciones de los automatas.
         *      2.1 el final de izq le agrego una transicion null al inicio de der. (transicion epsilon)
         *      2.2 borrar la bandera que indica que es estado de aceptacion del estado final del automata izq.
         * 3.- Regreso el nuevo automata.
         */

        //creo el nuevo automata.
        NFA automata = new NFA(izq.getStartState(),der.getEndState());

        //Cambios a transiciones.

        //creo la nueva transicion
        Transition trans_epsilon = new Transition(null, der.getStartState()); //transicion epsilon.

        //borrar la bandera del estado final del izq.
        izq.getEndState().isFinal=false;

        //agregamos la nueva transicion al estado.
        izq.getEndState().transitions.add(trans_epsilon);

        return automata;
    }

    /**
     * Metodo para realizar la estrella de kleene.
     * 
     * Precondiciones: Un automata NFA(a).
     * 
     * Postcondiciones: Un automata NFA(a)*.
     * 
     *  */
    public NFA kleene(NFA a){
        /**
         * Pasos para realizar la estrella de kleene.
         * 1.- Creo un nuevo estado inicial y un nuevo estado final.
         * 2.- Creo un nuevo automata con dichos estados.
         * 3.- Elimino la bandera de estado final del estado final del automata a.
         * 4.- Agrego las nuevas transiciones (son 2) al nuevo estado inicial.
         * 5.- Agrego dos nuevas transiciones al estado final viejo de a.
         * 6.- Asignar True a la bandera del nuevo estado final.
         */
        //creo el nuevo estado inicial y final.
        State e_inicial = new State();
        State e_final = new State();

        //creo el nuevo automata.
        NFA automata = new NFA(e_inicial, e_final);

        //elimino bandera de estado final.
        a.getEndState().isFinal=false;

        //creamos las nuevas transiciones para el estado inicial y es final.
        Transition trans_epsilon_inicial_a= new Transition(null, a.getStartState());
        Transition trans_epsilon_final_nuevo= new Transition(null, e_final);

        //agregar nuevas transiciones al estado inicial nuevo.
        e_inicial.transitions.add(trans_epsilon_inicial_a);
        e_inicial.transitions.add(trans_epsilon_final_nuevo);

        //agregar las nuevas trancisiones al estado final viejo (el del automata a).
        a.getEndState().transitions.add(trans_epsilon_inicial_a);
        a.getEndState().transitions.add(trans_epsilon_final_nuevo);

        //asignar true a la bandera del nuevo estado final.
        e_final.isFinal=true;

        return automata;
    }
    
    /*
     * Metodo que realiza el OR de dos automatas.
     * Precondiciones: Dos automatas NFA(A), NFA(B)
     * Postcondiciones: Un automata NFA con la union de los 
     * automatas NFA(A|B)
     * 
     */
    public NFA union(NFA a, NFA b){
        /**
         * Pasos a seguir para el OR
         * 1.- Crear 2 nuevos estados, uno inicial y uno final.
         * 2.- Crear un nuevo automata con dichos estados.
         * 3.- Eliminar las banderas de estado final de los dos automatas
         * 4.- agregar las transiciones epsilon de salida para el estado inicial.
         * 5.- Agregar las transiciones de entrada para el nuevo estado final.
         * 6.- Agregar true a la bandera del nuevo estado final.
         */

        //creo el nuevo estado inicial y final.
        State e_inicial = new State();
        State e_final = new State();

        //creo el nuevo automata.
        NFA automata = new NFA(e_inicial, e_final);

        //elimino bandera de estado final de los 2 automatas.
        a.getEndState().isFinal=false;
        b.getEndState().isFinal=false;

        //creamos las nuevas transiciones para el estado inicial.
        Transition trans_epsilon_inicial_a= new Transition(null, a.getStartState());
        Transition trans_epsilon_inicial_b= new Transition(null, b.getStartState());

        //agregar nuevas transiciones al estado inicial nuevo.
        e_inicial.transitions.add(trans_epsilon_inicial_a);
        e_inicial.transitions.add(trans_epsilon_inicial_b);

        //creamos la nueva transicion epsilon que va al nuevo estado final.
        Transition trans_epsilon_nuevo_final= new Transition(null, a.getStartState());
        
        //agregamos la nueva transicion al ex estado final de cada automata.
        a.getEndState().transitions.add(trans_epsilon_nuevo_final);
        b.getEndState().transitions.add(trans_epsilon_nuevo_final);

        //establecemos la bandera de final como true.
        automata.getEndState().isFinal=true;

        return automata;
    }
}