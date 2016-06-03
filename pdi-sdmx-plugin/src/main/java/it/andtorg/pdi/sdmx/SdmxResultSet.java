/*
 * The MIT License
 *
 * Copyright (c) 2016 Andrea Torre, https://twitter.com/AndtorG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.PortableTimeSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to conveniently manage sdmx timeseries as rows
 *
 * @author Andrea Torre
 * @since 01/06/16
 */
public class SdmxResultSet {
  public static SdmxResultSet getResultSet( List<PortableTimeSeries> series ) {
    return new SdmxResultSet( series );
  }

  private List<PortableTimeSeries> ts;
  private int totalRowNumber;
  private List<Object[]> rowSet;
  private int remainingRow;

  private SdmxResultSet(List<PortableTimeSeries> ts) {
    this.ts = ts;
    this.totalRowNumber = this.remainingRow = calculateRowTotalNumber();
    this.rowSet = createRowSet();
  }

  // // TODO: understanding if synchronization is needed
  public Object[] getRow() {
    if ( isEmpty() ) return null;
    int rowIdx = totalRowNumber - remainingRow--;
    return rowSet.get( rowIdx );
  }

  public int getTotalRowNumber() {
    return totalRowNumber;
  }

  public boolean isEmpty() {
    return !(remainingRow > 0);
  }

  private int calculateRowTotalNumber(){
    int n = 0;
    for (PortableTimeSeries s : ts ) {
      n += s.getObservations().size();
    }
    return n;
  }

  private List<Object[]> createRowSet() {
    List<Object[]> retval = new ArrayList<>();

    // the number of dimensions is constant across the ts List
    int dimensionNumber = ts.get(0).getDimensions().size();
    String token = "=";

    for ( PortableTimeSeries s : ts ) { // for each series
      for ( int i = 0; i < s.getObservations().size(); i++ ) { // ...and for each observation within a series
        Object[] r = new Object[ dimensionNumber + 2]; // add 2 fields for time and value
        r[ 0 ] = s.getTimeSlots().get( i );
        r[ 1 ] = s.getObservations().get( i );

        int j = 0;
        for ( String dimension : s.getDimensions() ) { // add dimension codes
          String code = dimension.split(token)[1];
          r[ j + 2 ] = code;
          j++;
        }
        retval.add( r );
      }
    }
    return retval;
  }
}
