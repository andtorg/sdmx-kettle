package it.andtorg.pdi.sdmx;


import it.bancaditalia.oss.sdmx.api.PortableTimeSeries;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Andrea Torre
 * @since 01/06/16
 */
public class SdmxResultSetTest {
  private List<PortableTimeSeries> ts;
  private SdmxResultSet rs;

  @Before
  public void setUp() throws Exception {
    ts = new ArrayList<>();
    Hashtable<String, String> dumb_attributes = new Hashtable<>();

    PortableTimeSeries s1 = new PortableTimeSeries();

    s1.setDimensions( Arrays.asList( "dim_1=SICILY", "dim_2=RED", "dim_3=MEDITERRANEAN" ) );
    s1.addObservation( "100", "2010-01", dumb_attributes );
    s1.addObservation( "120", "2010-02", dumb_attributes );
    s1.addObservation( "100", "2010-03", dumb_attributes );

    PortableTimeSeries s2 = new PortableTimeSeries();
    s2.setDimensions( Arrays.asList( "dim_1=IRELAND", "dim_2=GREEN", "dim_3=ATLANTIC" ) );
    s2.addObservation( "300", "2011-01", dumb_attributes );
    s2.addObservation( "350", "2011-02", dumb_attributes );
    s2.addObservation( "380", "2011-03", dumb_attributes );

    ts.add( s1 );
    ts.add( s2 );

    rs = SdmxResultSet.getResultSet( ts );
  }

  @Test
  public void shouldReturnFirstRow() throws Exception {
    Object[] expectedRow = new Object[]{ "2010-01", 100.0, "SICILY", "RED", "MEDITERRANEAN" };
    Object[] actualRow = rs.getRow();

    assertArrayEquals( expectedRow, actualRow );
  }

  @Test
  public void shouldReturnRowAfterNIteration() {
    rs.getRow();
    rs.getRow();
    rs.getRow();
    Object[] expectedRow = new Object[]{ "2011-01", 300.0, "IRELAND", "GREEN", "ATLANTIC" };
    Object[] actualRow = rs.getRow();

    assertArrayEquals( expectedRow, actualRow );
  }

  @Test
  public void shouldReturnNullIfRowAreFinished() {
    for (int i = 0; i < 6; i++ ){
      rs.getRow();
    }

    assertNull(rs.getRow());
  }

  @Test
  public void shouldReturnTheTotalNumberOfRows() {
    assertEquals(6, rs.getTotalRowNumber());
  }

  @Test
  public void shouldTellIfResultSetIsEmpty() {

    assertTrue( !rs.isEmpty() );

    for (int i = 0; i < 6; i++ ){
      rs.getRow();
    }

    assertTrue( rs.isEmpty() );
  }
}
