/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Fabian
 */
public class AutoCompleteComboBoxModel implements ComboBoxModel{

    private ArrayList<String> list = null;
    private String selectedO = null;
    private int selectedIndex = 0;

    public void setList(ArrayList<String> list) {
        this.list = list;
    }




    @Override
    public void setSelectedItem(Object arg0) {
        selectedIndex = list.indexOf(arg0);
    }

    @Override
    public Object getSelectedItem() {
        return list.get(selectedIndex);
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int arg0) {
        return list.get(arg0);
    }

    @Override
    public void addListDataListener(ListDataListener arg0) {

    }

    @Override
    public void removeListDataListener(ListDataListener arg0) {

    }

}
