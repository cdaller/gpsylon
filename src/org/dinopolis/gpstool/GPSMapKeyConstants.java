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

package org.dinopolis.gpstool;

//----------------------------------------------------------------------
/**
 * This class holds only the keys for the properties.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */


public interface GPSMapKeyConstants  
{

      // gps device settings
  public static final String KEY_GPS_DEVICE_PREFIX = "gps.device";
  public static final String KEY_GPS_DEVICE_SERIAL_PORT = "gps.device.serial_port";
  public static final String KEY_GPS_DEVICE_SERIAL_SPEED = "gps.device.serial_speed";
  public static final String KEY_GPS_DEVICE_MODE = "gps.device.mode";
  public static final String KEY_GPS_DEVICE_PROTOCOL = "gps.device.protocol";
  public static final String KEY_GPS_DEVICE_GPSD_HOST = "gps.device.gpsd.host";
  public static final String KEY_GPS_DEVICE_GPSD_PORT = "gps.device.gpsd.port";
  public static final String KEY_GPS_DEVICE_DATA_FILENAME = "gps.device.data.filename";
  public static final String KEY_GPS_DEVICE_NMEALOGFILE = "gps.device.nmealogfile";
  public static final String KEY_GPS_DEVICE_NMEA_DELAY = "gps.device.nmea.delay";
  public static final String KEY_GPS_DEVICE_NMEA_IGNORE_CHECKSUM = "gps.device.nmea.ignore_checksum";

  public static final String VALUE_KEY_DEVICE_MODE_SERIAL = "serial";
  public static final String VALUE_KEY_DEVICE_MODE_GPSD = "tcp";
  public static final String VALUE_KEY_DEVICE_MODE_FILE = "file";
  public static final String VALUE_KEY_DEVICE_MODE_NONE = "none";
  
  public static final String VALUE_KEY_DEVICE_PROTOCOL_NMEA = "nmea";
  public static final String VALUE_KEY_DEVICE_PROTOCOL_GARMIN = "garmin";

  public static final String KEY_GPS_DEVICE_SERIAL_SPEED_DEFAULT_GARMIN = "gps.device.serial_speed.default.garmin";
  public static final String KEY_GPS_DEVICE_SERIAL_SPEED_DEFAULT_NMEA = "gps.device.serial_speed.default.nmea";
  public static final String KEY_GPS_DEVICE_SERIAL_SPEED_DEFAULT_SIRF2 = "gps.device.serial_speed.default.sirf2";

  public static final String KEY_LOCKFILES = "lockfiles";
  
  // alarm settings
  public static final String KEY_ALARM_FILE = "alarm_file";
      // position settings
  public static final String KEY_CURRENT_GPS_POSITION_REMEMBER_CURRENT_GPS_POSITION = "current_gps_position.remember_current_gps_position";
  public static final String KEY_CURRENT_GPS_POSITION_LATITUDE = "current_gps_position.latitude";
  public static final String KEY_CURRENT_GPS_POSITION_LONGITUDE = "current_gps_position.longitude";
  public static final String KEY_CURRENT_MAP_POSITION_REMEMBER_CURRENT_MAP_POSITION = "current_map_position.remember_current_map_position";
  public static final String KEY_CURRENT_MAP_POSITION_LATITUDE = "current_map_position.latitude";
  public static final String KEY_CURRENT_MAP_POSITION_LONGITUDE = "current_map_position.longitude";
  public static final String KEY_MAP_SCALE_REMEMBER_MAP_SCALE = "map.scale.remember_map_scale";
  public static final String KEY_MAP_SCALE = "map.scale.value";

      // file/directory settings
  public static final String KEY_FILE_MAINDIR = "file.maindir";
  public static final String KEY_FILE_MAP_DIR = "file.map.dir";
  public static final String KEY_FILE_MAP_DESCRIPTION_FILE = "file.map.description_file";
  public static final String KEY_FILE_MAP_FILENAME_PREFIX = "file.map.filename.prefix";
  public static final String KEY_FILE_MAP_FILENAME_PATTERN = "file.map.filename.pattern";
  public static final String KEY_FILE_TRACK_DIR = "file.track.dir";
  public static final String KEY_FILE_LOCATION_DIR = "file.location.dir";
  public static final String KEY_FILE_LOCATION_FILENAME = "file.location.filename";
  public static final String KEY_FILE_PLUGIN_DIRS = "file.plugin.dirs";
  public static final String KEY_FILE_MAP_DESCRIPTION_FILE_DETECT_CHANGES = "file.map.description_file_detect_changes";
  
