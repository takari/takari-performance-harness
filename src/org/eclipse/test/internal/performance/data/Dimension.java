/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.data;



/**
 * @since 3.1
 */
public class Dimension {
    private final int fId;
	private final Unit fUnit;
	private final int fMultiplier;
	
	public Dimension(int id, Unit unit) {
		this(id, unit, 1);
	}

	public Dimension(int id, Unit unit, int multiplier) {
		fId= id;
		fUnit= unit;
		fMultiplier= multiplier;
	}

	public String getName() {
		return DimensionMessages.getString(fId);
	}
	
	public Unit getUnit() {
		return fUnit;
	}
	
	public String toString() {
		return "Dimension [name=" + getName() + ", " + fUnit + "]";
	}
	
	public int getMultiplier() {
		return fMultiplier;
	}

	public DisplayValue getDisplayValue(Scalar scalar) {
		return fUnit.getDisplayValue((double) scalar.getMagnitude() / fMultiplier);
	}
	
	public DisplayValue getDisplayValue(double scalar) {
		return fUnit.getDisplayValue(scalar / fMultiplier);
	}
}
