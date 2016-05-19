package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dimension;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author andrea torre
 */
public class SdmxDialogDataTest {
  private SdmxDialogData sdc;
  private List<Dimension> dims;
  private Dimension dim1;
  private Dimension dim2;

  @Before
  public void setUp() throws Exception {
    sdc = new SdmxDialogData();

    dim1 = new Dimension();
    dim1.setId( "dim_1" );

    dim2 = new Dimension();
    dim2.setId( "dim_2" );

    dims = new ArrayList<>();
    dims.add( dim1);
    dims.add( dim2 );
  }

  @Test
  public void shouldSeparateFlowIdFromDescription() throws Exception {
    String flow = "educ_ilang - Language learning";
    sdc.setChosenFlowFrom(flow);
    assertEquals( "Flow id mismatch","educ_ilang", sdc.getFlowId() );
    assertEquals( "Flow description mismatch","Language learning", sdc.getFlowDescription() );
  }

  @Test
  public void shouldReturnDimensionGivenItsId() throws Exception {
      assertEquals( dim1, sdc.findDimensionById( "dim_1" ) );
  }

  @Test
  public void shouldInitializeDimensionsWithDotCodes() {
    sdc.initializeFlowDimensions( dims );

    String expectedCode1 = ".";
    assertEquals( expectedCode1, sdc.getSelectedCodesByDimension( dim1 ) );

    String expectedCode2 = ".";
    assertEquals( expectedCode2, sdc.getSelectedCodesByDimension( dim2 ) );
  }

  @Test
  public void shouldReturnDimensionsSortedByPosition() {
    dim1.setPosition( 90 );
    dim2.setPosition( 42 );

    // add a third dimension for better consistency
    Dimension dim3 = new Dimension();
    dim3.setId( "dim_3" );
    dim3.setPosition( 15 );
    dims.add( dim3 );

    sdc.initializeFlowDimensions( dims );

    Set<Dimension> dimKeys = sdc.getCurrentFlowDimensionToCodes().keySet();
  }

  @Test
  public void shouldUpdateCodesByDimension() {
    sdc.initializeFlowDimensions( dims );
    String codes = "AC+AB";
    sdc.updateDimensionCodes( "dim_1", codes );

    assertEquals( codes, sdc.findCodesByDimensionId( "dim_1" ) );
  }
}
