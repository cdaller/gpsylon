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


package org.dinopolis.gpstool.alarm;

import org.dinopolis.util.io.Tokenizer;

import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;

//----------------------------------------------------------------------
/**
 * This class manages multiple alarm conditions and reacts on
 * triggered alarms.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class AlarmConditionManager implements AlarmListener
{

//----------------------------------------------------------------------
/**
 * Constructor
 */
  public AlarmConditionManager()
  {
  }

//----------------------------------------------------------------------
/**
 * Read
 */
  public void addAlarms(String filename, GPSDataProcessor processor)
    throws IOException
  {
    Reader reader = new BufferedReader(new FileReader(filename));
    Tokenizer tokenizer = new Tokenizer(reader);
    tokenizer.setDelimiter(',');
    tokenizer.respectQuotedWords(true);
    tokenizer.respectEscapedCharacters(true);
    
    List tokens;
    String name;
    double latitude;
    double longitude;
    double radius_m;
    String message;
    while(tokenizer.hasNextLine())
    {
      tokens = tokenizer.nextLine();
      name = (String)tokens.get(0);
      latitude = Double.valueOf((String)tokens.get(1)).doubleValue();
      longitude = Double.valueOf((String)tokens.get(2)).doubleValue();
      radius_m = Double.valueOf((String)tokens.get(3)).doubleValue();
      message = (String)tokens.get(4);
      PrintAlarmAction action = new PrintAlarmAction(name + ": " + message);
      PositionAlarmCondition condition =
	new PositionAlarmCondition(latitude,longitude,radius_m);
      condition.setAlarmAction(action);
      condition.setAlarmListener(this);
      processor.addGPSDataChangeListener(GPSDataProcessor.LOCATION,condition);
    }
  }

  
  
//----------------------------------------------------------------------
/**
 * Called on a gps alarm and executes the alarm action.
 * @param event the alarm event
 */
  public void gpsAlarm(AlarmEvent event)
  {
    AlarmCondition condition = event.getSource();
    AlarmAction action = event.getAction();

    action.execute();

  }

  public static void main(String[] args)
  {
    AlarmConditionManager manager = new AlarmConditionManager();
  }
}