      // proxy settings
  public static final String KEY_HTTP_PROXY_PREFIX = "http.proxy";
  public static final String KEY_HTTP_PROXY_USE = "http.proxy.use";
  public static final String KEY_HTTP_PROXY_HOST = "http.proxy.host";
  public static final String KEY_HTTP_PROXY_PORT = "http.proxy.port";
  public static final String KEY_HTTP_PROXY_AUTHENTICATION_USE = "http.proxy.authentication.use";
  public static final String KEY_HTTP_PROXY_AUTHENTICATION_USERNAME = "http.proxy.authentication.username";
  public static final String KEY_HTTP_PROXY_AUTHENTICATION_PASSWORD = "http.proxy.authentication.password";

      // windows settings
      /** the key for the width of the frame */
  public final static String KEY_WINDOW_DIMENSION_WIDTH = "window.dimension.width";

      /** the key for the height of the frame */
  public final static String KEY_WINDOW_DIMENSION_HEIGHT = "window.dimension.height";

      /** the key for the x location of the frame */
  public final static String KEY_WINDOW_LOCATION_X = "window.location.x";

      /** the key for the y location of the frame */
  public final static String KEY_WINDOW_LOCATION_Y = "window.location.y";

      /** the key for the remember window position and dimension */
  public final static String KEY_REMEMBER_FRAME_SETTINGS =
  "window.remember_settings";

      /** format for latitude and longitude */
  public static final String KEY_ANGLE_FORMAT_LATLON = "angle.format.latlon";
  public static final String KEY_ANGLE_FORMAT_HEADING = "angle.format.heading";
  public static final String KEY_ANGLE_FORMAT_VALID_FORMATS = "angle.format.valid_formats";

      /** splash screen */
  public static final String KEY_SPLASH_IMAGE = "splash.image";
  public static final String KEY_SPLASH_MAX_PROGRESS = "splash.max_progress";
  
      /** other number formats */
  public static final String KEY_NUMBER_FORMAT_DISTANCE = "number.format.distance";
  public static final String KEY_UNIT_DISTANCE = "unit.distance";

      /** tachometer settings */
  public static final String KEY_TACHOMETER_REFRESH_TIME = "tachometer.refresh_time";
  
