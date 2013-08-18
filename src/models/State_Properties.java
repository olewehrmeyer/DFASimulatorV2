package models;

import java.io.Serializable;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.3289D7DB-C89B-5F67-EDC9-7932FA63A9BB]
// </editor-fold> 
public class State_Properties implements Serializable{
    private static final long serialVersionUID = -5590868576506617927L;

    /** x-position in Paint-Panel */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.20CCD347-10B1-6597-1FCB-7359E09DBC7E]
    // </editor-fold> 
    private int xPos = 0;

    /** y-position in Paint-Panel */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.AC2F7C63-187B-B7F8-65CC-6904E9124D8B]
    // </editor-fold> 
    private int yPos = 0;

    /** State's name */
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.64AB935F-9911-49BB-8A4F-F2C5F29CB21B]
    // </editor-fold> 
    private String name;

    /** Is state selected? */
    private boolean selected = false;
    /** Is state visible? */
    private boolean visible = true;
    /** Determines the state's color */
    private HighlightTypes HighlightIndex = HighlightTypes.NoHighlight;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.D426DFFE-9580-1E93-59AF-36423FBEE20E]
    // </editor-fold>
    /**
     * Returns new state properties.
     */
    public State_Properties () {
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.B24CB749-9D17-113F-9C3B-A5AC8840D4AE]
    // </editor-fold>
    /**
     * Returns the state's name.
     * @return name
     */
    public String getName () {
        return name;
    }

    /**
     * Is the state visible?
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether the state is visible.
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Checkx wheather the state is selected.
     * @return true, iff the state is selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the selected status of the state.
     * @param selected true, iff the state is selected.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * get highlight index - important for drawing purposes
     * @return
     */
    public HighlightTypes getHighlightIndex() {
        return HighlightIndex;
    }

    /**
     * set highlight index - eg by a mouse event
     * @param HighlightIndex
     */
    public void setHighlightIndex(HighlightTypes HighlightIndex) {
        this.HighlightIndex = HighlightIndex;
    }

  


    

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.3E219D5B-EEDB-5FFB-2D87-9A39F53CA3BD]
    // </editor-fold>
    /**
     * Sets the state's name.
     * @param val State's name.
     * @throws IllegalArgumentException name must not be null.
     */
    public void setName (String val) throws IllegalArgumentException{
        if (val == null)
            throw new IllegalArgumentException();
        //val != null
        this.name = val;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.4EC8C8BE-5DC8-ECBB-9552-1E85C212C9E9]
    // </editor-fold>
    /**
     * Reurns the state's x-position in the editor pane.
     * @return x-position
     */
    public int getXPos () {
        return xPos;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.2D8B2722-F34E-34B7-719D-748D0C0BE744]
    // </editor-fold>
    /**
     * Sets the state's x-position in the editor pane.
     * @param val x-position
     */
    public void setXPos (int val) {
        this.xPos = val;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.0D198C83-374B-0A48-CCC4-DDAA503A880B]
    // </editor-fold>
    /**
     * Returns the state's y-position in the editor pane.
     * @return y-position
     */
    public int getYPos () {
        return yPos;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.BD51CF2E-763A-F837-E179-865EF60020C7]
    // </editor-fold>
    /**
     * Sets the state's y-position in the editor pane.
     * @param val y-position
     */
    public void setYPos (int val) {
        this.yPos = val;
    }

}

