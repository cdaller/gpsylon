package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;

public class GarminWaypointD107 extends GarminWaypointD103
{
  protected Color color_;
  
  protected final static byte WAYPOINT_TYPE = 107;
  
  protected final static Color[] COLORS =
  new Color[] {Color.black,            // black
               Color.red,            // red
               Color.green,            // green
               Color.blue};             // blue
               
  protected final static int DEFAULT_COLOR_INDEX = 0;


  public GarminWaypointD107()
  {
  }

  public GarminWaypointD107(int[] buffer)
  {
//     for(int index = 0; index < buffer.length; index++)
//     {
//       System.out.println(index+":"+buffer[index] + " / " + (char)buffer[index]);
//     }
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
        // unused = GarminDataConverter.getGarminLong(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();  
    symbol_type_ = SYMBOL_TYPE[GarminDataConverter.getGarminByte(buffer,60)];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_options_ = DISPLAY_OPTIONS[GarminDataConverter.getGarminByte(buffer,61)];
        // distance = GarminDataConverter.getGarminFloat(buffer,62);
    short color_index = GarminDataConverter.getGarminByte(buffer,63);
    if(color_index == 0xff)
      color_index = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index];
    
  }

  
//----------------------------------------------------------------------
/**
 * Get the Waypoint Color
 *
 * @return Waypoint Color
 * @throws UnsupportedOperationException
 */
	
	public Color getColor()
    throws UnsupportedOperationException
  {
    return(color_);
  }
	
 
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminWaypoint[");
    buffer.append("type=").append(WAYPOINT_TYPE).append(", ");
    buffer.append("display_options=").append(display_options_).append(", ");
    buffer.append("symbol_type=").append(symbol_type_).append(", ");
    buffer.append("symbol_name=").append(symbol_name_).append(", ");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("identification=").append(identification_).append(", ");
    buffer.append("comment=").append(comment_).append(", ");
    buffer.append("color=").append(color_);
    buffer.append("]");
    return(buffer.toString());
  }
}
