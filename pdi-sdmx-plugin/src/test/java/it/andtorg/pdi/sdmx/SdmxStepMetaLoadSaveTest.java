/*
 * The MIT License
 *
 * Copyright (c) 2016 Andrea Torre, https://twitter.com/AndtorG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.andtorg.pdi.sdmx;


import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.client.Provider;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Andrea Torre
 * @since 09/06/16
 */
public class SdmxStepMetaLoadSaveTest {

  private LoadSaveTester tester;

  @Before
  public void setUp() throws Exception {

    List<String> attributes = Arrays.asList( "provider", "dataflow", "sdmxQuery"  ); //sdmxquery

    Map<String,String> getters = new HashMap<>();
    getters.put( "provider", "getProvider" );
    getters.put( "dataflow", "getDataflow");
    getters.put( "sdmxQuery", "getSdmxQuery");

    Map<String,String> setters = new HashMap<>();
    setters.put( "provider", "setProvider" );

    Map<String, FieldLoadSaveValidator<?>> attributeValidators = new HashMap<>( );
    Map<String, FieldLoadSaveValidator<?>> typeValidators = new HashMap<>(  );

//    attributeValidators.put( "provider", new ProviderValidator()  );

//    FieldLoadSaveValidator<String[]> stringArrayLoadSaveValidator = new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 25 );

//    typeValidators.put( SdmxInputField[].class.getCanonicalName(), new ArrayLoadSaveValidator<SdmxInputField>( new SdmxInputFieldValidator() ) );
//    typeValidators.put( Provider[].class.getCanonicalName(), new ArrayLoadSaveValidator<Provider>( new ProviderValidator(), 10 ) );
    typeValidators.put( Provider.class.getCanonicalName(),  new ProviderValidator() );
    typeValidators.put( Dataflow.class.getCanonicalName(),  new DataflowValidator() );

    tester = new LoadSaveTester( SdmxStepMeta.class, attributes, getters, setters, attributeValidators,
        typeValidators );
  }

  @Test
  public void testXMLSerialization() throws KettleException{
    tester.testXmlRoundTrip();
  }

  @Test
  public void testRepositorySerialization() throws KettleException {
    tester.testRepoRoundTrip();
  }


  public class SdmxInputFieldValidator implements FieldLoadSaveValidator<SdmxInputField> {

    @Override
    public SdmxInputField getTestObject() {
      return new SdmxInputField( UUID.randomUUID().toString(), new Random().nextInt() );
    }

    @Override
    public boolean validateTestObject(SdmxInputField testObject, Object actual) {
      if ( !( actual instanceof SdmxInputField ) ) {
        return false;
      }

      SdmxInputField another = ( SdmxInputField ) actual;
      return new EqualsBuilder()
          .append( testObject.getName(), another.getName() )
          .append( testObject.getLength(), another.getLength() )
          .isEquals();
    }
  }

  public class ProviderValidator implements FieldLoadSaveValidator<Provider>{

    @Override
    public Provider getTestObject() {
      return SdmxProviderHandler.INSTANCE.getProviders().get(0);
    }

    @Override
    public boolean validateTestObject(Provider testProvider, Object actualProvider) {
      if ( !( actualProvider instanceof Provider ) ) {
        return false;
      }
      Provider anotherProvider = ( Provider ) actualProvider;
      return new EqualsBuilder()
          .append( testProvider.getName() , anotherProvider.getName() )
          .append( testProvider.getDescription(), anotherProvider.getDescription() )
          .isEquals();
    }
  }

  public class DataflowValidator implements FieldLoadSaveValidator<Dataflow> {

    @Override
    public Dataflow getTestObject() {
      Dataflow df = new Dataflow();
      df.setId( UUID.randomUUID().toString() );
      df.setName( UUID.randomUUID().toString() );
      return df;
    }

    @Override
    public boolean validateTestObject(Dataflow testDataflow, Object actualDataflow) {
      if ( !( actualDataflow instanceof Dataflow ) ) {
        return false;
      }

      Dataflow anotherDataflow = ( Dataflow ) actualDataflow;
      return new EqualsBuilder()
          .append( testDataflow.getId(), anotherDataflow.getId() )
          .append( testDataflow.getName(), anotherDataflow.getName() )
          .isEquals();
    }
  }
}
