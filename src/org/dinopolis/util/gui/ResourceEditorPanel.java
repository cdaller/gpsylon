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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.property_editor.ArrayEditor;
import org.dinopolis.util.gui.property_editor.BooleanEditor;
import org.dinopolis.util.gui.property_editor.DoubleEditor;
import org.dinopolis.util.gui.property_editor.IntEditor;
import org.dinopolis.util.gui.property_editor.StringChooser;
import org.dinopolis.util.gui.property_editor.StringSelector;
import org.dinopolis.util.resource.ResourceGroup;

//----------------------------------------------------------------------
/**
 * A Class the provides a Property Editor for the properties of a
 * 'Map'.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class ResourceEditorPanel extends JPanel
implements ActionListener, PropertyChangeListener
{
  /** the name of my resource file */
  public final static String RESOURCE_BOUNDLE_NAME = "ResourceEditor";

  /** the name of my resource directory */
  public final static String RESOURCE_DIR_NAME = "resources";

  /** the resources to edit */
  private static Resources edit_resources_;
  
  /** my private resources */
  private static Resources my_resources_;

  /** the key for the apply button resource */
  public final static String KEY_APPLY_BUTTON = "resource_editor.button.apply";

  /** the apply command name */
  public final static String APPLY_COMMAND = "apply";

  /** the key for the buttons insets resource */
  public final static String KEY_BUTTONS_INSETS = "buttons.insets";

  /** the title of the default tab */
  public final static String KEY_TAB_DEFAULT = "resource_editor.tab.default";

  /** the search path key to find PropertyEditors */
  public final static String KEY_PROPERTY_EDITOR_SEARCH_PATH  = "resource_editor.editor_search_path";

  /** the key for the keys that are to be hided */
  public final static String KEY_HIDE_RESOURCES = "resources.hidden";

  /** the panel holding all editors */
  private JTabbedPane editor_panel_;

  /** the button insets */
  private Insets button_insets_;

  /** the apply button */
  private JButton apply_button_;

  /** a map holding all property keys and there corresponding
   * values that are to be displayed. */
  private HashMap key_editor_map_;

  /** a set holding all property keys that are not to be displayed */
  private HashSet hide_key_set_;

  /** the property editor manager */
  private PropertyEditorManager property_editor_manager_;

  /** the property editor manager default search path */
  private String[] property_editor_manager_default_search_path_;

  /** a vector containing all type save editors */
  private Vector type_save_;

  /** the swing worker creating the panel */
  private Thread update_thread_;

//----------------------------------------------------------------------
/**
 * Creates a new ResourceEditorPanel that is able to edit the
 * resources given in <code>resources</code>.
 *
 * @param resources the reource bounble to edit.
 */

  public ResourceEditorPanel(Resources resources)
  {
    this(resources, RESOURCE_DIR_NAME);
  }

//----------------------------------------------------------------------
/**
 * Creates a new ResourceEditorPanel that is able to edit the
 * resources given in <code>resources</code>. The private resource
 * file is searched in resource_dir, relative to the users home dir.
 *
 * @param resources the reource bounble to edit.
 * @param resource_dir the relative directory to search users
 * resources. 
 */

  public ResourceEditorPanel(Resources resources, String resource_dir)
    throws MissingResourceException
  {
    this(resources, resource_dir, true);
  }

