package gui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import controller.DFAPainter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Fabian
 */
public class PaintPanel extends JPanel {

DFAPainter dFAPainter;



    public DFAPainter getdFAPainter() {
        return dFAPainter;
    }

    public void setdFAPainter(DFAPainter dFAPainter) {
        this.dFAPainter = dFAPainter;
    }

/**
 * override the paintComponent method to have own painting methods
 * @param g canvas
 */
@Override public void paintComponent(Graphics g) {
         super.paintComponent(g);    // paints background
         if (dFAPainter != null)
             dFAPainter.updateGraphics((Graphics2D)g);

}



}
