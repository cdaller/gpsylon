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

package org.dinopolis.gpstool.util;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.filechooser.FileFilter;

/**
 * A convenience implementation of FileFilter that filters out
 * all files except for those type extensions that it knows about.
 *
 * Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macinthosh. Case is ignored.
 *
 * Example - create a new filter that filerts out all files
 * but gif and jpg image files:
 *
 *     JFileChooser chooser = new JFileChooser();
 *     ExampleFileFilter filter = new ExampleFileFilter(
 *                   new String{"gif", "jpg"}, "JPEG & GIF Images")
 *     chooser.addChoosableFileFilter(filter);
 *     chooser.showOpenDialog(this);
 *
 * @version 1.5 06/13/02
 * @author Jeff Dinkins
 */
public class ExtensionFileFilter extends FileFilter
{

  private static String TYPE_UNKNOWN = "Type Unknown";
  private static String HIDDEN_FILE = "Hidden File";

  private Hashtable filters_ = null;
  private String description_ = null;
  private String full_description_ = null;
  private boolean use_extension_in_description_ = true;

  protected Object auxiliary_obj_;

      /**
       * Creates a file filter. If no filters are added, then all
       * files are accepted.
       *
       * @see #addExtension
       */
  public ExtensionFileFilter()
  {
    this.filters_ = new Hashtable();
  }

      /**
       * Creates a file filter that accepts files with the given extension.
       * Example: new ExtensionFileFilter("jpg");
       *
       * @see #addExtension(java.lang.String)
       */
  public ExtensionFileFilter(String extension)
  {
    this(extension,null);
  }

      /**
       * Creates a file filter that accepts the given file type.
       * Example: new ExtensionFileFilter("jpg", "JPEG Image Images");
       *
       * Note that the "." before the extension is not needed. If
       * provided, it will be ignored.
       *
       * @see #addExtension(java.lang.String)
       */
  public ExtensionFileFilter(String extension, String description)
  {
    this();
    if(extension!=null) addExtension(extension);
    if(description!=null) setDescription(description);
  }

      /**
       * Creates a file filter from the given string array.
       * Example: new ExtensionFileFilter(String {"gif", "jpg"});
       *
       * Note that the "." before the extension is not needed and
       * will be ignored.
       *
       * @see #addExtension(java.lang.String)
       */
  public ExtensionFileFilter(String[] filters)
  {
    this(filters, null);
  }

      /**
       * Creates a file filter from the given string array and description.
       * Example: new ExtensionFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
       *
       * Note that the "." before the extension is not needed and will be ignored.
       *
       * @see #addExtension(java.lang.String)
       */
  public ExtensionFileFilter(String[] filters, String description)
  {
    this();
    for (int i = 0; i < filters.length; i++)
    {
          // add filters one by one
	    addExtension(filters[i]);
    }
    if(description!=null) setDescription(description);
  }

      /**
       * Return true if this file should be shown in the directory pane,
       * false if it shouldn't.
       *
       * Files that begin with "." are ignored.
       *
       * @see #getExtension
       * @see javax.swing.filechooser.FileFilter#accept
       */
  public boolean accept(File f)
  {
    if(f != null)
    {
	    if(f.isDirectory())
      {
        return true;
	    }
	    String extension = getExtension(f);
	    if(extension != null && filters_.get(getExtension(f)) != null)
      {
        return true;
	    };
    }
    return false;
  }

      /**
       * Return the extension portion of the file's name .
       *
       */
  public String getExtension(File f)
  {
    if(f != null)
    {
	    String filename = f.getName();
	    int i = filename.lastIndexOf('.');
	    if(i>0 && i<filename.length()-1)
      {
        return filename.substring(i+1).toLowerCase();
	    };
    }
    return null;
  }

      /**
       * Adds a filetype "dot" extension to filter against.
       *
       * For example: the following code will create a filter that filters
       * out all files except those that end in ".jpg" and ".tif":
       *
       *   ExtensionFileFilter filter = new ExtensionFileFilter();
       *   filter.addExtension("jpg");
       *   filter.addExtension("tif");
       *
       * Note that the "." before the extension is not needed and will be ignored.
       */
  public void addExtension(String extension)
  {
    if(filters_ == null)
    {
	    filters_ = new Hashtable(5);
    }
    filters_.put(extension.toLowerCase(), this);
    full_description_ = null;
  }


      /**
       * Returns the human readable description of this filter. For
       * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
       *
       * @see #setDescription
       * @see #setExtensionListInDescription
       * @see #isExtensionListInDescription
       * @see javax.swing.filechooser.FileFilter#getDescription
       */
  public String getDescription()
  {
    if(full_description_ == null)
    {
	    if(description_ == null || isExtensionListInDescription())
      {
        full_description_ = description_==null ? "(" : description_ + " (";
            // build the description from the extension list
        Enumeration extensions = filters_.keys();
        if(extensions != null)
        {
          full_description_ += "." + (String) extensions.nextElement();
          while (extensions.hasMoreElements())
          {
            full_description_ += ", ." + (String) extensions.nextElement();
          }
        }
        full_description_ += ")";
	    }
      else
      {
        full_description_ = description_;
	    }
    }
    return full_description_;
  }

      /**
       * Sets the human readable description of this filter. For
       * example: filter.setDescription("Gif and JPG Images");
       *
       * @see #setDescription
       * @see #setExtensionListInDescription
       * @see #isExtensionListInDescription
       */
  public void setDescription(String description)
  {
    this.description_ = description;
    full_description_ = null;
  }

      /**
       * Determines whether the extension list (.jpg, .gif, etc) should
       * show up in the human readable description.
       *
       * Only relevent if a description was provided in the constructor
       * or using setDescription();
       *
       * @see #getDescription
       * @see #setDescription
       * @see #isExtensionListInDescription
       */
  public void setExtensionListInDescription(boolean b)
  {
    use_extension_in_description_ = b;
    full_description_ = null;
  }

      /**
       * Returns whether the extension list (.jpg, .gif, etc) should
       * show up in the human readable description.
       *
       * Only relevent if a description was provided in the constructor
       * or using setDescription();
       *
       * @see #getDescription
       * @see #setDescription
       * @see #setExtensionListInDescription
       */
  public boolean isExtensionListInDescription()
  {
    return(use_extension_in_description_);
  }


  
//----------------------------------------------------------------------
/**
 * Get the auxiliary_obj.
 *
 * @return the auxiliary_obj.
 * @see #setAuxiliaryObject(java.lang.Object)
 */
  public Object getAuxiliaryObject() 
  {
    return (auxiliary_obj_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the auxiliary object for this extension filter. This object may
 * be used after a file was chosen in a file chooser to identify the
 * type of the used extension filter. E.g. if this extension filter is
 * used to describe files of application xyz, this value may be stored
 * as an auxiliary object. So after the file chooser dialog was
 * closed, it can be retrieved again by the {@link
 * #getAuxiliaryObject()} method to hand the file
 * directly to application xyz. Otherwise, the filter description or
 * the filter extension are the only hint that the file is a xyz-file.
 *
 * @param auxiliary_obj the auxiliary_obj.
 */
  public void setAuxiliaryObject(Object auxiliary_obj) 
  {
    auxiliary_obj_ = auxiliary_obj;
  }
  
}
