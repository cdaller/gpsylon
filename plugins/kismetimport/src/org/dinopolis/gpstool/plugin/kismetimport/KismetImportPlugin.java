/***********************************************************************
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
 * Inffeldgasse 16c, A-8010 Graz, Austria.
 *
 * Copyright (c) 2003 Sven Boeckelmann
 * Langendreerstrasse 30, 44892 Bochum, Germany
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


package org.dinopolis.gpstool.plugin.kismetimport;

import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import com.bbn.openmap.Layer;

import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.util.Resources;

import org.dinopolis.util.gui.MenuFactory;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JButton;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSource;

/**
 * The KismetImportPlugin will simply invoke a FileChooser dialog
 * which will let you select the Kismet scan XML file.
 * Once an XML file was selected for import it will 
 * be 'SAX parsed' using the KismetLocationHandler.
 *
 * @author sven@boeckelmann.org
 *
 */

public class KismetImportPlugin implements GuiPlugin, ActionListener {
    
    /** current version
     */
    private static final String version = "0.01";
    
    /** menu item which will be displayed in gpstool's main menu
     */
    protected JMenuItem menuItem = null;
    
    /** chooser for XML data file
     */
    protected JFileChooser fileChooser = null;
    
    /** resources needed
     */
    protected Resources resources = null;

    /** Creates a new instance of KismetImportPlugin */
    public KismetImportPlugin() {
    }
    
    /** If the plugin wants to draw anything on the map it may return a
     * layer here or <code>null</code> if not. If this method is called
     * more than once, the plugin should return always the same layer!
     *
     * @return the layer the plugin wants to paint into.
     * @see com.bbn.openmap.Layer
     */
    public Layer getLayer() {
        return null;
    }
    
    /** The plugin may return a JMenu object to be used in the main menu of
     * the application and may (should) contain other menu items. The
     * menuitems returned should provide an icon, a mnemonic key, and a
     * localized name (and a accelerator key).
     *
     * @return A menu that is used in the main menu in the
     * application or <code>null</code>, if no main menu is needed.
     *
     */
    public JMenu getMainMenu() {
        return null;
    }
    
    /** Every plugin may provide one or more mouse modes. These mouse modes
     * may react on mouse clicks, drags, etc.
     *
     * @return mouse modes that are used by this plugin in the application or
     * <code>null</code>, if no mouse modes are used.
     *
     */
    public MouseMode[] getMouseModes() {
        return null;
    }
    
    /** Returns a description of the Plugin. The description should be
     * human readable and understandable like "This plugin saves the
     * content of the main window as an image in jpeg format". It is
     * prefereable but not necessary that the description is localized.
     *
     * @return The description of the plugin.
     */
    public String getPluginDescription() {
        return "import kismet scan file";
    }
    
    /** Returns the unique id of the plugin. The id is used to identify
     * the plugin and to distinguish it from other plugins.
     *
     * @return The id of the plugin.
     */
    public String getPluginIdentifier() {
        return "kismetimport";
    }
    
    /** Returns the name of the Plugin. The name should be a human
     * readable and understandable name like "Save Image as JPEG". It is
     * prefereable but not necessary that the name is localized.
     *
     * @return The name of the plugin.
     */
    public String getPluginName() {
        return "Kismet Import";
    }
    
    /** Returns the version of the plugin. The version may be used to
     * choose between different version of the same plugin.
     *
     * @return The version of the plugin.
     */
    public float getPluginVersion() {
        return Float.parseFloat(version);
    }
    
    /** The application provides a sub menu for every plugin that may be
     * used. The JMenuItem (or JMenu) returned is added to a submenu in
     * the "plugins" menu item.  The menuitems returned should provide an
     * icon, a mnemonic key, and a localized name (and a accelerator key).
     *
     * @return A menuitem (or a JMenu) that are used in a sub menu in the
     * application or <code>null</code>, if no submenus are needed.
     *
     */
    public JMenuItem getSubMenu() {
        if (menuItem == null)   {
            menuItem = new JMenuItem("Kismet Import");
            menuItem.addActionListener(this);
        }
        return menuItem;
    }
    
    /** Initialize the plugin and pass a PluginSupport that provides
     * objects, the plugin may use.
     *
     * @param support the PluginSupport object
     */
    public void initializePlugin(PluginSupport support) {
        resources = support.getResources();
    }
    
    /** Returns if the plugin is active or not.
     *
     * @return <code>true</code> if the plugin is active and paints
     * something.
     */
    public boolean isActive() {
        return false;
    }
    
    /** Called by the application to switch the layer on or off. If the
     * layer is switched off, it must not paint anything and should not
     * consume any calculational power.
     *
     * @param active if <code>true</code> the layer is switched on and
     * should react on changes of the projection and draw anything in the
     * paintComponent method.
     */
    public void setActive(boolean active) {
    }
    
    /** The application calls this method to indicate that the plugin is
     * activated and will be used from now on. The Plugin should
     * initialize any needed resources (files, etc.) in this method.
     *
     * @throws Exception if an error occurs. If this method throws an
     * exception, the plugin will not be used by the application.
     */
    public void startPlugin() throws Exception {
    }
    
    /** The application calls this method to indicate that the plugin is
     * deactivated and will not be used any more. The Plugin should
     * release all resources (close files, etc.) in this method.
     *
     * @throws Exception if an error occurs.
     */
    public void stopPlugin() throws Exception {
    }
    
    /** the action event handler will start the Kismet XML file import
     */
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        showFrame();
    }
    
    /** show file chooser */
    private final void showFrame()  {
        if (fileChooser == null)  {
            fileChooser = new JFileChooser();
            // set up the file filter for Kismet files
            fileChooser.setFileFilter(
                new javax.swing.filechooser.FileFilter()
                {
                    public boolean accept(java.io.File file) {
                        if (file.isDirectory() ||
                            file.getName().toUpperCase().startsWith("KISMET") &&
                            file.getName().toUpperCase().endsWith(".XML")
                            )
                            return true;
                        else 
                            return false;
                    }

                    public String getDescription() {
                        return "Kismet XML";
                    }

                }
            );
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setDialogTitle("Kismet Import");
        }
        
        // import the file when user requested to do so
        if (fileChooser.showDialog(new JFrame(),"import") == fileChooser.APPROVE_OPTION)
            importFile(fileChooser.getSelectedFile());
    }
    
    /** parse the selected file using the KismetLocationHandler */
    private final void importFile(File file) {
        System.out.println("importing : " + file.getName());
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(false);
        try {
            FileInputStream is = new FileInputStream(file);
            InputSource input = new InputSource(is);
            SAXParser parser = parserFactory.newSAXParser();
            LocationMarkerSource locationMarkerSource = new KismetLocationLayer(resources).getLocationMarkerSource(); 
            parser.parse(input, new KismetLocationHandler(locationMarkerSource));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
