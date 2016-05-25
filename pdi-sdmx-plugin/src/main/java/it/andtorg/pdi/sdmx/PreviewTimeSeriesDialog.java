package it.andtorg.pdi.sdmx;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

/**
 * Displays the available Sdmx timeseries in a {@link org.pentaho.di.ui.core.widget.TableView}
 *
 * @author andrea torre
 */

public class PreviewTimeSeriesDialog {
  private static Class<?> PKG = PreviewTimeSeriesDialog.class;

  private Shell shell;
  private Shell parentShell;
  private VariableSpace variables;

  private String title, message;
  private String stepName; //todo needed?

  private RowMetaInterface rowMeta;
  private PropsUI props;

  private TableView wSeriesTable;
  private FormData fdSeriesTable;

  private int style = SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN;

  public PreviewTimeSeriesDialog(Shell parent, int style, RowMetaInterface rowMeta, VariableSpace space ) {
    this.parentShell = parent;
    this.rowMeta = rowMeta;
    this.style = ( style != SWT.None ) ? style : this.style;
    this.variables = space;
    props = PropsUI.getInstance();

    title = null;
    message = null;
  }

  public void setTitleMessage( String title, String message, String stepName ) {
    this.title = title;
    this.message = message;
    this.stepName = stepName;
  }

  public void open() {

    shell = new Shell( parentShell, style );
    props.setLook( shell );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    if ( title == null ) {
      title = BaseMessages.getString( PKG, "Sdmx.PreviewTimeSeriesDialog.Title" );
    }
    if ( message == null ) {
      message = BaseMessages.getString( PKG, "Sdmx.PreviewTimeSeriesDialog.Header", stepName );
    }

    // we don't need a rowbuffer, but a timeseries buffer can help. // TODO: 24/05/16 find out how to implement
//    if ( buffer != null ) {
//      message += " " + BaseMessages.getString( PKG, "PreviewRowsDialog.NrRows", "" + buffer.size() );
//    }

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
  }

}
