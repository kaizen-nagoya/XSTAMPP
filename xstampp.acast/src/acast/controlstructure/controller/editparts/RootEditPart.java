/*******************************************************************************
 * Copyright (c) 2013 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
 * Grahovac, Jarkko Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian
 * Sieber, Fabian Toth, Patrick Wickenhäuser, Aliaksei Babkovich, Aleksander
 * Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package acast.controlstructure.controller.editparts;

import java.util.Comparator;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;

import acast.controlstructure.controller.policys.CSConnectionPolicy;
import acast.controlstructure.controller.policys.CSEditPolicy;
import acast.controlstructure.figure.RootFigure;
import acast.model.controlstructure.components.ComponentType;
import acast.model.controlstructure.interfaces.IRectangleComponent;
import acast.model.interfaces.IControlStructureEditorDataModel;
import messages.Messages;


/**
 * 
 * The ComponentEditPart defines a ComponentView for the model, the
 * EditPartFactory has given it, and manages the interaction between model and
 * view
 * 
 * @version 1.0
 * @author Lukas Balzer, Aliaksei Babkovich
 * 
 */
public class RootEditPart extends CSAbstractEditPart {

	/**
	 * this constuctor sets the unique ID of this EditPart which is the same in
	 * its model and figure
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param model
	 *            The DataModel which contains all model classes
	 * @param stepId
	 *            TODO
	 */
	public RootEditPart(IControlStructureEditorDataModel model, String stepId) {
		super(model, stepId, 1);
		
	}

	@Override
	protected IFigure createFigure() {

		IFigure figureTemp = new RootFigure(this.getId());
		figureTemp.setFocusTraversable(false);
		ConnectionLayer connLayer = (ConnectionLayer) this
				.getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ManhattanConnectionRouter());

		return figureTemp;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#performRequest(org.eclipse.gef.Request)
	 * @author Lukas Balzer, Aliaksei Babkovich
	 */
	@Override
	public void performRequest(Request req) {
		// Overrides the perform Request function in CSAbstractEditPart
	}

	@Override
	public void refresh() {
		super.refresh();
		for (IRectangleComponent f : this.getModelChildren()) {
			if ((f.getComponentType() == ComponentType.CONTROLACTION)
					&& (this.getDataModel().getControlActionU(
							f.getControlActionLink()) == null)) {
				this.getDataModel().removeComponent(f.getId());
			}
		}
	}
	

	@Override
	protected void refreshVisuals() {
		getModelChildren().sort(new Comparator<IRectangleComponent>() {

		

			@Override
			public int compare(
					IRectangleComponent arg0,
					IRectangleComponent arg1) {
				if(arg0.getComponentType() == ComponentType.DASHEDBOX){
					return 1;
				}
				return -1;
			}
		});
		
		this.refreshChildren();
		this.refreshConnections();
		for (Object child : this.getChildren()) {
			((CSAbstractEditPart) child).refreshVisuals();
		}
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(Messages.SnapFeedback, new SnapFeedbackPolicy());
		// this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new
		// CSDeletePolicy(this.getDataModel()));
		this.installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new CSEditPolicy(this.getDataModel(), this.getStepId()));
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new CSConnectionPolicy(this.getDataModel(), this.getStepId()));
	}

	@Override
	public void translateToRoot(Translatable t) {
		this.getFigure().translateToParent(t);
	}

	/**
	 * this method enables an offset around the child figures to find connection
	 * anchors with a certain "snap-to" effect
	 * 
	 * @author Lukas Balzer
	 * 
	 */
	public void enableFigureOffset() {
		((RootFigure) this.figure).enableOffset();
		((RootFigure) this.figure).generalEnableOffset();
	}

	/**
	 * This method disables the use of the offset area around the figures bounds
	 * 
	 * @author Lukas Balzer
	 * 
	 */
	public void disableFigureOffset() {
		((RootFigure) this.figure).disableOffset();
		((RootFigure) this.figure).generalDisableOffset();
	}

	/**
	 * Draws all anchors on component.
	 * 
	 * @author Aliaksei Babkovich
	 * 
	 */
	public void addAnchorsGrid() {
		((RootFigure) this.figure).addAnchorsGrid(this.getDataModel()
				.getComponent(this.getId()).getChildren());
	}

	/**
	 * Delete all drawn anchors on component
	 * 
	 * @author Aliaksei Babkovich
	 * @param mode TODO
	 * 
	 */
	public void setAnchorsGrid(boolean mode) {
		if(!mode){
			((RootFigure) this.figure).removeAnchorsGrid();
		}else{
			addAnchorsGrid();
		}
	}
}