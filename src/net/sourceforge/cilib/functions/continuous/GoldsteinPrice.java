/*
 * GoldsteinPrice.java
 * 
 * Created on Mar 14, 2006
 *
 * Copyright (C) 2003 - 2006 
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science 
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package net.sourceforge.cilib.functions.continuous;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.Vector;

/**
 * The Goldstein-Price function<br><br>
 * 
 * Minimum: f(x) = 3; x = (0, -1)<br><br>
 * 
 * -2 &lt;= x &lt;= 2<br><br>
 * 
 * @author Gary Pampara
 *
 */
public class GoldsteinPrice extends ContinuousFunction {
	
	public Object getMinimum() {
		return new Double(3.0);
	}

	@Override
	public double evaluate(Vector x) {
		double part1 = 1 + (x.getReal(0)+x.getReal(1)+1.0)*(x.getReal(0)+x.getReal(1)+1.0)*(19.0 - 14.0*x.getReal(0) + 3*x.getReal(0)*x.getReal(0) - 14*x.getReal(1) + 6*x.getReal(0)*x.getReal(1) + 3*x.getReal(1)*x.getReal(1));
		double part2 = 30 + (2*x.getReal(0)-3*x.getReal(1))*(2*x.getReal(0)-3*x.getReal(1))*(18 - 32*x.getReal(0) + 12*x.getReal(0)*x.getReal(0) + 48*x.getReal(1) - 36*x.getReal(0)*x.getReal(1) + 27*x.getReal(1)*x.getReal(1));
		return part1 * part2;
	}

}
