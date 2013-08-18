package models;

import controller.DFAPainter;
import controller.Simulator;
import gui.DFAMainWin;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.2B0808A6-6365-E921-E748-22BEC69D6EAD]
// </editor-fold> 






//--------------------  STATES ---------------------------

public class DfaEditor{

    private boolean isEditable;
    private double zoomfactor = 1;
    private int offsetX = 0;
    private int offsetY = 0;
    private int oldoffsetX = 0;
    private int oldoffsetY = 0;
    private int panStartX = 0;
    private int panStartY = 0;

    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    
    private DFAMainWin dFAMainWin = null;

    private State currentStateSelected = null;
    private Transition currentTransSelected = null;

    private State transitionAddFrom = null;
    private State transitionAddTo = null;

    boolean waitForEditWindow = false;

    //-- dummies for userinteraction --
    private State dummyState = new State("new", -1);
    private Transition dummyTransition = new Transition(null,null);

    //-- LineToucher --
    private TouchButton touchButton = new TouchButton();

    //-- states --
    private EditorSelectionStates selectionState;
    private EditorToolStates toolState;
    private EditorTransitionStates transitionState;

    // -- connections to other objects -.
    private Dfa dfa = null;
    private DFAPainter dFAPainter;
    private Simulator dfaSim = null;

    public Dfa getDfa() {
        return dfa;
    }

    public void setDfa(Dfa dfa) {
        this.dfa = dfa;
    }

    public DFAPainter getdFAPainter() {
        return dFAPainter;
    }

    public TouchButton getTouchButton() {
        return touchButton;
    }

    public void setTouchButton(TouchButton touchButton) {
        this.touchButton = touchButton;
    }

    public State getDummyState() {
        return dummyState;
    }

    /**
     * get the selection state for the context menu
     * @return true if something is selected
     */
    public boolean isAnythingSelected()
    {
        return currentStateSelected != null || currentTransSelected != null;

    }

    public void setDummyState(State dummyState) {
        this.dummyState = dummyState;
    }


