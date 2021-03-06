/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of Software
 * Technology, Software Engineering Group University of Stuttgart, Germany
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package xstampp.usermanagement.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import xstampp.ui.common.shell.ModalShell;
import xstampp.usermanagement.UserSystem;
import xstampp.usermanagement.api.IUser;

/**
 * A {@link ModalShell} which has a content consisting of two entry fileds for username and
 * password.
 * 
 * @author Lukas Balzer - initial implementation
 *
 */
public abstract class AbstractUserShell extends ModalShell {
  private final UserSystem userSystem;
  private boolean hidePassword;
  private TextInput passwordInput;
  private TextInput usernameInput;
  private IUser selectedUser;
  private int userLabelStyle;
  private Group inputGroup;

  /**
   * Constructs a {@link ModalShell} with <code>User</code> as title .
   * 
   * @param userSystem
   *          The user system in which the user wants to login
   * @param hidePassword
   *          whether of not the password input should hide or show characters
   */
  public AbstractUserShell(UserSystem userSystem, boolean hidePassword) {
    super("User", Style.PACKED);
    setUserLabelStyle(SWT.None);
    this.selectedUser = null;
    this.userSystem = userSystem;
    this.hidePassword = hidePassword;
  }

  @Override
  protected void createCenter(Shell shell) {
    inputGroup = new Group(shell, SWT.None);
    inputGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    inputGroup.setLayout(new GridLayout(2, false));
    if (selectedUser != null) {
      this.usernameInput = new TextInput(inputGroup, getUserLabelStyle(), "Username",
          selectedUser.getUsername());
    } else {
      this.usernameInput = new TextInput(inputGroup, getUserLabelStyle(), "Username");
    }

    int passwordStyle = SWT.None;
    if (hidePassword) {
      passwordStyle = SWT.PASSWORD;
    }
    this.passwordInput = new TextInput(inputGroup, passwordStyle, "password");
  }

  public String getPassword() {
    return passwordInput.getText();
  }

  public String getUsername() {
    return usernameInput.getText();
  }

  /**
   * method which calls {@link #open()} to define the selected user if its' current value is
   * <code>null</code>. This method just returns the selected user when the value by the time of the
   * method call is not <code>null</code>.
   * 
   * @return the user that has been selected in the shell or null if none has been selected.
   */
  public IUser pullUser() {
    if (this.selectedUser == null) {
      open();
    }
    return this.selectedUser;
  }

  @Override
  protected boolean validate() {
    try {
      return !passwordInput.getText().isEmpty() && !usernameInput.getText().isEmpty();
    } catch (NullPointerException exc) {
      return false;
    }
  }

  public void setSelectedUser(IUser selectedUser) {
    this.selectedUser = selectedUser;
  }

  /**
   * The user system with which this user shell is interacting.
   * 
   * @return the userSystem
   */
  public UserSystem getUserSystem() {
    return userSystem;
  }

  public int getUserLabelStyle() {
    return userLabelStyle;
  }

  public void setUserLabelStyle(int userLabelStyle) {
    this.userLabelStyle = userLabelStyle;
  }

  void setEnableUserInput(boolean enabled) {
    for (Control control : this.inputGroup.getChildren()) {
      control.setEnabled(enabled);
    }
  }
}
