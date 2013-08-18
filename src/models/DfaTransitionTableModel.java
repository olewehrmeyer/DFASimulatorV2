/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Fabian
 */
public class DfaTransitionTableModel extends AbstractTableModel {

    private Dfa dfa = null;
    private ArrayList<String> alphabet = null;

    public void setDfa(Dfa dfa) {
        this.dfa = dfa;
    }

    public void setAlphabet(ArrayList<String> alphabet) {
        this.alphabet = alphabet;
    }



    @Override
    public int getRowCount() {
        if (dfa != null)
            return dfa.getStates().size(); else
                return 0;
    }

    @Override
    public int getColumnCount() {
        if (alphabet != null)
            return alphabet.size()+1;
                return 1;
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        if (arg1 == 0)
            return (dfa.getStates().get(arg0).getState_Properties().getName()); else
                return getEntry(arg0, arg1-1);
    }


    private String getEntry(int row, int col)
    {
        String s = "";

        State state = dfa.getStates().get(row);
        State toState = state.getTargetState(alphabet.get(col));
        if (toState != null)
        s = toState.getState_Properties().getName();
        return s;
    }
}
