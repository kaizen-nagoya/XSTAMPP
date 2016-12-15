package xstpa.ui;

import java.util.Collections;
import java.util.List;
import java.util.Observable;

import messages.Messages;
import xstampp.astpa.model.interfaces.IExtendedDataModel;
import xstampp.astpa.ui.sds.AbstractFilteredTableView;
import xstampp.model.AbstractLtlProvider;
import xstampp.model.ObserverValue;
import xstpa.model.XSTPADataController;

public class RefinedSafetyConstraintsView extends AbstractFilteredTableView{

	XSTPADataController dataController;
	
	public RefinedSafetyConstraintsView() {
		super(new RefinedEntryFilter(), new String[]{ Messages.ID,
													  "Refined Unsafe Control Actions", 
													  Messages.ID, 
													  "Refined Safety Constraints"});

		setColumnWeights(new int[]{-1,5,-1,5});
	}
	@Override
	protected List<?> getInput() {
		if(getDataInterface() == null){
			return null;
		}
		List<AbstractLtlProvider> allRUCA = ((IExtendedDataModel)getDataInterface()).getAllRefinedRules(true);
  	    Collections.sort(allRUCA);
  	    
  	    return allRUCA;
	}
	
	@Override
	public String getTitle() {
		return "Refined Safety Constraints";
	}
	
	/**
	 * @return the dataController
	 */
	public XSTPADataController getDataController() {
		return this.dataController;
	}

	/**
	 * @param dataController the dataController to set
	 */
	public void setDataController(XSTPADataController dataController) {
		if(this.dataController != dataController){
			this.dataController = dataController;
			
			this.update(dataController, ObserverValue.CONTROL_ACTION);
			this.dataController.addObserver(this);
		}
	}
	
	@Override
	public void update(Observable IExtendedDataModel, Object updatedValue) {
		ObserverValue type = (ObserverValue) updatedValue;
		switch (type) {
			case Extended_DATA:
					packColumns();
		default:
			break;
		}
	}
	
	@Override
	protected CSCLabelProvider getColumnProvider(int columnIndex) {

		switch(columnIndex){
		case 0: 
			return new CSCLabelProvider(){
				@Override
				public String getText(Object element) {
					return "RSR1."+((AbstractLtlProvider)element).getNumber();
				}
			};
		case 1:
			return new CSCLabelProvider(){
				
				@Override
				public String getText(Object element) {
					return ((AbstractLtlProvider)element).getRefinedUCA();
				}
			};
		case 2:
			return new CSCLabelProvider(){
				@Override
				public String getText(Object element) {
					return "SC2."+((AbstractLtlProvider)element).getNumber();
				}
			};
		case 3:
			return new CSCLabelProvider(){
				@Override
				public String getText(Object element) {
					return ((AbstractLtlProvider)element).getRefinedSafetyConstraint();
				}
			};
			
			
		}
		return null;
	
	}
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected boolean hasEditSupport() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}

