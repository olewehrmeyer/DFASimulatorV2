/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;

/**
 * a class to load HTML URL from the JAR file
 * @author Fabian
 */
public class HelpFileLoader {
    
    private HashMap<String,URL> helpfiles = new HashMap<String, URL>();

    public HelpFileLoader()
    {
        loadFiles();
    }

    /**
     * load files
     * @return OK
     */
    public boolean loadFiles()
    {
        boolean readOk = true;
        loadFile("handtool");
        loadFile("addstatetool");
        loadFile("addtransitiontool");
        loadFile("simulation");
        return readOk;
    }
/**
 * load a specific file from the jar
 * @param s
 */
    private void loadFile(String s)
    {
        URL d = this.getClass().getResource("help/"+s+".html");
        helpfiles.put(s, d);
    }

    /**
     * get the url to a request
     * @param k
     * @return
     */
    public URL getUrlByKey(String k)
    {
        return helpfiles.get(k);
    }




}