    public void setdFAPainter(DFAPainter dFAPainter) {
        this.dFAPainter = dFAPainter;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isWaitForEditWindow() {
        return waitForEditWindow;
    }

    public void setWaitForEditWindow(boolean waitForEditWindow) {
        this.waitForEditWindow = waitForEditWindow;
    }

    public DFAMainWin getdFAMainWin() {
        return dFAMainWin;
    }

    public void setdFAMainWin(DFAMainWin dFAMainWin) {
        this.dFAMainWin = dFAMainWin;
    }

    public Transition getDummyTransition() {
        return dummyTransition;
    }

    public void setDummyTransition(Transition dummyTransition) {
        this.dummyTransition = dummyTransition;
    }


    // <editor-fold defaultstate="collapsed" desc=" UML Marker ">
    // #[regen=yes,id=DCE.07B7D0A4-8D42-06F0-1C6A-6AB91D0ED769]
    // </editor-fold>
    public DfaEditor (Simulator dfaSimulator) {
        dFAPainter = new DFAPainter(dfaSimulator);
        dfaSim = dfaSimulator;
        dFAPainter.setDfaEditor(this);
        initEditor();
    }


    /**
     * Init all things needed for the editor 
     */
    private void initEditor()
    {
        this.resetEditor();
    }


    /**
     * reset state variables and
     */
    public void resetEditor()
    {
        this.isEditable = true;
        this.selectionState = EditorSelectionStates.selectNothing;
        this.toolState = EditorToolStates.handTool;
        this.transitionState = EditorTransitionStates.selectFromState;
        this.offsetX = 0;
        this.offsetY = 0;
        this.currentStateSelected = null;
        this.currentTransSelected = null;
        this.panStartX = 0;
        this.panStartY = 0;
        touchButton.hideAndReset();
        setToolEnvirionmentOptions();
    }

    /**
     * Tool options settings must be correct after changing tools.
     */
    public void setToolEnvirionmentOptions()
    {
        if (toolState == EditorToolStates.handTool)
        {
            dummyState.getState_Properties().setVisible(false);
            dummyTransition.setVisible(false);
            touchButton.setVisible(false);
            touchButton.setMoving(false);
            touchButton.setSelected(false);
            
        }
        if (toolState == EditorToolStates.addState)
        {
             dummyState.getState_Properties().setVisible(true);
             dummyTransition.setVisible(false);
        }
        if (toolState == EditorToolStates.addTransition)
        {
             dummyState.getState_Properties().setVisible(false);
             dummyTransition.setVisible(false);
             this.transitionState = EditorTransitionStates.selectFromState;
        }
    }

    /**
     * clear all selection flags from states and transitions
     *
     */
    public void removeAllSelections()
    {
        this.currentStateSelected = null;
        this.currentTransSelected = null;
        touchButton.hideAndReset();
        
        for (int i=0;i<getDfa().getStates().size();i++)
        {
            State st = getDfa().getStates().get(i);
            for (int j=0;j<st.getOutgoingTransitions().size();j++)
            {
                Transition tt = st.getOutgoingTransitions().get(j);
                tt.setSelected(false);
                tt.setHighlightStatus(HighlightTypes.NoHighlight);
            }
            st.getState_Properties().setSelected(false);
            st.getState_Properties().setHighlightIndex(HighlightTypes.NoHighlight);
            
        }
    }

/**
 * handle the mouse PRESSED event
 * @param evt
 */
    public void handleMousePressed(java.awt.event.MouseEvent evt)
    {
        if (toolState == EditorToolStates.handTool)
        {
            handleObjectSelection(evt);            
        }
        //-- mouse pan --
        panStartX = evt.getX();
        panStartY = evt.getY();
        oldoffsetX = offsetX;
        oldoffsetY = offsetY;
    }

    public void handleMouseDragged(java.awt.event.MouseEvent evt)
    {
         handleToolHandMouseDragged(evt);
    }


    /**
 * handle the mouse MOVED event
 * @param evt
 */
    public void handleMouseMoved(java.awt.event.MouseEvent evt)
    {
        if (toolState == EditorToolStates.handTool)
        {
            handleObjectHighlighting(evt);
        }
        if (toolState == EditorToolStates.addState)
        {
            if (!waitForEditWindow)
                handleAddStatesMove(evt);
            updateGraphicsAll();
        }
        if (toolState == EditorToolStates.addTransition)
        {
            handleObjectHighlighting(evt);
            if (!waitForEditWindow)
            showPossibleTransitons(evt);
            updateGraphicsAll();
        }


    }

/**
 * user interaction helper in add transition add mode
 * @param evt
 */
    public void showPossibleTransitons(java.awt.event.MouseEvent evt)
    {
        if (this.transitionState == EditorTransitionStates.selectToState)
        {
            if (transitionAddFrom != null)
            {
                 State stateMouseOver = getStateAtMouse(evt.getX(), evt.getY(), false, HighlightTypes.NoHighlight, false);
                 if (stateMouseOver != null)
                 {
                     //-- draw highlight transitions --
                     dummyTransition.setFromState(transitionAddFrom);
                     dummyTransition.setToState(stateMouseOver);
                     dummyTransition.setVisible(true);
                 }

            }

        }
    }


/**
 * handle the mouse RELEASED event
 * @param evt
 */
    public void handleMouseReleased(java.awt.event.MouseEvent evt)
    {
        if (toolState == EditorToolStates.handTool)
        {
            handleHandToolMouseRelease(evt);
        }
        if (toolState == EditorToolStates.addState)
        {
            if (!waitForEditWindow)
             handleAddState(evt);
             updateGraphicsAll();
        }
        if (toolState == EditorToolStates.addTransition)
        {
            if (!waitForEditWindow)
            handleAddTransitionStep(evt);
        }
    }


    /**
     * a new transition is added, steps control
     * @param evt
     */
    public void handleAddTransitionStep(java.awt.event.MouseEvent evt)
    {
        if (this.transitionState == EditorTransitionStates.selectFromState && !waitForEditWindow)
        {
            State stateHit = getStateAtMouse(evt.getX(), evt.getY(), true, HighlightTypes.NoHighlight, true);
            if (stateHit != null)
            {
                transitionAddFrom = stateHit;
                this.transitionState = EditorTransitionStates.selectToState;
            }
        } else
        {
            State stateHit = getStateAtMouse(evt.getX(), evt.getY(), true, HighlightTypes.NoHighlight, true);
            if (stateHit != null && !waitForEditWindow)
            {
                transitionAddTo = stateHit;
                stateHit.getState_Properties().setSelected(false);
                this.transitionState = EditorTransitionStates.selectFromState;
                if (transitionAddFrom != null )
                {
                    addNewTransition(transitionAddFrom,transitionAddTo);
                    dummyTransition.setVisible(false);
                    updateGraphicsAll();
                }
            } else
            {
                transitionAddTo = null;
                transitionAddFrom = null;
                this.transitionState = EditorTransitionStates.selectFromState;
                dummyTransition.setVisible(false);
            }


        }
    }

    /**
     * handle Keyboard commands from the user
     * @param evt
     */
    public void handleEditorKeyPressed(java.awt.event.KeyEvent evt)
    {
        if (isEditable)
        {
            //-- only allow with certain tools --
            if (toolState == EditorToolStates.handTool || toolState == EditorToolStates.addState)
            {
                int key = evt.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    handleDoubleClick(null);
                }
                if (key == KeyEvent.VK_DELETE) {
                    handleDeleteObject(evt);

                }



            }
        }
    }

/**
 * delete selected objects
 * @param evt
 */
    public void handleDeleteObject(java.awt.event.KeyEvent evt)
    {
        if (currentStateSelected != null && currentTransSelected != null)
        {
           //-- too much selected --
        } else
        {
            if (currentStateSelected != null)
            {
                deleteState(currentStateSelected, true);
            }
            if (currentTransSelected != null)
            {
                deleteTransition(currentTransSelected, true);
            }
        }
    }

/**
 * ask user and delete state
 * @param s State to be deleted
 * @param askUser boolean var ask user yes or no
 * @return state deleted
 */
    public boolean deleteState(State s, boolean askUser)
    {
        boolean deleted = false;
        if (askUser)
        {
            if (dFAMainWin.askUserMessageBoxYesNo("Delete State","Should the state '"+
                    s.getState_Properties().getName()+"' be deleted?"))
            {
                deleted = true;
            }
        } else
            deleted = true;

        if (deleted)
        {
            //-- now really delete --
            dfa.removeState(s);
            currentStateSelected = null;
            updateGraphicsAll();
        }
        return deleted;
    }

