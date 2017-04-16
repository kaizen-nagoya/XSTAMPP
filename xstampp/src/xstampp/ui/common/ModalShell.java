/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner
 * Institute of Software Technology, Software Engineering Group
 * University of Stuttgart, Germany
 *  
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package xstampp.ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import xstampp.util.ColorManager;

/**
 * defines an Application model shell which should be used for all dialogs in
 * XSTAMPP.
 * 
 * @author Lukas Balzer - initial implementation
 *
 */
public abstract class ModalShell {

  private String title;
  private Point size;
  private String acceptLabel;
  private Label invalidLabel;
  private Button okButton;

  public ModalShell(String name, String acceptLabel) {
    this.title = name;

    this.acceptLabel = acceptLabel;
  }

  public ModalShell(String name) {
    this(name, "Ok");
  }

  public void open() {
    final Shell shell = new Shell(Display.getCurrent().getActiveShell(),
        SWT.APPLICATION_MODAL | SWT.SHEET);
    GridLayout gridLayout = new GridLayout(2, false);
    shell.setLayout(gridLayout);
    if (this.size != null) {
      shell.setSize(this.size);
    }

    shell.setText(title);

    createCenter(shell);
    Composite footer = new Composite(shell, SWT.None);
    footer.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1));
    footer.setLayout(new GridLayout(3, false));
    this.invalidLabel = new Label(footer, SWT.WRAP);
    invalidLabel.setForeground(ColorManager.COLOR_RED);
    invalidLabel.setText("invalid content!");
    invalidLabel.setVisible(false);
    invalidLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    Button cancelButton = new Button(footer, SWT.PUSH);
    cancelButton.setText("Cancel");
    cancelButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        shell.close();
      }
    });
    cancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));

    okButton = new Button(footer, SWT.PUSH);
    okButton.setText(acceptLabel);
    okButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (doAccept()) {
          shell.close();
        }
      }
    });
    okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
    okButton.setEnabled(false);
    shell.open();
    while (!shell.isDisposed()) {
      if (!Display.getDefault().readAndDispatch()) {
        Display.getDefault().sleep();
      }
    }

  }

  /**
   * This method sets the content to be invalid and informs the user by
   * displaying a text label in the footer of the dialog. This text is hidden
   * again when {@link #canAccept()} is called
   * 
   * @param error
   *          a text informing the user why the content is not valid
   */
  protected void invalidate(String error) {
    if (!invalidLabel.isDisposed()) {
      this.invalidLabel.setText(error);
      this.invalidLabel.setVisible(true);
    }
  }

  /**
   * setter for the text in the headline of the shell
   * 
   * @param acceptLabel
   *          text in the headline of the shell
   */
  public void setAcceptLabel(String acceptLabel) {
    this.acceptLabel = acceptLabel;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSize(int width, int height) {
    this.size = new Point(width, height);
  }

  /**
   * this method should be called whenever a change is made. It calls
   * {@link #validate()} and sets the ok/accept button (in-)active accordingly
   */
  protected final void canAccept() {
    try {
      this.okButton.setEnabled(validate());
      setUnchecked();
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  protected abstract boolean validate();

  /**
   * This method is called whenever the ok/accept button is pressed. Depending
   * on the return value the modal shell is closed.
   * 
   * <p>
   * if the content can not be accepted and the method returns false, the
   * {@link ModalShell#invalidate(String)} method can be called informing the
   * user about details
   * 
   * @return if the input should be accepted or not
   */
  protected abstract boolean doAccept();

  protected abstract void createCenter(Shell parent);

  private final void setUnchecked() {
    if (!invalidLabel.isDisposed()) {
      this.invalidLabel.setVisible(false);
    }
  }
}
