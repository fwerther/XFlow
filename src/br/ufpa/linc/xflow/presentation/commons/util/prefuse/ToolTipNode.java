package br.ufpa.linc.xflow.presentation.commons.util.prefuse;

import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


public class ToolTipNode extends ControlAdapter {

    private String[] label;
    private StringBuilder builder;
    private int width = 200;
    
    public ToolTipNode(String field) {
        this(new String[] {field});
    }

    public ToolTipNode(String[] fields) {
        label = fields;
        if ( fields.length > 1 )
        	builder = new StringBuilder();
    }
    
    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        ToolTipManager.sharedInstance().setDismissDelay(100000);
        
        
        builder.delete(0, builder.length());
        builder.append((new StringBuilder("<html><div style=\"width:")).append(width).append("\"").append("><table BGCOLOR=\"#f8eca6\">").toString());
        for ( int i=0; i<label.length; ++i ) {
        	if ( item.canGetString(label[i]) ) 
        	{
        		builder.append("<tr valign='top'><td align='left' BGCOLOR=\"#c9bc75\"><b>");
        		builder.append((new StringBuilder(String.valueOf(label[i]))).append("</b></td><td align='left'>").toString());
        		builder.append(item.getString(label[i]).replaceAll(",", "\n"));
        	}
        	builder.append("</td></tr>");
        }
        d.setToolTipText(builder.toString());

    }
    
    public void itemExited(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setToolTipText(null);
    }
    
} 

