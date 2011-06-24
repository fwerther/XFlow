package br.ufpa.linc.xflow.presentation.visualizations.treemap.controls;

import java.awt.event.MouseEvent;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

public class TooltipControl extends ControlAdapter {

	private final String tooltipFields[] = new String[]{
			"name"
	};

	public TooltipControl(){

	}

	public void itemEntered(VisualItem item, MouseEvent e){
		Display display = (Display)e.getSource();
		if(tooltipFields.length == 1){
			display.setToolTipText(item.getString(tooltipFields[0]));
		} 
		else {
			
			StringBuilder builder = new StringBuilder();
			
			builder.delete(0, builder.length());
			for(int i = 0; i < tooltipFields.length; i++)
				if(item.canGetString(tooltipFields[i])){
					if(builder.length() > 0){
						builder.append("\n");
					}
					builder.append(item.getString(tooltipFields[i]));
				}
			display.setToolTipText(builder.toString());
		}
	}

	public void itemExited(VisualItem item, MouseEvent e){
		Display d = (Display)e.getSource();
		d.setToolTipText(null);
	}
	
}