//----------------------------------------------------------------------
/**
 * Creates a new ResourceEditorPanel that is able to edit the
 * resources given in <code>resources</code>. The private resource
 * file is searched in resource_dir, relative to the users home dir.
 *
 * @param resources the reource bounble to edit.
 * @param resource_dir the relative directory to search users
 * resources. 
 * @param show_apply_button true if an apply button should be
 * visualized, false otherwise.
 */

  public ResourceEditorPanel(Resources resources, String resource_dir,
                             boolean show_apply_button)
    throws MissingResourceException
  {
    super();
    my_resources_ = ResourceManager.getResources(getClass(),
                                                 RESOURCE_BOUNDLE_NAME, 
                                                 resource_dir, 
                                                 Locale.getDefault());
    my_resources_.addPropertyChangeListener(this);
    edit_resources_ = resources;
    type_save_ = new Vector();

    property_editor_manager_ = new PropertyEditorManager();
    // set our primitive editors here
    PropertyEditorManager.registerEditor(Integer.TYPE, IntEditor.class);
	PropertyEditorManager.registerEditor(Double.TYPE, DoubleEditor.class);
	PropertyEditorManager.registerEditor(Boolean.TYPE, BooleanEditor.class);

    property_editor_manager_default_search_path_ =
	PropertyEditorManager.getEditorSearchPath();

    hide_key_set_ = new HashSet();
    key_editor_map_ = new HashMap();
    updateResources();

    setLayout(new BorderLayout());

    
    editor_panel_ = new JTabbedPane();
    add(editor_panel_, BorderLayout.CENTER);
    
    Component buttons = createButtons();
    if (show_apply_button)
      add(buttons, BorderLayout.SOUTH);
    //    setAllEditors();
  }

  //----------------------------------------------------------------------
  /**
   * @return returns the resources for resource editors.
 */

  public static Resources getResources()
  {
    return(my_resources_);
  }

  //----------------------------------------------------------------------
  /**
   */

  public void updateResources()
  {
    String[] seach_path =
      my_resources_.getStringArray(KEY_PROPERTY_EDITOR_SEARCH_PATH, (String[])null);
    if (seach_path != null)
    {
      String[] addeed_path = new
        String[property_editor_manager_default_search_path_.length+seach_path.length]; 
      System.arraycopy(seach_path, 0, addeed_path,
                       0, seach_path.length);
      System.arraycopy(property_editor_manager_default_search_path_,
                       0, addeed_path, 
                       property_editor_manager_default_search_path_.length, property_editor_manager_default_search_path_.length);
		PropertyEditorManager.setEditorSearchPath(addeed_path);
    }
    if (apply_button_ != null)
    {
      int insets = my_resources_.getInt(KEY_BUTTONS_INSETS);
      button_insets_ = new Insets(insets,insets,insets,insets);
      apply_button_.setText(my_resources_.getString(KEY_APPLY_BUTTON));
      apply_button_.setMargin(button_insets_);
    }
    hide_key_set_.clear();
    String[] to_hide =
      edit_resources_.getStringArray(KEY_HIDE_RESOURCES, new String[0]);
    for (int count = 0; count < to_hide.length; count++)
      hide_key_set_.add(to_hide[count]);
  }

  //----------------------------------------------------------------------
  /**
   * Register an editor class to be used to editor values of
   * a given target class.
   * 
   * 
   * @param type the Class object of the type to be edited.
   * @param editor the Class object of the editor class.  If
   *	   this is null, then any existing definition will be removed.
   * @exception  SecurityException  if a security manager exists and its  
   * <code>checkPropertiesAccess</code> method doesn't allow setting
   * of system properties.
   */

  public void registerEditor(Class type, Class editor) 
  {
	PropertyEditorManager.registerEditor(type, editor);
  }

//----------------------------------------------------------------------
/**
 * Gets a PropertyEditor for the given key.
 *
 * @param key the name of the resource to create the editor from.
 * @return the property editor for the given key.
 */

  protected PropertyEditor getEditor(String key)
  {
    PropertyEditor editor = (PropertyEditor)key_editor_map_.get(key);
    if (editor != null)
      return(editor);
    
    editor = createTypeSaveEditor(key);
    
    if (editor == null)
      editor = createStringEditor(key);
    else
      type_save_.add(key);

    if (editor != null)
    {
      key_editor_map_.put(key, editor);
      editor.addPropertyChangeListener(this);
    }
    return(editor);
  }