      /** localization of GUI */
  public static final String KEY_LOCALIZE_LATITUDE = "localize.latitude";
  public static final String KEY_LOCALIZE_LONGITUDE = "localize.longitude";
  public static final String KEY_LOCALIZE_ALTITUDE = "localize.altitude";
  public static final String KEY_LOCALIZE_RAWGPSDATAFRAME_TITLE ="localize.raw_gps_data_frame_title";
  public static final String KEY_LOCALIZE_SCALE = "localize.scale";
  public static final String KEY_LOCALIZE_HEIGHT = "localize.height";
  public static final String KEY_LOCALIZE_WIDTH = "localize.width";
  public static final String KEY_LOCALIZE_INFO = "localize.info";
  public static final String KEY_LOCALIZE_TRACK = "localize.track";
  public static final String KEY_LOCALIZE_TRACKPOINT = "localize.trackpoint";
  public static final String KEY_LOCALIZE_ROUTE = "localize.route";
  public static final String KEY_LOCALIZE_ROUTEPOINT = "localize.routepoint";
  public static final String KEY_LOCALIZE_WAYPOINT = "localize.waypoint";
  public static final String KEY_LOCALIZE_DISTANCE = "localize.distance";
  public static final String KEY_LOCALIZE_CANCEL_BUTTON = "localize.cancel_button";
  public static final String KEY_LOCALIZE_CLOSE_BUTTON = "localize.close_button";
  public static final String KEY_LOCALIZE_CLEAR_BUTTON = "localize.clear_button";
  public static final String KEY_LOCALIZE_SAVE_BUTTON = "localize.save_button";
  public static final String KEY_LOCALIZE_PREVIEW_BUTTON = "localize.preview_button";
  public static final String KEY_LOCALIZE_FILENAME = "localize.filename";
  public static final String KEY_LOCALIZE_MESSAGE_LATITUDE_WRONG_FORMAT = "localize.message.latitude_wrong_format";
  public static final String KEY_LOCALIZE_MESSAGE_LONGITUDE_WRONG_FORMAT = "localize.message.longitude_wrong_format";
  public static final String KEY_LOCALIZE_MESSAGE_ERROR_TITLE = "localize.message.error_title";
  public static final String KEY_LOCALIZE_MESSAGE_INFO_TITLE = "localize.message.info_title";
  public static final String KEY_LOCALIZE_MESSAGE_FILE_NOT_FOUND_MESSAGE = "localize.message.file_not_found_message";
  public static final String KEY_LOCALIZE_LOAD_SHAPE_DIALOG_TITLE = "localize.load_shape.dialog_title";
  public static final String KEY_LOCALIZE_LOAD_LOCATION_DIALOG_TITLE = "localize.load_location.dialog_title";
  public static final String KEY_LOCALIZE_LOAD_PROGRESS = "localize.load_progress";
  public static final String KEY_LOCALIZE_MAPS = "localize.maps";
  public static final String KEY_LOCALIZE_BYTES = "localize.bytes";
  public static final String KEY_LOCALIZE_CONTINUE_BUTTON = "localize.continue_button";
  public static final String KEY_LOCALIZE_IGNORE_BUTTON = "localize.ignore_button";
  public static final String KEY_LOCALIZE_DELETELOCK_BUTTON = "localize.deletelock_button";
  public static final String KEY_LOCALIZE_MESSAGE_WARNING_TITLE = "localize.message.warning_title";
  public static final String KEY_LOCALIZE_MESSAGE_LOCK_EXISTS_TEXT = "localize.message.lock_exists.text";
  public static final String KEY_LOCALIZE_MESSAGE_LOCK_EXISTS_TITLE = "localize.message.lock_exists.title";
  public static final String KEY_LOCALIZE_SAVE_AS_DIALOG_TITLE = "localize.save_as.dialog_title";
  public static final String KEY_LOCALIZE_MESSAGE_LINES_READ_MESSAGE = "localize.message.lines_read_message";
  public static final String KEY_LOCALIZE_IMAGES = "localize.images";
  public static final String KEY_LOCALIZE_NEW_LOCATION_MARKER_TITLE = "localize.new_location_marker_title";
  public static final String KEY_LOCALIZE_NAME = "localize.name";
  public static final String KEY_LOCALIZE_CATEGORY = "localize.category";
  public static final String KEY_LOCALIZE_LEVEL_OF_DETAIL = "localize.level_of_detail";
  public static final String KEY_LOCALIZE_OK_BUTTON = "localize.ok_button";
  public static final String KEY_LOCALIZE_APPLY_BUTTON = "localize.apply_button";
  public static final String KEY_LOCALIZE_GOTO_BUTTON = "localize.goto_button";
  public static final String KEY_LOCALIZE_CLICK_TO_CHANGE_POS_MESSAGE = "localize.click_to_change_pos_message";
  public static final String KEY_LOCALIZE_EMPTY_MARKER_NAME = "localize.empty_marker_name";
  public static final String KEY_LOCALIZE_MESSAGE_LATITUDE_OR_LONGITUDE_WRONG_FORMAT = "localize.message.latitude_or_longitude_wrong_format";
  public static final String KEY_LOCALIZE_MESSAGE_CREATE_DATABASE_MESSAGE="localize.message.create_database.message";
  public static final String KEY_LOCALIZE_MESSAGE_OPTIMIZE_DATABASE_MESSAGE = "localize.message.optimize_database.message";
  public static final String KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX="localize.location.marker.category.id.";
  public static final String KEY_LOCALIZE_SELECT_LOCATION_MARKER_CATEGORIES_TITLE = "localize.select_location_marker_categories_title";
  public static final String KEY_LOCALIZE_SEARCH_LOCATION_MARKER_TITLE = "localize.search_location_marker_title";
  public static final String KEY_LOCALIZE_DISPLAY = "localize.display";
  public static final String KEY_LOCALIZE_CENTER_MAP_DIALOG_TITLE = "localize.center_map_dialog_title";
  public static final String KEY_LOCALIZE_EXPORT_LOCATION_DIALOG_TITLE = "localize.export_location.dialog_title";
  public static final String KEY_LOCALIZE_MESSAGE_EXPORT_ERROR_MESSAGE = "localize.message.export_error_message";
  public static final String KEY_LOCALIZE_ALL = "localize.all";
  public static final String KEY_LOCALIZE_NONE = "localize.none";
  public static final String KEY_LOCALIZE_NOTHING_FOUND = "localize.nothing_found";
  public static final String KEY_LOCALIZE_MESSAGE_GPS_PROPERTIES_EFFECT_ON_RESTART = "localize.message.gps_properties_effect_on_restart";
  
  
      /** current position */
  public static final String KEY_POSITION_ICON = "position.icon";
  public static final String KEY_POSITION_USE_ICON  = "position.use_icon";
  public static final String KEY_POSITION_CIRCLE_RADIUS = "position.circle.radius";
  public static final String KEY_POSITION_CIRCLE_COLOR = "position.circle.color";
  public static final String KEY_POSITION_CIRCLE_LINE_WIDTH = "position.circle.line.width";
  public static final String KEY_POSITION_FOLLOW_ME_RELATIVE_BORDER = "position.follow_me.relative_border";
  public static final String KEY_POSITION_FOLLOW_ME_MODE = "position.follow_me.mode";
  
