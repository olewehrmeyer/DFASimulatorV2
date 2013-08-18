/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dfasimulator;

import controller.Simulator;
import gui.DFAMainWin;
import javax.swing.UIManager;

/**
 * The main function where the main window is created
 * @author Fabian
 */
public class Main {


    public static void main(String[] args) {
        //-- get system look and feel --
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }


       Simulator simulator = new Simulator();
       //-- show the mainwindow --

       DFAMainWin mainwin = new DFAMainWin();
       mainwin.setDfaSim(simulator);
       mainwin.setVisible(true);
    }

}