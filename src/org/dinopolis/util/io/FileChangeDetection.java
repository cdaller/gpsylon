/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2003 IICM, Graz University of Technology
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


package org.dinopolis.util.io;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;



//----------------------------------------------------------------------
/**
 * This class checks a given file and detects any modifications (if
 * the modificaiton time changes) and informs any listeners on this change.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class FileChangeDetection extends Thread 
{
  protected final static long DEFAULT_LOOKUP_PERIOD = 5000L;
  protected String filename_;
  protected long lookup_period_;
  protected boolean loop_thread_;
  protected long last_modified_ = -1;
  protected File observed_file_;
  protected Vector listeners_;

//----------------------------------------------------------------------
/**
 * Constructor using the default lookup period. 
 */
  public FileChangeDetection()
  {
    setLookupPeriod(DEFAULT_LOOKUP_PERIOD);
  }
  
//----------------------------------------------------------------------
/**
 * Constructor observing the given file and using the default lookup period.
 *
 * @param filename the file to observe
 */
  public FileChangeDetection(String filename)
  {
    this(filename,DEFAULT_LOOKUP_PERIOD);
  }

//----------------------------------------------------------------------
/**
 * Constructor observing the given file and the given lookup period.
 *
 * @param filename the file to observe
 * @param lookup_period period given in milliseconds to check the file
 * for changes.
 */
  public FileChangeDetection(String filename, long lookup_period)
  {
    setFile(filename);
    setLookupPeriod(lookup_period);
  }

//----------------------------------------------------------------------
/**
 * Set the file to check for changes.
 *
 * @param filename the file to observe
 */
  public void setFile(String filename)
  {
    setFile(new File(filename));
  }

//----------------------------------------------------------------------
/**
 * Set the file to check for changes.
 *
 * @param file the file to observe
 */
  public void setFile(File file)
  {
    observed_file_ = file;
  }

//----------------------------------------------------------------------
/**
 * Set the period to check the file for changes.
 *
 * @param lookup_period period in milliseconds
 */
  public void setLookupPeriod(long lookup_period)
  {
    lookup_period_ = lookup_period;
  }

//----------------------------------------------------------------------
/**
 * Starts the thread that detects any modifications of the file.
 */
  public void startChangeDetection()
  {
    loop_thread_ = true;
    super.start();
  }

//----------------------------------------------------------------------
/**
 * Stops the thread that detects any modifications of the file.
 */
  public void stopChangeDetection()
  {
    loop_thread_ = false;
  }

//----------------------------------------------------------------------
/**
 * Adds a listener that is informed about changes of the observed file.
 *
 * @param listener the listener to add.
 */
  public void addFileChangeListener(FileChangeListener listener)
  {
    if(listeners_ == null)
      listeners_ = new Vector();
    if(listener != null)
      listeners_.add(listener);
  }

//----------------------------------------------------------------------
/**
 * Removes a listener that is informed about changes of the observed file.
 *
 * @param listener the listener to remove.
 */
  public void remoteFileChangeListener(FileChangeListener listener)
  {
    if(listeners_ == null)
      return;
    listeners_.remove(listener);
  }

//----------------------------------------------------------------------
/**
 * Informs all registered listeners about a change.
 */
  protected void fireChangeDetected()
  {
    if(listeners_ == null)
      return;
    Vector listeners = new Vector();
    synchronized(listeners_)
    {
      listeners.addAll(listeners_);
    }
    Iterator iterator = listeners.iterator();
    while(iterator.hasNext())
    {
      ((FileChangeListener)iterator.next()).fileChanged(observed_file_);
    }
  }

//----------------------------------------------------------------------
/**
 * Checks the file for its modification time and informs the listeners
 * on a change. Then waits the amount of time set.
 */
  public void run()
  {
    long modified;
    while(loop_thread_)
    {
      if(observed_file_ != null)
      {
        modified = observed_file_.lastModified();
        if((modified != last_modified_) && (last_modified_ >= 0))
          fireChangeDetected();
        last_modified_ = modified;
      }
      synchronized(this)
      {
        try
        {
          wait(lookup_period_);
        }
        catch(InterruptedException ignore) {}
      }
    }
  }
}


