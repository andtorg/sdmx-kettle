package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dimension;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by andrea on 23/05/16.
 */
public class SdmxStepMetaTest {
  private SdmxStepMeta meta;

  @Before
  public void setUp() throws Exception {
    meta = new SdmxStepMeta();
  }

  @Test
  public void shouldInitializeADimToCodesMapInTheConstructor() {
    assertEquals( "Map should be void when meta object is instantiated", 0, meta.getDimensionToCodes().size() );
  }

  @Test
  public void shouldUpdateCodeInDimToCodeMap() {
    Dimension d = new Dimension();
    d.setId("FOO");
    String code = "AA+BB";

    meta.updateCodesByDimension( d, code );
    assertEquals( code, meta.getDimensionToCodes().get( d ));

  }

}
