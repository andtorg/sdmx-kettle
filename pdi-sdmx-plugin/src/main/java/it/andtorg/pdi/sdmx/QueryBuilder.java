package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.api.Dimension;
import java.util.Map;

public interface QueryBuilder {

  String getSdmxQuery(String dataFlowId, Map<Dimension, String> dimToCodes);
}
