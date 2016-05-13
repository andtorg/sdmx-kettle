package it.andtorg.pdi.sdmx;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by andrea on 12/05/16.
 */
public class StepDialogControllerTest {
  private StepDialogController sdc;

  @Before
  public void setUp() throws Exception {
    sdc = new StepDialogController();
  }

  @Test
  public void shouldSeparateFlowIdFromDescription() throws Exception {
    String flow = "educ_ilang - Language learning";
    sdc.setChosenFlowFrom(flow);
    assertEquals( "Flow id mismatch","educ_ilang", sdc.getFlowId() );
    assertEquals( "Flow description mismatch","Language learning", sdc.getFlowDescription() );
  }
}
