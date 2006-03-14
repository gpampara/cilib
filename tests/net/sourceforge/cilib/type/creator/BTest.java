/*
 * BTest.java
 * 
 * Created on Dec 8, 2005
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
package net.sourceforge.cilib.type.creator;

import net.sourceforge.cilib.type.creator.B;
import net.sourceforge.cilib.type.types.Bit;
import net.sourceforge.cilib.type.types.Type;
import junit.framework.TestCase;

/**
 * 
 * @author Gary Pampara
 */
public class BTest extends TestCase {
	
	private B creator = null;
	
	public void setUp() {
		creator = new B();
	}
	
	public void testCreateNoBounds() {
		Type b = creator.create();
		
		assertTrue(b instanceof Bit);
	}
	
	public void testCreateBounds() {
		Type b = null;
		try {
			b = creator.create(0, 3);
		}
		catch (Exception e) {
			assertTrue(b == null);
			return;
		}
		
		fail("No exception generated!");
	}

}
