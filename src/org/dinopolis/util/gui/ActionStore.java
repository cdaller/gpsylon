/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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
 
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.Action;

//----------------------------------------------------------------------
/**
 * This class represents a container for arbitrary actions.
 * ActionStores are bound under a unique name and may be requested
 * with this name again.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class ActionStore implements ActionGenerator
{
  /** all possible actions */
  private Hashtable actions_ = new Hashtable();
  
  /** all possible actions instances */
  private static Hashtable instances_ = new Hashtable();

  //----------------------------------------------------------------------
  /**
   * Creates a new ActionStore that is bound under the given name
   * <code>key</code>.
   *
   * @param key the name to bind this ActionStore.
   */

  private ActionStore(String key)
  {
    instances_.put(key, this);
  }

  //----------------------------------------------------------------------
  /**
   * Returns the store that is bound under the given name, or null if
   * no store is bound under this name.
   *
   * @param key the key to get the bound store.
   * @return the store that is bound under the given name, or null if
   * no store is bound under this name.
   */

  public static ActionStore getStore(String key)
  {
    ActionStore ret = (ActionStore)instances_.get(key);
    if (ret == null)
      ret = new ActionStore(key);
    return(ret);
  }

  //----------------------------------------------------------------------
  /**
   * Adds the given action to this store.
   *
   * @param action the action to add.
   */

  public void addAction(Action action)
  {
    actions_.put(action.getValue(Action.NAME), action);
  }

  //----------------------------------------------------------------------
  /**
   * Adds the given actions to this store.
   *
   * @param actions the actions to add.
   */

  public void addActions(Action[] actions)
  {
    for (int count = 0; count < actions.length; count++) 
      addAction(actions[count]);
  }

  //----------------------------------------------------------------------
  /**
   * Removes the given action from this store.
   *
   * @param action the action to remove.
   */

  public void removeAction(Action action)
  {
    actions_.remove(action.getValue(Action.NAME));
  }

  //----------------------------------------------------------------------
  /**
   * Returns the action registered under <code>key</code> within this
   * ActionStore, or null if no action was found.
   *
   * @param key the name of the action to look for.
   * @return the action registered under <code>key</code>.
   */

  public Action getAction(String key)
  {
    return((Action)actions_.get(key));
  }

  //----------------------------------------------------------------------
  /**
   * Returns an enumeration of all action keys within this
   * ActionStore.
   *
   * @return an enumeration of the keys.
   */

  public Enumeration getActionKeys()
  {
    return(actions_.keys());
  }

//----------------------------------------------------------------------
/**
 * @return the string representation
 */

  public String toString()
  {
    StringBuffer ret = new StringBuffer(); 
    ret.append("[");
    Enumeration enum = actions_.keys();
    while (enum.hasMoreElements())
    {
      ret.append(enum.nextElement());
      if (enum.hasMoreElements())
        ret.append(", ");
    }
    ret.append("]");
    return(ret.toString());
  }
}
