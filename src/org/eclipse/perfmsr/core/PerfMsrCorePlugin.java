/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.perfmsr.core;

public class PerfMsrCorePlugin {
    
    static BasePerformanceMonitor fgPerformanceMonitor;

    public static IPerformanceMonitor getPerformanceMonitor(boolean shared) {
		BasePerformanceMonitor pm= null;
		
		if (!shared)
		    pm= BasePerformanceMonitor.create();
		else {
			if (fgPerformanceMonitor == null)
			    fgPerformanceMonitor= BasePerformanceMonitor.create();
			pm= fgPerformanceMonitor;
		}
		BasePerformanceMonitor.debug("PerfMsrCorePlugin.getPerformanceMonitor() returning");
		return pm;
    }
}
