/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2002 IICM, Graz University of Technology
 * Inffeldgasse 16c, A-8010 Graz, Austria.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL)
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 ***********************************************************************/

package org.dinopolis.util.gui;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.util.Vector;
import java.awt.Frame;

//----------------------------------------------------------------------
/**
 * A dialog box that asks for a username and a password.
 * Example:
 * <pre>
 * LoginDialog dialog = new LoginDialog(null,"Login","Enter your name/password");
 * System.out.println("after dialog");
 * int result = dialog.getValue();
 * System.out.println("result: "+result);
 * if(result == LoginDialog.CLOSED_OPTION)
 *   System.out.println("closed");
 * else
 *   if(result == LoginDialog.CANCEL_OPTION)
 *     System.out.println("canceled");
 *   else
 *   {
 *     System.out.println("username: "+dialog.getUsername());
 *     System.out.println("password: "+dialog.getPassword());
 *   }
 * dialog = null;
 * * </pre>
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LoginDialog 
{

  protected JOptionPane option_pane_;

  protected String username_title_ = "Username:";
  protected String password_title_ = "Password:";

  protected JTextField username_textfield_;
  protected JPasswordField password_textfield_;

  public final static int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;
  public final static int OK_OPTION = JOptionPane.OK_OPTION;
  public final static int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
  
//----------------------------------------------------------------------
/**
 * Constructor using a default title and default position.
 *
 */
  public LoginDialog()
  {
    this(null,"Enter Username/Password",null);
  }
  
//----------------------------------------------------------------------
/**
 * Constructor using the title for the dialog box. The dialog will be
 * centered on screen.
 *
 * @param title the title of the dialog box.
 *
 */
  public LoginDialog(String title)
  {
    this(null,title,null);
  }
  
//----------------------------------------------------------------------
/**
 * Constructor using the title for the dialog box. The dialog will be
 * centered on the parent frame.
 *
 * @param parent_frame the parent frame used for placing the dialog.
 * @param title the title of the dialog box.
 */
  public LoginDialog(Frame parent_frame, String title)
  {
    this(parent_frame,title,null);
  }
  
//----------------------------------------------------------------------
/**
 * Constructor using the title for the dialog box and additional
 * message object(s). The dialog will be centered on the parent frame.
 *
 * @param parent_frame the parent frame used for placing the dialog.
 * @param title the title of the dialog box.
 * @param message the message object may be a simple string, but may
 * also be a array of elements. See {@link javax.swing.JOptionPane}
 * for details.
 */
  public LoginDialog(Frame parent_frame, String title, Object message)
  {
    Vector messages = new Vector();
    if(message != null)
      messages.add(message);
    messages.add(username_title_);
    messages.add(username_textfield_ = new JTextField(20));
    messages.add(password_title_);
    messages.add(password_textfield_ = new JPasswordField(20));

    Object[] options = {new Integer(OK_OPTION), new Integer(CANCEL_OPTION)};

    option_pane_ = new JOptionPane(messages.toArray(),
                                   JOptionPane.QUESTION_MESSAGE,
                                   JOptionPane.OK_CANCEL_OPTION);

    JDialog dialog = option_pane_.createDialog(parent_frame,title);
    dialog.show();
  }

//----------------------------------------------------------------------
/**
 * Returns the result of the dialog (OK, CANCEL or CLOSED).
 *
 * @return the result of the dialog (OK, CANCEL or CLOSED).
 */
  public int getValue()
  {
    Object result = option_pane_.getValue();
    if(result == null)
      return(CLOSED_OPTION);
    return(((Integer)result).intValue());
  }
  
//----------------------------------------------------------------------
/**
 * Get the username.
 *
 * @return the username.
 */
  public String getUsername() 
  {
    return (username_textfield_.getText());
  }
  
//----------------------------------------------------------------------
/**
 * Set the username.
 *
 * @param username the username.
 */
  public void setUsername(String username) 
  {
    username_textfield_.setText(username);
  }
  
//----------------------------------------------------------------------
/**
 * Get the password.
 *
 * @return the password.
 */
  public String getPassword() 
  {
    return (new String(password_textfield_.getPassword()));
  }
  
//----------------------------------------------------------------------
/**
 * Set the password.
 *
 * @param password the password.
 */
  public void setPassword(String password) 
  {
    password_textfield_.setText(password);
  }


  public static void main(String[] args)
  {
    LoginDialog dialog = new LoginDialog(null,"Login","Enter your name/password");
    System.out.println("after dialog");
    int result = dialog.getValue();
    System.out.println("result: "+result);
    if(result == LoginDialog.CLOSED_OPTION)
      System.out.println("closed");
    else
      if(result == LoginDialog.CANCEL_OPTION)
        System.out.println("canceled");
      else
      {
        System.out.println("username: "+dialog.getUsername());
        System.out.println("password: "+dialog.getPassword());
      }
    dialog = null;
  }
}






