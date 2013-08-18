
package controller;

import gui.PaintPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import models.Dfa;
import models.DfaEditor;
import models.EditorToolStates;
import models.HighlightTypes;
import models.NoSuchTransitionException;
import models.State;
import models.TouchButton;
import models.Transition;


/**
 *
 * @author Fabian
 */
public class DFAPainter {

    private final Color COLORSTATEHIGHLIGHT = new Color(197, 222, 255);
    private final Color COLORSTATESELECTED = new Color(0, 0, 255);
    private final Color COLORSTATECURRENT = new Color(255, 253, 100);
    private final Color COLORSTATEACCEPTED2 = new Color(29, 124, 0);
    private final Color COLORSTATEACCEPTED = new Color(68, 255, 11);
    private final Color COLORSTATENORMAL = Color.WHITE;
    private final Color COLORSTATEFONTNORMAL = Color.BLACK;
    private final Color COLORSTATEFONTSELECTED = Color.WHITE;
    private final Color COLORSTATELINESELECED = Color.WHITE;
    private final Color COLORSTATELINESNOTSELECTED = Color.BLACK;
    private final Color COLORSTATELINESSELECTED = Color.BLUE;
    private final Color COLORSTATELINESCURRENT = new Color(181,44,1);
    private final Color COLORTRANSITIONLINENORMAL = Color.BLACK;
    private final Color COLORTRANSITIONLINEHIGHLIGHTED = new Color(73, 137, 255);
    private final Color COLORTRANSITIONLINESELECTED = Color.BLUE;
    private final Color COLORTRANSITIONLINETAKEN = new Color(181,44,1);
    private final Color COLORTRANSITIONLABELHIGHLIGHTED = new Color(197, 222, 255);
    private final Color COLORTRANSITIONLABELSELECTED = Color.BLUE;
    private final Color COLORTRANSITIONLABELNORMAL = new Color(255, 255, 255, 155);
    private final Color COLORTRANSITIONFONTNORMAL = Color.BLACK;
    private final Color COLORTRANSITIONFONTSELECTED = Color.WHITE;
    private final Color COLORADDNEWELEMENT = new Color(130, 130, 130);
    private final Color COLORADDNEWELEMENT2 = new Color(90, 90, 90);
    private final int STATEDRAWSIZE = 50;
    private final int TEXTSIZE = 16;
    private final double ARCDISTANCE = 25;
    private final int VIRTUALTRANSITIONSITE = 20;

    private boolean antialiasing = true;

    private Simulator dfaSim = null;
    private DfaEditor dfaEditor = null;
    private Graphics2D graphics = null;
    private PaintPanel paintPanel = null;
    private Font transitionFont = null;

    public DFAPainter(Simulator dfaSimulator) {
        this.dfaSim = dfaSimulator;
    }

    public DfaEditor getDfaEditor() {
        return dfaEditor;
    }

