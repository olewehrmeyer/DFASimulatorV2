

package models;

/**
 * Adds functionality to states which is needed for square automaton computation
 * @author Kai
 */
public class SquareState extends State{
    /** State 1 */
    private State state1;
    /** State 1 */
    private State state2;

    public State getState1() {
        return state1;
    }

    public void setState1(State state1) throws IllegalArgumentException{
        if(state1 != null)
            this.state1 = state1;
        else
            throw new IllegalArgumentException();
    }

    public State getState2() {
        return state2;
    }

    public void setState2(State state2) throws IllegalArgumentException{
        if(state2 != null)
            this.state2 = state2;
        else
            throw new IllegalArgumentException();
    }

    public SquareState(String name, int id) throws IllegalArgumentException {
        super(name, id);
        state1 = null;
        state2 = null;
    }
    
}