//----------------------------------------------------------------------
/**
 * Creates a new PropertyEditor for the given key.
 *
 * @param key the name of the resource to create the editor from.
 * @return the property editor for the given key.
 */

  protected PropertyEditor createTypeSaveEditor(String key)
  {
    Class type = edit_resources_.getType(key);
    if (type == null)
      return(null);

    PropertyEditor editor = PropertyEditorManager.findEditor(type);

    if (editor != null)
      return(editor);

    if (type.isArray())
    {
      type = type.getComponentType();
      editor = PropertyEditorManager.findEditor(type);
      if (editor != null)
        return (new ArrayEditor(type, editor));
    }
    return(null); // no editor found!
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key to get the StringEditor for
   * @return the String property editor.
   */

  protected PropertyEditor createStringEditor(String key)
  {
    return(PropertyEditorManager.findEditor(String.class));
  }
  
//----------------------------------------------------------------------
/**
 * @param editor the property editor to get the editing or view component from
 * @return the corresponding component.
 */

  protected Component getEditingOrViewComponent(String key,
                                                PropertyEditor editor) 
  {
    if (editor == null)
      return(null); // paranoia
    String[] possible_values = edit_resources_.getPossibleValues(key);
    
    if (possible_values != null)
    {
      Class type = edit_resources_.getType(key);
      String[] values = edit_resources_.getPossibleValues(key);
      String[] titles = new String[values.length];
      for (int count = 0; count < titles.length; count++)
        titles[count] = edit_resources_.getTitle(key+"."+values[count]);

      if ((type != null) && (type.isArray()))
        return(new StringSelector(editor, values, titles));
      return(new StringChooser(editor, values, titles));
    }
    if (editor.getTags() != null)
    {
      Class type = edit_resources_.getType(key);
      if ((type != null) && (type.isArray()))
        return(new StringSelector(editor));
      return(new StringChooser(editor));
    }
    Component editor_component = editor.getCustomEditor();
    if (editor_component != null)
      return(editor_component);
    
    return(null); // no editing component found !
  }

  //----------------------------------------------------------------------
  /**
   * @return the apply button
   */

  public JButton getApplyButton()
  {
    return(apply_button_);
  }

  //----------------------------------------------------------------------
  /**
   * @return the button insets.
   */

  public Insets getButtonInsets()
  {
    return(button_insets_);
  }

//----------------------------------------------------------------------
/**
 * @return the panel that contains the close button
 */

  protected Component createButtons()
  {
    JPanel button_panel = new JPanel();
    button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.X_AXIS));
    button_panel.add(Box.createGlue());

    int insets = my_resources_.getInt(KEY_BUTTONS_INSETS);
    button_insets_ = new Insets(insets,insets,insets,insets);

        // add the apply button
    apply_button_ = new JButton(my_resources_.getString(KEY_APPLY_BUTTON));
    apply_button_.setMargin(button_insets_);
    apply_button_.setActionCommand(APPLY_COMMAND);
    apply_button_.addActionListener(this);
    button_panel.add(apply_button_);

    button_panel.add(Box.createGlue());
    return(button_panel);
  }



//----------------------------------------------------------------------
/**
 * Updates the whole editor and shows all components for the given
 * Map. This method uses a swing worker to create all components in
 * background.
 *
 * @param node the node to show the properties.
 */
  public void updateEditors()
  {
    if (update_thread_ == null)
    {
      update_thread_ = new Thread() 
        {
          public void run()
          {
            while (true)
            {
              try
              {
                synchronized (this)
                {
                  setAllEditors();
                  wait();
                }
              }
              catch (InterruptedException exc)
              {
              }
            }
          }
        };
      update_thread_.start();
    }
    else
    { 
      synchronized (update_thread_)
      {
        update_thread_.notify();
      }
    }
  }

  
