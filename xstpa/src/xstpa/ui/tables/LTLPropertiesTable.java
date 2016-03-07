package xstpa.ui.tables;

import java.util.List;
import java.util.Observable;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import xstampp.astpa.model.DataModelController;
import xstampp.model.AbstractLTLProvider;
import xstampp.model.ObserverValue;
import xstampp.ui.common.ProjectManager;
import xstampp.ui.editors.StandartEditorPart;
import xstpa.ui.View;

public class LTLPropertiesTable extends StandartEditorPart{
	private class LtlViewLabelProvider extends LabelProvider implements
	ITableLabelProvider,IColorProvider{
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {

			return null;
		}
		@Override
		public Color getBackground(Object element) {
			int index = ((List<?>)ltlViewer.getInput()).indexOf(element);
			if(index % 2 == 0){
				return new Color(null, 230,230,230);
			}
			return new Color(null, 255,255,255);
		}
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof AbstractLTLProvider){
				switch(columnIndex){
				case 0: 
					return  "SSR1." + ((AbstractLTLProvider) element).getNumber();
				case 1:
					return  ((AbstractLTLProvider) element).getLtlProperty();
					
				}
				
			}
			return null;
		}
		@Override
		public Color getForeground(Object element) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private TableViewer ltlViewer;
	private Table ltlTable;

	public void createPartControl(Composite parent) {
		ProjectManager.getContainerInstance().getDataModel(getProjectID()).addObserver(this);
		Composite back = new Composite(parent, SWT.NONE);
		TableColumnLayout tLayout = new TableColumnLayout();
		back.setLayout(tLayout);
		
		ltlViewer = new TableViewer(back, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL );
		ltlViewer.setContentProvider(new ArrayContentProvider());
		ltlViewer.setLabelProvider(new LtlViewLabelProvider());
	    ltlTable = ltlViewer.getTable();
	    ltlTable.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				ProjectManager.getContainerInstance().getDataModel(getProjectID()).deleteObserver(LTLPropertiesTable.this);
			}
		});
	    ltlTable.setHeaderVisible(true);
	    ltlTable.setLinesVisible(true);
	    ltlTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    // add columns for ltl tables	   
	    TableColumn ltlColumn = new TableColumn(ltlTable, SWT.LEFT);
	    ltlColumn.setText(View.ENTRY_ID);
	    tLayout.setColumnData(ltlColumn, new ColumnWeightData(1, 10, false));
	    
	    ltlColumn = new TableColumn(ltlTable, SWT.LEFT);
	    ltlColumn.setText(View.LTL_RULES);
	    tLayout.setColumnData(ltlColumn, new ColumnWeightData(8, 30, false));
	    ltlTable.pack();
	    ltlViewer.setInput(((DataModelController) ProjectManager.getContainerInstance().getDataModel(getProjectID())).getLTLPropertys());
	}

	@Override
	public String getId() {
		return "xstpa.editor.ltl";
	}

	@Override
	public void dispose() {
		ProjectManager.getContainerInstance().getDataModel(getProjectID()).deleteObserver(LTLPropertiesTable.this);
	}
	@Override
	public void update(final Observable dataModelController, Object updatedValue) {
		final ObserverValue value= (ObserverValue) updatedValue; 
		switch(value){
			case Extended_DATA:
				if(dataModelController instanceof DataModelController){
					new Runnable() {
						@Override
						public void run() {
							ltlViewer.setInput(((DataModelController) dataModelController).getLTLPropertys());
						}
					}.run();
				}
		default:
			break;
		}
	}
}
