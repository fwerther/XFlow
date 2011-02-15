package br.ufpa.linc.xflow.presentation.commons.util.prefuse;

import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


public class ToolTipNode extends ControlAdapter {

    private String[] label;
    private StringBuffer buffer;
    private int width = 200;
    
    public ToolTipNode(String field) {
        this(new String[] {field});
    }

    public ToolTipNode(String[] fields) {
        label = fields;
        if ( fields.length > 1 )
            buffer = new StringBuffer();
    }
    
    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        ToolTipManager.sharedInstance().setDismissDelay(100000);
        
        
        buffer.delete(0, buffer.length());
        buffer.append((new StringBuilder("<html><div style=\"width:")).append(width).append("\"").append("><table BGCOLOR=\"#f8eca6\">").toString());
        for ( int i=0; i<label.length; ++i ) {
        	if ( item.canGetString(label[i]) ) 
        	{
        		buffer.append("<tr valign='top'><td align='left' BGCOLOR=\"#c9bc75\"><b>");
        		buffer.append((new StringBuilder(String.valueOf(label[i]))).append("</b></td><td align='left'>").toString());
        		buffer.append(item.getString(label[i]).replaceAll(",", "\n"));
        	}
        	buffer.append("</td></tr>");
        }
        d.setToolTipText(buffer.toString());

    }
    
    public void itemExited(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setToolTipText(null);
    }
    
} 

