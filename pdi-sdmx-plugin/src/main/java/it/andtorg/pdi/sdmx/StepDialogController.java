package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.client.Provider;

import java.util.Map;

/**
 * A class to store temporary data and behaviour
 * </p>
 * It helps avoiding logic pollution in {@link SdmxStepDialog}
 *
 * @author Andrea Torre
 */
public class StepDialogController {
  private Provider chosenProvider;
  private Map<String, String> availableFlows;
  private Dataflow chosenFlow;

  public Provider getChosenProvider() {
    return chosenProvider;
  }

  public void setChosenProvider(Provider chosenProvider) {
    this.chosenProvider = chosenProvider;
  }

  public Map<String, String> getAvailableFlows() {
    return availableFlows;
  }

  public void setAvailableFlows(Map<String, String> availableFlows) {
    this.availableFlows = availableFlows;
  }

  public Dataflow getChosenFlow() {
    return chosenFlow;
  }

  public void setChosenFlow(Dataflow chosenFlow) {
    this.chosenFlow = chosenFlow;
  }

  public void setChosenFlowFrom(String flow) {
    chosenFlow = new Dataflow();
    int hyphenPos = flow.indexOf("-");
    chosenFlow.setId( flow.substring( 0, hyphenPos-1 ) );
    chosenFlow.setName( flow.substring( hyphenPos + 2 ) );
  }

  public String getFlowId() {
    return chosenFlow.getId();
  }

  public String getFlowDescription() {
    return chosenFlow.getDescription();
  }
}
