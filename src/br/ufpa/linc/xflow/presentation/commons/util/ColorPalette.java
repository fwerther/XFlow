/* 
 * 
 * XFlow
 * _______
 * 
 *  
 *  (C) Copyright 2010, by Universidade Federal do Pará (UFPA), Francisco Santana, Jean Costa, Pedro Treccani and Cleidson de Souza.
 * 
 *  This file is part of XFlow.
 *
 *  XFlow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XFlow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XFlow.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  =================
 *  ColorPalette.java
 *  =================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.commons.util;

import prefuse.util.ColorLib;

public class ColorPalette {

	private static int authorsColorPalette[] = new int[]{
		ColorLib.rgb(64,128,0), ColorLib.rgb(64,128,128),
		ColorLib.rgb(128,0,0), ColorLib.rgb(255,0,255), 
		ColorLib.rgb(204, 255, 255), ColorLib.rgb(204, 204, 255),
//		ColorLib.rgb(255, 204, 255), ColorLib.rgb(255, 204, 204), 
		ColorLib.rgb(128,128,0), ColorLib.rgb(64,0,64),
		ColorLib.rgb(121,255,188), ColorLib.rgb(225,211,155),
		ColorLib.rgb(95,148,16), ColorLib.rgb(183,111,255),
		ColorLib.rgb(213,112,94), ColorLib.rgb(100,190,12),
		ColorLib.rgb(0,0,255), ColorLib.rgb(255,0,0),
//		ColorLib.rgb(0,255,0), ColorLib.rgb(255,255,0),
		ColorLib.rgb(255,128,128), ColorLib.rgb(255,128,0),
		ColorLib.rgb(255,0,128), ColorLib.rgb(128,255,255), 
		ColorLib.rgb(0,64,0), ColorLib.rgb(192,192,192), 
		ColorLib.rgb(0,64,128), ColorLib.rgb(128,0,128),
		ColorLib.rgb(0,255,64), ColorLib.rgb(128,128,64),
		ColorLib.rgb(128,128,192), ColorLib.rgb(0,0,128),
		ColorLib.rgb(35,92,128), ColorLib.rgb(158,44,5),
		ColorLib.rgb(10,19,128), ColorLib.rgb(158,10,90),

		ColorLib.rgb(255, 255, 204), ColorLib.rgb(204, 255, 204),
		ColorLib.rgb(204, 204, 204), ColorLib.rgb(51, 255, 255),
		ColorLib.rgb(51, 102, 255), ColorLib.rgb(153, 153, 255),
		ColorLib.rgb(255, 102, 204), ColorLib.rgb(255, 102, 102),
		ColorLib.rgb(255, 204, 102), ColorLib.rgb(102, 255, 102),
		ColorLib.rgb(102, 205, 204), ColorLib.rgb(153, 153, 153),
		ColorLib.rgb(0, 204, 204), ColorLib.rgb(0, 51, 204),
		ColorLib.rgb(204, 0, 255), ColorLib.rgb(255, 0, 51), 
		ColorLib.rgb(153, 102, 0), ColorLib.rgb(102, 51, 0),
		ColorLib.rgb(204, 204, 0), ColorLib.rgb(51, 153, 0), 
		ColorLib.rgb(0, 153, 153), ColorLib.rgb(0, 102, 102),
		ColorLib.rgb(102, 0, 102), ColorLib.rgb(102, 102, 102),
		ColorLib.rgb(204, 0, 0), ColorLib.rgb(51, 255, 51),
		ColorLib.rgb(0, 51, 51), ColorLib.rgb(51, 51, 51),
		ColorLib.rgb(255, 153, 0), ColorLib.rgb(102, 0, 102),

		ColorLib.rgb(128,64,0), ColorLib.rgb(64,0,0)
	};

	public static int[] getAuthorsColorPalette() {
		return authorsColorPalette;
	}

	public static void initiateColors(final int size) {
		if(authorsColorPalette.length >= size){
			// enough colors to represent authors.
		}
		else {
			int colorsNeededFactor = (int) Math.ceil((double) size / authorsColorPalette.length);
			System.out.println(colorsNeededFactor);
			int[] colorsArray = new int[colorsNeededFactor*authorsColorPalette.length];
			System.arraycopy(authorsColorPalette, 0, colorsArray, 0, authorsColorPalette.length);
			for (int i = 1; i < colorsNeededFactor; i++) {
				System.arraycopy(authorsColorPalette, 0, colorsArray, authorsColorPalette.length*i, authorsColorPalette.length);
			}
			authorsColorPalette = colorsArray;
		}
	}
}
