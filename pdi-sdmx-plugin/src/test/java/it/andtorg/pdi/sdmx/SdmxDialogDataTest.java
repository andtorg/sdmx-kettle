package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dimension;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by andrea on 12/05/16.
 */
public class SdmxDialogDataTest {
  private SdmxDialogData sdc;

  @Before
  public void setUp() throws Exception {
    sdc = new SdmxDialogData();
  }

  @Test
  public void shouldSeparateFlowIdFromDescription() throws Exception {
    String flow = "educ_ilang - Language learning";
    sdc.setChosenFlowFrom(flow);
    assertEquals( "Flow id mismatch","educ_ilang", sdc.getFlowId() );
    assertEquals( "Flow description mismatch","Language learning", sdc.getFlowDescription() );
  }

  @Test
  public void shouldReturnDimensionGivenItsName() throws Exception {
    Dimension one = new Dimension();
    one.setName("dim_1");

    Dimension two = new Dimension();
    two.setName( "dim_2" );

    List<Dimension> dims = new ArrayList<>();
    dims.add( one );
    dims.add( two );

    sdc.setCurrentFlowDimensions( dims );

    assertEquals( one, sdc.findDimensionByName( "dim_1" ) );
  }

  @Test
  public void shouldInitializeDimensionsWithDotCodes() {
    Dimension one = new Dimension();
    one.setName("dim_1");

    Dimension two = new Dimension();
    two.setName( "dim_2" );

    List<Dimension> dims = new ArrayList<>();
    dims.add( one );
    dims.add( two );

    sdc.initializeFlowDimensions( dims );

    String expectedCode1 = ".";
    assertEquals( expectedCode1, sdc.getSelectedCodesByDimension( one ) );

    String expectedCode2 = ".";
    assertEquals( expectedCode2, sdc.getSelectedCodesByDimension( two ) );

  }
}
