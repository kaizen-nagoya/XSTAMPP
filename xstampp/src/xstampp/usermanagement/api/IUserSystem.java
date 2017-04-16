/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner
 * Institute of Software Technology, Software Engineering Group
 * University of Stuttgart, Germany
 *  
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package xstampp.usermanagement.api;

import java.util.List;
import java.util.UUID;

/**
 * The API to access he user system and create one on the platform.
 * 
 * @author Lukas Balzer - initial implementation and API
 *
 */
public interface IUserSystem {

  boolean createUser();

  boolean canCreateUser();

  boolean deleteUser(UUID userId);

  boolean canDeleteUser(UUID userId);

  boolean grantAccessTo(IUser user, AccessRights right);

  public UUID getSystemId();

  public UUID getCurrentUserId();

  List<IUser> getRegistry();

  /**
   * checks whether the user is allowed to access the given resource
   * 
   * @param entryId
   *          the id of the entry that shall be manipulated
   * @param accessRight
   *          The level the Access takes place e.g. {@link AccessRights#CREATE},
   *          {@link AccessRights#WRITE}
   *
   * @return if the user is allowed to access.
   */
  public boolean checkAccess(UUID entryId, AccessRights accessRight);

  /**
   * checks whether the user is allowed to access the given access level
   *
   * @param accessRight
   *          The level the Access takes place e.g. {@link AccessRights#CREATE},
   *          {@link AccessRights#WRITE}
   *
   * @return if the user is allowed to access.
   */
  public boolean checkAccess(AccessRights accessRight);

}