/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.db;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.test.internal.performance.InternalDimensions;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
//import org.eclipse.test.internal.performance.data.Scalar;
import org.eclipse.test.internal.performance.eval.StatisticsSession;

/**
 * @since 3.1
 */
public class Scenario {
    
    final static boolean DEBUG= false;

	private String fConfigName;
	private String[] fBuildPatterns;
    private String fScenarioName;
    private String[] fBuildNames;
    private StatisticsSession[] fSessions;
    private Dim[] fDimensions;
    private Dim[] fQueryDimensions;
    private Map fSeries= new HashMap();

    
    public Scenario(String configName, String buildPattern, String scenario, Dim[] dimensions) {
        fConfigName= configName;
    	fBuildPatterns= new String[] { buildPattern };
        fScenarioName= scenario;
        fQueryDimensions= dimensions;
    }
    
    public Scenario(String configName, String[] buildPatterns, String scenario) {
    	fConfigName= configName;
    	fBuildPatterns= buildPatterns;
        fScenarioName= scenario;
        fQueryDimensions= null;
    }
    
   public String getScenarioName() {
        return fScenarioName;
    }

    public Dim[] getDimensions() {
        load();
        if (fDimensions == null)
        	return new Dim[0];
        return fDimensions;
    }
    
    public String[] getTimeSeriesLabels() {
        load();
        if (fBuildNames == null)
        	return new String[0];
        return fBuildNames;
    }
    
    public TimeSeries getTimeSeries(Dim dim) {
        load();
        TimeSeries ts= (TimeSeries) fSeries.get(dim);
        if (ts == null) {
            double[] ds= new double[fSessions.length];
            double[] sd= new double[fSessions.length];
            for (int i= 0; i < ds.length; i++) {
                ds[i]= fSessions[i].getAverage(dim);
                sd[i]= fSessions[i].getStddev(dim);                
            }
            ts= new TimeSeries(fBuildNames, ds, sd);
            fSeries.put(dim, ts);
        }
        return ts;
    }
    
    public void dump(PrintStream ps) {
	    ps.println("Scenario: " + getScenarioName()); //$NON-NLS-1$
	    Report r= new Report(2);
	    
	    String[] timeSeriesLabels= getTimeSeriesLabels();
	    r.addCell("Builds:"); //$NON-NLS-1$
	    for (int j= 0; j < timeSeriesLabels.length; j++)
	        r.addCellRight(timeSeriesLabels[j]);
	    r.nextRow();
	                
	    Dim[] dimensions= getDimensions();
	    for (int i= 0; i < dimensions.length; i++) {
	        Dim dim= dimensions[i];
	        r.addCell(dim.getName() + ':');
	        
	        TimeSeries ts= getTimeSeries(dim);
	        int n= ts.getLength();
	        for (int j= 0; j < n; j++) {
	            String stddev= " [" + dim.getDisplayValue(ts.getStddev(j)) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	            r.addCellRight(dim.getDisplayValue(ts.getValue(j)) + stddev);
	        }
	        r.nextRow();
	    }
	    r.print(ps);
	    ps.println();
    }
    
    //---- private
        
    private void load() {
        if (fBuildNames != null)
            return;
        InternalDimensions.COMITTED.getId();	// trigger loading class InternalDimensions
        
        long start= System.currentTimeMillis();
        ArrayList buildNames= new ArrayList();
        for (int i= 0; i < fBuildPatterns.length; i++) {
            if (fBuildPatterns[i].indexOf('%') >= 0)
                DB.queryBuildNames(buildNames, fConfigName, fBuildPatterns[i], fScenarioName);
            else
                buildNames.add(fBuildPatterns[i]);
        }
        String[] names= (String[])buildNames.toArray(new String[buildNames.size()]);
        if (DEBUG) System.err.println("names: " + (System.currentTimeMillis()-start));
       
        ArrayList sessions= new ArrayList();
        ArrayList names2= new ArrayList();
        
        start= System.currentTimeMillis();
        Set dims= new HashSet();
        for (int t= 0; t < names.length; t++) {
            DataPoint[] dps= DB.queryDataPoints(fConfigName, names[t], fScenarioName);
            if (DEBUG) System.err.println("  dps length: " + dps.length);
            if (dps.length > 0) {
            	dims.addAll(dps[0].getDimensions2());
            	StatisticsSession ss= new StatisticsSession(dps);
            	sessions.add(ss);
            	names2.add(names[t]);
            }
        }
        if (DEBUG) System.err.println("data: " + (System.currentTimeMillis()-start));

        fSessions= (StatisticsSession[]) sessions.toArray(new StatisticsSession[sessions.size()]);
        fBuildNames= (String[]) names2.toArray(new String[sessions.size()]);
        
        fDimensions= (Dim[]) dims.toArray(new Dim[dims.size()]);
        Arrays.sort(fDimensions,
            new Comparator() {
            	public int compare(Object o1, Object o2) {
            	    Dim d1= (Dim)o1;
            	    Dim d2= (Dim)o2;
            	    return d1.getName().compareTo(d2.getName());
            	}
        	}
        );
    }
}