    /**
     * delete a transition
     * @param t concering transition
     * @param askUser boolean flag if a messagebox should appear to ask the user
     * @return was it deleted?
     */

    public boolean deleteTransition(Transition t, boolean askUser)
    {
        boolean deleted = false;
        if (askUser)
        {
            if (dFAMainWin.askUserMessageBoxYesNo("Delete transition","Should the selected transition be deleted?"))
            {
                deleted = true;
            }
        } else
            deleted = true;

        if (deleted)
        {
            //-- now really delete --
            dfa.removeTransition(t);
            currentTransSelected = null;
            updateGraphicsAll();
        }
        return deleted;
    }



/**
 * add a new Transition between 2 States
 * @param from State to start
 * @param to State to end
 * @return the new created Transition
 */
    private Transition addNewTransition(State from, State to)
    {
       Transition t =  dfa.addTransition(from, to);
       currentTransSelected = t;
       removeAllSelections();
       t.setSelected(true);
       dFAMainWin.showTransEditWin(t,true);
       updateGraphicsAll();
       return t;
    }


    /**
     * handle the mouse DOUBLE click
     * @param evt
     */
    public void handleDoubleClick(java.awt.event.MouseEvent evt)
    {
        if (toolState == EditorToolStates.handTool)
        {
            CheckOpenPropertiesWindows(evt);
        }
    }


/**
 * open a properties window if needed (like double clicking of the user on an
 * object)
 * @param evt
 */
    private void CheckOpenPropertiesWindows(java.awt.event.MouseEvent evt)
    {
        if (currentStateSelected != null)
        {
            dFAMainWin.showStateEditWin(currentStateSelected,false);
        } else
        if (currentTransSelected != null)
        {
            dFAMainWin.showTransEditWin(currentTransSelected,false);
        }

    }

