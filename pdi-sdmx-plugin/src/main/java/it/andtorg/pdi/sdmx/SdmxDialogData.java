package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.api.Dimension;
import it.bancaditalia.oss.sdmx.client.Provider;
import org.pentaho.di.i18n.BaseMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to store temporary data and behaviour when navigating Sdmx providers
 * </p>
 * It helps avoiding logic pollution in {@link SdmxStepDialog}
 *
 * @author Andrea Torre
 */
public class SdmxDialogData {
  private static Class<?> PKG = SdmxDialogData.class; // for i18n purposes

  private Provider chosenProvider;
  private Map<String, String> availableFlows;
  private Dataflow chosenFlow;
  private List<Dimension> currentFlowDimensions;
  private Dimension activeDimension;
  private String sdmxQuery;
  private Map<Dimension,String> dimSelectedCodes;

  public SdmxDialogData() {
    this.dimSelectedCodes = new HashMap<>();
  }

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

  public List<Dimension> getCurrentFlowDimensions() {
    return currentFlowDimensions;
  }

  public void setCurrentFlowDimensions(List<Dimension> currentFlowDimensions) {
    this.currentFlowDimensions = currentFlowDimensions;
  }

  public Dimension findDimensionByName( String name ) {
    if ( currentFlowDimensions == null ) {
      throw new IllegalStateException( BaseMessages.getString( PKG, "SdmxDialogData.NoDimensionsInFlowEx.Message" ) );
    }
    Dimension dim;

    for ( Dimension d : currentFlowDimensions ){
      if ( d.getName().equals( name ) ) {
        return d;
      }
    }
    throw new IllegalStateException( BaseMessages.getString( PKG, "SdmxDialogData.NoDimensionEx.Message" ) + " with name: " + name  );
  }

  public String getSelectedCodesByDimension( Dimension d ){
    return dimSelectedCodes.get( d );
  }

  /**
   * Refresh the Dimensions collection.
   * </p>
   * Additionally, for each dimension, the wildcard char for
   * codes is set: "." (i.e. All)
   *
   * @param dims
   */
  public void initializeFlowDimensions(List<Dimension> dims) {
    this.currentFlowDimensions = dims;
    this.dimSelectedCodes.clear();
    for ( Dimension d : dims ) {
      this.dimSelectedCodes.put( d, "." );
    }
  }
}
