package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import models.Dfa;
import models.DfaEditor;
import models.SquareState;
import models.State;
import models.Transition;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.245EA3C4-3790-7E8C-5593-89B2D60EAD85]
// </editor-fold> 
public class Simulator {

    /** The DFA to simulate */
    private Dfa dfa;
    /** The DFA-Editor */
    private DfaEditor dfaEditor;
    /** Is simulation running? */
    private boolean isRunning;
    /** Is the program in simulation mode? */
    private boolean simulationModeActive = false;
    /** Last transition taken */
    private Transition lastTransitionTaken = null;
    /** Current highlighted state */
    private State currentHighlightedState = null;

    private boolean hasFinallyAccepted = false;
    /**
     * Creates a new simulator.
     */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.265879AE-72DD-068D-26F6-2EA61AD07845]
    // </editor-fold> 
    public Simulator () {
        dfa = new Dfa();
        dfaEditor = new DfaEditor(this);
        dfaEditor.setDfa(dfa);
        isRunning = false;

    }

    public boolean isHasFinallyAccepted() {
        return hasFinallyAccepted;
    }

    public void setHasFinallyAccepted(boolean hasFinallyAccepted) {
        this.hasFinallyAccepted = hasFinallyAccepted;
    }

    

    /**
     * Returns the DFA to simulate.
     * @return DFA.
     */
    public Dfa getDfa() {
        return dfa;
    }

    /**
     * Sets the DFA to simulate.
     * @param dfa DFA to simulate.
     */
    public void setDfa(Dfa dfa) {
        if(dfa != null) {
            this.dfa = dfa;
            this.dfaEditor.setDfa(dfa);
        }
    }

    /**
     * Returns the last transition taken.
     * @return Transition.
     */
    public Transition getLastTransitionTaken() {
        return lastTransitionTaken;
    }

    /**
     * Tells whether program is in simulation mode.
     * @return
     */
    public boolean isSimulationModeActive() {
        return simulationModeActive;
    }

    /**
     * Returns the current highlighted state.
     * @return Current highlighted state.
     */
    public State getCurrentHighlightedState() {
        return currentHighlightedState;
    }

    /**
     * Switch simulation mode on/off.
     * @param simulationModeActive
     */
    public void setSimulationModeActive(boolean simulationModeActive) {
        this.simulationModeActive = simulationModeActive;
        if (!simulationModeActive)
            stopSimulation();
        else
            resetDfa();
    }

    /**
     * Returns the DFA-Editor.
     * @return
     */
    public DfaEditor getDfaEditor() {
        return dfaEditor;
    }

    /**
     * Sets the DFA-Editor.
     * @param dfaEditor
     */
    public void setDfaEditor(DfaEditor dfaEditor) {
        this.dfaEditor = dfaEditor;
    }

    /**
     * Sets whether simulation is running.
     * @param isRunning
     */
    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }


    /**
     * Sets the simulator to simulation mode.
     * @param input Input String.
     * @throws IncompleteAutomatonException
     */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.8BEDC026-8469-BB20-3338-A1F54C9D9D44]
    // </editor-fold> 
    public void startSimulation (String input) throws IncompleteAutomatonException {
        //first check preconditions
        checkPreconditions(input);
        isRunning = true;
        lastTransitionTaken = null;
        currentHighlightedState = null;
    }

    /**
     * compare 2 alphabets represented as ArrayList<String>
     * @param inputAlphabet
     * @param transitionsAlphabet
     * @return true or false
     */
    private boolean alphabetsAreEqual(ArrayList<String> inputAlphabet, ArrayList<String> transitionsAlphabet) {
        if(inputAlphabet.size() > transitionsAlphabet.size())
            return false;
        Collections.sort(inputAlphabet);
        Collections.sort(transitionsAlphabet);
        int j = 0;
        for(int i=0; i<inputAlphabet.size(); i++) {
            boolean found = false;
            for(;j<transitionsAlphabet.size() && !found;j++) {
                found = inputAlphabet.get(i).equals(transitionsAlphabet.get(j));
            }
            if(!found)
                return false;
        }
        return true;
    }

    /**
     * simulate the DFA in one step
     */
    public void simulateAll () {
        int currentPosition = dfa.getCurrentPosition();
        String input = dfa.getInput();
        for(int i=0; i<input.length()-currentPosition && isRunning; i++)
            nextStep();
    }

    /**
     * check if the DFA accpets the given word
     * @return true or false
     */
    public boolean isAccepting() {
        int currentPosition = dfa.getCurrentPosition();
        State currentSate = dfa.getCurrentState();
        String input = dfa.getInput();
        if(currentSate == null)
            return false;
        else
        {
              return ((input.length() == 0 || currentPosition == input.length()-1) && currentSate.getIsFinalState());
        }
          
    }

