package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dimension;

import java.util.Map;

/**
 * Creates a query string to be passed to {@link it.bancaditalia.oss.sdmx.client.SdmxClientHandler} to obtain timeseries
 *
 */
public class SimpleQueryBuilder implements QueryBuilder {

  @Override
  public String getSdmxQuery( String dataFlowId, Map<Dimension, String> dimToCodes ) {
    StringBuilder query = new StringBuilder( dataFlowId + "/" );
    int dimNumber = dimToCodes.size();

    int i = 0;
    for ( Dimension d : dimToCodes.keySet() ) {
      i++;
      if ( i == dimNumber ) {
        query.append( dimToCodes.get( d ) );
      } else {
        query.append( dimToCodes.get( d ) + "." );
      }
    }
    return query.toString();
  }
}
