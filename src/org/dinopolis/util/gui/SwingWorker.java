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

import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 * 
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorker
{
  private Object value_;  // see getValue(), setValue()
//  private Thread thread_;
  private ThreadVar thread_var_;
  protected String name_ = "";  // the name of the swingworker (for debugging reasons)

      /** 
       * Class to maintain reference to current worker thread
       * under separate synchronization control.
       */
  private static class ThreadVar
  {
    private Thread thread;
    ThreadVar(Thread t)
    {
      thread = t;
    }

    synchronized Thread get()
    {
      return thread;
    }

    synchronized void clear()
    {
      thread = null;
    }
  }


      /** 
       * Get the value produced by the worker thread, or null if it 
       * hasn't been constructed yet.
       */
  protected synchronized Object getValue()
  { 
    return value_; 
  }

      /** 
       * Set the value produced by worker thread 
       */
  private synchronized void setValue(Object x)
  { 
    value_ = x; 
  }

      /** 
       * Compute the value to be returned by the <code>get</code> method. 
       */
  public abstract Object construct();

      /**
       * Called on the event dispatching thread (not on the worker thread)
       * after the <code>construct</code> method has returned.
       */
  public void finished()
  {
  }

      /**
       * A new method that interrupts the worker thread.  Call this method
       * to force the worker to stop what it's doing.
       */
  public void interrupt()
  {
    Thread t = thread_var_.get();
    if (t != null)
    {
      t.interrupt();
    }
    thread_var_.clear();
  }

      /**
       * Return the value created by the <code>construct</code> method.  
       * Returns null if either the constructing thread or the current
       * thread was interrupted before a value was produced.
       * 
       * @return the value created by the <code>construct</code> method
       */
  public Object get()
  {
    while (true) {  
      Thread t = thread_var_.get();
      if (t == null)
      {
        return getValue();
      }
      try
      {
        t.join();
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt(); // propagate
        return null;
      }
    }
  }


      /**
       * Start a thread that will call the <code>construct</code> method
       * and then exit.
       */
  public SwingWorker()
  {
    final Runnable doFinished = new Runnable()
      {
        public void run()
        {
          finished();
        }
      };

    Runnable doConstruct = new Runnable()
      { 
        public void run()
        {
          try
          {
            setValue(construct());
          }
          finally
          {
            thread_var_.clear();
          }

          SwingUtilities.invokeLater(doFinished);
        }
      };

    Thread t = new Thread(doConstruct);
    thread_var_ = new ThreadVar(t);
  }

      /**
       * Start the worker thread.
       */
  public void start()
  {
    Thread t = thread_var_.get();
    if (t != null)
    {
      t.start();
    }
  }
}