/**
 * check preconditions for simulation
 * @return
 * @throws IncompleteAutomatonException
 */
    private ArrayList<String> checkPreconditions()  throws IncompleteAutomatonException {
        //check all pre-conditions
        //check for start state
        if (dfa.getStartState() == null) {
            throw new IncompleteAutomatonException("No start state defined!");
        }

        //check for completeness of the transition function
        ArrayList<String> alphabet = getAlphabetFromTransitions();

        for(State s:dfa.getStates()) {
            ArrayList<Transition> transitions = s.getOutgoingTransitions();
            for(String c:alphabet) {
                boolean found = false;
                for(Transition t:transitions) {
                    if(t.getInput().contains(c)) {
                        found = true;
                        break;
                    }
                }
                if(!found)
                    throw new IncompleteAutomatonException("Missing transition for character "+c+" in state "+s.getState_Properties().getName()+".");
            }
        }
        return alphabet;
    }

    /**
     * check preconditions for simulation
     * @param input
     * @throws IncompleteAutomatonException
     */
    private void checkPreconditions(String input) throws IncompleteAutomatonException {
        //check all pre-conditions
        //check for start state
        if(input == null)
            input = new String();
        dfa.setInput(input);
        if (dfa.getStartState() == null) {
            throw new IncompleteAutomatonException("Please define a start state first!");
        }

        //Make sure the input alphabet equals the dfa's alphabet
        ArrayList<String> alphabetFromInput = getAlphabetFromInput(input);
        ArrayList<String> alphabetFromTransitions = getAlphabetFromTransitions();
        if (!alphabetsAreEqual(alphabetFromInput, alphabetFromTransitions)) {
            String msg = "Error: The input alphabet does not match the alphabet derived from the transitions!";
            throw new IncompleteAutomatonException(msg);
        }
        
        //check for completeness of the transition function
        ArrayList<String> alphabet = getAlphabetFromInput(input);

        for(State s:dfa.getStates()) {
            ArrayList<Transition> transitions = s.getOutgoingTransitions();
            for(String c:alphabet) {
                boolean found = false;
                for(Transition t:transitions) {
                    if(t.getInput().contains(c)) {
                        found = true;
                        break;
                    }
                }
                if(!found)
                    throw new IncompleteAutomatonException("Missing transition for character "+c+" in state "+s.getState_Properties().getName()+".");

            }
        }
    }

    /**
     * get the alphabet as ArrayList<String> derived from the labels of the transition
     * @return a list of letters
     */
    public ArrayList<String> getAlphabetFromTransitions() {
        ArrayList<String> alphabet = new ArrayList<String>();
        ArrayList<State> states = dfa.getStates();

        for(State s:states) {
            for(Transition t:s.getOutgoingTransitions()) {
                for(String c:t.getInput()) {
                    if(!alphabet.contains(c))
                        alphabet.add(c);
                }
            }
        }
        return alphabet;
    }

    /**
     * get the Alphabet from Input (union all different letters)
     * @param input String
     * @return List of unique letters
     */
    private ArrayList<String> getAlphabetFromInput(String input) {
        ArrayList<String> alphabet = new ArrayList<String>();
        for(int i=0; i<input.length(); i++) {
            String c = input.substring(i, i+1);
            if(!alphabet.contains(c))
                alphabet.add(c);
        }
        return alphabet;
    }

    /**
     * add missing transitions to all states
     * @return number of labels added
     */
    public int autocompleteDFATransitions( ArrayList<String> alphabet, int selectedindex)
    {
        int counter = 0;

        for (int i = 0;i<dfa.getStates().size();i++)
        {
            HashMap<String,Boolean> checklist = new HashMap<String, Boolean>();
            State s = dfa.getStates().get(i);
            for (int j=0;j<s.getOutgoingTransitions().size();j++)
            {
                Transition t = s.getOutgoingTransitions().get(j);
                for (int k = 0; k<t.getInput().size();k++)
                {
                    checklist.put(t.getInput().get(k),new Boolean(true));
                }
            }
            ArrayList<String> transittionsToAdd = new ArrayList<String>();
            for (int l = 0; l<alphabet.size();l++)
            {
                if (checklist.get(alphabet.get(l))==null)
                {
                    transittionsToAdd.add(alphabet.get(l));
                }
            }
            if (transittionsToAdd.size() > 0)
            {
                try {
                    Transition newTrans;
                    if (selectedindex == 0)
                        newTrans = new Transition(s, s);
                    else
                        newTrans = new Transition(s, dfa.getStates().get(selectedindex-1));


                    newTrans.setInput(transittionsToAdd);
                    s.addOutgoingTransition(newTrans, false);
                    counter++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return counter;
    }


/**
 * get a textual print of the current automaton
 */
    private void print_automaton() {
        System.out.println("Starting a simulation with the following automaton:");
        for(State s:dfa.getStates()) {
            String stateMsg = "State "+s.getState_Properties().getName();
            if(s.getIsStartState() && s.getIsFinalState())
                stateMsg += " (start, final):";
            else
                if(s.getIsStartState())
                    stateMsg += " (start):";
                else
                    if(s.getIsFinalState())
                        stateMsg += " (final):";
                    else
                        stateMsg += ":";
            System.out.println(stateMsg);
            ArrayList<Transition> transitions = s.getOutgoingTransitions();
            for(Transition t:transitions) {
                System.out.print("\tTransition from "+t.getFromState().getState_Properties().getName()+" to "+t.getToState().getState_Properties().getName()+" labled with: ");
                for(String c:t.getInput()) {
                    System.out.print(c);
                }
                System.out.print("\n");
            }
        }
    }

/**
 * simulate the next step
 */
    public void nextStep () {

        String input = dfa.getInput();
        if(input.length() == 0)
            isRunning = false;
        if(isRunning) {
            int currentPosition = dfa.getCurrentPosition();
            currentHighlightedState = dfa.getCurrentState();
            int nextposition = currentPosition;
            if(currentPosition < input.length()) {
                State currentState = dfa.getCurrentState();
                String read = input.substring(currentPosition, currentPosition+1);
                ArrayList<Transition> transitions = currentState.getOutgoingTransitions();
                for(int i=0; i<transitions.size() && nextposition == currentPosition; i++) {
                    Transition t = transitions.get(i);
                    if(t.getInput().contains(read)) {
                        //take transition and set new current state
                        lastTransitionTaken = t;
                        
                        nextposition = currentPosition+1;
                        dfa.setCurrentState(t.getToState());
                        currentHighlightedState = t.getToState();
                        if(nextposition < input.length()) {
                            dfa.setCurrentPosition(nextposition);

                        } else {
                            //all input has been read
                            isRunning = false;
                        }
                    }
                }
            }
        }
    }

    /**
     * Tells whether simulation is running.
     * @return
     */
    public boolean getIsRunning() {
        return isRunning;
    }
    

    /**
     * resets simulation variables
     */
    public void stopSimulation()
    {
        resetDfa();
        isRunning = false;

    }

    /**
     * reset the dfa to normal start parameters
     */
    public void resetDfa() {
        if(this.dfa.getInput().length() > 0)
            dfa.setCurrentPosition(0);
        dfa.setCurrentState(dfa.getStartState());
        dfa.setInput(new String());
        lastTransitionTaken = null;
        currentHighlightedState = dfa.getStartState();
        hasFinallyAccepted = false;
    }

    /**
     * Minimizes a DFA.
     * @param inputDfa DFA to minimize.
     * @return Minimized DFA.
     */
    public Dfa minimizeDfa(Dfa inputDfa) throws IncompleteAutomatonException, Exception {
        int dfanum = 1;
        checkPreconditions();
        removeAllIsolatedStates(inputDfa);
        Dfa squared = calcSquareAutomaton(inputDfa);
        reverseTransitions(squared);
        for(State s:squared.getStates()) {
            State s1 = ((SquareState)s).getState1();
            State s2 = ((SquareState)s).getState2();
            if((s1.getIsFinalState() ^ s2.getIsFinalState()) && s.getDfsNum() == 0) {
                dfanum = dfaDfs(s, dfanum);
            }
        }
        LinkedList<SquareState> eqivalentStates = new LinkedList<SquareState>();
        ArrayList<State> sqStates = squared.getStates();
        for(int i=0; i<squared.getStates().size(); i++) {
            SquareState sqState = (SquareState)sqStates.get(i);
            State s1 = sqState.getState1();
            State s2 = sqState.getState2();
            if(s1 == s2)
                continue;
            if(sqState.getDfsNum() == 0) {
                boolean found = false;
                ListIterator iterator = eqivalentStates.listIterator();
                for(int j=0; j<eqivalentStates.size() && !found; j++) {
                    SquareState x = (SquareState)iterator.next();
                    found = x.getState1() == s2 && x.getState2() == s1;
                }
                if(!found) {
                    eqivalentStates.add(sqState);
                }
            }
        }
        for (SquareState sqState : eqivalentStates) {
            State removable = sqState.getState1();
            State keepable = sqState.getState2();
            if (removable.getIsStartState()) {
                removable = sqState.getState2();
                keepable = sqState.getState1();
            }
            if (inputDfa.getStates().contains(removable)) {
                Transition[] transArray = new Transition[removable.getIncomingTransitions().size()];
                for (int i = 0; i < transArray.length; i++) {
                    transArray[i] = removable.getIncomingTransitions().get(i);
                }
                for (int i = 0; i < transArray.length; i++) {
                    Transition t = transArray[i];
                    t.getFromState().removeOutgoingTransition(t);
                    t.getToState().removeIncomingTransition(t);
                    t.setToState(keepable);
                    t.getFromState().addOutgoingTransition(t, true);
                }
                inputDfa.removeState(removable);
            }
        }
        return inputDfa;
    }

    /**
     * Removes all isolated states from the input DFA, that is states that cannot be reached from
     * the DFA's start state.
     * @param inputDfa The input DFA.
     * @return DFA without isolated states.
     */
    private Dfa removeAllIsolatedStates(Dfa inputDfa) {
        dfaDfs(inputDfa.getStartState(), 1);
        State[] states = inputDfa.getStates().toArray(new State[inputDfa.getStates().size()]);
        for(State s:states) {
            if(s.getDfsNum() == 0) {
                inputDfa.removeState(s);
            } else {
                s.setDfsNum(0);
            }
        }
        return inputDfa;
    }

    /**
     * Calculates dfa^2, that is the automaton that works on QxQ
     * @param Dfa to square.
     * @return Squared Automaton.
     */
    private Dfa calcSquareAutomaton(Dfa inputDfa) throws IncompleteAutomatonException, Exception {
        Dfa squared = new Dfa();
        ArrayList<State> inputStates = inputDfa.getStates();
        int statenum = inputStates.size();
        HashMap<Integer, HashMap<Integer, SquareState>> stateStorage = new HashMap<Integer, HashMap<Integer, SquareState>>(statenum);
        //generate states
        for (int i = 0; i < statenum; i++) {
            State state1 = inputStates.get(i);
            String name1 = state1.getState_Properties().getName();
            boolean state1IsFinal = state1.getIsFinalState();
            boolean state1isStart = state1.getIsStartState();
            HashMap<Integer, SquareState> hashmap = new HashMap<Integer, SquareState>(statenum);
            for (int j = 0; j < statenum; j++) {
                State state2 = inputStates.get(j);
                String name2 = state2.getState_Properties().getName();
                SquareState newState = new SquareState("("+name1+","+name2+")", i*statenum+j);
                newState.setState1(state1);
                newState.setState2(state2);
                squared.addState(newState);
                hashmap.put(state2.getId(), newState);
                if(state1IsFinal && state2.getIsFinalState())
                    newState.setIsFinalState(true);
                if(state1isStart && state1 == state2)
                    squared.setStartState(newState);
            }
            stateStorage.put(state1.getId(), hashmap);
        }
        //make sure the input dfa is completely defined
        ArrayList<String> alphabet = checkPreconditions();
        ArrayList<State> states = squared.getStates();
        SquareState[] sqStateArray = new SquareState[states.size()];
        for(int i=0; i<states.size(); i++) {
            sqStateArray[i] = (SquareState)states.get(i);
        }
        for(int i=0; i<states.size(); i++) {
            SquareState sqState = (SquareState)states.get(i);
            State s1 = sqState.getState1();
            State s2 = sqState.getState2();
            for(String c:alphabet) {
                State target1 = s1.getTargetState(c);
                State target2 = s2.getTargetState(c);
                SquareState targetState = stateStorage.get(target1.getId()).get(target2.getId());
                Transition t = new Transition(sqState, targetState);
                t.addToInput(c);
                sqState.addOutgoingTransition(t, true);
            }
        }
        return squared;
    }

    /**
     * Reverses all transitions in a DFA.
     * @param dfa The input DFA.
     */
    private void reverseTransitions(Dfa dfa) throws Exception {
        ArrayList<State> states = dfa.getStates();
        LinkedList<Transition> transitions = new LinkedList<Transition>();
        for(State s:states) {
            transitions.addAll(s.getOutgoingTransitions());
            s.removeAllOutgoingTransitions();
        }
        for(Transition t:transitions) {
            State toState = t.getToState();
            State fromState = t.getFromState();
            t.setFromState(toState);
            t.setToState(fromState);
            /**
             * Turn off consistency check (we can get an invalid DFA after reversing its transitions)
             */
            toState.addOutgoingTransition(t, false);
        }
    }

    /**
     * Perform a depth first search on the input DFA.
     * @param inputDfa Input DFA.
     * @param dfsnum DFS number to start with
     * @return DFS number
     */
    private int dfaDfs(State startState, int dfsnum) {
        int num = dfsnum+1;
        if(startState != null) {
            startState.setDfsNum(dfsnum);
            ArrayList<Transition> transitions = startState.getOutgoingTransitions();
            for(int i=0; i<transitions.size(); i++) {
                State toState = transitions.get(i).getToState();
                if(toState.getDfsNum() == 0) {
                    num = dfaDfs(toState, num);
                }
            }
            return num;
        }
        return 0;
    }

}