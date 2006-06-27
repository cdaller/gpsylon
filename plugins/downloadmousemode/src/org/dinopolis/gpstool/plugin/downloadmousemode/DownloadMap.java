package org.dinopolis.gpstool.plugin.downloadmousemode;


import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.map.MapInfo;
import org.dinopolis.gpstool.hook.MapManagerHook;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.util.*;
import org.dinopolis.gpstool.plugin.PluginSupport;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Projection;

//----------------------------------------------------------------------
/** This class provides a plugin that allows the user to download maps
 * from servers from the internet (mapblast, expedia, etc.).
 * This class has been extracted from DownloadMouseModeLayer.
 * 
 * @author Christof Dallermassl / Manuel Habermacher
 * @version $Revision$ 
 *
 */
public class DownloadMap 
	implements GpsylonKeyConstants
{
	public static final String KEY_DEVELOPMENT_DOWNLOAD_SIMULATE_ONLY = "development.download.simulate_only";
    static final int DOWNLOAD_SUCCESS = 0;
    static final int DOWNLOAD_ERROR = 1;
    
    public static final int DOWNLOAD_MAP_AUTOMATIC_MODE = 1;
    public static final int DOWNLOAD_MAP_MANUAL_MODE = 0;
	
	Resources application_resources_;
	
	DownloadFrame download_frame_;
	MapManagerHook map_manager_;
	ProgressListener progress_listener_;
	
	DownloadThread download_thread_;
	DownloadInfoQueue download_queue_;
	
		// has either value DOWNLOAD_MAP_AUTOMATIC_MODE or DOWNLOAD_MAP_MANUAL_MODE
	private int download_mode_;
		
//	----------------------------------------------------------------------
/**
 * Default Constructor
 */
	public DownloadMap() {
	}
	
//	----------------------------------------------------------------------	
/** 
 * Sets the download mode to either DOWNLOAD_MAP_AUTOMATIC_MODE or DOWNLOAD_MAP_MANUAL_MODE
 * 
 * @param download_mode
 */
	public void setDownloadMode(int download_mode) {
		download_mode_ = download_mode;
	}

//	----------------------------------------------------------------------
/** 
 * sets the download_frame_ parameter
 * 
 * @param download_frame the frame that displays the progress in map downloading
 */
	public void setDownloadFrame(DownloadFrame download_frame) {
		download_frame_ = download_frame;
	}

//	----------------------------------------------------------------------
	/** 
	 * sets which ProgressListener may be informed about progress in download
	 * 
	 * @param progress_listener sets the progress_listener
	 */
	public void setProgressListener(ProgressListener progress_listener) {
		progress_listener_ = progress_listener;
	}
	
//----------------------------------------------------------------------
/**
 * Initialize the plugin and pass a PluginSupport that provides
 * objects, the plugin may use.
 *
 * @param support the PluginSupport object
 
 * @param progress_listener : since DownloadThread is no longer in DownloadMouseModeLayer,
 *      DownloadMouseModeLayer somehow needs to be informed about progress in this Thread,
 *      so the progressbar in the Download Menu can be updated
 */
	public void initializePlugin(PluginSupport support) {
		application_resources_ = support.getResources();
		map_manager_ = support.getMapManagerHook();
		download_mode_ = DOWNLOAD_MAP_MANUAL_MODE;
		
			// starts already download thread
		download_queue_ = new DownloadInfoQueue();
		download_thread_ = new DownloadThread(this);
		download_thread_.SetDownloadQueue(download_queue_);
		download_thread_.start();
	}
	
	
//----------------------------------------------------------------------
/**
 * Download the maps described in the parameter. If a map has been already downloaded or is in
 * the download queue, it won't download it again.
 * 
 * @param map_rectangles the map information needed to download the maps.
 */
	  public void downloadMaps(MapRectangle[] map_rectangles) {
		int nof_downloads = 0;
		  if (download_thread_.cur_down_map_info != null)
			nof_downloads ++;
		
			// looks which maps have to be downloaded and which don't 
		boolean[] is_unknown_map = new boolean[map_rectangles.length]; 
		for(int index = 0; index < map_rectangles.length; index++) {
	    	if ((download_mode_ == DOWNLOAD_MAP_MANUAL_MODE) ||
	    		(! (mapAlreadyDownloaded(map_rectangles[index]) || 
	    	    	mapAlreadyInQueue(map_rectangles[index]))
	    	    ) ) 
	    	{
	    		nof_downloads++;
	    		is_unknown_map[index] = true;
	    	}
	    	else
	    		is_unknown_map[index] = false;
	    }
		
			//Updates the image progress bar download_frame_
		download_frame_.progress_bar_images_.setValue(0);
		download_frame_.progress_bar_images_.setMaximum(nof_downloads);
		if (nof_downloads > 0)
			download_frame_.setInfo(nof_downloads + " maps");
		
			// now download the new maps
		for(int index = 0; index < map_rectangles.length; index++) {
			if (is_unknown_map[index])
				downloadMap(map_rectangles[index]);
	    }
		
		//System.out.println("Queuelength: "+download_queue_.get_queue_length());
	  }

//----------------------------------------------------------------------
/**
  * Compares the attributes (like Lat/Lon, Scale, Width/Height) of two maps. If they are
  * equal: return true.
  *  
  * @param map1
  * @param map2
*/
	  private boolean sameMaps(MapInfo map1, MapRectangle map2) {
		  
		  if ((map1 == null) || (map2 == null))
			  return false;
		  
		  double latdiff = Math.abs(map1.getLatitude() - map2.getLatitude());
		  double londiff = Math.abs(map1.getLongitude() - map2.getLongitude());
		   
		  		// Some of the map1 used here are still in the download queue. Their actual 
		  		// getScale()-value may change when saved to disk.
		  		// Because this method shouldnt make a difference between comparing already
		  		// downloaded Maps and others in queue, it just looks if one of the two possible
		  		// getScale()-values is the same.
		  double map2scale = download_frame_.getMapRetrievalPlugin()
		  			.getMapScale(	map2.getLatitude(), map2.getLongitude(), map2.getScale(),
		  							(int) map2.getHeight(), (int) map2.getWidth());
		  boolean sameScale = ( (map1.getScale() == map2.getScale())  ||  
				  				(map1.getScale() == map2scale));
		
		  		// computer can't compare doubles exactly, it just looks if the doubles 
		  		// have about the same value +/- 1E-8
		  if 	(sameScale &
				(latdiff / map1.getScale() < 1E-8)&
				(londiff / map1.getScale() < 1E-8)&
				(map1.getHeight() == map2.getHeight())&
				(map1.getWidth() == map2.getWidth()))
		  	{
			  	//System.out.println("DownloadMap: Identical maps found ");//+map+"\n"+omther_map);
			  	return true;
		  	}
		  else
			  return false;
	  }
	  
//----------------------------------------------------------------------
/**
 * Determines if given map already has been downloaded
 * This method will be used when automatic map download is turned on
 * Should this method move to classes MapManager and MapManagerHook?
 * 
 * @param map
*/
	  private boolean mapAlreadyDownloaded(MapRectangle map) {
		  java.util.List maps = map_manager_.getMapInfos(map.getLatitude(),map.getLongitude());
		  		  
		  Iterator iter = maps.iterator();
		  while (iter.hasNext()) {
			  MapInfo other_map = (MapInfo) iter.next();
			  
			  if (sameMaps(other_map,map))
				  return true;
		  }
		  return false;
	  }
	  
	  /**
	   * Determines if given map is in the download queue.
	   * This method will only be used when automatic map download is turned on
	   *  
	   * @param map
	   */
	  private boolean mapAlreadyInQueue(MapRectangle map) {
		  if (sameMaps(download_thread_.cur_down_map_info, map))
			  return true;
		 
		  		// hopefully this queue.iterator never collides with queue.put or queue.get
		  		// this error should be very very rare
		  Iterator iter = download_queue_.queue_.iterator();
		  while (iter.hasNext()) {
			  MapInfoAndMode map_info_mode = (MapInfoAndMode) iter.next();
			  if (sameMaps(map_info_mode.mapinfo_, map))
				  return true;				  
		  }
		  return false;
	  }
	  
	  public void deleteDownloadQueue() {
		  download_queue_.deleteDownloadQueue();
	  }

	  
	 
	  
	  

//----------------------------------------------------------------------
/**
 * Download the map described in the parameter.
 * 
 * @param map_rectangle the map information needed to download the map.
*/
	  public void downloadMap(MapRectangle map_rectangle)
	  {
	    if(Debug.DEBUG)
	      Debug.println("GPSmap_downloadmap","try to download map lat:"+map_rectangle.getLatitude()
	                    +" long:"+map_rectangle.getLongitude()
	                    +" scale="+map_rectangle.getScale());

	    String dirname = FileUtil.getAbsolutePath(application_resources_.getString(KEY_FILE_MAINDIR),
	                                              application_resources_.getString(KEY_FILE_MAP_DIR));
	    
	    File dir = new File(dirname);
	    if(!dir.isDirectory())
	    {
	      System.err.println("Directory '"+dirname+"' does not exist, creating it.");
	      dir.mkdirs();
	    }

	        // create a new MapInfo object that describes the map to
	        // download.  The target directory is set as filename, as the
	        // final filename cannot be determined at this moment. It will
	        // be created from the download thread.
	    MapInfo map_info = new MapInfo(dirname,
	                                   (double)map_rectangle.getLatitude(),
	                                   (double)map_rectangle.getLongitude(),
	                                   map_rectangle.getScale(),
	                                   (int)map_rectangle.getWidth(),
	                                   (int)map_rectangle.getHeight());
	            
	      download_queue_.put(new MapInfoAndMode(map_info, download_mode_));
	    
//	     }
//	     catch(MissingResourceException mre)
//	     {
//	       System.err.println("ERROR: Server "+map_server+" not specified correctly in the resource file.");
//	       mre.printStackTrace();
//	       JOptionPane.showMessageDialog(this,
//	                 resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_MESSAGE)
//	                 +"\nServer "+map_server+" not specified correctly in the resource file.",
//	                 application_resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
//	                 JOptionPane.ERROR_MESSAGE);

//	     }
//	     catch(MalformedURLException mfue)
//	     {
//	       mfue.printStackTrace();
//	       JOptionPane.showMessageDialog(this,
//	 				    resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_MESSAGE)
//	 				    +"\n"+mfue.getMessage(),
//	 				    application_resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
//	 				    JOptionPane.ERROR_MESSAGE);
//	     }
	  }

	
//	----------------------------------------------------------------------
	  /**
	   * Callback for the DownloadThread to inform about the termination of
	   * the download.
	   *
	   * @param map_info The map info object of the downloaded map.
	   * @param status The status of the download
	   * @param message the message (success or error)
	   */
	    protected void downloadTerminated(MapInfo map_info,int status,String message)
	    {
	      if(status == DOWNLOAD_ERROR)
	      {
	    	System.out.println("Error on Download!: Image not downloaded."+"\n");  
	        /*JOptionPane.showMessageDialog(,
	                                      "Error on Download"
	                                      +"\n"+message,
	                                      application_resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
	                                      JOptionPane.ERROR_MESSAGE);
	        */
    		download_frame_.setInfo(" Error on Download");
	      }
	      else
	      {
	        System.out.println("DownloadMap: Image '"+map_info.getFilename()+"' successfully downloaded.");
        	
        	download_frame_.progress_bar_images_.setValue(download_frame_.progress_bar_images_.getValue()+1);
        	if  ( 	download_frame_.progress_bar_images_.getMaximum() ==
        			download_frame_.progress_bar_images_.getValue()) {
        		download_frame_.setInfo(message);
        	}
        		
	        if(map_manager_ != null)
	          map_manager_.addNewMap(map_info);
	      }
	    }

	  
	  
//	 ----------------------------------------------------------------------
//	 Inner Classes
//	 ----------------------------------------------------------------------

//	-----------------------------------------------------------------------
	
	 // stores along with the mapinfo the information if this map has been 
	 // downloaded manually or automatically
	 class MapInfoAndMode {
		 MapInfo mapinfo_;
		 int mode_;
		 
		 MapInfoAndMode(MapInfo mapinfo, int mode) {
			 mapinfo_ = mapinfo;
			 mode_ = mode;
		 }
		 
	}
	    
	 /**
	 * Download thread
	 */
	  
	  class DownloadThread extends Thread
	  {

	    public static final int BUFFER_SIZE = 4096;
	    boolean run_ = true; // while true, keep on running
	    
	    DownloadInfoQueue queue_;
	    boolean simulate_only = application_resources_.getBoolean(KEY_DEVELOPMENT_DOWNLOAD_SIMULATE_ONLY);
	    
	    	// currently in thread downloaded map
	    MapInfo cur_down_map_info;
//	----------------------------------------------------------------------
	/**
	 * Constructor
	 */
	    
	    public DownloadThread(DownloadMap download_map)
	    {
	      super("gpsylon download thread");
	      setDaemon(true);
	  
	      cur_down_map_info= null;
	    }

	    public void SetDownloadQueue(DownloadInfoQueue queue){
	    	queue_ = queue;
	    }

//	----------------------------------------------------------------------
	/**
	 *
	 */
	    public void run()
	    {
	      while(run_)
	      {
	            // get the next MapInfo object from the queue (block
	            // unless one is available):
	    	MapInfoAndMode map_info_mode = (MapInfoAndMode)queue_.get();
	    	cur_down_map_info = map_info_mode.mapinfo_;
	    	    	
    			
	        
    			// read dirname from the map info object
    		String dirname  = cur_down_map_info.getFilename();
            	// create the new filename for the map:
    		String filename = 
    			FileUtil.getNextFileName(dirname,
                                   application_resources_.getString(KEY_FILE_MAP_FILENAME_PREFIX),
                                   application_resources_.getString(KEY_FILE_MAP_FILENAME_PATTERN),
                                   ".???"); // extension, not known yet (done by plugin)
        
    		String file_path_wo_extension = filename.substring(0,filename.length()-3); // cut off ".???" again
    		if(Debug.DEBUG)
    			Debug.println("map_download","Filename (without extension): "+file_path_wo_extension);
    		try
    		{
    			MapInfo result = download_frame_.getMapRetrievalPlugin().getMap(
    					cur_down_map_info.getLatitude(),cur_down_map_info.getLongitude(),
    					cur_down_map_info.getScale(), cur_down_map_info.getWidth(),
    					cur_down_map_info.getHeight(), file_path_wo_extension, progress_listener_);
    			downloadTerminated(result,DOWNLOAD_SUCCESS,"downloaded");
    		}
    		catch(Exception e)
    		{
    			System.out.println("in run(), exception message: "+e.getMessage());
    			downloadTerminated(null,DOWNLOAD_ERROR,e.getMessage());
    		}

	        cur_down_map_info = null;
	      }
	    }
	  }
	  
	  
//-----------------------------------------------------------------------
//	-----------------------------------------------------------------------
	  /**
	   * DownloadInfoQueue. This Queue notifies any waiting threads if an
	   * object is put into it and it puts a requesting thread to sleep
	   * (wait) if nothing is in the queue.
	   */

	    class DownloadInfoQueue
	    {
	      LinkedList queue_;
	      
//	  -----------------------------------------------------------------------
	  /**
	   * Constructor
	   */
	      public DownloadInfoQueue()
	      {
		    if(queue_ == null)
			   queue_ = new LinkedList();
	      }

	      public void wait_() {
	    	  while((queue_ == null) || (queue_.size() == 0))
		        {
		          try
		          {
		            wait();
		          }
		          catch(InterruptedException ie)
		          {
		          }
		        }
	      }

      //  -----------------------------------------------------------------------
	  /**
	   * Adds an Object to the Queue.
	   */
	      public synchronized void put(Object obj)
	      {
	        queue_.add(obj);
	        notify();
	      }

	      //  -----------------------------------------------------------------------
		  /**
		   * returns length of the Queue.
		   */
	      public synchronized int get_queue_length() {
	    	  return queue_.size();
	      }

	      //  -----------------------------------------------------------------------
		  /**
		   * Gets an Object from the Queue.
		   */
		  public synchronized Object get() {
			  wait_();
		        return queue_.removeFirst();  
		  }
		  
	      //  -----------------------------------------------------------------------
		  /**
		   * Deletes all maps in queue that haven't been manually selected to download
		   * All maps the user has selected in download mouse mode stay 
		   */
		  public synchronized void deleteDownloadQueue() {
			  
			  DownloadInfoQueue other_queue = new DownloadInfoQueue();
			  
			  while (download_queue_.get_queue_length() > 0){
				  MapInfoAndMode mapinfomode = (MapInfoAndMode) download_queue_.get();
				  if (mapinfomode.mode_ == DOWNLOAD_MAP_MANUAL_MODE) {
					  other_queue.put(mapinfomode);
				  }
			  }
			  
			  while (other_queue.get_queue_length() > 0)
				  download_queue_.put(other_queue.get());
		  }
	    }    


}