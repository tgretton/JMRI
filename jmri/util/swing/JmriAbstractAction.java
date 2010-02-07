// JmriAbstractAction.java

 package jmri.util.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;

import jmri.util.swing.*;

/**
 * Abstract base for actions that will work with
 * multiple JMRI GUIs
 *
 * @author		Bob Jacobsen Copyright (C) 2010
 * @version		$Revision: 1.1 $
 */
 
abstract public class JmriAbstractAction extends javax.swing.AbstractAction {

    /**
     * Enhanced constructor for placing the pane in various 
     * GUIs
     */
 	public JmriAbstractAction(String s, WindowInterface wi) {
    	super(s);
    	this.wi = wi;
    	if (wi == null) new Exception("null wi found in ctor").printStackTrace();
    }
     
 	public JmriAbstractAction(String s, Icon i, WindowInterface wi) {
    	super(s, i);
    	this.wi = wi;
    }
     
    /**
     * Original constructor for compatibility with
     * older menus. Assumes SDI GUI.
     */
 	public JmriAbstractAction(String s) {
    	super(s);
    	this.wi = new jmri.util.swing.sdi.JmriJFrameInterface();
    }
     
    public void setWindowInterface(WindowInterface wi) {
        this.wi = wi;
    }
    
    public void setName(String name) {
        putValue(javax.swing.Action.NAME, name);
    }
    
    public String toString() {
        return (String) getValue(javax.swing.Action.NAME);
    }
    
    WindowInterface.Hint hint = WindowInterface.Hint.DEFAULT;
    public JmriAbstractAction setHint(WindowInterface.Hint hint) {
        this.hint = hint;
        return this;
    }

    WindowInterface wi;
     
    public void actionPerformed(ActionEvent e) {
        JmriPanel p;
        if (wi.multipleInstances()) {
            p = makePanel();
        } else {
            if (cache == null)
                cache = makePanel();
            p = cache;
        }
         
        wi.show(p, this, hint);  // no real context, this is new content
    }
    
    public void dispose() {
        if (cache != null) {
            cache.dispose();
            cache = null;
        }
    }
    JmriPanel cache = null;
    
    abstract public JmriPanel makePanel();

}

/* @(#)JmriAbstractAction.java */