//----------------------------------------------------------------------
/**
 * Updates the whole editor and shows all components for the given
 * Map.
 *
 * @param node the node to show the properties.
 */

  protected void setAllEditors()
  {
    editor_panel_.removeAll();
    key_editor_map_.clear();

    TreeSet keys = new TreeSet();
    Enumeration enum = edit_resources_.getKeys();
    while (enum.hasMoreElements())
      keys.add((String)enum.nextElement());

    // extract groups
    String[] groups = getChildren(Resources.GROUPS);
    
    String group_or_element;
    String sub_group_or_element;
    String sub_sub_group_or_element;
    String[] sub_groups;
    String[] sub_sub_groups;
    boolean hide = false;

    for (int count = 0; count < groups.length; count++)
    {
      group_or_element = groups[count];
      if (isResourceGroup(group_or_element))
      {
        hide = isToHide(group_or_element); 
        sub_groups = getChildren(group_or_element);
        if (sub_groups.length > 0)
        {
          JPanel group = null;
          for (int sub_count = 0; sub_count < sub_groups.length;
               sub_count++)
          {
            sub_group_or_element = sub_groups[sub_count];
            hide = hide && isToHide(sub_group_or_element); 
            if (isResourceGroup(sub_group_or_element))
            {
              sub_sub_groups = getChildren(sub_group_or_element);
              if (sub_sub_groups.length > 0)
              {
                JPanel sub_group = null;
                for (int sub_sub_count = 0; sub_sub_count < sub_sub_groups.length;
                     sub_sub_count++)
                {
                  sub_sub_group_or_element = sub_sub_groups[sub_sub_count];
                  hide = hide && isToHide(sub_sub_group_or_element); 
                  if (!isResourceGroup(sub_sub_group_or_element))
                  {
                    if (!hide)
                    {
                      if (group == null)
                      {
                        group = createTab(group_or_element);
                        editor_panel_.addTab(getTitle(group_or_element), createTabWrapper(group));        
                      }
                      if (sub_group == null)
                      {
                        sub_group = createGroup(sub_group_or_element);
                        group.add(sub_group);
                      }
                      addEditor(sub_sub_group_or_element, sub_group);
                    }
                    keys.remove(sub_sub_group_or_element); // to not add editors twice
                  }
                }
              }
            }
            else
            {
              if (!hide)
              {
                if (group == null)
                {
                  group = createTab(group_or_element);
                  editor_panel_.addTab(getTitle(group_or_element), createTabWrapper(group));        
                }
                addEditor(sub_group_or_element, group);
              }
              keys.remove(sub_group_or_element); // to not add editors twice
            }
          }
        }
      }
//       if(Thread.interrupted())  // check for interruption of swing worker
//         return;
    }
    if (!isToHide(KEY_TAB_DEFAULT))
    {
      JPanel default_panel = null;

      String key;
      Iterator iterator = keys.iterator();
      while (iterator.hasNext())
      {
        key = (String)iterator.next();
        if (!isToHide(key))
        {
          if (default_panel == null)
          {
            default_panel = createTab("default");
            editor_panel_.addTab(my_resources_.getString(KEY_TAB_DEFAULT),createTabWrapper(default_panel));
          }
          addEditor(key, default_panel);
        }
      }
    }
    apply_button_.setEnabled(false);
    invalidate();
    validate();
    repaint();
  }


  //----------------------------------------------------------------------
  /**
   * @param key the key of the resources, an editor should be created and added for.
   * @param panel the panel to add the editor to.
   * @return 
   */

  private void addEditor(String key,JPanel panel)
  {
    PropertyEditor editor = getEditor(key);
    if (editor == null)
      return;
    String title = edit_resources_.getTitle(key);
    if (title == null)
      title = key;
    String description = edit_resources_.getDescription(key);
    boolean value_set = false;
    try
    {
      if (type_save_.contains(key))
      {
        try
        {
          Object value = edit_resources_.get(key);
          editor.setValue(value);
        }
        catch (MissingResourceException exc)
        {
          editor.setValue(null);
        }
        value_set = true;
      }
    }
    catch (IllegalArgumentException exc)
    {
    }
    if (!value_set)
    {
      String string_value = "";
      try
      {
        string_value = edit_resources_.getString(key);
      }
      catch (MissingResourceException exc)
      {
      }
      try
      {
        editor.setAsText(string_value);
      }
      catch (IllegalArgumentException exc)
      {
        System.err.println(exc.getMessage());
        return;
      }
    }

    // add the component
    Component comp = getEditingOrViewComponent(key, editor);
    if (comp != null)
    {
      JPanel container_panel = new JPanel();
      container_panel.setLayout(new BorderLayout());
      container_panel.setBorder(BorderFactory.createTitledBorder(title));
      container_panel.add(comp, BorderLayout.CENTER);
      
      Icon info_icon = my_resources_.getIcon("resource_editor.info_icon");
      JButton info_button = new JButton(info_icon);
      info_button.setMargin(new Insets(0,0,0,0));
      info_button.addActionListener(new AbstractAction(description)
        {
          //----------------------------------------------------------------------
          /**
           * Invoked when the info button was pressed.
           *
           * @param event the action event
           */

          public void actionPerformed(ActionEvent event)
          {
            JOptionPane.showMessageDialog(ResourceEditorPanel.this, 
                                          (String)getValue(NAME),
                                          "Description",
                                          JOptionPane.INFORMATION_MESSAGE);
          }
        }
                                  );
      info_button.setEnabled(description != null);
      container_panel.add(info_button, BorderLayout.EAST);
      
//        if (description != null)
//          container_panel.setToolTipText(description);
      panel.add(container_panel);
    }
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key of the resource to check if it is a group.
   * @return if the given key represents a group, false otherwise.
   */

  private boolean isResourceGroup(String key)
  {
    return(ResourceGroup.class == edit_resources_.getType(key));
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key of the group to get all children from.
   * @return all children, guaranteed to be non-null.
   */

  private String[] getChildren(String key)
  {
    return(edit_resources_.getStringArray(key, new String[0]));
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key of the group to cteate the tab for.
   * @return a pannel, representing a tab of a JTabbedPane for the
   * given key.
   */

  private JPanel createTab(String key)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    return(panel);
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key of the group to cteate the tab for.
   * @return a pannel, representing a tab of a JTabbedPane for the
   * given key.
   */

  private Component createTabWrapper(JPanel tab)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(tab, BorderLayout.NORTH);
    JScrollPane scroll_pane = new JScrollPane(panel);
    return(scroll_pane);
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key of the group to cteate the panel for.
   * @return a pannel, representing a group for the given key.
   */

  private JPanel createGroup(String key)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(1,0));
    panel.setBorder(BorderFactory.createTitledBorder(getTitle(key)));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    return(panel);
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key to get the title for.
   * @return a title for the given key.
   */

  public String getTitle(String key)
  {
    String title = edit_resources_.getTitle(key);
    if (title == null)
      title = key;
    return(title);
  }

  //----------------------------------------------------------------------
  /**
   * Returns true if the resource with the given key is to be hidden,
   * false otherwise. 
   *
   * @param key the key to check if it is to hide.
   * @return true if the resource with the given key is to be hidden,
   * false otherwise. 
   */

  private boolean isToHide(String key)
  {
    return(hide_key_set_.contains(key));
  }

  //----------------------------------------------------------------------
  /**
   */

  public void apply()
  {
    Iterator key_editor_map_iterator = key_editor_map_.keySet().iterator();
    
    String key;
    PropertyEditor editor;
    String resource_value;
    String editors_value;
    boolean changed = false;
    
    while (key_editor_map_iterator.hasNext())
    {
      key = (String)key_editor_map_iterator.next();
      editor = (PropertyEditor)key_editor_map_.get(key);
      resource_value = edit_resources_.getString(key, null);
      editors_value = editor.getAsText();
      if (!areEqual(editors_value, resource_value))
      {
        //          editor.setAsText(edit_resources_.getString(key));
        if (editors_value == null)
          edit_resources_.unset(key);
        else
          edit_resources_.setString(key, editors_value);
        changed = true;
      }
    }
    if (changed)
    {
      try
      {
        edit_resources_.store();
      }
      catch (IOException exc)
      {
        exc.printStackTrace();
      }
      int selected_tab = editor_panel_.getSelectedIndex();
      setAllEditors();
      editor_panel_.setSelectedIndex(selected_tab);
    }  
  }
  
//----------------------------------------------------------------------
/**
 * Invoked when the Close button was pressed.
 *
 * @param event the action event
 */

  public void actionPerformed(ActionEvent event)
  {
    String command = event.getActionCommand();
    if (command.equals(APPLY_COMMAND))
      apply();
  }

//----------------------------------------------------------------------
/**
 * @param event the property change event.
 */

  public void propertyChange(PropertyChangeEvent event)
  {
    if (event.getSource() == my_resources_)
    {
      updateResources();
      invalidate();
      validate();
      repaint();
      return;
    }
    Iterator key_editor_map_iterator = key_editor_map_.keySet().iterator();
    String key;
    PropertyEditor editor;
    Object resource_value;
    Object editors_value;
    boolean changed = false;

    while (key_editor_map_iterator.hasNext())
    {
      key = (String)key_editor_map_iterator.next();
      editor = (PropertyEditor)key_editor_map_.get(key);
      editors_value = editor.getValue();
      try
      {
        resource_value = edit_resources_.get(key);
      }
      catch (MissingResourceException exc)
      {
        resource_value = null;
      }
      catch (IllegalArgumentException exc)
      {
        resource_value = edit_resources_.getString(key);
        editors_value = editor.getAsText();
      }
      
      if (!areEqual(editors_value, resource_value))
        changed = true;
    }
    apply_button_.setEnabled(changed);
  }

  //----------------------------------------------------------------------
  /**
   * @param first the firs value
   * @param second the second value.
   * @return true if the two given values are equal, false otherwise
   */

  private static boolean areEqual(Object first,Object second)
  {
    if (first == second)
      return(true);
    if (first != null)
      return(first.equals(second));
    return(false);
  }
}