    /**
     * handle mouse DRAG event
     * @param evt
     */
    private void handleToolHandMouseDragged(java.awt.event.MouseEvent evt)
    {

        if (SwingUtilities.isLeftMouseButton(evt))
        {
            handleTouchStartDrag(evt);
             if (currentStateSelected == null)
             {
                if (touchButton.isSelected())
                {
                    handleTouchButtonMoveValue(evt);
                } else
                {
                    setOffsetX(oldoffsetX+evt.getX()-panStartX);
                    setOffsetY(oldoffsetY+evt.getY()-panStartY);                  
                    
                }


                 updateGraphicsAll();
                 //System.out.println("offsetX:"+offsetX+"   offsetx:"+offsetY+" panstartX"+panStartX+"  panstarty "+panStartY+"   MX"+evt.getX()+"   MY"+evt.getY()+"    dx"+(evt.getX()-panStartX));
             } else
             {
                if (currentStateSelected != null)
                {
                    if (currentTransSelected == null)
                         handleStateMovement(evt);
                }

                 updateGraphicsAll();
             }
        }
       
    }

/**
 * handle the mouse TOUCH BUTTON VALUE
 * @param evt
 */
    private void handleTouchButtonMoveValue(java.awt.event.MouseEvent evt)
    {
       
        if (currentTransSelected != null)
        {
            
            //-- calc arc factor --
            int s1x = currentTransSelected.getFromState().getState_Properties().getXPos();
            int s1y = currentTransSelected.getFromState().getState_Properties().getYPos();

            int s2x = currentTransSelected.getToState().getState_Properties().getXPos();
            int s2y = currentTransSelected.getToState().getState_Properties().getYPos();

            double mx = (s1x+s2x)/2;
            double my = (s1y+s2y)/2;
            
            double dx = s2x - s1x;
            double dy = s2y - s1y;

            double l = 0;
            
            double vlength = calcVectorLength(dx,dy);

            if (vlength > 0)
            {
                double centerx = zoomfactor*(s2x + s1x)/2;
                double centery = zoomfactor*(s2y + s1y)/2;

                double normx = zoomfactor*dx/vlength;
                double normy = zoomfactor*dy/vlength;

                double turnedx = normy;
                double turnedy = -normx;

                double ddx = evt.getX()-offsetX-centerx;
                double ddy = evt.getY()-offsetY-centery;


                l = (6*(ddx*turnedx + ddy*turnedy)/vlength)/(zoomfactor*zoomfactor);
            }
            currentTransSelected.setCurveFactor(l);
            touchButton.setCurrentValue(l);
            touchButton.setVisible(true);
        }
        
    }

    /**
     * helping function to get the euclidian length of a vector
     * @param dx
     * @param dy
     * @return
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
/**
 * handle the mouse drag of a state
 * @param evt
 */
    private void handleAddStatesMove(java.awt.event.MouseEvent evt)
    {
       dummyState.getState_Properties().setVisible(true);
       dummyState.getState_Properties().setXPos(evt.getX()-getOffsetX());
       dummyState.getState_Properties().setYPos(evt.getY()-getOffsetY());
    }

    /**
     * handle the ADDING state procedure
     * @param evt
     */
    private void handleAddState(java.awt.event.MouseEvent evt)
    {
        dummyState.getState_Properties().setVisible(false);

        removeAllSelections();
        State s = dfa.addState();
        s.getState_Properties().setName("q"+(dfa.getStates().size()-1));
        s.getState_Properties().setSelected(true);
        s.getState_Properties().setXPos((int)(this.dummyState.getState_Properties().getXPos()/zoomfactor));
        s.getState_Properties().setYPos((int)(this.dummyState.getState_Properties().getYPos()/zoomfactor));
        updateGraphicsAll();
        dFAMainWin.showStateEditWin(s,true);
    }


