package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A state for the DFA.
 * @author Kai
 */


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.9670F4B5-80DA-DDDC-EF00-A0A4D206ADAD]
// </editor-fold> 
public class State implements Serializable{
    private static final long serialVersionUID = -5590868376506217927L;

    /** Is it final/accepting? */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.4B631DDD-D7DD-EBBD-0264-5BC4D4D8A198]
    // </editor-fold> 
    private boolean isFinalState;

    /** Is it start state? */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.C0EE8059-23DE-B596-6A75-675F1615D3A7]
    // </editor-fold> 
    private boolean isStartState;
    /** List of outgoing transitions */
    private ArrayList<Transition> outgoingTransitions;
    /** List of incoming transitions */
    private ArrayList<Transition> incomingTransitions;
    /** State properties */
    private State_Properties prop;
    /** Number in DFS Run */
    private int dfsNum;
    /** Numerical ID */
    private int id;

    /**
     * Returns the number in DFS run
     * @return
     */
    public int getDfsNum() {
        return dfsNum;
    }

    /**
     * Sets the DFS Number (called by DFS algorithm)
     * @param dfsNum
     */
    public void setDfsNum(int dfsNum) {
        this.dfsNum = dfsNum;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.440B62F6-A5F4-8219-24A6-DD573FD0A0A6]
    // </editor-fold>
    /**
     * Creates a new state with an empty set of transitions.
     * @param name Name of the state.
     * @throws IllegalArgumentException Name of the state must not be null.
     */
    State (String name, int id) throws IllegalArgumentException{
        outgoingTransitions = new ArrayList<Transition>();
        incomingTransitions = new ArrayList<Transition>();
        prop = new State_Properties();
        dfsNum = 0;
        this.id = id;
        if (name == null)
            throw new IllegalArgumentException();
        //name != null
        prop.setName(name);
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.7EE5376A-1D36-7D72-468D-57F087DA0423]
    // </editor-fold>
    /**
     * Checks whether the state is accepting/final.
     * @return true, iff the state is accepting/final.
     */
    public boolean getIsFinalState () {
        return isFinalState;
    }

    /**
     * Returns the state's numerical ID.
     * @return
     */
    public int getId() {
        return id;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.4ECCB74E-5CE0-A317-B258-9B00928E446B]
    // </editor-fold>
    /**
     * Sets whether the state is accepting/final.
     * @param val true/false.
     */
    public void setIsFinalState (boolean val) {
        this.isFinalState = val;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.3ABC89EF-7B6D-E88A-4230-FEC75D030A8D]
    // </editor-fold>
    /**
     * Checks whether the state is the start state.
     * @return true, iff the state is the start state.
     */
    public boolean getIsStartState () {
        return isStartState;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.BB239EBA-CB09-0909-2D77-43099BE24A94]
    // </editor-fold>
    /**
     * Sets whether the state is the start state.
     * @param val true/false.
     */
    void setIsStartState (boolean val) {
        this.isStartState = val;
    }

    /**
     * Checks if there is already a Transition with this state as start state and target
     * state t.getToState(). If there is such a transition, this existing Transition will be
     * returned. Otherwise null will be returned.
     * @param t The Transition to be checked.
     * @return Transition, if existing. Otherwise null.
     */
    public Transition getExistingOutgoingTransition(Transition t) {
        Transition existing = null;
        State toState = t.getToState();
        for(int i=0; i<outgoingTransitions.size() && existing == null; i++) {
            State from = outgoingTransitions.get(i).getFromState();
            State to = outgoingTransitions.get(i).getToState();
            if(from.equals(this) && to.equals(toState)) {
                existing = outgoingTransitions.get(i);
            }
        }
        return existing;
    }

    /**
     * Merges Transitions t1 and t2 so that the labels of t2 get added to the labels of t1.
     * @param t1 Transition t1.
     * @param t2 Transition t2-
     * @return Transition t1 expanded by the labels of t2.
     */
    private Transition mergeOutgoingTransitions(Transition t1, Transition t2) throws NoSuchTransitionException, Exception {
        for(String c:t2.getInput()) {
                addLabelToOutgoingTransition(t1, c);
            }
        return t1;
    }
    /**
     * Adds transition t to the state.
     * @param t The transition to be added.
     * @param consistency Determines weather consistency should be preserved. If set to false,
     * there will not be an exception thrown in case of for example two different transitions with
     * the same label.
     * @return The added transition.
     * @throws Exception Throws an Exception if there is already a transition between
     * the same states or if there is already a transition with the same label.
     */
    public Transition addOutgoingTransition(Transition t, boolean consistency) throws Exception{
        //check if there is not yet another transition with the same target state
        Transition existing = getExistingOutgoingTransition(t);

        if(existing != null) {
            //merge
            return mergeOutgoingTransitions(existing, t);
        }
        //check if there is not yet another transition with the same label
        if(!outgoingTransitions.contains(t)) {
            if(t.getInput().size() == 0 || !consistency) {
                outgoingTransitions.add(t);
                t.getToState().addIncomingTransition(t);
            } else {
                ArrayList<String> input = t.getInput();
                t.setInput(new ArrayList<String>());
                outgoingTransitions.add(t);
                t.getToState().addIncomingTransition(t);
                for(String s:input) {
                    try{
                        addLabelToOutgoingTransition(t, s);
                    } catch(Exception ex) {
                        outgoingTransitions.remove(t);
                        throw ex;
                    }
                }
            }
            return t;
        } else {
            String from = t.getFromState().getState_Properties().getName();
            String to = t.getToState().getState_Properties().getName();
            throw new Exception("Trying to add transition from "+from+" to "+to+" but there is already a transition!");
        }
    }

    /**
     * Adds transition t to the list of incoming transitions of this state.
     * @param t The incoming transition
     * @return The added incoming transition.(null iff t is null)
     */
    public Transition addIncomingTransition(Transition t) {
        if(t != null && !incomingTransitions.contains(t)) {
            incomingTransitions.add(t);
            return t;
        }
        return null;
    }

    /**
     *  Adds a label to a transition.
     * @param t The transition to be labled.
     * @param label The label to be added.
     * @return The modified transition.
     * @throws Exception There must not be a second transition with the same label.
     * @throws NoSuchTransitionException The given transition must be part of the DFA.
     */
    public Transition addLabelToOutgoingTransition(Transition t, String label) throws Exception, NoSuchTransitionException{
         if(outgoingTransitions.contains(t)) {
             for (Transition trans : outgoingTransitions) {
                 if(trans == t)
                     continue;
                 if (trans.getInput().contains(label)) {
                     String t_from = t.getFromState().getState_Properties().getName();
                     String t_to = t.getToState().getState_Properties().getName();
                     String from = trans.getFromState().getState_Properties().getName();
                     String to = trans.getToState().getState_Properties().getName();
                     throw new Exception("Trying to label transition from "+t_from+" to "+t_to+" with '"+label+"' but transition from " + from + " to " + to + " is already labled with '" + label + "'.");
                 }
             }
             t.addToInput(label);
             return t;
         } else {
             throw new NoSuchTransitionException();
         }
    }

    /**
     * Sets the labels for transition t. If there are already labels set, these labels will be replaced by the new labels.
     * @param t The transition.
     * @param input ArrayList of the new labels.
     * @return Transition t.
     * @throws NoSuchTransitionException Transition t is not part of the DFA.
     * @throws Exception There is already another transition with the same label.
     */
    public Transition setOutgoingTransitionInput(Transition t, ArrayList<String> input) throws NoSuchTransitionException, Exception{
        if(outgoingTransitions.contains(t)) {
            t.setInput(null);
            for(String label:input) {
                addLabelToOutgoingTransition(t, label);
            }
            return t;
        } else {
            Transition existing = getExistingOutgoingTransition(t);
            if(existing != null) {
                //merge
                for(String c:input)
                    addLabelToOutgoingTransition(existing, c);
                return existing;
            } else {
                throw new NoSuchTransitionException();
            }
        }
    }

    /**
     * Removes a transition from the DFA.
     * @param t Transition to be removed.
     */
    public void removeOutgoingTransition(Transition t) {
        t.getToState().removeIncomingTransition(t);
        outgoingTransitions.remove(t);
    }

    /**
     * Removes all transitions from this state, that is after running this method there
     * are no transitions to other states.
     */
    public void removeAllOutgoingTransitions() {
        for(Transition t:outgoingTransitions) {
            t.getToState().removeIncomingTransition(t);
        }
        outgoingTransitions = new ArrayList<Transition>();
    }

    /**
     * Removes t as an incoming transition for this state.
     * @param t Incoming transition to remove.
     */
    public void removeIncomingTransition(Transition t) {
        if(t != null && incomingTransitions.contains(t)) {
            incomingTransitions.remove(t);
        }
    }

    /**
     * Calculates the state that the DFA will change to when it reads 'input'
     * @param input The input read.
     * @return The target state. (null if input is null)
     */
    public State getTargetState(String input) {
        if(input != null) {
            for(Transition t:outgoingTransitions) {
                if(t.getInput().contains(input))
                    return t.getToState();
            }
        }
        return null;
    }

    /**
     * Returns the state's properties.
     * @return The State_Properties object.
     */
    public State_Properties getState_Properties() {
        return prop;
    }

    /**
     * Returns the transition to a given state.
     * @param toState Target state of the transition.
     * @return Transition to toState.
     * @throws NoSuchTransitionException Throws an exception if there is no transition
     * to toState.
     */
    public Transition getOutgoingTransition (State toState) throws NoSuchTransitionException {
        Transition trans = null;
        for(Transition t:outgoingTransitions) {
            if (t.getToState() == toState) {
                trans = t;
                break;
            }
        }
        if(trans == null)
            throw new NoSuchTransitionException();
        else
            return trans;
    }

    /**
     * Returns the set of outgoing transitions
     * @return Set of transitions as ArrayList
     */
    public ArrayList<Transition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    /**
     * Returns the set of incoming transitions
     * @return Set of transitions as ArrayList
     */
    public ArrayList<Transition> getIncomingTransitions() {
        return incomingTransitions;
    }

}
