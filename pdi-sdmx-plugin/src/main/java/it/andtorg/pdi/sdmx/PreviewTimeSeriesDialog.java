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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import java.util.List;

/**
 * Displays the available Sdmx timeseries in a {@link org.pentaho.di.ui.core.widget.TableView} dialog
 *
 * @author andrea torre
 */

public class PreviewTimeSeriesDialog {
  private static Class<?> PKG = PreviewTimeSeriesDialog.class;

  private Shell shell;
  private Shell parentShell;
  private VariableSpace variables;

  private String title, message;
  private List<List<String>> timeSeries;

  private RowMetaInterface rowMeta;
  private PropsUI props;

  private TableView wSeriesTable;

  @SuppressWarnings("FieldCanBeLocal")
  private FormData fdSeriesTable;

  private int style = SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN;

  public PreviewTimeSeriesDialog(Shell parent, int style, RowMetaInterface rowMeta, VariableSpace space,
                                 List<List<String>> timeSeries ) {
    this.parentShell = parent;
    this.rowMeta = rowMeta;
    this.timeSeries = timeSeries;
    this.style = ( style != SWT.None ) ? style : this.style;
    this.variables = space;
    props = PropsUI.getInstance();

    title = null;
    message = null;
  }

  public void open() {

    shell = new Shell( parentShell, style );
    props.setLook( shell );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    if ( title == null ) {
      title = BaseMessages.getString( PKG, "Sdmx.PreviewTimeSeriesDialog.Title" ) + " " + timeSeries.size();
    }
    if ( message == null ) {
      message = BaseMessages.getString( PKG, "Sdmx.PreviewTimeSeriesDialog.Header" );
    }

    shell.setLayout( formLayout );
    shell.setText( title );

    addTableView();

    shell.open();
    while (!shell.isDisposed()) {
      if (!shell.getDisplay().readAndDispatch())
        shell.getDisplay().sleep();
    }
  }

  private void addTableView() {
    int margin = Const.MARGIN;
    ColumnInfo[] colinf = new ColumnInfo[rowMeta.size()];
    for ( int i = 0; i < rowMeta.size(); i++ ) {
      ValueMetaInterface v = rowMeta.getValueMeta( i );
      colinf[i] = new ColumnInfo( v.getName(), ColumnInfo.COLUMN_TYPE_TEXT, v.isNumeric() );
      colinf[i].setToolTip( v.toStringMeta() );
      colinf[i].setValueMeta( v );
    }

    wSeriesTable =
        new TableView( variables, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, 0, null, props );
    wSeriesTable.setShowingBlueNullValues( true ); //// TODO: 25/05/16 wat?

    fdSeriesTable = new FormData();
    fdSeriesTable.left = new FormAttachment( 0, 0 );
    fdSeriesTable.top = new FormAttachment( 0, margin );
    fdSeriesTable.right = new FormAttachment( 100, 0 );
    fdSeriesTable.bottom = new FormAttachment( 100, -50 );
    wSeriesTable.setLayoutData( fdSeriesTable );

    fillTableView();
  }

  // add a row in the table for each available series
  private void fillTableView(){
    for ( int i = 0; i < timeSeries.size(); i++ ) {
      TableItem item;
      if ( i == 0 ) {
        item = wSeriesTable.table.getItem( i );
      } else {
        item = new TableItem( wSeriesTable.table, SWT.NONE );
      }
      List<String> ts = timeSeries.get( i );
      for ( int c = 0; c < rowMeta.size(); c++ ) {

        item.setText( c + 1 , ts.get( c ) );
      }
    }
    wSeriesTable.removeEmptyRows();
    wSeriesTable.setRowNums();
    wSeriesTable.optWidth( true );
  }
}
