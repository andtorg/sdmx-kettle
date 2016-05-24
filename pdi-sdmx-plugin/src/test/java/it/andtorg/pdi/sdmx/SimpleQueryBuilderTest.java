package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.api.Dimension;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class SimpleQueryBuilderTest {
  private SimpleQueryBuilder qb;
  private Dataflow df;
  private Dimension dim_1, dim_2, dim_3, dim_4;
  private Map<Dimension, String> dimToCodes;

  @SuppressWarnings("Convert2Lambda")
  @Before
  public void setUp() throws Exception {
    qb = new SimpleQueryBuilder();
    df = new Dataflow();
    df.setId( "BKN_PUB" );

    dim_1 = new Dimension();
    dim_1.setId( "FOO" );
    dim_1.setPosition( 10 );

    dim_2 = new Dimension();
    dim_2.setId( "BAR" );
    dim_2.setPosition( 20 );

    dim_3 = new Dimension();
    dim_3.setId( "SID" );
    dim_3.setPosition( 30 );

    dim_4 = new Dimension();
    dim_4.setId( "TET" );
    dim_4.setPosition( 40 );

    Comparator<Dimension> comparator = new Comparator<Dimension>() {
      @Override
      public int compare(Dimension o1, Dimension o2) {
        return o1.getPosition() - o2.getPosition();
      }
    };

    dimToCodes = new TreeMap<>( comparator );

  }

  /*
   * Several test cases.
   * Todo: parameterize tests
   */
  @Test
  public void shouldReturnQueryStringWithAllDots() {
    dimToCodes.put( dim_3, "" );
    dimToCodes.put( dim_4, "" );
    dimToCodes.put( dim_1, "" );
    dimToCodes.put( dim_2, "" );

    String expectedQuery = "BKN_PUB/...";
    String actualQuery = qb.getSdmxQuery( df.getId(), dimToCodes );

    assertEquals( expectedQuery, actualQuery );
  }

  @Test
  public void shouldReturnQueryStringStartingWithDot() {
    dimToCodes.put( dim_3, "" );
    dimToCodes.put( dim_2, "AB+AC" );
    dimToCodes.put( dim_4, "DE" );
    dimToCodes.put( dim_1, "" );

    String expectedQuery = "BKN_PUB/.AB+AC..DE";
    String actualQuery = qb.getSdmxQuery( df.getId(), dimToCodes );

    assertEquals( expectedQuery, actualQuery );
  }

  @Test
  public void shouldReturnQueryStringNotStartingWithCodes() {
    dimToCodes.put( dim_1, "FF" );
    dimToCodes.put( dim_2, "AB+AC" );
    dimToCodes.put( dim_3, "" );
    dimToCodes.put( dim_4, "DE" );

    String expectedQuery = "BKN_PUB/FF.AB+AC..DE";
    String actualQuery = qb.getSdmxQuery( df.getId(), dimToCodes );

    assertEquals( expectedQuery, actualQuery );
  }

  @Test
  public void shouldReturnQueryStringEndingWithDot() {
    dimToCodes.put( dim_1, "FF" );
    dimToCodes.put( dim_2, "AB+AC" );
    dimToCodes.put( dim_3, "" );
    dimToCodes.put( dim_4, "" );

    String expectedQuery = "BKN_PUB/FF.AB+AC...";
    String actualQuery = qb.getSdmxQuery( df.getId(), dimToCodes );

    assertEquals( expectedQuery, actualQuery );
  }

}