    /**
     * handle the SELECTION process of objects
     * @param evt
     */
    private void handleObjectSelection(java.awt.event.MouseEvent evt)
    {
        State stateHit = currentStateSelected;
        Transition transHit = currentTransSelected;

        if (touchButton.isSelected())
        {
           touchButton.setMoving(handleTouchUpHighlight(evt));
        }


        if (!touchButton.isMoving())
        {
             stateHit = getStateAtMouse(evt.getX(), evt.getY(), false, HighlightTypes.NoHighlight, true);
             transHit = getTransitionatMouse(evt.getX(), evt.getY(), false, HighlightTypes.NoHighlight, true);

            if (stateHit != null && transHit != null)
            {
                    stateHit.getState_Properties().setSelected(false);
                    stateHit = null;
            }
        }
        if (transHit != null)
        {
            if (stateHit != null)
            {
                stateHit.getState_Properties().setSelected(false);
                currentStateSelected = null;
            }
        } else
        if (stateHit != null)
        {
            updateGraphicsAll();
            dragOffsetX = (int) (evt.getX()-offsetX-stateHit.getState_Properties().getXPos()*zoomfactor);
            dragOffsetY = (int) (evt.getY()-offsetY-stateHit.getState_Properties().getYPos()*zoomfactor);
           
        }
        this.currentStateSelected = stateHit;
        this.currentTransSelected = transHit;
    }


/**
 * get State at mouse position an select it or highlight it if needed
 * @param px Mouse x
 * @param py Mouse y
 * @param changeHighlight boolean should the highlight value be changed?
 * @param highlightIndex to which  index should be changed in case of hit
 * @param selectOnHit should it be selected in case of hit
 * @return the State hit or null
 */
    private State getStateAtMouse(int px, int py, boolean changeHighlight, HighlightTypes highlightIndex, boolean selectOnHit)
    {
        State s = null;
        double z = zoomfactor;
        double tx = (px - offsetX)/z;
        double ty = (py - offsetY)/z;

        for (int i=0;i<getDfa().getStates().size();i++)
        {
            State st = getDfa().getStates().get(i);
            double dx = tx-st.getState_Properties().getXPos();
            double dy = ty-st.getState_Properties().getYPos();

            if ((dx*dx+dy*dy) < dFAPainter.getStateDrawSize()*dFAPainter.getStateDrawSize()/4)
            {
                s = st;
                if (changeHighlight)
                st.getState_Properties().setHighlightIndex(highlightIndex);
                if (selectOnHit)
                st.getState_Properties().setSelected(true);
            } else
            {
                if (changeHighlight)
                st.getState_Properties().setHighlightIndex(HighlightTypes.NoHighlight);
                if (selectOnHit)
                st.getState_Properties().setSelected(false);
            }
        }
        return s;
    }

    /**
     * get transition which is hit by mouse
     * @param px Mousex
     * @param py Mousey
     * @param changeHighlight should the highlight state be changed?
     * @param highlightIndex to which index should be switched?
     * @param selectOnHit should transition be selected on hit?
     * @return the tranistion hit or null
     */
      private Transition getTransitionatMouse(int px, int py, boolean changeHighlight, HighlightTypes highlightIndex, boolean selectOnHit)
    {
        Transition t = null;
        double z = zoomfactor;
        double tx = (px - offsetX)/z;
        double ty = (py - offsetY)/z;

        for (int i=0;i<getDfa().getStates().size();i++)
        {
            State st = getDfa().getStates().get(i);
            for (int j=0;j<st.getOutgoingTransitions().size();j++)
            {
                Transition tt = st.getOutgoingTransitions().get(j);
                
                double dx = tx-tt.getClickPositionX();
                double dy = ty-tt.getClickPositionY();

                if ((dx*dx+dy*dy) < dFAPainter.getVirtualTransitionSite()*dFAPainter.getVirtualTransitionSite()/4)
                {
                    t = tt;
                    if (changeHighlight)
                        tt.setHighlightStatus(highlightIndex);
                    if (selectOnHit)
                        tt.setSelected(selectOnHit);
                } else
                {
                
                 if (changeHighlight)
                    tt.setHighlightStatus(HighlightTypes.NoHighlight);
                 if (selectOnHit)
                     tt.setSelected(false);
                }
            }

        }

        return t;
      }



