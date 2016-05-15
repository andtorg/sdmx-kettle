/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
*
*******************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
******************************************************************************/

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
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;

import java.util.*;
import java.util.List;

/**
 * This class is part of the demo step plug-in implementation.
 * It demonstrates the basics of developing a plug-in step for PDI. 
 * 
 * The demo step adds a new string field to the row stream and sets its
 * value to "Hello World!". The user may select the name of the new field.
 *   
 * This class is the implementation of StepDialogInterface.
 * Classes implementing this interface need to:
 * 
 * - build and open a SWT dialog displaying the step's settings (stored in the step's meta object)
 * - write back any changes the user makes to the step's meta object
 * - report whether the user changed any settings when confirming the dialog 
 * 
 */
public class SdmxStepDialog extends BaseStepDialog implements StepDialogInterface {

	/**
	 *	The PKG member is used when looking up internationalized strings.
	 *	The properties file with localized keys is expected to reside in 
	 *	{the package of the class specified}/messages/messages_{locale}.properties   
	 */
	private static Class<?> PKG = SdmxStepMeta.class; // for i18n purposes

	// this is the object the stores the step's settings
	// the dialog reads the settings from it when opening
	// the dialog writes the settings to it when confirmed 
	private SdmxStepMeta meta;
  private SdmxProviderHandler providerHandler;
  private SdmxDialogData sdmxDialogData;
  private ModifyListener lsMod;

  private boolean gotProviders;

	// text field holding the name of the field to add to the row stream
	private Text wHelloFieldName;
  private CTabFolder wTabFolder;
  private FormData fdTabFolder;
  private CTabItem wSettingTab;
  private ScrolledComposite wSettingsSComp;

  private Composite wSettingComp;
  private FormData fdSettingComp;

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
		Display display = parent.getDisplay();

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

		// output field value
		Label wlValName = new Label(shell, SWT.RIGHT);
		wlValName.setText(BaseMessages.getString(PKG, "Sdmx.FieldName.Label"));
		props.setLook(wlValName);
		FormData fdlValName = new FormData();
		fdlValName.left = new FormAttachment(0, 0);
		fdlValName.right = new FormAttachment(middle, -margin);
		fdlValName.top = new FormAttachment(wStepname, margin);
		wlValName.setLayoutData(fdlValName);

		wHelloFieldName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wHelloFieldName);
		wHelloFieldName.addModifyListener(lsMod);
		FormData fdValName = new FormData();
		fdValName.left = new FormAttachment(middle, 0);
		fdValName.right = new FormAttachment(100, 0);
		fdValName.top = new FormAttachment(wStepname, margin);
		wHelloFieldName.setLayoutData(fdValName);

		wTabFolder = new CTabFolder( shell, SWT.BORDER );
		props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );
		wTabFolder.setSimple( false );

    addSettingTab();

		fdTabFolder = new FormData();
		fdTabFolder.left = new FormAttachment( 0, 0 );
		fdTabFolder.top = new FormAttachment( wHelloFieldName, margin );
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
		wHelloFieldName.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window and cancel the dialog properly
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {cancel();}
		});

    wTabFolder.setSelection( 0 );
		// Set/Restore the dialog size based on last position on screen
		// The setSize() method is inherited from BaseStepDialog
		setSize();

		// populate the dialog with the values from the meta object
    // TODO: 13/05/16 it does the same things as getData(). Delete one of them
		populateDialog();
		
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
		wHelloFieldName.setText(meta.getOutputField());	
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
		meta.setOutputField(wHelloFieldName.getText());
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

  private void setFlows(){
    System.out.println("flowing in the wind");
  }

  private void getData ( SdmxStepMeta meta ){
    if ( meta.getProvider() != null ) {
      sdmxDialogData.setChosenProvider( meta.getProvider() );
      wProvider.setText( meta.getProvider().getName() + ": " + meta.getProvider().getDescription());
    }

    if ( meta.getDataflow() != null ){
      Dataflow df = meta.getDataflow();
      sdmxDialogData.setChosenFlow( df );
      wFlow.setText( df.getId() + " - " + df.getDescription() );
    }
  }

  private void saveMeta( SdmxStepMeta stepMeta) {
    stepname = wStepname.getText(); // return value

    stepMeta.setProvider( sdmxDialogData.getChosenProvider() );
    stepMeta.setDataflow( sdmxDialogData.getChosenFlow() );

  }

  private String concatenateArrayValues ( String[] arr ){
    StringBuilder builder = new StringBuilder();
    for (String s : arr) {
      if (builder.length() > 0) {
        builder.append("+");
      }
      builder.append(s);
    }
    return builder.toString();
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
            sdmxDialogData.setAvailableFlows(SdmxClientHandler.getFlows(p.getName(),null));
          } catch (SdmxException e1) {
            e1.printStackTrace();
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
  }

  private void addDimensionButton() {
    wbDimensions = new Button(wSettingComp, SWT.PUSH);
    wbDimensions.setText( BaseMessages.getString( PKG, "SdmxDialog.GetDimensions.Button" ));
    props.setLook(wbDimensions);
    fdDimensions = new FormData();
    fdDimensions.left = new FormAttachment( wbBrowseFlows, 0, SWT.LEFT );
    fdDimensions.top = new FormAttachment( wbBrowseFlows, margin );
    wbDimensions.setLayoutData( fdDimensions );

    wbDimensions.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        List<Dimension> dims = null;
        try {
          dims = SdmxClientHandler.getDimensions(sdmxDialogData.getChosenProvider().getName(), sdmxDialogData.getChosenFlow().getId());
          sdmxDialogData.setCurrentFlowDimensions( dims );
          wDimensionList.removeAll();
          for (Dimension d : dims ){
            wDimensionList.add( d.getId(), "" );
          }
          wDimensionList.removeEmptyRows();
          wDimensionList.setRowNums();
          wDimensionList.optWidth( true );
        } catch (SdmxException e1) {
          e1.printStackTrace();
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
    fdCodeList.left = new FormAttachment( wbDimensions , margin );
    fdCodeList.right = new FormAttachment( 100 , -margin );
    fdCodeList.bottom = new FormAttachment( 70, 0 );
    wCodeList.setLayoutData( fdCodeList );
  }
}
