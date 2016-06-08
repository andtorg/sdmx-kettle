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
import it.bancaditalia.oss.sdmx.api.Dimension;
import it.bancaditalia.oss.sdmx.client.Provider;
import it.bancaditalia.oss.sdmx.client.SdmxClientHandler;
import it.bancaditalia.oss.sdmx.helper.ProviderComparator;
import it.bancaditalia.oss.sdmx.util.SdmxException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.ShowMessageDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;

import java.util.*;
import java.util.List;

/**
 * The plugin GUI plugin class.
 *
 * @author Andrea Torre
 * @since 01/06/16
 */
@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})
public class SdmxStepDialog extends BaseStepDialog implements StepDialogInterface {
	private static Class<?> PKG = SdmxStepMeta.class; // for i18n purposes

	private SdmxStepMeta meta;
  private SdmxProviderHandler providerHandler;
  private SdmxDialogData sdmxDialogData;
  private ModifyListener lsMod;

  private boolean gotProviders;

  private CTabFolder wTabFolder;
  private FormData fdTabFolder;

  private CTabItem wSettingTab;
  private ScrolledComposite wSettingsSComp;
  private Composite wSettingComp;
  private FormData fdSettingComp;

  private CTabItem wFieldsTab;
  private ScrolledComposite wFieldsSComp;
  private Composite wFieldsComp;
  private FormData fdFieldsComp;

  private Label wlProvider;
  private FormData fdlProvider;
  private CCombo wProvider;
  private FormData fdProvider;

  private Label wlFlow;
  private Text wFlow;
  private Button wbBrowseFlows;
  private FormData fdlFlow, fdFlows, fdBrowseFlows;

  private Button wbDimensions;
  private TableView wDimensionList;
  private FormData fdDimensions, fdDimensionList;

  private TableView wCodeList;
  private FormData fdCodeList;

  private Button wbCodes;
  private FormData fdCodes;

  private Button wbTimeSeries;
  private FormData fdTimeSeries;

  private TableView wFields;
  private FormData fdFields;

  private Display display;
  private int middle, margin;

  /**
	 * The constructor should simply invoke super() and save the incoming meta
	 * object to a local variable, so it can conveniently read and write settings
	 * from/to it.
	 *
	 * @param parent 	the SWT shell to open the dialog in
	 * @param in		the meta object holding the step's settings
	 * @param transMeta	transformation description
	 * @param sname		the step name
	 */
	public SdmxStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (SdmxStepMeta) in;
    providerHandler = SdmxProviderHandler.INSTANCE;
    sdmxDialogData = new SdmxDialogData();
	}

	/**
	 * This method is called by Spoon when the user opens the settings dialog of the step.
	 * It should open the dialog and return only once the dialog has been closed by the user.
	 * 
	 * If the user confirms the dialog, the meta object (passed in the constructor) must
	 * be updated to reflect the new step settings. The changed flag of the meta object must 
	 * reflect whether the step configuration was changed by the dialog.
	 * 
	 * If the user cancels the dialog, the meta object must not be updated, and its changed flag
	 * must remain unaltered.
	 * 
	 * The open() method must return the name of the step after the user has confirmed the dialog,
	 * or null if the user cancelled the dialog.
	 */
	public String open() {

		// store some convenient SWT variables 
		Shell parent = getParent();
		display = parent.getDisplay();

		// SWT code for preparing the dialog
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);
		
		// Save the value of the changed flag on the meta object. If the user cancels
		// the dialog, it will be restored to this saved value.
		// The "changed" variable is inherited from BaseStepDialog
		changed = meta.hasChanged();
		
		// The ModifyListener used on all controls. It will update the meta object to 
		// indicate that changes are being made.
		lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				meta.setChanged();
			}
		};

		// ------------------------------------------------------- //
		// SWT code for building the actual settings dialog        //
		// ------------------------------------------------------- //
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "Sdmx.Shell.Title"));

		middle = props.getMiddlePct();
		margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName")); 
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);


		wTabFolder = new CTabFolder( shell, SWT.BORDER );
		props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );
		wTabFolder.setSimple( false );

    addSettingTab();
    addFieldsTab();

		fdTabFolder = new FormData();
		fdTabFolder.left = new FormAttachment( 0, 0 );
		fdTabFolder.top = new FormAttachment( wStepname, margin );
		fdTabFolder.right = new FormAttachment( 100, 0 );
		fdTabFolder.bottom = new FormAttachment( 100, -50 );
		wTabFolder.setLayoutData( fdTabFolder );

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); 
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); 

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, wTabFolder);