    /**
     * highlight objects handlers
     */
    private void handleObjectHighlighting(java.awt.event.MouseEvent evt)
    {
        if (toolState == EditorToolStates.handTool)
        {
            State s = getStateAtMouse(evt.getX(), evt.getY(),true,HighlightTypes.MouseOver,false);
            Transition transHit = getTransitionatMouse(evt.getX(), evt.getY(), true, HighlightTypes.MouseOver, false);
            handleTouchUpHighlight(evt);
            updateGraphicsAll();
        }
        if (toolState == EditorToolStates.addTransition)
        {
            State s = getStateAtMouse(evt.getX(), evt.getY(),true,HighlightTypes.MouseOver,false);
            
        }
        if (toolState == EditorToolStates.addState)
        {
            
        }
    }


    /**
     * handle the state movement procedure
     * @param evt
     */
    private void handleStateMovement(java.awt.event.MouseEvent evt)
    {
        if (currentStateSelected != null)
        {

            double tx = (evt.getX()- dragOffsetX -offsetX);
            double ty = (evt.getY()- dragOffsetY -offsetY);

            int px = (int)(tx/zoomfactor);
            int py = (int)(ty/zoomfactor);
            currentStateSelected.getState_Properties().setXPos(px);
            currentStateSelected.getState_Properties().setYPos(py);
        }
    }


