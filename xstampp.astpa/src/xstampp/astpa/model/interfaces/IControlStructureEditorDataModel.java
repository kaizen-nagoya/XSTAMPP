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

package xstampp.astpa.model.interfaces;

import java.util.List;
import java.util.UUID;

import org.eclipse.draw2d.geometry.Rectangle;

import xstampp.astpa.controlstructure.CSEditor;
import xstampp.astpa.controlstructure.CSEditorWithPM;
import xstampp.astpa.model.ITableModel;
import xstampp.astpa.model.controlstructure.components.Anchor;
import xstampp.astpa.model.controlstructure.components.ComponentType;
import xstampp.astpa.model.controlstructure.components.ConnectionType;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.model.IDataModel;

/**
 * This class provides methods for the DataModel access of the CSDiagramm
 * 
 * @author Lukas Balzer, Fabian Toth
 * 
 */
public interface IControlStructureEditorDataModel extends IDataModel {

	/**
	 * Adds a new component to a root component with the given values. <br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param parentId
	 *            the id of the parent
	 * @param layout
	 *            the layout of the new component
	 * @param text
	 *            the text of the new component
	 * @param type
	 *            the type of the new component
	 * @return the id of the created component. Null when the component could
	 *         not be added
	 * 
	 * @author Fabian Toth
	 */
	UUID addComponent(UUID parentId, Rectangle layout, String text,
			ComponentType type);

	/**
	 * Adds a new component to a root component with the given values. <br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param parentId
	 *            the id of the parent
	 * @param layout
	 *            the layout of the new component
	 * @param text
	 *            the text of the new component
	 * @param type
	 *            the type of the new component
	 * @return the id of the created component. Null when the component could
	 *         not be added
	 * 
	 * @author Fabian Toth
	 * @param controlActionId
	 */
	UUID addComponent(UUID controlActionId, UUID parentId, Rectangle layout,
			String text, ComponentType type);

	/**
	 * Removes a control action. <br>
	 * Triggers an update for {@link xstampp.model.ObserverValue#CONTROL_ACTION}
	 * 
	 * @author Jarkko Heidenwag
	 * 
	 * @param controlActionId
	 *            The ID of the control action which has to be deleted
	 * @return true if the control action has been removed
	 */
	boolean removeControlAction(UUID controlActionId);

	/**
	 * Adds a new root component with the given values. <br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param layout
	 *            the layout of the new component
	 * @param text
	 *            the text of the new component
	 * @return the id of the created component. Null when the component could
	 *         not be added
	 * 
	 * @author Fabian Toth
	 */
	UUID setRoot(Rectangle layout, String text);

	/**
	 * Adds a control action. <br>
	 * Triggers an update for {@link xstampp.model.ObserverValue#CONTROL_ACTION}
	 * 
	 * @author Jarkko Heidenwag
	 * 
	 * @param description
	 *            The description of the new control action
	 * @param title
	 *            The title of the new control action
	 * @return String ID of the new control action
	 */
	UUID addControlAction(String title, String description);

	/**
	 * Setter for the title of a control action. <br>
	 * Triggers an update for {@link xstampp.model.ObserverValue#CONTROL_ACTION}
	 * 
	 * @author Jarkko Heidenwag
	 * @param controlActionId
	 *            The control action's id
	 * 
	 * @param title
	 *            The control action's new title
	 * @return true if the title has been set
	 */
	boolean setControlActionTitle(UUID controlActionId, String title);

	/**
	 * Getter for all existing control actions
	 * 
	 * @author Jarkko Heidenwag
	 * 
	 * @return All control actions
	 */
	List<ITableModel> getAllControlActions();

	/**
	 * Get a control action by it's ID.
	 * 
	 * @author Jarkko Heidenwag, Patrick Wickenhaeuser
	 * @param controlActionId
	 *            the ID of the control action.
	 * 
	 * @return the control action.
	 */
	ITableModel getControlAction(UUID controlActionId);

	/**
	 * Searches for the component with the given id and changes the layout of
	 * it. Can also change root components<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param componentId
	 *            the id of the component
	 * @param layout
	 *            the new text
	 * @param step1
	 *            if the layout of step 1 should be changed
	 * @return true if the text could be changed
	 * 
	 * @author Fabian Toth,Lukas Balzer
	 * 
	 * @see CSEditor#ID
	 * @see CSEditorWithPM#ID
	 */
	boolean changeComponentLayout(UUID componentId, Rectangle layout,
			boolean step1);

	/**
	 * Synchronizes the layouts of all children drawn on the root from the 1.
	 * and 3. step
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return true, if all layouts have been synchronized
	 */
	boolean synchronizeLayouts();