//		 Add listeners for cancel and OK
		lsCancel = new Listener() {
			public void handleEvent(Event e) {cancel();}
		};
//		lsCancel = event -> cancel();

		lsOK = new Listener() {
			public void handleEvent(Event e) {ok();}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);


		// default listener (for hitting "enter")
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {ok();}
		};
		wStepname.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window and cancel the dialog properly
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {cancel();}
		});


    wGet.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent selectionEvent) {
        getFields();
      }
    });


    wTabFolder.setSelection( 0 );
		// Set/Restore the dialog size based on last position on screen
		// The setSize() method is inherited from BaseStepDialog
		setSize();

		// populate the dialog with the values from the meta object
    // TODO: 13/05/16 it does the same things as getData(). Delete one of them
//		populateDialog();
		
		// restore the changed flag to original value, as the modify listeners fire during dialog population 
		meta.setChanged(changed);


    getData( meta );

		// open dialog and enter event loop
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}


		// at this point the dialog has closed, so either ok() or cancel() have been executed
		// The "stepname" variable is inherited from BaseStepDialog
		return stepname;
	}

  /**
	 * This helper method puts the step configuration stored in the meta object
	 * and puts it into the dialog controls.
	 */
	private void populateDialog() {
		wStepname.selectAll(); //todo whats this for?
	}

	/**
	 * Called when the user cancels the dialog.  
	 */
	private void cancel() {
		// The "stepname" variable will be the return value for the open() method. 
		// Setting to null to indicate that dialog was cancelled.
		stepname = null;
		// Restoring original "changed" flag on the met aobject
		meta.setChanged(changed);
		// close the SWT dialog window
		dispose();
	}
	
	/**
	 * Called when the user confirms the dialog
	 */
	private void ok() {
    if ( Const.isEmpty( wStepname.getText() ) ) {
      return;
    }

		// The "stepname" variable will be the return value for the open() method. 
		// Setting to step name from the dialog control
		stepname = wStepname.getText(); 
		// Setting the  settings to the meta object

		// close the SWT dialog window

    saveMeta( meta );

		dispose();
	}

  private void addSettingTab() {
    // ////////////////////////
    // START OF SETTING TAB ///
    // ////////////////////////

    wSettingTab = new CTabItem( wTabFolder, SWT.NONE );
    wSettingTab.setText( BaseMessages.getString( PKG, "SdmxDialog.SettingTab.TabTitle" ) );

    wSettingsSComp = new ScrolledComposite( wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL );
    wSettingsSComp.setLayout( new FillLayout() );

    wSettingComp = new Composite( wSettingsSComp, SWT.NONE );
    props.setLook(wSettingComp);

    FormLayout settingLayout = new FormLayout();
    settingLayout.marginWidth = 3;
    settingLayout.marginHeight = 3;

    wSettingComp.setLayout( settingLayout );

    addProviderLabel();
    addProviderCombo();

    addFlowLabel();
    addFlowTextInput();
    addFlowBrowsingButton();

    addDimensionTableView();
    addDimensionButton();

    addCodeListTableView();
    addCodeButton();

    addViewTimeSeriesButton();

    wSettingComp.pack();
    Rectangle bounds = wSettingComp.getBounds();

    wSettingsSComp.setContent( wSettingComp );
    wSettingsSComp.setExpandHorizontal( true );
    wSettingsSComp.setExpandVertical( true );
    wSettingsSComp.setMinWidth( bounds.width );
    wSettingsSComp.setMinHeight( bounds.height );

    fdSettingComp = new FormData();
    fdSettingComp.left = new FormAttachment( 0, 0 );
    fdSettingComp.top = new FormAttachment( 0, 0 );
    fdSettingComp.right = new FormAttachment( 100, 0 );
    fdSettingComp.bottom = new FormAttachment( 100, 0 );
    wSettingComp.setLayoutData( fdSettingComp );

    wSettingTab.setControl( wSettingsSComp );
  }

  private void addFieldsTab() {
    // ////////////////////////
    // START OF FIELD TAB ///
    // ////////////////////////
    wFieldsTab = new CTabItem( wTabFolder, SWT.NONE );
    wFieldsTab.setText( BaseMessages.getString( PKG, "SdmxDialog.FieldTab.TabTitle" ) );

    FormLayout fieldsLayout = new FormLayout();
    fieldsLayout.marginWidth = Const.FORM_MARGIN;
    fieldsLayout.marginHeight = Const.FORM_MARGIN;

    wFieldsComp = new Composite(wTabFolder, SWT.NONE );
    wFieldsComp.setLayout( fieldsLayout );
    props.setLook(wFieldsComp);

    wGet = new Button( wFieldsComp, SWT.PUSH );
    wGet.setText( BaseMessages.getString( PKG, "System.Button.GetFields" ) );
    fdGet = new FormData();
    fdGet.left = new FormAttachment( 50, 0 );
    fdGet.bottom = new FormAttachment( 100, 0 );
    wGet.setLayoutData( fdGet );

    final int fieldsRows = meta.getInputFields().length;

    // add code here
    ColumnInfo[] colinf =
        new ColumnInfo[] {
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.NameColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.TypeColumn.Column" ), ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMetaBase.getTypes(), true ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.FormatColumn.Column" ),ColumnInfo.COLUMN_TYPE_FORMAT, 2 ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.LengthColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.PrecisionColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.CurrencyColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.DecimalColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.GroupColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
//            new ColumnInfo( BaseMessages.getString( PKG, "SdmxtDialog.NullIfColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
//            new ColumnInfo( BaseMessages.getString( PKG, "SdmxInputDialog.IfNullColumn.Column" ), ColumnInfo.COLUMN_TYPE_TEXT, false ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.TrimTypeColumn.Column" ), ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMetaBase.trimTypeDesc, true ),
            new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.RepeatColumn.Column" ), ColumnInfo.COLUMN_TYPE_CCOMBO,
                new String[] { BaseMessages.getString( PKG, "System.Combo.Yes" ), BaseMessages.getString( PKG, "System.Combo.No" ) }, true ) };

    wFields = new TableView( transMeta, wFieldsComp, SWT.FULL_SELECTION | SWT.MULTI, colinf, fieldsRows, lsMod, props );

    fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( 0, 0 );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( wGet, -margin );
    wFields.setLayoutData( fdFields );

    fdFieldsComp = new FormData();
    fdFieldsComp.left = new FormAttachment( 0, 0 );
    fdFieldsComp.top = new FormAttachment( 0, 0 );
    fdFieldsComp.right = new FormAttachment( 100, 0 );
    fdFieldsComp.bottom = new FormAttachment( 100, 0 );

    wFieldsComp.setLayoutData(fdFieldsComp);
    wFieldsComp.layout();
    wFieldsTab.setControl( wFieldsComp );
  }

  private void setProviders(){

    // Providers list of the text file:
    if ( !gotProviders ) { // TODO: what is this for?
      gotProviders = true;

      wProvider.removeAll();

      List<Provider> providers = providerHandler.getProviders();
      Collections.sort(providers, new ProviderComparator());
      wProvider.add(""); // add a blank line for deselection
      for (Iterator<Provider> iterator = providers.iterator(); iterator.hasNext();) {
        Provider p = iterator.next();
        String provider = p.getName() + ": " + p.getDescription();
        wProvider.add(provider);
      }
    }
  }

  private void getData ( SdmxStepMeta meta ){

    if ( meta.getProvider() != null ) {
      sdmxDialogData.setChosenProvider( meta.getProvider() );
      wProvider.setText( meta.getProvider().getName() + ": " + meta.getProvider().getDescription());
    }

    if ( meta.getDataflow() != null ) {
      Dataflow df = meta.getDataflow();
      sdmxDialogData.setChosenFlow( df );
      if ( df.getId() != null ) wFlow.setText( df.getId() + " - " + df.getDescription() );
    }

    Map<Dimension, String> dimToCodes = meta.getDimensionToCodes();
    sdmxDialogData.loadDimensionToCodes( meta.getDimensionToCodes() );


      for ( Dimension d : dimToCodes.keySet() ) {
        wDimensionList.add( d.getId(), dimToCodes.get( d ) );
      }
      wDimensionList.removeEmptyRows();
      wDimensionList.setRowNums();
      wDimensionList.optWidth( true );

    for ( int i = 0; i < meta.getInputFields().length; i++ ) {
      TableItem item = wFields.table.getItem( i );
      String fieldName = meta.getInputFields()[i].getName();
      String type = meta.getInputFields()[i].getTypeDesc();
      String length = "" + meta.getInputFields()[i].getLength();
      String precision = "" + meta.getInputFields()[i].getPrecision();
      String trim = meta.getInputFields()[i].getTrimTypeDesc();
      String repeat =
          meta.getInputFields()[i].isRepeated() ? BaseMessages.getString( PKG, "System.Combo.Yes" ) : BaseMessages
              .getString( PKG, "System.Combo.No" );
      String format = meta.getInputFields()[i].getFormat();
      String currency = meta.getInputFields()[i].getCurrencySymbol();
      String decimal = meta.getInputFields()[i].getDecimalSymbol();
      String grouping = meta.getInputFields()[i].getGroupSymbol();

      if ( fieldName != null ) item.setText( 1, fieldName );

      if ( type != null ) item.setText( 2, type );

      if ( format != null ) item.setText( 3, format );

      if ( length != null ) item.setText( 4, length );

      if ( precision != null ) item.setText( 5, precision );

      if ( currency != null ) item.setText( 6, currency );

      if ( decimal != null ) item.setText( 7, decimal );

      if ( grouping != null ) item.setText( 8, grouping );

      if ( trim != null ) item.setText( 9, trim );

      if ( repeat != null ) item.setText( 10, repeat );
    }

    wFields.removeEmptyRows();
    wFields.setRowNums();
    wFields.optWidth( true );
  }

  private void saveMeta( SdmxStepMeta stepMeta ) {
    stepname = wStepname.getText(); // return value

    stepMeta.setProvider( sdmxDialogData.getChosenProvider() );
    stepMeta.setDataflow( sdmxDialogData.getChosenFlow() );
    stepMeta.wipeDimensions();
    Map<Dimension, String> dimToSave = sdmxDialogData.getCurrentFlowDimensionToCodes();
    for ( Dimension d : dimToSave.keySet() ) {
      stepMeta.updateCodesByDimension( d, dimToSave.get( d ) );
    }
    stepMeta.setSdmxQuery( sdmxDialogData.getSdmxQuery() );

    int nrOfFields = wFields.nrNonEmpty();
    stepMeta.allocateFields( nrOfFields );

    for ( int i = 0; i < nrOfFields; i++  ) {
      TableItem item = wFields.getNonEmpty( i );
      meta.getInputFields()[i] = new SdmxInputField();

      meta.getInputFields()[i].setName( item.getText( 1 ) );
      meta.getInputFields()[i].setType( ValueMetaBase.getType( item.getText( 2 ) ) );
      meta.getInputFields()[i].setFormat( item.getText( 3 ) );

      String fLength = item.getText( 4 );
      meta.getInputFields()[i].setLength( Const.toInt( fLength, -1 ) );

      String sPrec = item.getText( 5 );
      meta.getInputFields()[i].setPrecision( Const.toInt( sPrec, -1 ) );

      meta.getInputFields()[i].setCurrencySymbol( item.getText( 6 ) );
      meta.getInputFields()[i].setDecimalSymbol( item.getText( 7 ) );
      meta.getInputFields()[i].setGroupSymbol( item.getText( 8 ) );

      meta.getInputFields()[i].setTrimType( ValueMetaBase.getTrimTypeByDesc( item.getText( 9 ) ) );

      meta.getInputFields()[i].setRepeated( BaseMessages.getString( PKG, "System.Combo.Yes" ).equalsIgnoreCase(
          item.getText( 10 ) ) );
    }
  }

  private void addProviderLabel(){
    wlProvider = new Label(wSettingComp, SWT.RIGHT );
    wlProvider.setText( BaseMessages.getString( PKG, "SdmxDialog.Provider.Label" ) );
    props.setLook( wlProvider );
    fdlProvider = new FormData();
    fdlProvider.left = new FormAttachment( 0, 0 );
    fdlProvider.top = new FormAttachment( 0, 0 );
    fdlProvider.right = new FormAttachment( 5 , -margin );
    wlProvider.setLayoutData( fdlProvider );
  }

  private void addProviderCombo(){
    wProvider = new CCombo(wSettingComp, SWT.BORDER | SWT.READ_ONLY );
    wProvider.setEditable( true );
    props.setLook( wProvider );
    wProvider.addModifyListener( lsMod );

    fdProvider = new FormData();
    fdProvider.left = new FormAttachment( wlProvider , margin );
    fdProvider.top = new FormAttachment( 0, 0 );
    fdProvider.right = new FormAttachment( middle , 0 );
    wProvider.setLayoutData( fdProvider );

    wProvider.addFocusListener( new FocusListener() {
      public void focusLost( org.eclipse.swt.events.FocusEvent e ) {
      }

      public void focusGained( org.eclipse.swt.events.FocusEvent e ) {
        Cursor busy = new Cursor( shell.getDisplay(), SWT.CURSOR_WAIT );
        shell.setCursor( busy );
        setProviders();
        shell.setCursor( null );
        busy.dispose();
      }
    } );

    wProvider.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        sdmxDialogData.setChosenProvider( providerHandler.getProviderByName(((CCombo)e.getSource()).getText().split(":")[0]) );
      }
    });

  }

  private void addFlowLabel(){
    wlFlow = new Label(wSettingComp, SWT.RIGHT );
    wlFlow.setText( BaseMessages.getString( PKG, "SdmxDialog.Flow.Label" ) );
    props.setLook( wlFlow );
    fdlFlow = new FormData();
    fdlFlow.left = new FormAttachment( 0, 0 );
    fdlFlow.top = new FormAttachment( wProvider, margin );
    fdlFlow.right = new FormAttachment( 5, -margin );
    wlFlow.setLayoutData( fdlFlow);
  }

  private void addFlowTextInput(){
    wFlow = new Text(wSettingComp, SWT.BORDER | SWT.SINGLE | SWT.LEFT );
    wFlow.setEditable( true );
    props.setLook(wFlow);
    fdFlows = new FormData();
    fdFlows.left = new FormAttachment( wlFlow , margin );
    fdFlows.right = new FormAttachment( middle , 0 );
    fdFlows.top = new FormAttachment( wProvider, margin );
    wFlow.setLayoutData( fdFlows );
  }

  private void addFlowBrowsingButton() {
    wbBrowseFlows = new Button(wSettingComp,SWT.PUSH);
    wbBrowseFlows.setText( BaseMessages.getString( PKG, "SdmxDialog.BrowseFlows.Button" ));
    props.setLook(wbBrowseFlows);
    fdBrowseFlows = new FormData();
    fdBrowseFlows.left = new FormAttachment( wFlow , margin );
    fdBrowseFlows.top = new FormAttachment( wProvider, margin );
    wbBrowseFlows.setLayoutData( fdBrowseFlows );

    wbBrowseFlows.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        if (sdmxDialogData.getChosenProvider() !=null ){
          Provider p = sdmxDialogData.getChosenProvider();
          try {
            setWaitCursor();
            sdmxDialogData.setAvailableFlows(SdmxClientHandler.getFlows(p.getName(),null));
          } catch (SdmxException e1) {
            e1.printStackTrace();
          } finally {
            setArrowCursor();
          }
          Map <String,String> flows = sdmxDialogData.getAvailableFlows();
          String[] flowDesc = new String[flows.values().size()];
          int i = 0;
          for ( String k : flows.keySet() ){
            flowDesc[i++] = k + " - " + flows.get(k);
          }
          EnterSelectionDialog esd = new EnterSelectionDialog( shell, flowDesc,
              BaseMessages.getString( PKG, "FlowDialog.SelectInfoType.DialogTitle"),
              BaseMessages.getString( PKG, "FlowDialog.SelectInfoType.DialogMessage") );
          String string = esd.open();
          if ( string != null ) {
            sdmxDialogData.setChosenFlowFrom(string);
            wFlow.setText(string);
          }
          meta.setChanged();
        }
      }
    });

  }

  private void addDimensionTableView() {
    int FieldsCols = 2;

    ColumnInfo[] colinfo = new ColumnInfo[FieldsCols];
    colinfo[0] = new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.Dimension.Column"), ColumnInfo.COLUMN_TYPE_TEXT, false );
    colinfo[1] = new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.DimensionCode.Column"), ColumnInfo.COLUMN_TYPE_TEXT, false );

    colinfo[0].setToolTip( BaseMessages.getString( PKG, "SdmxDialog.Dimension.Column.Tooltip" ) );
    colinfo[0].setSelectionAdapter(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        wCodeList.removeAll();

        String provider = sdmxDialogData.getChosenProvider().getName();
        String flow = sdmxDialogData.getFlowId();
        String dim =  wDimensionList.getItem(e.y)[0] ;
        sdmxDialogData.setActiveDimensionId( dim ); //store for later codelist binding

        try {
          Map<String, String> codes = SdmxClientHandler.getCodes( provider, flow, dim );
          for ( String k : codes.keySet() ){
            wCodeList.add( k , codes.get( k ) );
          }
          wCodeList.removeEmptyRows();
          wCodeList.setRowNums();

        } catch (SdmxException e1) {
          e1.printStackTrace();
        }
      }
    });

    wDimensionList = new TableView( transMeta, wSettingComp, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER, colinfo, 1, lsMod, props );
    props.setLook( wDimensionList );
    fdDimensionList = new FormData();
    fdDimensionList.top = new FormAttachment( wbBrowseFlows, margin );
    fdDimensionList.left = new FormAttachment( 5 , 0 );
    fdDimensionList.right = new FormAttachment( middle , 0 );
    fdDimensionList.bottom = new FormAttachment( 70, 0 );
    wDimensionList.setLayoutData( fdDimensionList );

    wDimensionList.setContentListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent modifyEvent) {
        updateDataWithTableViewContent();
      }
    });

    wDimensionList.addListener(SWT.Modify, new Listener() {
      @Override
      public void handleEvent(Event event) {
        updateDataWithTableViewContent();
      }
    });
  }

  private void addDimensionButton() {
    wbDimensions = new Button(wSettingComp, SWT.PUSH);
    wbDimensions.setText( BaseMessages.getString( PKG, "SdmxDialog.GetDimensions.Button" ));
    props.setLook(wbDimensions);
    fdDimensions = new FormData();
    fdDimensions.left = new FormAttachment( wbBrowseFlows, margin );
    fdDimensions.top = new FormAttachment( wProvider, margin );
    wbDimensions.setLayoutData( fdDimensions );

    wbDimensions.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        List<Dimension> dims = null;
        try {
          setWaitCursor();
          dims = SdmxClientHandler.getDimensions(sdmxDialogData.getChosenProvider().getName(), sdmxDialogData.getChosenFlow().getId());
          sdmxDialogData.initializeFlowDimensions( dims );
          wDimensionList.removeAll();
          for (Dimension d : dims ){
            wDimensionList.add( d.getId(), sdmxDialogData.getSelectedCodesByDimension( d ) );
          }
          wDimensionList.removeEmptyRows();
          wDimensionList.setRowNums();
          wDimensionList.optWidth( true );
        } catch (SdmxException e1) {
          e1.printStackTrace();
        } finally {
          setArrowCursor();
        }
      }
    });
  }

  private void addCodeListTableView() {
    int FieldsCols = 2;
    ColumnInfo[] colinfo = new ColumnInfo[FieldsCols];
    colinfo[0] = new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.CodeListId.Column"), ColumnInfo.COLUMN_TYPE_TEXT, false );
    colinfo[1] = new ColumnInfo( BaseMessages.getString( PKG, "SdmxDialog.CodeListDescription.Column"), ColumnInfo.COLUMN_TYPE_TEXT, false );

    wCodeList = new TableView( transMeta, wSettingComp, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER, colinfo, 1, lsMod, props );
    props.setLook( wCodeList );
    fdCodeList = new FormData();
    fdCodeList.top = new FormAttachment( wbBrowseFlows, margin );
    fdCodeList.left = new FormAttachment( wDimensionList , margin*2 );
    fdCodeList.right = new FormAttachment( 90 , -margin );
    fdCodeList.bottom = new FormAttachment( 70, 0 );
    wCodeList.setLayoutData( fdCodeList );
  }

  private void addCodeButton() {
    wbCodes = new Button(wSettingComp, SWT.PUSH);
    wbCodes.setText( BaseMessages.getString( PKG, "SdmxDialog.AddCodes.Button"));
    props.setLook(wbCodes);
    fdCodes = new FormData();
    fdCodes.left = new FormAttachment( wCodeList, margin );
    fdCodes.top = new FormAttachment( wbDimensions, margin );
    wbCodes.setLayoutData( fdCodes );

    wbCodes.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        StringBuilder builder = new StringBuilder();

        int ind[] = wCodeList.getSelectionIndices();

        for (int anInd : ind) {
          if (builder.length() > 0) {
            builder.append("+");
          }
          builder.append(wCodeList.getItem(anInd)[0]);
        }
        updateDimensionTable( sdmxDialogData.getActiveDimensionId(), builder.toString() );
      }
    });
  }

  private void addViewTimeSeriesButton() {
    wbTimeSeries = new Button(wSettingComp, SWT.PUSH);
    wbTimeSeries.setText( BaseMessages.getString( PKG, "SdmxDialog.ViewTimeSeries.Button"));
    props.setLook( wbTimeSeries );
    fdTimeSeries = new FormData();
    fdTimeSeries.left = new FormAttachment( wCodeList, margin );
    fdTimeSeries.top = new FormAttachment( wbCodes, margin );
    wbTimeSeries.setLayoutData( fdTimeSeries );

    wbTimeSeries.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        RowMetaInterface rowMeta = new RowMeta();
        ValueMetaInterface seriesName = new ValueMetaString( "Series" );
        rowMeta.addValueMeta( seriesName );

        for ( Dimension d : sdmxDialogData.getCurrentFlowDimensionToCodes().keySet() ){
          ValueMetaInterface field = new ValueMetaString( d.getId() );
          rowMeta.addValueMeta( field );
        }
        try {
          setWaitCursor();
          List<List<String>> ts = sdmxDialogData.getAvailableTimeSeriesNames();
          PreviewTimeSeriesDialog tsd = new PreviewTimeSeriesDialog( shell, SWT.NONE, rowMeta, transMeta, ts );
          tsd.open();
        } catch (SdmxException ex) {
          ShowMessageDialog dialog = new ShowMessageDialog( shell, SWT.OK | SWT.ICON_WARNING,
              BaseMessages.getString( PKG, "SdmxDialog.NoSeries.Text" ),
              BaseMessages.getString( PKG, "SdmxDialog.NoSeries.Message" ) );
          dialog.open();
          ex.printStackTrace();
        } finally {
          setArrowCursor();
        }
      }
    });
  }

  /*
   * Update the tableview row identified by parameter dimension
   * with the parameter code
   */
  private void updateDimensionTable( String dimension, String code ) {
    String[] dims = wDimensionList.getItems( 0 );
    int dimRow = findDimensionRow( dimension, dims );
    wDimensionList.setText( code, 2, dimRow );
    wDimensionList.optWidth( true );
    wDimensionList.notifyListeners( SWT.Modify, new Event() );
  }


  /* It scans the tableview and returns the number
   * of the row where the string d (dimension)
   * appears
   */
  private int findDimensionRow(String d, String[] dimensions ){
    for ( int i=0; i < dimensions.length; i++ ){
      if (d.equals( dimensions[i] )) return i;
    }
    throw new IllegalStateException();
  }

  /**
   * Save the tableview content in the map object
   * contained in SdmxDialogData instance
   */
  private void updateDataWithTableViewContent(){
    int itemNumber = wDimensionList.getItemCount();
    for ( int i=0; i < itemNumber; i++ ){
      String[] item = wDimensionList.getItem( i );
      sdmxDialogData.updateDimensionCodes( item[0], item[1] );
    }
  }

  /**
   * Get the list of dim fields in the sdmx flow and put the result in the fields table view.
   */
  private void getFields() {
    RowMetaInterface fields = new RowMeta();

    SdmxStepMeta info = new SdmxStepMeta();
    saveMeta( info );
    int clearFields = SWT.YES;

    if ( wFields.nrNonEmpty() > 0 ) {
      MessageBox messageBox = new MessageBox( shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION );
      messageBox.setMessage( BaseMessages.getString( PKG, "SdmxDialog.ClearFieldList.DialogMessage" ) );
      messageBox.setText( BaseMessages.getString( PKG, "SdmxDialog.ClearFieldList.DialogTitle" ) );
      clearFields = messageBox.open();
      if ( clearFields == SWT.CANCEL ) {
        return;
      }
    }

    try {
      // add the time slot field
      String timeFieldName = "TIME_SLOT";
      int timeFieldType = ValueMetaInterface.TYPE_STRING;

      // add the observation value field
      String observationFieldName = "VALUE";
      int observationFieldType = ValueMetaInterface.TYPE_NUMBER;

      fields.addValueMeta( ValueMetaFactory.createValueMeta( timeFieldName, timeFieldType ) );
      fields.addValueMeta( ValueMetaFactory.createValueMeta( observationFieldName, observationFieldType ) );

      // add all the dimensions to RowMeta
      for ( Dimension d : info.getDimensionToCodes().keySet() ){
        String dimName = d.getId() ;
        fields.addValueMeta( ValueMetaFactory.createValueMeta( dimName, ValueMetaInterface.TYPE_STRING ));
      }
    } catch (KettlePluginException e) {
      e.printStackTrace();
    }

    if ( fields.size() > 0) {
      if ( clearFields == SWT.YES ){
        wFields.clearAll( false );
      }
      for (int i = 0; i < fields.size(); i++ ) {
        ValueMetaInterface field = fields.getValueMeta( i );
        wFields.add( field.getName(), field.getTypeDesc(), "", "", "", "", "", "", "none", "N" );
      }

      wFields.removeEmptyRows();
      wFields.setRowNums();
      wFields.optWidth( true );
    } else {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_WARNING );
      mb.setMessage( BaseMessages.getString( PKG, "SdmxDialog.UnableToFindFields.DialogMessage" ) );
      mb.setText( BaseMessages.getString( PKG, "SdmxDialog.UnableToFindFields.DialogTitle" ) );
      mb.open();
    }
  }

  private void setWaitCursor() {
    shell.setCursor( new Cursor( display, SWT.CURSOR_WAIT ) );
  }

  private void setArrowCursor() {
    shell.setCursor( new Cursor( display, SWT.CURSOR_ARROW ) );
  }

}
