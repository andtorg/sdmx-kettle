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

import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;

/**
 * Describes a single dimension field in a Sdmx flow.
 *
 * @author Andrea Torre
 * @since 30/05/16
 */
public class SdmxInputField implements Cloneable {
  private String name;
  private int type;
  private String format;
  private int length;
  private int precision;
  private String currencySymbol;
  private String decimalSymbol;
  private String groupSymbol;
  private int trimType;
  private boolean repeat;

  // TODO: 30/05/16 is a position parameter needed? It's in text input but not in excel input

  public SdmxInputField(String fieldName, int length) {
    this.name = fieldName;
    this.length = length;
    this.type = ValueMetaInterface.TYPE_STRING;
    this.format = "";
    this.precision = -1;
    this.currencySymbol = "";
    this.decimalSymbol = "";
    this.groupSymbol = "";
    this.trimType = ValueMetaInterface.TRIM_TYPE_NONE;
    this.repeat = false;
  }

  public SdmxInputField() {
    this( null,-1 );
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      return null;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String fieldName) {
    this.name = fieldName;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setType( String typeDesc ) {
    this.type = ValueMetaBase.getType( typeDesc );
  }

  public String getTypeDesc() {
    return ValueMetaBase.getTypeDesc( type );
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getGroupSymbol() {
    return groupSymbol;
  }

  public void setGroupSymbol(String groupSymbol) {
    this.groupSymbol = groupSymbol;
  }

  public String getDecimalSymbol() {
    return decimalSymbol;
  }

  public void setDecimalSymbol(String decimalSymbol) {
    this.decimalSymbol = decimalSymbol;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }

  public boolean isRepeated() {
    return repeat;
  }

  public void setRepeated( boolean repeat ) {
    this.repeat = repeat;
  }

  public void flipRepeated() {
    repeat = !repeat;
  }

  public int getTrimType() {
    return trimType;
  }

  public String getTrimTypeCode() {
    return ValueMetaBase.getTrimTypeCode( trimType );
  }

  public String getTrimTypeDesc() {
    return ValueMetaBase.getTrimTypeDesc( trimType );
  }

  public void setTrimType(int trimType) {
    this.trimType = trimType;
  }

  public String toString() {
    return name + ":" + getTypeDesc() + "(" + length + "," + precision + ")";
  }
}
