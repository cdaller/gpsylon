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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * This class represents a menu factory. It reads values from a
 * resource file and generates menues accordingly.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class MenuFactory  
{
//    /** the identifier for the currently selected toggle-menues */
//    public final static String SELECT = "select";
  /** the identifier for selected checkboxes or radio buttons */
  public final static String SELECTED = "selected";
  
  /** the key for the menu resource */
  public final static String KEY_MENUES = "menu";

  /** the key for the menu resource */
  public final static String KEY_MENUE_PREFIX = KEY_MENUES+".";
  
  /** the suffix used for Labels in resource files */
  public final static String KEY_RESOURCE_LABEL_SUFFIX = ".label";
  
  /** the suffix used for Icons in resource files */
  public final static String KEY_RESOURCE_ICON_SUFFIX = ".icon";

  /** the suffix used for Actions in resource files */
  public final static String KEY_RESOURCE_ACTION_SUFFIX = ".action";
  
  /** the suffix used for types in resource files */
  public final static String KEY_RESOURCE_TYPE_SUFFIX = ".type";
  
  /** the suffix used for Icons in resource files */
  //  public final static String KEY_RESOURCE_SELECTED_SUFFIX = ".selected";

  /** the key for the context menu prefix */
  public final static String KEY_CONTEXT_MENU_PREFIX = "contextmenu.";

  /** the identifier for boxes within the resource file */
  public final static String BOX_IDENTIFIER = "|";

  /** the identifier for boxes within the resource file */
  public final static String SEPARATOR_IDENTIFIER = "-";

  /** the identifier for toggle-menues within the resource file */
  public final static String OR_SEPERATOR = "|";

  /** the key for the accelerator menu prefix */
  public final static String KEY_RESOURCE_ACCELERATOR_SUFFIX = ".accelerator";
  
  /** the key for the accelerator menu prefix */
  public final static String KEY_RESOURCE_MNEMONIC_SUFFIX = ".mnemonic";

  /** the type identifier for menus */
  public final static String TYPE_MENU = "menu";

  /** the type identifier for menus */
  public final static String TYPE_EMPTY_MENU = "empty_menu";

  /** the type identifier for radio buttons */
  public final static String TYPE_RADIO = "radio";

  /** the type identifier for checkboxes */
  public final static String TYPE_CHECK = "check";

  /** the type identifier for items (default) */
  public final static String TYPE_ITEM = "item";

  //----------------------------------------------------------------------
  /**
   * Creates a menubar from the given resource string.
   *
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   */

  public static JMenuBar createMenuBar(Resources resources,
                                       ActionGenerator action_gen)
  {
    JMenuBar menu_bar = new JMenuBar();
    String[] menues = resources.getStringArray(KEY_MENUES, " ");
    //    boolean 
    for (int count = 0; count < menues.length; count ++)
    {
      if (menues[count].equals(BOX_IDENTIFIER))
      {
        if (menu_bar.getLayout() instanceof BoxLayout)
          menu_bar.add(Box.createHorizontalGlue());
      }
      else
        menu_bar.add(createMenu(KEY_MENUE_PREFIX, menues[count],
                                resources, action_gen));
    }
    return(menu_bar);
  }

  //----------------------------------------------------------------------
  /**
   * Creates a menu from the given resource string.
   *
   * @param prefix the prefix used to identify the menu.
   * @param menu_name the string the menu should be created from.
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu.
   */

  public static JMenu createMenu(String prefix,
                                    String menu_name,
                                    Resources resources,
                                    ActionGenerator action_gen)
  {
        // check for empty menu:
    String type = resources.getString(prefix+menu_name+KEY_RESOURCE_TYPE_SUFFIX, "");
    if(TYPE_EMPTY_MENU.equalsIgnoreCase(type))
    {
      JMenu menu = new JMenu();
      initializeMenu(menu,prefix,menu_name,resources);
      return(menu);
    }
    
    String[] item_keys = resources.getStringArray(prefix+menu_name, " ,");
    if (item_keys.length<1)
      return (null);
    JMenu menu = new JMenu();
    initializeMenu(menu, prefix, menu_name, resources); 

    for (int count = 0; count < item_keys.length; count++)
    {
      JComponent comp = createMenuComponent(prefix, item_keys[count],
                                            resources, action_gen); 
      if (comp != null)
        menu.add(comp);
    }
    return(menu);
  }

  //----------------------------------------------------------------------
  /**
   * Creates a popup menu from the given resource string.
   *
   * @param prefix the prefix used to identify the menu.
   * @param menu_string the string the popup menu should be created
   * from. 
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu.
   */
    
  public static JPopupMenu createPopupMenu(String prefix,
                                           String menu_string,
                                           Resources resources,
                                           ActionGenerator action_gen) 
  {
    StringTokenizer tok = new StringTokenizer(menu_string);
    // create the popup menu
    JPopupMenu popup_menu = new JPopupMenu();
    
    String menu_item_string;
    while (tok.hasMoreTokens())
    {
      menu_item_string = tok.nextToken();
      popup_menu.add(createMenuComponent(prefix,menu_item_string, 
                                         resources, action_gen));
    }
    return(popup_menu);
  }

  //----------------------------------------------------------------------
  /**
   * Creates a popup menu from the given resource string.
   *
   * @param menu_string the string the popup menu should be created
   * from. 
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu.
   */
    
  public static JPopupMenu createPopupMenu(String menu_string,
                                           Resources resources,
                                           ActionGenerator action_gen) 
  {
    return(createPopupMenu(KEY_CONTEXT_MENU_PREFIX, menu_string, resources, action_gen));
  }

  //----------------------------------------------------------------------
  /**
   * Creates a single menu item.
   *
   * @param prefix the prefix for menues used within the resource
   * file.
   * @param menu_name the name of the menu.
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu item.
   */
 
  public static JComponent createMenuComponent(String prefix,
                                                  String menu_name, 
                                                  Resources resources,
                                                  ActionGenerator action_gen) 
  {
    if (menu_name.trim().equals(SEPARATOR_IDENTIFIER))
      return(new JSeparator());

    String type = resources.getString(prefix+menu_name+KEY_RESOURCE_TYPE_SUFFIX,
                                      TYPE_ITEM); 
      
    if (type.equalsIgnoreCase(TYPE_MENU)) 
      return(createMenu(prefix, menu_name, resources,
                        action_gen));
    if (type.equalsIgnoreCase(TYPE_RADIO)) 
      return(createRadioButtonMenuItem(prefix, menu_name,
                                       resources, action_gen));
    if (type.equalsIgnoreCase(TYPE_CHECK)) 
      return(createCheckBoxMenuItem(prefix, menu_name,
                                    resources, action_gen));
    if(type.equalsIgnoreCase(TYPE_EMPTY_MENU))
    {
      JMenu menu = new JMenu();
      initializeMenu(menu,prefix,menu_name,resources);
      return(menu);
    }
    
    // TYPE_ITEM (normal menu entry)
    return(createMenuItem(prefix, menu_name, resources, action_gen));
  }

  //----------------------------------------------------------------------
  /**
   * @param prefix the prefix
   * @param menu_names the name(s) of the menu.
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu item.
   */
 
  public static JComponent createMenuItem(String prefix,
                                             String menu_names, 
                                             Resources resources,
                                             ActionGenerator action_gen) 
  {
    StringTokenizer tok = new StringTokenizer(menu_names, OR_SEPERATOR);
    JMenuItem menu_item = new JMenuItem();
  
    MenuItemActionChangedListener action_listener = new MenuItemActionChangedListener(menu_item);

    Action action_to_set = null;
    Action action = null;
    String menu_name;
    
    while (tok.hasMoreTokens())
    {
      menu_name = tok.nextToken();
      action = initializeMenuItem(menu_item, prefix,
                                         menu_name, resources,
                                         action_gen);
      
      if (action != null) 
      {
        if ((action_to_set == null) || (action.isEnabled()))
          action_to_set = action;

        action.addPropertyChangeListener(action_listener);
      }
      else
      {
        System.err.println("WARNING: MenuFactory.createMenuItem: action for '"+prefix+menu_name+
                           "' with name '"+resources.getString(prefix+menu_names+KEY_RESOURCE_ACTION_SUFFIX
                                                               , "")+
                           "' not found in action generator.");
      }

    }
    
    if (action_to_set != null)
      action_listener.setSelected(action_to_set);

    return(menu_item);
  }

  //----------------------------------------------------------------------
  /**
   * @param prefix the prefix
   * @param menu_name the name of the menu.
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu item.
   */
 
  public static JComponent createRadioButtonMenuItem(String prefix,
                                                        String menu_name, 
                                                        Resources resources,
                                                        ActionGenerator action_gen) 
  {
    JRadioButtonMenuItem radio_menu_item = new JRadioButtonMenuItem();    
    Action action = initializeMenuItem(radio_menu_item, prefix,
                                       menu_name, resources,
                                       action_gen);
    if (action != null)
    {
      MenuButtonActionChangedListener action_listener = new MenuButtonActionChangedListener(radio_menu_item);
      action.addPropertyChangeListener(action_listener);

      Object selected = action.getValue(SELECTED);
      if ((selected != null) && (selected instanceof Boolean))
        radio_menu_item.setSelected(((Boolean)selected).booleanValue());
    }
    else
      System.err.println("WARNING: MenuFactory.createRadioButtonMenuItem: action for '"+prefix+menu_name
                           +"' with name '"
                           +resources.getString(prefix+menu_name+KEY_RESOURCE_ACTION_SUFFIX, menu_name)
                           +"' not found in action generator.");
    return(radio_menu_item);
  }

  //----------------------------------------------------------------------
  /**
   * @param prefix the prefix
   * @param menu_name the name of the menu.
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the new created menu item.
   */
 
  public static JComponent createCheckBoxMenuItem(String prefix,
                                                     String menu_name, 
                                                     Resources resources,
                                                     ActionGenerator action_gen) 
  {
    //    static String xxx = menu_name;
    final JCheckBoxMenuItem checkbox_menu_item = new JCheckBoxMenuItem();

    Action action = initializeMenuItem(checkbox_menu_item, prefix,
                                       menu_name, resources,
                                       action_gen); 
    if (action != null)
    {
      MenuButtonActionChangedListener action_listener = new MenuButtonActionChangedListener(checkbox_menu_item);
      action.addPropertyChangeListener(action_listener);
      Object selected = action.getValue(SELECTED);
      if ((selected != null) && (selected instanceof Boolean))
        checkbox_menu_item.setSelected(((Boolean)selected).booleanValue());
    }
    else
      System.err.println("WARNING: MenuFactory.createCheckBoxMenuItem: action for '"+prefix+menu_name
                           +"' with name '"
                           +resources.getString(prefix+menu_name+KEY_RESOURCE_ACTION_SUFFIX, menu_name)
                           +"' not found in action generator.");
    return(checkbox_menu_item);
  }

  //----------------------------------------------------------------------
  /**
   * Initializes the given menu item with all its parameters.
   *
   * @param menu_item the menu item to initialize.
   * @param prefix the prefix
   * @param menu_name the name(s) of the menu.
   * @param resources the resource file to read the required resources
   * from. 
   * @param action_gen the ActionGenerator to generate the required
   * action.
   * @return the bound action or null if no action was found.
   */
 
  protected static Action initializeMenuItem(JMenuItem menu_item, 
                                             String prefix,
                                             String menu_name, 
                                             Resources resources,
                                             ActionGenerator action_gen) 
  {
    String action_name =
      resources.getString(prefix+menu_name+KEY_RESOURCE_ACTION_SUFFIX, menu_name);

    String menu_item_label =
      resources.getString(prefix+menu_name+KEY_RESOURCE_LABEL_SUFFIX,
                          null);

    Icon menu_item_icon = resources.getIcon(prefix+menu_name+KEY_RESOURCE_ICON_SUFFIX,
                                            null);
    if ((menu_item_label == null) && (menu_item_icon == null)) 
      menu_item_label = prefix+menu_name; // default is name!
    menu_item.setText(menu_item_label);
    Action action = action_gen.getAction(action_name);
    
    if (action != null) 
    {
      action.putValue(Action.SMALL_ICON, menu_item_icon);
      action.putValue(Action.NAME, menu_item_label);
      action.putValue(Action.ACTION_COMMAND_KEY, action_name);

//       if (action.getValue(SELECTED) != null)
//         action.putValue(SELECTED, new Boolean(resources.getBoolean(prefix+menu_name+
//                                                                    KEY_RESOURCE_SELECTED_SUFFIX, 
//                                                                    true)));
      
      // Mnemonic
      String mnemonic =
        resources.getString(prefix+menu_name+KEY_RESOURCE_MNEMONIC_SUFFIX, null);
      if ((mnemonic != null) && (mnemonic.length() > 0))
      {
        action.putValue(Action.MNEMONIC_KEY, new Integer(mnemonic.charAt(0)));
//        menu_item.setMnemonic(mnemonic.charAt(0));
      }
      // Accelerator
      String accelerator =
        resources.getString(prefix+menu_name+KEY_RESOURCE_ACCELERATOR_SUFFIX,
                            null);

      if (accelerator != null)
      {
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
//        menu_item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
      }
      menu_item.setAction(action);
    }
    else 
    {
      menu_item.setEnabled(false);
    }
    return(action);
  }

  //----------------------------------------------------------------------
  /**
   * Initializes the given menu item with all its parameters.
   *
   * @param menu the menu to initialize.
   * @param prefix the prefix
   * @param menu_name the name(s) of the menu.
   * @param resources the resource file to read the required resources
   * from. 
   */
 
  protected static void initializeMenu(JMenu menu, 
                                       String prefix,
                                       String menu_name, 
                                       Resources resources)
  {
    String menu_label =
      resources.getString(prefix+menu_name+KEY_RESOURCE_LABEL_SUFFIX,
                          null);

    Icon menu_icon = resources.getIcon(prefix+menu_name+KEY_RESOURCE_ICON_SUFFIX,
                                            null);
    if ((menu_label == null) && (menu_icon == null)) 
      menu_label = prefix+menu_name; // default is name!

    menu.setText(menu_label);
    if (menu_icon != null)
      menu.setIcon(menu_icon);
    
    // Mnemonic
    String mnemonic =
      resources.getString(prefix+menu_name+KEY_RESOURCE_MNEMONIC_SUFFIX, null);
    if ((mnemonic != null) && (mnemonic.length() > 0))
      menu.setMnemonic(mnemonic.charAt(0));
  }
}

