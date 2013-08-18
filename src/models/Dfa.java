package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.CCC2B68F-8475-9ECB-2126-E50681B0C4B0]
// </editor-fold> 
public class Dfa extends Observable implements Serializable{
    private static final long serialVersionUID = -5590868576506217927L;

    /** Input string to read */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.71083E97-6397-D2D6-853D-97DBD63CFF6A]
    // </editor-fold> 
    protected String input;

    /** Current reading position in input string */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.B5A933E2-233B-A1A4-EDA6-51D88CB7AD62]
    // </editor-fold> 
    protected int currentPosition;
    /** Description for DFA */
    protected String description = "";

    /** DFA's current state during simulation */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.605BE7FE-5F6E-ADC8-1805-4DB235B9D755]
    // </editor-fold> 
    protected State currentState;
    /** Start state */
    protected State startState;
    /** List of all states */
    protected ArrayList<State> states;
    /** Total number of states */
    protected int states_added;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.EC7BEFBE-E5DE-0F26-2936-4860283E2109]
    // </editor-fold>
    /**
     * Creates a new and empty deterministic finite automaton (DFA)
     * 
     */
    public Dfa () {
        states = new ArrayList<State>();
        input = new String();
        states_added = 0;
        currentPosition = 0;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.3DB78021-72EE-B158-BB53-455BE680A212]
    // </editor-fold>
    /**
     * Adds a new state to the DFA. The name is given automatically. It
     * is "s" plus the number of states -1.
     * @return The new state.
     */
    public State addState () {
        State s = new State("s" + states_added, states_added);
        this.states.add(s);
        states_added++;
        setChanged();
        notifyObservers();
        return s;
    }

    /**
     * adds a state s to the automaton
     * @param s State to add
     * @return the state
     */
    public State addState(State s) {
        if(s != null) {
            this.states.add(s);
            states_added++;
            setChanged();
            notifyObservers();
            return s;
        }
        return null;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.DE235C0F-584F-4591-F990-DBB9F923B5E7]
    // </editor-fold>
    /**
     * Removes the state s from the DFA.
     * @param s The state to remove.
     * @throws IllegalArgumentException
     */
    public void removeState (State s) throws IllegalArgumentException{
        if (s == null)
                throw new IllegalArgumentException();
        Transition[] transitions = s.getIncomingTransitions().toArray(new Transition[s.getIncomingTransitions().size()]);
        for(Transition t:transitions)
            removeTransition(t);
        // s != null
        this.states.remove(s);
        if (s.getIsStartState())
            startState = null;
        setChanged();
        notifyObservers();
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.36246B2B-BC85-4EC0-7F8C-4EE51CC8FB99]
    // </editor-fold>
    /**
     * Creates a new Transition from state s1 to state s2 with an empty label.
     * @param s1 The start state (from).
     * @param s2 The target state (to).
     * @return The new transition.
     */
    public Transition addTransition (State s1, State s2) {
        Transition t = new Transition(s1, s2);
        try {
            s1.addOutgoingTransition(t, true);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        //check for transition in opposite direction
        boolean found = false;
        int i = 0;
        ArrayList<Transition> transitions = s2.getOutgoingTransitions();
        while(i<transitions.size() && !found) {
            Transition s2_transition = transitions.get(i);
            if(s2_transition.getToState().equals(s1)) {
                found = true;
                s2_transition.setHasBackTransition(true);
                t.setHasBackTransition(true);
            }
            i++;
        }
        setChanged();
        notifyObservers();
        return t;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.4F6EFCDF-1AEE-5853-F440-BE54E9622042]
    // </editor-fold>
    /**
     * Removes the transition t.
     * @param t The transition to remove.
     */
    public void removeTransition (Transition t) {
        t.getFromState().removeOutgoingTransition(t);
        setChanged();
        notifyObservers();
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.CA9EABF6-A357-AF6E-683E-53A7818F164B]
    // </editor-fold>
    /**
     * Gets the DFA's current reading position in the input string.
     * @return Current reading position in input string.
     */
    public int getCurrentPosition () {
        return currentPosition;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.A4D9AE94-C539-78F0-1740-7A8D5FFF3404]
    // </editor-fold>
    /**
     * Sets the current reading position in the input string.
     * @param Index in input string.
     * @throws IllegalArgumentException
     */
    public void setCurrentPosition (int val) throws IllegalArgumentException{
        if(val >= 0 && val < input.length())
            this.currentPosition = val;
        else
            throw new IllegalArgumentException("Index out of range!");
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.F3D95C55-DEF9-7C3D-4B2D-A334F3DB4EB0]
    // </editor-fold>
    /**
     * Returns the DFA's current state.
     * @return Current state.
     */
    public State getCurrentState () {
        return currentState;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.8AEB9F4E-43E6-4450-7CFB-CC87B81852BF]
    // </editor-fold>
    /**
     * Sets the DFA's current state. If the state val is unknown for the DFA
     * it simply does nothing.
     * @param State meant to be set current.
     */
    public void setCurrentState (State val) {
        if(states.contains(val))
            this.currentState = val;
    }

    /**
     * Returns the start state of the DFA.
     * @return Start state.
     */
    public State getStartState () {
        return startState;
    }

    /**
     * Returns a description for the DFA.
     * @return Description.
     */
    public String getDescription() {
        if (description == null)
            return new String(""); else
        return description;
    }

    /**
     * Sets a description for the DFA.
     * @param description Description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets state s as the start state of the DFA. A DFA must have exactly one start state.
     * If there is already a state set as start state, it gets overwritten.
     * @param s The state to be set as the start state. null deletes the current start state.
     * @throws IllegalArgumentException If the given state is not part of the DFA.
     */
    public void setStartState (State s) throws IllegalArgumentException {
        if (s != null && !state_known(s)) {
            throw new IllegalArgumentException("The state you are trying to declare as the start state is not a part of the DFA!");
        }
        if (this.startState != null) {
            this.startState.setIsStartState(false);
        }
        if (s == null) {
            startState = null;
            currentState = null;
        } else {
            this.startState = s;
            s.setIsStartState(true);
            currentState = startState;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Declares a state to be accepting/final.
     * @param s State to be set as accepting/final.
     * @throws IllegalArgumentException If the given state is not part of the DFA.
     */
    public void setFinalState (State s) throws IllegalArgumentException {
        if (!state_known(s))
            throw new IllegalArgumentException("The state you are trying to declare as the final state is not a part of the DFA!");
        //else
        s.setIsFinalState(true);
    }

    /**
     * Declares a state to be <b>not</b> accepting/final.
     * @param s State to be set as not accepting/final.
     */
    public void removeFinalState (State s) {
        if (state_known(s))
            s.setIsFinalState(false);
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.4E4D456F-822D-37C2-EDC3-AB7C014DC48F]
    // </editor-fold>
    /**
     * Returns the DFA's input string.
     * @return Input string.
     */
    public String getInput () {
        return input;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.F23FD5A5-CC69-E8F3-605D-98669A577B79]
    // </editor-fold>
    /**
     * Sets the DFA's input string.
     * @param val Input string. Can also be null (e.g. for the empty word).
     */
    public void setInput (String val) {
        this.input = val;
    }

    /**
     * Tests if the given state is part of the DFA.
     * @param s State to be tested.
     * @return true, iff s is part of the DFA.
     */
    private boolean state_known (State s) {
        for(State i:states)
            if (i == s)
                return true;
        return false;
    }

    /**
     * Returns the DFA's set of states.
     * @return States as an ArrayList
     */
    public ArrayList<State> getStates() {
        return states;
    }

    /**
     * Checks if there is a transition from s1 to s2 as well as from s2 to s1.
     * @param s1 State 1 of the transition.
     * @param s2 State 2 of the transition.
     * @return true, iff there is a transition from s1 to s2 as well as from s2 to s1.
     * @throws NoSuchTransitionException There is no transition from s1 to s2.
     */
    public boolean isBidirectionalTransition(State s1, State s2) throws NoSuchTransitionException {
        Transition t = s1.getOutgoingTransition(s2);
        return t.isHasBackTransition();
    }


    /**
     * Autoformat and arrange states for display
     * Warning: Fairly simple...
     */
    public void autoArrangeDFA()
    {
        int cx = -30;
        int cy = -20;
        int row = 1;
        int col = 1;
        int optrowsize = (int)Math.ceil(Math.sqrt(states.size()))+1;

        int spaceBetween = 100;

        for (int i=0; i<states.size();i++)
        {
            State s = states.get(i);
            s.getState_Properties().setXPos(cx+col*spaceBetween);
            s.getState_Properties().setYPos(cy+row*spaceBetween);
            col++;
            if (col % optrowsize == 0)
            {
                col = 1;
                row++;
            }
        }
    }


/**
 * get the label of a transition, comma seperated values
 * @param t Transition
 * @return commaseperated string
 */
    public String getCommaSeperatedTranstionInputs(Transition t)
    {
        if (t == null)
            return "";
        else
        {
            String c = "";
            for (int i=0;i<t.getInput().size();i++)
            {
                if (i == t.getInput().size()-1)
                {
                    c = c + t.getInput().get(i);
                } else
                {
                    c = c + t.getInput().get(i) + ",";
                }
            }
            return c;
        }

    }

    /**
     * Adds an observer (usually DfaMainWin)
     * @param o Observer
     */
    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        setChanged();
        notifyObservers();
    }

}

