package it.andtorg.pdi.sdmx;

import it.bancaditalia.oss.sdmx.client.Provider;
import it.bancaditalia.oss.sdmx.client.SDMXClientFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrea on 04/05/16.
 */
public enum SdmxProviderHandler {
  INSTANCE;
  List<Provider> providers;
  Map<String, Provider> nameToProviders;

  SdmxProviderHandler() {
    this.providers = new ArrayList<>(SDMXClientFactory.getProviders().values());
    nameToProviders = new HashMap<>();
    for (Provider p : providers){
      nameToProviders.put(p.getName(),p);
    }
  }

  public List<Provider> getProviders() {
    return providers;
  }

  public Provider getProviderByName(String name){
    return nameToProviders.get(name);
  }

}