    public void setDfaEditor(DfaEditor dfaEditor) {
        this.dfaEditor = dfaEditor;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public PaintPanel getPaintPanel() {
        return paintPanel;
    }

    public void setPaintPanel(PaintPanel paintPanel) {
        this.paintPanel = paintPanel;
    }

    public int getVirtualTransitionSite() {
        return VIRTUALTRANSITIONSITE;
    }
/**
 * request a complete repaint of the drawing area
 */
    public void requestRepaintAll()
    {
        if (this.paintPanel != null)
            this.paintPanel.repaint();
    }

    /**
     * set the offset values to an optimal fit
     */
    public void optimizeCropPan(int optoffset)
    {
        //-- get dimensions of image --
        int minX = 9999999;
        int minY = 9999999;
        int maxX = -9999999;
        int maxY = -9999999;

        Dfa d = getDfaEditor().getDfa();
        for (int i=0;i<d.getStates().size();i++)
        {
            State s = d.getStates().get(i);
            if (s.getState_Properties().getXPos() < minX)
            {
                 minX = s.getState_Properties().getXPos();
                 if (s.getIsStartState())
                     minX = minX-30;
            }

            if (s.getState_Properties().getYPos() < minY)
                minY = s.getState_Properties().getYPos();
            if (s.getState_Properties().getXPos() > maxX)
                maxX = s.getState_Properties().getXPos();
            if (s.getState_Properties().getYPos() > maxY)
                maxY = s.getState_Properties().getYPos();

            for (int j=0;j<s.getOutgoingTransitions().size();j++)
            {
                Transition t = s.getOutgoingTransitions().get(j);

                if (t.getClickPositionX() < minX)
                    minX = (int)t.getClickPositionX();
                if (t.getClickPositionY() < minY)
                    minY = (int)t.getClickPositionY();
                if (t.getClickPositionX() > maxX)
                    maxX = (int)t.getClickPositionX();
                if (t.getClickPositionY() > maxY)
                    maxY = (int)t.getClickPositionY();
            }
        }
        if (d.getStates().size()>0)
        {
            int safetyDistance = 35;
            int imWidth = Math.max(0,maxX-minX)+3*safetyDistance;
            int imHeight = Math.max(0,maxY-minY)+2*safetyDistance;

            dfaEditor.setOffsetX(-minX+(int)(1.5*safetyDistance)+optoffset);
            dfaEditor.setOffsetY(-minY+safetyDistance+optoffset);
        }
    }


    /**
     * Export the current DFA as a cropped picture
     * @param f Destination file
     * @return true if all OK
     */
    public boolean exportPNGFile(File f)
    {
        //-- get dimensions of image --
        int minX = 9999999;
        int minY = 9999999;
        int maxX = -9999999;
        int maxY = -9999999;

        Dfa d = getDfaEditor().getDfa();
        for (int i=0;i<d.getStates().size();i++)
        {
            State s = d.getStates().get(i);
            if (s.getState_Properties().getXPos() < minX)
            {
                 minX = s.getState_Properties().getXPos();
                 if (s.getIsStartState())
                     minX = minX-30;
            }

            if (s.getState_Properties().getYPos() < minY)
                minY = s.getState_Properties().getYPos();
            if (s.getState_Properties().getXPos() > maxX)
                maxX = s.getState_Properties().getXPos();
            if (s.getState_Properties().getYPos() > maxY)
                maxY = s.getState_Properties().getYPos();

            for (int j=0;j<s.getOutgoingTransitions().size();j++)
            {
                Transition t = s.getOutgoingTransitions().get(j);

                if (t.getClickPositionX() < minX)
                    minX = (int)t.getClickPositionX();
                if (t.getClickPositionY() < minY)
                    minY = (int)t.getClickPositionY();
                if (t.getClickPositionX() > maxX)
                    maxX = (int)t.getClickPositionX();
                if (t.getClickPositionY() > maxY)
                    maxY = (int)t.getClickPositionY();
            }
        }
            if (d.getStates().size()>0)
            {
                int safetyDistance = 35;
                int imWidth = (int)(Math.max(0,maxX-minX)*getDfaEditor().getZoomfactor()+3*safetyDistance*getDfaEditor().getZoomfactor());
                int imHeight = (int)(Math.max(0,maxY-minY)*getDfaEditor().getZoomfactor()+2*safetyDistance*getDfaEditor().getZoomfactor());

                dfaEditor.setOffsetX((int)((-minX+1.5*safetyDistance)*getDfaEditor().getZoomfactor()));
                dfaEditor.setOffsetY((int)((-minY+ safetyDistance)*getDfaEditor().getZoomfactor()));


                BufferedImage bi = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D gimg = (Graphics2D) bi.getGraphics();
                gimg.setColor(Color.WHITE);
                gimg.fillRect(0, 0, imWidth, imHeight);
                updateGraphics(gimg);

            try {
                ImageIO.write(bi, "png", f);
                JOptionPane.showMessageDialog(null, "File saved successfully!","Image export",JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
              JOptionPane.showMessageDialog(null, "Error while saving file!","Image export",JOptionPane.ERROR_MESSAGE);
            }

                return true;
            } else
            {
                JOptionPane.showMessageDialog(null, "No elements to export!","Image export",JOptionPane.ERROR_MESSAGE);

                return false;
            }



    }


/**
 * Paints DFA main procedure
 * @param g Destination Graphics2D object
 */
    public void updateGraphics(Graphics2D g)
    {
        if (g != null)
        {
            this.graphics = g;
            if (antialiasing)
            {
                //-- nice rendering --
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            int width = 1;
            if (getDfaEditor().getZoomfactor() < 1.1)
                width = 1; else
                    if (getDfaEditor().getZoomfactor() < 2)
                        width = 2; else
                            width = (int)getDfaEditor().getZoomfactor();

            g.setStroke(new BasicStroke(width));

            paintStates();
            paintTransitions();
            paintUserActions();

        }
    }

    /**
     * paint the states of the DFA
     */
    private void paintStates()
    {
        Graphics2D g = this.graphics;
        Dfa dfa = dfaEditor.getDfa();

        //-- set fonts --
         Font nameFont = new Font("Arial",Font.PLAIN, (int)(TEXTSIZE*dfaEditor.getZoomfactor()));
         g.setFont(nameFont);

        for (int i=0; i < dfa.getStates().size(); i++)
        {
            State s = dfa.getStates().get(i);
            int x = s.getState_Properties().getXPos();
            int y = s.getState_Properties().getYPos();

            Color backgroundColor = COLORSTATENORMAL;
            Color lineColor = COLORSTATELINESNOTSELECTED;
            Color fontColor = COLORSTATEFONTNORMAL;
            Color startLineColor;


            if (s.getState_Properties().getHighlightIndex() == HighlightTypes.MouseOver)
            {
                 backgroundColor = COLORSTATEHIGHLIGHT ;
            }

            if (s.getState_Properties().isSelected())
            {
                 backgroundColor = COLORSTATESELECTED;
                 lineColor = COLORSTATELINESSELECTED;
                 fontColor = COLORSTATEFONTSELECTED;
            }

            startLineColor = lineColor;

            if(dfaSim.isSimulationModeActive() && dfaSim.getCurrentHighlightedState() == s) {
                lineColor = COLORSTATELINESCURRENT;
                backgroundColor = COLORSTATECURRENT;
                if (s.getIsStartState() && dfa.getCurrentPosition() == 0)
                    startLineColor = COLORSTATELINESCURRENT;
                if (dfaSim.isHasFinallyAccepted())
                {
                    backgroundColor = COLORSTATEACCEPTED;
                    lineColor = COLORSTATEACCEPTED2;
                }

            }

            int centerX = (int)(dfaEditor.getOffsetX() + dfaEditor.getZoomfactor()*x);
            int centerY = (int)(dfaEditor.getOffsetY() + dfaEditor.getZoomfactor()*y);
            int radius = (int)(dfaEditor.getZoomfactor()*STATEDRAWSIZE/2);

	   if (s.getIsFinalState())
           {
                double z = dfaEditor.getZoomfactor();
                int additionalradius = (int) (dfaEditor.getZoomfactor()*4);
                g.setColor(backgroundColor);
                g.fillOval((int)(centerX-radius), (int)(centerY-radius),(int)(z*STATEDRAWSIZE), (int)(z*STATEDRAWSIZE));
                g.setColor(lineColor);
                g.drawOval((int)(centerX-radius),(int)( centerY-radius),(int)(z*STATEDRAWSIZE),(int)( z*STATEDRAWSIZE));
                if (s.getState_Properties().isSelected())
                    g.setColor(COLORSTATELINESELECED);
                g.drawOval((int)(centerX-(radius-additionalradius)),(int)(centerY-(radius-additionalradius)),(int)(z*STATEDRAWSIZE-2*additionalradius), (int)( z*STATEDRAWSIZE-2*additionalradius));

           } else
           {
                g.setColor(backgroundColor);
                g.fillOval((int)(centerX-radius),(int)(centerY-radius),(int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()), (int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()));
                g.setColor(lineColor);
                g.drawOval((int)(centerX-radius),(int)(centerY-radius),(int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()), (int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()));
           }

            if (s.getIsStartState())
            {
                drawStartArrow((int)(centerX-radius-10*getDfaEditor().getZoomfactor()),centerY,startLineColor,g);
            }

        g.setColor(fontColor);
          //-- center the string --
         drawCenteredText(s.getState_Properties().getName(),centerX,centerY,nameFont,g);
        }
         g.setColor(Color.black);


    }


    /**
     * paint transition edges of the DFA
     */
    private void paintTransitions()
    {
        Graphics2D g = this.graphics;
        Dfa dfa = dfaEditor.getDfa();

        //-- set fonts --
         transitionFont = new Font("Arial", Font.PLAIN, (int)(0.9*TEXTSIZE*dfaEditor.getZoomfactor()));
         g.setFont(transitionFont);

        for (int i=0; i < dfa.getStates().size(); i++)
        {
            State s = dfa.getStates().get(i);
            for (int j=0; j<s.getOutgoingTransitions().size();j++)
            {
                Transition t = s.getOutgoingTransitions().get(j);
                State s1 = t.getFromState();
                State s2 = t.getToState();

                if (s1 != null && s2 != null)
                {

                    String caption = getStringFromInputArray(t);
                    if (caption.length() > 16)
                        caption = caption.substring(0, 15)+"...";
                    paintTransition(s1,s2,t,caption,Color.black, false);
                }
            }
        }
    }




/**
 * get the comma seperated string from Arraylist like 'a, b, c'
 * @param t Transition object
 * @return Stirng
 */
    private String getStringFromInputArray(Transition t)
    {
        if (t != null)
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
            
            //and append output of transition
            //@author Ole Wehrmeyer
            if (t.getOutput() != null && !"".equals(t.getOutput()))
                c = c + " | " + t.getOutput();
            
            return c;
        } else
        return "-";
    }

    /**
     * paints a transition
     * @param s1 From state
     * @param s2 To state
     * @param t Transition object
     * @param caption Label of the edge
     * @param color color (if special)
     * @param fakeTrans Boolean flag to distinguish between real and the helping transition in add transition mode
     */
    public void paintTransition(State s1, State s2, Transition t, String caption, Color color, boolean fakeTrans) {
        if (s1 != null && s2 != null) {
            int captionPositionX = 0;
            int captionPositionY = 0;
            Color colorCaptionColor = COLORTRANSITIONLABELNORMAL;
            Color colorLineColor = COLORTRANSITIONLINENORMAL;
            Color colorFont = COLORTRANSITIONFONTNORMAL;

            if (t.getHighlightStatus() == HighlightTypes.MouseOver) {
                colorCaptionColor = COLORTRANSITIONLABELHIGHLIGHTED;
                colorLineColor = COLORTRANSITIONLINEHIGHLIGHTED;
            }

            if (t.isSelected()) {
                colorCaptionColor = COLORTRANSITIONLABELSELECTED;
                colorLineColor = COLORTRANSITIONLINESELECTED;
                colorFont = COLORTRANSITIONFONTSELECTED;
            }

            if (fakeTrans) {
                colorCaptionColor = COLORADDNEWELEMENT2;
                colorLineColor = color;
                colorFont = Color.white;
            }

            //-- simulation purposes --
            if (dfaSim.isSimulationModeActive())
            {
                if (dfaSim.getLastTransitionTaken() != null && t == dfaSim.getLastTransitionTaken())
                {
                    colorCaptionColor = COLORTRANSITIONLINETAKEN;
                    colorLineColor = COLORTRANSITIONLINETAKEN;
                    colorFont = Color.white;
                }
            }

            boolean showTouchButton = (getDfaEditor().getToolState() == EditorToolStates.handTool) && t.isSelected();

            double z = dfaEditor.getZoomfactor();
          //  boolean paintLabelBackground = t.isSelected() || (t.getHighlightStatus() != HighlightTypes.NoHighlight || fakeTrans);
            boolean paintLabelBackground = true;
            Graphics2D g = this.graphics;
            int s1x = (int)(s1.getState_Properties().getXPos()*z);
            int s1y = (int)(s1.getState_Properties().getYPos()*z);

            int s2x = (int)(s2.getState_Properties().getXPos()*z);
            int s2y = (int)(s2.getState_Properties().getYPos()*z);

            g.setColor(color);

            //-- arc case or linear --
            boolean isBidirectional = false;
            if (!fakeTrans) {
                try {
                    isBidirectional = dfaEditor.getDfa().isBidirectionalTransition(s1, s2);
                } catch (NoSuchTransitionException ex) {
                    System.err.println(ex.getMessage());
                }
            } else {
                isBidirectional = true;
            }

            if (s1 != s2 && isBidirectional) {
                //-- get control point --
                int dx = s2x - s1x;
                int dy = s2y - s1y;
                double vlength = calcVectorLength(dx, dy);

                if (vlength > 0) {
                    QuadCurve2D c = new QuadCurve2D.Double();

                    double centerx = (s2x + s1x) / 2;
                    double centery = (s2y + s1y) / 2;

                    double normx = dx / vlength;
                    double normy = dy / vlength;

                    double additionalArcDistance = vlength / 100;
                    //-- turn vector 90 degrees --
                    double turnedx = ARCDISTANCE * normy * additionalArcDistance * t.getCurveFactor();
                    double turnedy = -ARCDISTANCE * normx * additionalArcDistance * t.getCurveFactor();

                    int cpointx = (int) (centerx + turnedx);
                    int cpointy = (int) (centery + turnedy);

                    double textAdaption = 35;
                    double absCurveFactor = Math.abs(t.getCurveFactor());
                    double direction = 1;
                     if (t.getCurveFactor() < 0)
                    {
                        direction = -1;
                        textAdaption = 35;
                    }
                    if (absCurveFactor < 2)
                    {
                        textAdaption =  Math.max(20, textAdaption*curveAdaptionFactor(absCurveFactor));
                    }
                    textAdaption = textAdaption*direction;
                    //System.out.println(t.getCurveFactor());

                    int textpointx = (int) (centerx + turnedx / (2) + normy * textAdaption*z);
                    int textpointy = (int) (centery + turnedy / (2) - normx * textAdaption*z);

                    //-- tangential crossing with the circles (start and end of curve) --
                    Vector<Double> p1 = getIntersectionPoint(s1x, s1y, cpointx, cpointy, z* 1.2 * STATEDRAWSIZE / 2);
                    Vector<Double> p2 = getIntersectionPoint(s2x, s2y, cpointx, cpointy, z * 1.4 * STATEDRAWSIZE / 2);

                    int h1x = (int) Math.round(p1.get(0)) + dfaEditor.getOffsetX();
                    int h1y = (int) Math.round(p1.get(1)) + dfaEditor.getOffsetY();

                    int h2x = (int) Math.round(p2.get(0)) + dfaEditor.getOffsetX();
                    int h2y = (int) Math.round(p2.get(1)) + dfaEditor.getOffsetY();

                    g.setColor(colorLineColor);
                    //-- quadratic arc --
                    if (vlength/z > STATEDRAWSIZE+15)
                    {
                         c.setCurve(h1x, h1y,
                            cpointx + dfaEditor.getOffsetX(), cpointy + dfaEditor.getOffsetY(),
                            h2x, h2y);
                       g.draw(c);
                    }
                    //-- draw text --
                    if (paintLabelBackground) {
                        Rectangle2D fbounds = getFontBounds(caption, textpointx + t.getCaptionOffsetX() + dfaEditor.getOffsetX(), textpointy + t.getCaptionOffsetY() + dfaEditor.getOffsetY(), transitionFont, g);
                        paintTransitionHighlightRectangle(fbounds, colorCaptionColor, (int) (4 * getDfaEditor().getZoomfactor()), g);
                    }

                    //-- arrow --
                    double ax = h2x - cpointx - dfaEditor.getOffsetX();
                    double ay = h2y - cpointy - dfaEditor.getOffsetY();

                    double arrowAngle = Math.atan2(ay, ax);
                    if (vlength <= (STATEDRAWSIZE + 25)*z)
                    arrowAngle = Math.atan2(dy, dx);
                    g.setColor(colorLineColor);
                    drawArrow(h2x, h2y, 4, arrowAngle, g);

                    g.setColor(colorFont);
                    drawCenteredText(caption, textpointx + t.getCaptionOffsetX() + dfaEditor.getOffsetX(), textpointy + t.getCaptionOffsetY() + dfaEditor.getOffsetY(), transitionFont, g);
                    captionPositionX = textpointx;
                    captionPositionY = textpointy;

                    //-- touchup button --

                    if (showTouchButton) {
                        drawTouchTransitionButton(t, normx, normy, additionalArcDistance, h1x, h1y, h2x, h2y);
                    }
                }

            } else if (s1 != s2) {
                //-- linear-case --
                Vector<Double> p1 = getIntersectionPoint(s1x, s1y, s2x, s2y, 1.2 * STATEDRAWSIZE / 2);
                Vector<Double> p2 = getIntersectionPoint(s2x, s2y, s1x, s1y, 1.4 * STATEDRAWSIZE / 2);
                int h1x = (int) Math.round(p1.get(0)) + dfaEditor.getOffsetX();
                int h1y = (int) Math.round(p1.get(1)) + dfaEditor.getOffsetY();

                int h2x = (int) Math.round(p2.get(0)) + dfaEditor.getOffsetX();
                int h2y = (int) Math.round(p2.get(1)) + dfaEditor.getOffsetY();
                g.setColor(colorLineColor);
                g.drawLine(h1x, h1y, h2x, h2y);

                double ax = s2x - s1x;
                double ay = s2y - s1y;


                double vlength = calcVectorLength(ax, ay);

                if (vlength > 0) {

                    double centerx = (s2x + s1x) / 2;
                    double centery = (s2y + s1y) / 2;

                    double normx = ax / vlength;
                    double normy = ay / vlength;

                    double arrowAngle = Math.atan2(ay, ax);
                    g.setColor(colorLineColor);
                    drawArrow(h2x, h2y, 4, arrowAngle, g);
                    // -- text --
                    int textX = (int) (centerx + 12 * normy * dfaEditor.getZoomfactor());
                    int textY = (int) (centery - 12 * normx * dfaEditor.getZoomfactor());


                    if (paintLabelBackground) {
                        Rectangle2D fbounds = getFontBounds(caption, textX + t.getCaptionOffsetX() + dfaEditor.getOffsetX(), textY + t.getCaptionOffsetY() + dfaEditor.getOffsetY(), transitionFont, g);
                        paintTransitionHighlightRectangle(fbounds, colorCaptionColor, (int) (4 * getDfaEditor().getZoomfactor()), g);
                    }
                    g.setColor(colorFont);
                    drawCenteredText(caption, textX + t.getCaptionOffsetX() + dfaEditor.getOffsetX(), textY + t.getCaptionOffsetY() + dfaEditor.getOffsetY(), transitionFont, g);
                    captionPositionX = textX;
                    captionPositionY = textY;
                }
            } else {
                //-- cirlce to state itself --
                double boxX = s1x - STATEDRAWSIZE * 0.3*z;
                double boxY = s1y - STATEDRAWSIZE * 0.95*z;
                double w = STATEDRAWSIZE * 0.6*z;
                double h = STATEDRAWSIZE * 0.6*z;

                g.setColor(colorLineColor);
                Arc2D arc = new Arc2D.Double(boxX + dfaEditor.getOffsetX(), boxY + dfaEditor.getOffsetY(), w, h, -20, 220, Arc2D.OPEN);
                g.draw(arc);

                // -- text --
                int textX = (int) s1x;
                int textY = (int) (s1y - STATEDRAWSIZE * 1.2*z);
                if (paintLabelBackground) {
                    Rectangle2D fbounds = getFontBounds(caption, textX + t.getCaptionOffsetX() + dfaEditor.getOffsetX(), textY + t.getCaptionOffsetY() + dfaEditor.getOffsetY(), transitionFont, g);
                    paintTransitionHighlightRectangle(fbounds, colorCaptionColor, (int) (4 * getDfaEditor().getZoomfactor()), g);
                }
                g.setColor(colorFont);
                drawCenteredText(caption, textX + t.getCaptionOffsetX() + dfaEditor.getOffsetX(), textY + t.getCaptionOffsetY() + dfaEditor.getOffsetY(), transitionFont, g);
                captionPositionX = textX;
                captionPositionY = textY;

                //-- arrow --
                double ax = s1x + 0.3 * STATEDRAWSIZE*z;
                double ay = s1y - 0.6 * STATEDRAWSIZE*z;
                double arrowAngle = 1.9D;
                g.setColor(colorLineColor);
                drawArrow((int) ax + dfaEditor.getOffsetX(), (int) ay + dfaEditor.getOffsetY(), 4, arrowAngle, g);
            }
            t.setClickPositionX((int)(captionPositionX/z));
            t.setClickPositionY((int)(captionPositionY/z));
            g.setColor(Color.black);
        }

    }

    /**
     * get the curve adaption factor for the quadratic curves
     * @param input double value
     * @return
     */
    private double curveAdaptionFactor(double input)
    {
        double f = (input-1);
        f = 1/(f*f*f*f+1);
        return 0.7*(input/2)*(input/2)+0.3*f;
    }

    /**
     * handle the drawing of the little touch button to change the curves of the transitions
     * @param t the Transition object
     * @param normx the normed vector x
     * @param normy the normed vector x
     * @param additionalArcDistance depends on distance
     * @param h1x special point for nice arrows
     * @param h1y special point for nice arrows
     * @param h2x special point for nice arrows
     * @param h2y special point for nice arrows
     */
    private void drawTouchTransitionButton(Transition t,double normx, double normy, double additionalArcDistance, double h1x, double h1y,double h2x, double h2y)
    {
        TouchButton tb = getDfaEditor().getTouchButton();
        tb.setVisible(true);

        Graphics2D g = this.graphics;

        double centerx = (h2x + h1x)/2;
        double centery = (h2y + h1y)/2;

         //-- turn vector 90 degrees --
        double turnedx = ARCDISTANCE*normy*additionalArcDistance*t.getCurveFactor();
        double turnedy = -ARCDISTANCE*normx*additionalArcDistance*t.getCurveFactor();

        int cpointx = (int) (centerx + turnedx);
        int cpointy = (int) (centery + turnedy);

        int textpointx = (int) (centerx + turnedx/(2)*0.93*curvePointAdaptionFactor(t.getCurveFactor(),1));
        int textpointy = (int) (centery + turnedy/(2)*0.93*curvePointAdaptionFactor(t.getCurveFactor(),1));

        Color fcolor = tb.getColorNormal();

        if (tb.isSelected())
        {
            fcolor = tb.getColorHightlight();
        }
        if (tb.isMoving())
            fcolor = tb.getColorMove();

        tb.setPx(textpointx);
        tb.setPy(textpointy);

        g.setColor(fcolor);
        g.fillOval(textpointx-tb.getSize(),
                textpointy-tb.getSize(), 2*tb.getSize(), 2*tb.getSize());
         g.setColor(COLORTRANSITIONLINESELECTED);
        g.drawOval(textpointx-tb.getSize(),
                textpointy-tb.getSize(), 2*tb.getSize(), 2*tb.getSize());
    }

    /**
     * another adaption factor for the best postions of captions
     * @param input
     * @param min
     * @return
     */
private double curvePointAdaptionFactor(double input,double min)
{
    double x = input - min;
    return 1-0.25/(x*x+1);
}


/**
 * paint the rounded background highlight box of transitions
 * @param r coordinates
 * @param c color to fill
 * @param additionalBorder determines the additional distance to the border
 * @param g canvas to draw on
 */
    private void paintTransitionHighlightRectangle(Rectangle2D r, Color c, int additionalBorder, Graphics2D g)
    {
        g.setColor(c);
       g.fillRoundRect((int)(r.getX()-additionalBorder), (int)( r.getY()-additionalBorder), (int)(r.getWidth()+2*additionalBorder),(int)( r.getHeight()+2*additionalBorder), 10, 10);
    }

    /**
     * draw the start state arrow
     * @param px position x
     * @param py position y
     * @param c Color to paint it
     * @param g canvas to paint on
     */
    private void drawStartArrow(int px, int py, Color c,Graphics2D g)
    {
        double s1x = px-20*getDfaEditor().getZoomfactor();
        double s1y = py;

        g.setColor(c);

        g.drawLine(px, py, (int)s1x, (int)s1y);
        drawArrow(px,py, 4,0,g);
    }


/**
 * draw the transition arrows
 * @param px position x
 * @param py position y
 * @param size size (not used in the moment)
 * @param angle Angle the arrow should be painted
 * @param g canvas to paint on
 */
    private void drawArrow(int px, int py, double size, double angle, Graphics2D g)
    {
        double p1x = -0.5*size*dfaEditor.getZoomfactor();
        double p1y = -1.1*size*dfaEditor.getZoomfactor();
        double p2x = -0.5*size*dfaEditor.getZoomfactor();
        double p2y = 1.1*size*dfaEditor.getZoomfactor();
        double p3x = 2*size*dfaEditor.getZoomfactor();
        double p3y = 0;

        double t1x = px + turnXbyAngle(p1x,p1y, angle);
        double t1y = py + turnYbyAngle(p1x,p1y, angle);
        double t2x = px + turnXbyAngle(p2x,p2y, angle);
        double t2y = py + turnYbyAngle(p2x,p2y, angle);
        double t3x = px + turnXbyAngle(p3x,p3y, angle);
        double t3y = py + turnYbyAngle(p3x,p3y, angle);
        Polygon s = new Polygon();
        s.addPoint((int)t1x,(int)t1y);
        s.addPoint((int)t2x,(int)t2y);
        s.addPoint((int)t3x,(int)t3y);
        g.fillPolygon(s);
    }

    /**
     * turn a x vector component
     * @param x vector x
     * @param y vector y
     * @param a angle
     * @return new x value
     */
    private double turnXbyAngle(double x, double y, double a)
    {
        return Math.cos(a)*x - Math.sin(a)*y;
    }
/**
 *  turn a y vector component
     * @param x vector x
     * @param y vector y
     * @param a angle
     * @return new y value
 */
    private double turnYbyAngle(double x, double y, double a)
    {
        return Math.sin(a)*x + Math.cos(a)*y;
    }

    /**
     * get the boundaries of a string with a special font context
     * @param s Text
     * @param centerX position x
     * @param centerY position y
     * @param f Font
     * @param g canvas
     * @return
     */
    private Rectangle2D getFontBounds(String s,int centerX, int centerY, Font f, Graphics2D g)
    {
         //-- center the string --
         FontMetrics fm   = g.getFontMetrics(f);
         Rectangle2D rect = fm.getStringBounds(s, g);

         int textHeight = (int)(rect.getHeight());
         int textWidth  = (int)(rect.getWidth());
         int textx = centerX  - textWidth/ 2;
         int texty = centerY - textHeight/ 2 ;

         rect.setRect(textx, texty, textWidth, textHeight);

         return rect;
    }

/**
 * draw a centered text at a certain position
 * @param s Text to draw
 * @param centerX position x
 * @param centerY position y
 * @param f Font
 * @param g canvas
 * @return rectangle of text bounds
 */
    private Rectangle2D drawCenteredText(String s, int centerX, int centerY , Font f, Graphics2D g)
    {
         //-- center the string --
         FontMetrics fm   = g.getFontMetrics(f);
         Rectangle2D rect = fm.getStringBounds(s, g);
         int textHeight = (int)(rect.getHeight());
         int textWidth  = (int)(rect.getWidth());
         int textx = centerX  - textWidth/ 2;
         int texty = centerY - textHeight/ 2 + fm.getAscent();

         //-- render text --
         g.drawString(s,textx,texty);
         return rect;
    }

    /**
     * calculate the intersection point of a line with a circle
     * @param fromX vector x
     * @param fromY vector y
     * @param toX vector x
     * @param toY vector y
     * @param distance radius/distance of circle
     * @return vector objects
     */
    public Vector<Double> getIntersectionPoint(double fromX, double fromY, double toX, double toY, double distance)
    {
        Vector<Double> v = new Vector<Double>();
        double dx = toX - fromX;
        double dy = toY - fromY;
        double l = calcVectorLength(dx,dy);

        if (l > 0)
        {
            double dnx = dx/l;
            double dny = dy/l;

            v.add(fromX+dnx*distance);
            v.add(fromY+dny*distance);
        } else
        {

            v.add(0D);
            v.add(0D);
        }
        return v;
    }

    /**
     * get vector length
     * @param dx
     * @param dy
     * @return length of vector
     */
    private double calcVectorLength(double dx, double dy)
    {
        if (dx == 0 && dy == 0)
        {
            return 0;
        } else
        {
            return Math.sqrt(dx*dx+dy*dy);
        }
    }

    public int getStateDrawSize() {
        return STATEDRAWSIZE;
    }


/**
 * paint user help actions like show possible transitions/states
 */
    private void paintUserActions() {
        if (dfaEditor.getDummyState().getState_Properties().isVisible())
        {
            paintDummyState(dfaEditor.getDummyState());
        }
       paintDummyTransition(dfaEditor.getDummyTransition());
    }


    /**
     * paint the user helping state
     * @param s State to paint
     */
    public void paintDummyState(State s){
        Graphics2D g = this.graphics;

        int radius = (int)(dfaEditor.getZoomfactor()*STATEDRAWSIZE/2);

        int px = s.getState_Properties().getXPos()-radius + dfaEditor.getOffsetX();
        int py = s.getState_Properties().getYPos()-radius + dfaEditor.getOffsetY();

        g.setColor(COLORADDNEWELEMENT);
        g.fillOval(px,py,(int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()), (int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()));
        g.setColor(COLORADDNEWELEMENT2);
        g.drawOval(px,py,(int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()), (int)(STATEDRAWSIZE*dfaEditor.getZoomfactor()));

        Font nameFont = new Font("Arial", Font.PLAIN, (int)(TEXTSIZE*dfaEditor.getZoomfactor()));
        g.setColor(Color.white);
        drawCenteredText(s.getState_Properties().getName(),px+(int)(radius/1.2) ,py+(int)(radius/1.5),nameFont,g);
        g.setColor(Color.black);
    }

    /**
     * paint the dummy transition in add transition mode
     * @param t paint transition
     */
    public void paintDummyTransition(Transition t)
    {
        if (t.isVisible())
        {
            paintTransition(t.getFromState(), t.getToState(), t, "Add transition "+t.getFromState().getState_Properties().getName()+
                    " > "+ t.getToState().getState_Properties().getName(), COLORADDNEWELEMENT, true);
        }

    }



}

