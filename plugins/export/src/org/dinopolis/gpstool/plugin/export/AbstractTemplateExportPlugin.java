/**
 *
 */
package org.dinopolis.gpstool.plugin.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.gpsinput.GPSRoute;
import org.dinopolis.gpstool.gpsinput.GPSTrack;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.WriteTrackPlugin;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.text.OneArgumentMessageFormat;

/**
 * @author christof.dallermassl
 *
 */
public abstract class AbstractTemplateExportPlugin implements WriteTrackPlugin
{

  private Resources resources_;


  /** the name of the resource file */
  private final static String RESOURCE_BUNDLE_NAME = "ExportPlugin";

  /** the name of the directory containing the resources */
  private final static String USER_RESOURCE_DIR_NAME = Gpsylon.USER_RESOURCE_DIR_NAME;

  /**
   * Default Constructor
   */
  public AbstractTemplateExportPlugin() {

  }

  /**
   * Returns the url of the template to use.
   * @return the url of the template to use.
   */
  protected abstract URL getTemplateUrl();

  //----------------------------------------------------------------------
  /**
   * Loads the resource file, or exits on a MissingResourceException.
   */

  void loadResources()
  {
    try
    {
      resources_ =
        ResourceManager.getResources(AbstractTemplateExportPlugin.class,
          RESOURCE_BUNDLE_NAME, USER_RESOURCE_DIR_NAME, Locale.getDefault());
    }
    catch (MissingResourceException mre)
    {
      if (Debug.DEBUG)
        Debug.println("ExportPlugin", mre.toString() + '\n' + Debug.getStackTrace(mre));
      System.err.println("ExportPlugin: resource file '"  + RESOURCE_BUNDLE_NAME + ".properties' not found");
      System.err.println("please make sure that this file is within the classpath !");
      System.exit(1);
    }
  }


  public void writeTracks(OutputStream out, List tracks) throws IOException
  {
    URL template_url = getTemplateUrl();
    InputStream template_in = template_url.openStream();
    VelocityContext context = new VelocityContext();
    context.put("printtracks", Boolean.TRUE);
    context.put("printwaypoints",Boolean.FALSE);
    context.put("printroutes", Boolean.FALSE);

    context.put("tracks",tracks);
    addDefaultValuesToContext(context);
    Reader template_reader = new InputStreamReader(template_in);
    Writer out_writer = new OutputStreamWriter(out);
    printTemplate(template_url.getFile(), context, template_reader, out_writer);
    out_writer.flush();
    out_writer.close();
  }

  protected void printTemplate(String name, VelocityContext context, Reader template, Writer out) throws IOException
  {
    try
    {
      Velocity.init();
      Velocity.evaluate(context, out, name, template);
    } catch (Exception e)
    {
      throw (IOException)new IOException(e.getMessage()).initCause(e);
    }

  }

  /**
   * Adds some important values to the velocity context (e.g. date, ...).
   *
   * @param context the velocity context holding all the data
   */
  public void addDefaultValuesToContext(VelocityContext context)
  {
        DecimalFormat latitude_formatter = (DecimalFormat)NumberFormat.getInstance(Locale.US);
        latitude_formatter.applyPattern("0.0000000");
        DecimalFormat longitude_formatter = (DecimalFormat)NumberFormat.getInstance(Locale.US);
        longitude_formatter.applyPattern("0.0000000");
        DecimalFormat altitude_formatter = (DecimalFormat)NumberFormat.getInstance(Locale.US);
        altitude_formatter.applyPattern("000000");
        OneArgumentMessageFormat string_formatter = new OneArgumentMessageFormat("{0}",Locale.US);
        context.put("dateformatter",new SimpleDateFormat());
        context.put("latitudeformatter", latitude_formatter);
        context.put("longitudeformatter", longitude_formatter);
        context.put("altitudeformatter", altitude_formatter);
        context.put("stringformatter", string_formatter);
            // current time, date
        Calendar now = Calendar.getInstance();
        context.put("creation_date",now.getTime());

            // author
        context.put("author",System.getProperty("user.name"));

            // extent of waypoint, routes and tracks:
        double min_latitude = 90.0;
        double min_longitude = 180.0;
        double max_latitude = -90.0;
        double max_longitude = -180.0;

        List routes = (List)context.get("routes");
        GPSRoute route;
        if(routes != null)
        {
          Iterator route_iterator = routes.iterator();
          while(route_iterator.hasNext())
          {
            route = (GPSRoute)route_iterator.next();
            min_longitude = route.getMinLongitude();
            max_longitude = route.getMaxLongitude();
            min_latitude = route.getMinLatitude();
            max_latitude = route.getMaxLatitude();
          }
        }

        List tracks = (List)context.get("tracks");
        GPSTrack track;
        if(tracks != null)
        {
          Iterator track_iterator = tracks.iterator();
          while(track_iterator.hasNext())
          {
            track = (GPSTrack)track_iterator.next();
            min_longitude = Math.min(min_longitude,track.getMinLongitude());
            max_longitude = Math.max(max_longitude,track.getMaxLongitude());
            min_latitude = Math.min(min_latitude,track.getMinLatitude());
            max_latitude = Math.max(max_latitude,track.getMaxLatitude());
          }
        }
        List waypoints = (List)context.get("waypoints");
        GPSWaypoint waypoint;
        if(waypoints != null)
        {
          Iterator waypoint_iterator = waypoints.iterator();
          while(waypoint_iterator.hasNext())
          {
            waypoint = (GPSWaypoint)waypoint_iterator.next();
            min_longitude = Math.min(min_longitude,waypoint.getLongitude());
            max_longitude = Math.max(max_longitude,waypoint.getLongitude());
            min_latitude = Math.min(min_latitude,waypoint.getLatitude());
            max_latitude = Math.max(max_latitude,waypoint.getLatitude());
          }
        }
        context.put("min_latitude",new Double(min_latitude));
        context.put("min_longitude",new Double(min_longitude));
        context.put("max_latitude",new Double(max_latitude));
        context.put("max_longitude",new Double(max_longitude));
      }

  /**
   * @return the resources
   */
  public Resources getResources()
  {
    return this.resources_;
  }

  /**
   * Initialize the plugin and pass a PluginSupport that provides
   * objects, the plugin may use.
   *
   * @param support the PluginSupport object
   */
  public void initializePlugin(PluginSupport support)
  {
    loadResources();
  }

  /**
   * The application calls this method to indicate that the plugin is
   * activated and will be used from now on. The Plugin should
   * initialize any needed resources (files, etc.) in this method.
   *
   * @throws Exception if an error occurs. If this method throws an
   * exception, the plugin will not be used by the application.
   */
  public void startPlugin() throws Exception
  {
  }

  /**
   * The application calls this method to indicate that the plugin is
   * deactivated and will not be used any more. The Plugin should
   * release all resources (close files, etc.) in this method.
   *
   * @throws Exception if an error occurs.
   */
  public void stopPlugin() throws Exception
  {
  }

}