   /** track resources */
  public static final String KEY_TRACK_FILE_FORMAT = "track.file.format";
  public static final String KEY_TRACK_FILE_PREFIX = "track.file.prefix";
  public static final String KEY_TRACK_FILE_PATTERN = "track.file.pattern";
  public static final String KEY_TRACK_FILE_EXTENSION = "track.file.extension";
  public static final String KEY_TRACK_FILE_DESCRIPTIVE_NAME = "track.file.descriptive_name";
  public static final String KEY_TRACK_ACTIVE_TRACK_IDENTIFIER = "track.active_track.identifier";
  
      /** graticule resources */
  public static final String KEY_GRATICULE_COLOR_TEN_LINES = "graticule.color.ten_lines";
  public static final String KEY_GRATICULE_COLOR_FIVE_LINES = "graticule.color.five_lines";
  public static final String KEY_GRATICULE_COLOR_ONE_LINES = "graticule.color.one_lines";
  public static final String KEY_GRATICULE_LINES_THRESHOLD = "graticule.lines.threshold";
  public static final String KEY_GRATICULE_LAYER_ACTIVE = "graticule.layer_active";
  public static final String KEY_GRATICULE_DRAW_TEXT = "graticule.draw_text";
  
      /** shape resources */
  public static final String KEY_SHAPE_FILE_EXTENSION = "shape.file.extension";
  public static final String KEY_SHAPE_FILE_DESCRIPTIVE_NAME = "shape.file.descriptive_name";
  public static final String KEY_SHAPE_LAYER_ACTIVE = "shape.layer_active";
  
      /** map resources */
  public static final String KEY_MAP_VISIBLE_MAP_SCALE_FACTOR = "map.visible_map_scale_factor";
  public static final String KEY_MAP_LAYER_ACTIVE = "map.layer_active";
  