// Yarked from JMenu, ideally this would be public.
//----------------------------------------------------------------------
/**
 * The item change listener for menues to change the menues properties
 * accordingly.
 */

class MenuItemActionChangedListener implements PropertyChangeListener 
{
  /** the corresponding menu item */
  private JMenuItem menu_item_;

  /** the corresponding action */
  private Action selected_;

  //----------------------------------------------------------------------
  /**
   * Constructor taking the menu item as its argument.
   *
   * @param menu_item the corresponding menu item.
   */
    
  MenuItemActionChangedListener(JMenuItem menu_item) 
  {
    super();
    menu_item_ = menu_item;
  }

  //----------------------------------------------------------------------
  /**
   * Changes the menu item's state according to the given value
   * <code>action</code>.
   *
   * @param action the selected action.
   */

  void setSelected(Action action)
  {
    selected_ = action;
    menu_item_.setAction(action);
  }

  // from PropertyChangeListener - interface
  //----------------------------------------------------------------------
  /**
   * Invoked on property change events received from the Action.
   * This will update the visualization of the menu item.
   *
   * @param event the property change event.
   */
  
  public void propertyChange(PropertyChangeEvent e) 
  {
    Action action = (Action)e.getSource();
    String property_name = e.getPropertyName();

    if ((selected_ != action) && (property_name.equals("enabled")))
    {
      boolean enabled = ((Boolean)e.getNewValue()).booleanValue();
      if (enabled)
      {
        if (selected_ != null)
          selected_.setEnabled(false);
        setSelected(action);
      }
    }
  }
}

// Yarked from JMenu, ideally this would be public.
//----------------------------------------------------------------------
/**
 * The selection change listener for menue buttons to change the
 * menues properties accordingly.
 */

class MenuButtonActionChangedListener implements PropertyChangeListener 
{
  /** the corresponding menu item */
  private JMenuItem menu_item_;

  //----------------------------------------------------------------------
  /**
   * Constructor taking the menu item as its argument.
   *
   * @param menu_item the corresponding menu item.
   */
    
  MenuButtonActionChangedListener(JMenuItem menu_item) 
  {
    super();
    menu_item_ = menu_item;
  }

  // from PropertyChangeListener - interface
  //----------------------------------------------------------------------
  /**
   * Invoked on property change events received from the Action.
   * This will update the visualization of the menu item.
   *
   * @param event the property change event.
   */
  
  public void propertyChange(PropertyChangeEvent e) 
  {
    String property_name = e.getPropertyName();
    if (MenuFactory.SELECTED.equals(property_name))
      menu_item_.setSelected(((Boolean)e.getNewValue()).booleanValue());
  }
}