    /**
     * touch up button hightlight (transition arc control) control
     *
     * @param evt
     * @return
     */
    private boolean handleTouchUpHighlight(java.awt.event.MouseEvent evt)
    {
        if (touchButton.isVisible())
        {
            int dx = touchButton.getPx() - evt.getX();
            int dy = touchButton.getPy() - evt.getY();
            boolean hit = dx*dx+dy*dy <= touchButton.getSize()*touchButton.getSize();
            touchButton.setSelected(hit);
            if (hit)
            {
                if (currentTransSelected != null)
                {
                    touchButton.setCurrentValue(currentTransSelected.getCurveFactor());
                } else
                    touchButton.hideAndReset();
            }
            return hit;
        } else
            return false;
    }

/**
 * handle START drag of Arc control touch up button control
 * @param evt
 * @return
 */
    private boolean handleTouchStartDrag(java.awt.event.MouseEvent evt)
    {
        if (touchButton.isVisible())
        {
            touchButton.setMoving(true);
            return true;
        } else
        {
             touchButton.setMoving(false);
             return false;
        }
    }

/**
 * handle END drag of Arc control touch up button control 
 * @param evt
 */
     private void handleTouchEndDrag(java.awt.event.MouseEvent evt)
    {
        if (touchButton.isMoving() && touchButton.isVisible())
        {
            touchButton.setMoving(false);            
        } else
        {
             touchButton.setMoving(false);           
        }
    }



/**
 * handle Hand Tool Mouse realease
 * @param evt
 */
    private void handleHandToolMouseRelease(java.awt.event.MouseEvent evt)
    {
        handleStateMovement(evt);

        if (toolState == EditorToolStates.handTool)
        {
            if (currentTransSelected == null)
            {
                State s = getStateAtMouse(evt.getX(), evt.getY(),true,HighlightTypes.MouseOver,true);
            }

             handleTouchEndDrag(evt);
            updateGraphicsAll();
        }
    }


/**
 * repaint all
 */
    private void updateGraphicsAll()
    {
        dFAPainter.requestRepaintAll();
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public double getZoomfactor() {
        return zoomfactor;
    }

    public void setZoomfactor(double zoomfactor) {
        this.zoomfactor = zoomfactor;
    }

    public EditorSelectionStates getSelectionState() {
        return selectionState;
    }

    public void setSelectionState(EditorSelectionStates selectionState) {
        this.selectionState = selectionState;
    }

    public EditorToolStates getToolState() {
        return toolState;
    }

    public void setToolState(EditorToolStates toolState) {
        this.toolState = toolState;
        setToolEnvirionmentOptions();
    }

    public EditorTransitionStates getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(EditorTransitionStates transitionState) {
        this.transitionState = transitionState;
    }

    public Dfa getExampleDFA(int no)
    {
        Dfa d = new Dfa();
        if (no == 1)
        {
         State s1 = new State("even", 0);
         State s2 = new State("odd", 1);

         s1.getState_Properties().setXPos(100);
         s1.getState_Properties().setYPos(100);
         s1.setIsStartState(true);
         s1.setIsFinalState(true);

         s2.getState_Properties().setXPos(200);
         s2.getState_Properties().setYPos(100);

         d.addState(s1);
         d.addState(s2);

         ArrayList<String> l;

         Transition t1 = d.addTransition(s1, s2);
         l = new ArrayList<String>();
         l.add("1");
         t1.setInput(l);

         Transition t2 = d.addTransition(s2, s1);
         l = new ArrayList<String>();
         l.add("1");
         t2.setInput(l);

         Transition t3 = d.addTransition(s1, s1);
         l = new ArrayList<String>();
         l.add("0");
         t3.setInput(l);

         Transition t4 = d.addTransition(s2, s2);
         l = new ArrayList<String>();
         l.add("0");
         t4.setInput(l);

         d.setStartState(s1);
         d.setDescription("This DFA accepts inputs with an even number of ones. 11011 will be accepted, 001 will be rejected. The empty word will also be accepted.");
        }
        

        if (no == 2)
        {
         State s1 = new State("start", 0);
         State s2 = new State("a", 1);
         State s3 = new State("ab", 1);
         State s4 = new State("abc", 1);

         s1.getState_Properties().setXPos(100);
         s1.getState_Properties().setYPos(100);
         s1.setIsStartState(true);

         s2.getState_Properties().setXPos(200);
         s2.getState_Properties().setYPos(100);

         s3.getState_Properties().setXPos(300);
         s3.getState_Properties().setYPos(100);

         s4.getState_Properties().setXPos(400);
         s4.getState_Properties().setYPos(100);
         s4.setIsFinalState(true);

         d.addState(s1);
         d.addState(s2);
         d.addState(s3);
         d.addState(s4);

         ArrayList<String> l;

         Transition t1 = d.addTransition(s1, s2);
         l = new ArrayList<String>();
         l.add("a");
         t1.setInput(l);

         Transition t2 = d.addTransition(s2, s3);
         l = new ArrayList<String>();
         l.add("b");
         t2.setInput(l);

         Transition t3 = d.addTransition(s3, s4);
         l = new ArrayList<String>();
         l.add("c");
         t3.setInput(l);
         

         Transition t4 = d.addTransition(s1, s1);
         l = new ArrayList<String>();
         l.add("b");
         l.add("c");
         t4.setInput(l);

         Transition t8 = d.addTransition(s2, s2);
         l = new ArrayList<String>();
         l.add("a");
         t8.setInput(l);

         Transition t5 = d.addTransition(s2, s1);
         l = new ArrayList<String>();
         l.add("c");
         t5.setInput(l);

         Transition t6 = d.addTransition(s3, s1);
         l = new ArrayList<String>();
         l.add("b");
         t6.setInput(l);
         t6.setCurveFactor(1.8D);


         Transition t9= d.addTransition(s3, s2);
         l = new ArrayList<String>();
         l.add("a");
         t9.setInput(l);


         Transition t7 = d.addTransition(s4, s1);
         l = new ArrayList<String>();
         l.add("b");
         l.add("c");
         t7.setInput(l);
         t7.setCurveFactor(3);


         Transition t10 = d.addTransition(s4, s2);
         l = new ArrayList<String>();
         l.add("a");
         t10.setInput(l);
         t10.setCurveFactor(2);

         d.setStartState(s1);
         d.setDescription("This DFA accepts inputs when it ends with 'abc'. The alphabet letters are a,b and c.");
        }





        return d;
    }




    /**
     * enum for select states
     */
 enum EditorSelectionStates {
     selectNothing, selecetState, selectTransition, selectAll
 }


 /**
  * enum for transition states
  */
 enum EditorTransitionStates {
     selectFromState, selectToState
 }


}