  /** location resources */
  public static final String KEY_LOCATION_FILE_CSV_EXTENSION = "location.file.csv.extension";
  public static final String KEY_LOCATION_FILE_CSV_DESCRIPTIVE_NAME = "location.file.csv.descriptive_name";
  public static final String KEY_LOCATION_FILE_GPSDRIVE_EXTENSION = "location.file.gpsdrive.extension";
  public static final String KEY_LOCATION_FILE_ZIP_EXTENSION = "location.file.zip.extension";
  public static final String KEY_LOCATION_FILE_GPSDRIVE_DESCRIPTIVE_NAME = "location.file.gpsdrive.descriptive_name";
  public static final String KEY_LOCATION_FILE_GEONET_EXTENSION = "location.file.geonet.extension";
  public static final String KEY_LOCATION_FILE_GEONET_COMPRESSED_EXTENSION = "location.file.geonet.compressed.extension";
  public static final String KEY_LOCATION_FILE_GEONET_DESCRIPTIVE_NAME = "location.file.geonet.descriptive_name";
  public static final String KEY_LOCATION_MARKER_TEXT_COLOR = "location.marker.text.color";
  public static final String KEY_LOCATION_MARKER_TEXT_BACKGROUND_COLOR = "location.marker.text.background_color";
  public static final String KEY_LOCATION_MARKER_TEXT_FONT_SIZE = "location.marker.text.font_size";
  public static final String KEY_LOCATION_MARKER_SHOW_NAMES = "location.marker.show_names";
  public static final String KEY_LOCATION_LAYER_ACTIVE = "location.layer_active";
  public static final String KEY_LOCATION_MARKER_DB_JDBCDRIVER = "location.marker.db.jdbcdriver";
  public static final String KEY_LOCATION_MARKER_DB_URL = "location.marker.db.url";
  public static final String KEY_LOCATION_MARKER_DB_USER = "location.marker.db.user";
  public static final String KEY_LOCATION_MARKER_DB_PASSWORD = "location.marker.db.password";
  public static final String KEY_LOCATION_MARKER_USE_DB = "location.marker.use_db";
  public static final String KEY_LOCATION_MARKER_DB_CREATE_DB_SCRIPT_URL = "location.marker.db.create_db_script_url";
  public static final String KEY_LOCATION_MARKER_DB_OPTIMIZE_DB_ON_START = "location.marker.db.optimize_db_on_start";
  public static final String KEY_LOCATION_MARKER_CATEGORY_AVAILABLE_CATEGORIES = "location.marker.category.available_categories";
  public static final String KEY_LOCATION_MARKER_CATEGORY_ICON_SUFFIX = "_icon";
  public static final String KEY_LOCATION_MARKER_CATEGORY_VISIBLE_SUFFIX = "_visible";
  public static final String KEY_LOCATION_MARKER_CATEGORY_LEVEL_OF_DETAIL_SUFFIX = "_levelofdetail";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_NAME_COLUMN_INDEX = "location.marker.select_category.name.column_index";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_VISIBLE_COLUMN_INDEX = "location.marker.select_category.visible.column_index";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_LEVEL_OF_DETAIL_COLUMN_INDEX = "location.marker.select_category.level_of_detail.column_index";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_NAME_COLUMN_WIDTH = "location.marker.select_category.name.column_width";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_VISIBLE_COLUMN_WIDTH = "location.marker.select_category.visible.column_width";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_LEVEL_OF_DETAIL_COLUMN_WIDTH = "location.marker.select_category.level_of_detail.column_width";
  public static final String KEY_LOCATION_MARKER_CATEGORY_LEVEL_OF_DETAIL_SCALES = "location.marker.category.level_of_detail.scales";
  public static final String KEY_LOCATION_MARKER_SELECT_CATEGORY_ALLOW_EDIT_LEVEL_OF_DETAIL = "location.marker.select_category.allow_edit.level_of_detail";

  
      /** scale resources */
  public static final String KEY_SCALE_RULE_AIMED_LENGTH = "scale.rule.aimed_length";
  public static final String KEY_SCALE_LAYER_ACTIVE = "scale.layer_active";

      /** other resources */
  public static final String KEY_RESOURCE_EDITOR_TITLE = "resource.editor_title";

      /** development resources */
  public static final String KEY_DEVELOPMENT_PLUGINS_CLASSLOADER_USE_DEFAULT_CLASSLOADER = "development.plugins.classloader.use_default_classloader";

  
  // menu resources
  public static final String KEY_MENU_MOUSE_MODE_LABEL = "menu.mouse_mode.label";
  public static final String KEY_MENU_PLUGIN_LABEL = "menu.plugin.label";
  public static final String KEY_MENU_LAYERS_LABEL = "menu.layers.label";

  // cursor resources 
  public static final String KEY_CURSOR_ZOOM_IN_ICON = "cursor.zoom_in.icon";
  public static final String KEY_CURSOR_ZOOM_OUT_ICON = "cursor.zoom_out.icon";
  public static final String KEY_CURSOR_PAN_ICON = "cursor.pan.icon";
  
  // Mouse Mode resources
  public static final String KEY_MOUSE_MODE_DEFAULT_MODE = "mouse_mode.default_mode";
}


