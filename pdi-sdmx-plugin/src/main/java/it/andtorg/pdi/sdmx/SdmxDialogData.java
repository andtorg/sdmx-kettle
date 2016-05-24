package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.api.Dimension;
import it.bancaditalia.oss.sdmx.client.Provider;
import org.pentaho.di.i18n.BaseMessages;

import java.util.*;

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
  private Map<Dimension,String> currentFlowDimensionToCodes;
  private String activeDimensionId;
  private String sdmxQuery;

  public SdmxDialogData() {
    Comparator<Dimension> comparator = new Comparator<Dimension>() {
      @Override
      public int compare(Dimension o1, Dimension o2) {
        return o1.getPosition() - o2.getPosition();
      }
    };
    this.currentFlowDimensionToCodes = new TreeMap<>(comparator);
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

  public Map<Dimension,String> getCurrentFlowDimensionToCodes() {
    return currentFlowDimensionToCodes;
  }

  public void setCurrentFlowDimensions(List<Dimension> dimensions) {
//    this.currentFlowDimensionToCodes = currentFlowDimensionToCodes;
  }

  public Dimension findDimensionById( String id ) {
    if ( currentFlowDimensionToCodes == null ) {
      throw new IllegalStateException( BaseMessages.getString( PKG, "SdmxDialogData.NoDimensionsInFlowEx.Message" ) );
    }

    for ( Dimension d : currentFlowDimensionToCodes.keySet() ){
      if ( d.getId().equals( id ) ) {
        return d;
      }
    }
    throw new IllegalStateException( BaseMessages.getString( PKG, "SdmxDialogData.NoDimensionEx.Message" ) + " with id: " + id  );
  }

  /**
   * Get the dimension currently selected.
   * </p>
   * It is the string id of the dimension. It is useful to bind the codes
   * searched through the relevant tableview widget.
   * @return
   */
  public String getActiveDimensionId() {
    return activeDimensionId;
  }

  /**
   * Set the {@link Dimension} currently selected.
   * </p>
   * It is the string id of the dimension. It is useful to bind the codes
   * searched through the relevant tableview widget.
   * @param activeDimensionId
   */
  public void setActiveDimensionId(String activeDimensionId) {
    this.activeDimensionId = activeDimensionId;
  }

  @Deprecated
  public String getSelectedCodesByDimension(Dimension d ){
    return currentFlowDimensionToCodes.get( d );
  }

  /**
   * Refresh the Dimensions collection.
   * </p>
   *
   * @param dims
   */
  public void initializeFlowDimensions(List<Dimension> dims) {
    currentFlowDimensionToCodes.clear();
    for ( Dimension d : dims ) {
      currentFlowDimensionToCodes.put( d, "" );
    }
  }

  public void updateDimensionCodes( String dimensionId, String codes ) {
    for ( Dimension d : currentFlowDimensionToCodes.keySet() ) {
      if ( d.getId().equals( dimensionId ) ) {
        currentFlowDimensionToCodes.put( d, codes );
        break;
      }
    }
  }

  public String findCodesByDimensionId( String id ) {
    for ( Dimension d : currentFlowDimensionToCodes.keySet() ){
      if ( d.getId().equals( id ) ) return currentFlowDimensionToCodes.get( d );
    }
    throw new IllegalArgumentException(BaseMessages.getString( PKG, "SdmxDialogData.NoDimensionEx.Message" ) + " with id: " + id );
  }

  /**
   * It clears all existing dimensions before passing the parameter.
   * </p>
   * @param dimToCodes
   */
  public void loadDimensionToCodes( Map<Dimension, String> dimToCodes ) {
    currentFlowDimensionToCodes.clear();
    currentFlowDimensionToCodes.putAll( dimToCodes );
  }
}