	/**
	 * Searches for the component with the given id and changes the text of it.
	 * Can also change root components<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param componentId
	 *            the id of the component
	 * @param text
	 *            the new text
	 * @return true if the text could be changed
	 * 
	 * @author Fabian Toth
	 */
	boolean changeComponentText(UUID componentId, String text);

	/**
	 * Searches recursively for the component with the given id and removes it.
	 * Can also remove root components<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param componentId
	 *            the id of the component to delete
	 * @return true if this controller contained the specified element
	 * 
	 * @author Fabian Toth
	 */
	boolean removeComponent(UUID componentId);

	/**
	 * This methode recovers a Component which was deleted before, from the
	 * componentTrash
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param parentId
	 *            the id of the parent
	 * @param componentId
	 *            the id of the component to recover
	 * @return whether the component could be recoverd or not
	 */
	public boolean recoverComponent(UUID parentId, UUID componentId);

	/**
	 * This function pops ControlActions out of a Trash
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param id
	 *            the id of the ControlAction which shall be recovered
	 * @return whether the ControlAction has been recovered or not
	 */
	public boolean recoverControlAction(UUID id);

	/**
	 * Searches recursively for the component with the given id
	 * 
	 * @param componentId
	 *            the id of the child
	 * @return the component with the given id, null if the component does not
	 *         exist
	 * 
	 * @author Fabian Toth
	 */
	IRectangleComponent getComponent(UUID componentId);

	/**
	 * Get the root component
	 * 
	 * @return the the components
	 * 
	 * @author Fabian Toth
	 */
	IRectangleComponent getRoot();

	/**
	 * Adds a new connection with the given values<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param sourceAnchor
	 *            the anchor at the source component
	 * @param targetAnchor
	 *            the anchor at the target component
	 * @param connectionType
	 *            the type of the connection
	 * @return the id of the new connection
	 * 
	 * @author Fabian Toth
	 */
	UUID addConnection(Anchor sourceAnchor, Anchor targetAnchor,
			ConnectionType connectionType);

	/**
	 * Searches for the connection with the given id and changes the connection
	 * type to the new value<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param connectionId
	 *            the id of the connection to change
	 * @param connectionType
	 *            the new connection type
	 * @return true if the connection type could be changed
	 * 
	 * @author Fabian Toth
	 */
	boolean changeConnectionType(UUID connectionId,
			ConnectionType connectionType);

	/**
	 * Searches for the connection with the given id and changes the targetId to
	 * the new value<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param connectionId
	 *            the id of the connection to change
	 * @param targetAnchor
	 *            the new source anchor
	 * @return true if the targetId could be changed
	 * 
	 * @author Fabian Toth
	 */
	boolean changeConnectionTarget(UUID connectionId, Anchor targetAnchor);

	/**
	 * Searches for the connection with the given id and changes the sourceId to
	 * the new value<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param connectionId
	 *            the id of the connection to change
	 * @param sourceAnchor
	 *            the new source anchor
	 * @return true if the sourceId could be changed
	 * 
	 * @author Fabian Toth
	 */
	boolean changeConnectionSource(UUID connectionId, Anchor sourceAnchor);

	/**
	 * Deletes the connection with the given id<br>
	 * Triggers an update for
	 * {@link xstampp.model.ObserverValue#CONTROL_STRUCTURE}
	 * 
	 * @param connectionId
	 *            the id of the connection
	 * @return true if this component contained the specified element
	 * 
	 * @author Fabian Toth
	 */
	boolean removeConnection(UUID connectionId);

	/**
	 * This methode recovers a Connection which was deleted before, from the
	 * connectionTrash
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param connectionId
	 *            the id of the component to recover
	 * @return whether the Connection could be recovered or not
	 */
	public boolean recoverConnection(UUID connectionId);

	/**
	 * Gets the connection with the given id
	 * 
	 * @param connectionId
	 *            the id of the connection
	 * @return the connection with the given id, null if the connection does not
	 *         exist
	 * 
	 * @author Fabian Toth
	 */
	IConnection getConnection(UUID connectionId);

	/**
	 * Gets all connections of the control structure diagram
	 * 
	 * @author Fabian Toth
	 * 
	 * @return all connections
	 */
	List<IConnection> getConnections();

	/**
	 * Sets the path to the picture of the control structure for the export
	 * 
	 * @author Fabian Toth
	 * 
	 * @param path
	 *            the path to the image
	 * @return true, if the path has been set
	 */
	boolean setCSImagePath(String path);

	/**
	 * Sets the path to the picture of the control structure with process model
	 * for the export
	 * 
	 * @author Fabian Toth
	 * 
	 * @param path
	 *            the path to the image
	 * @return true, if the value has been set
	 */
	boolean setCSPMImagePath(String path);

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return the amount of components currently in the trash
	 */
	public int getComponentTrashSize();

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return the amount of components currently in the trash
	 */
	public int getConnectionTrashSize();

}