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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dinopolis.gpstool.gui.layer.location.LocationMarker;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerCategory;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSource;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSourceException;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.io.Tokenizer;

//----------------------------------------------------------------------
/**
 * The GeonetDataConverter reads geonet files and converts them on
 * request to various formats.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */
public class GeonetDataConverter
{
      /** the tokenizer to read the geonet data */
  Tokenizer tokenizer_;

  Resources resources_;

  Map cat_name_map_;

  String DEFAULT_CATEGORY = "others";

  FeedBack feedback_;
  
  public static final String[] geonet_keys_ =
    new String[] {"Region Code (RC)","Unique Feature Id (UFI)",
		  "Unique Name Id (UNI)","Degree Latitude (DD_LAT)",
		  "Degree Lotitude (DD_LONG)",
		  "Deg Min Sec Latitude (DMS_LAT)","Deg Min Sec Longiutde (DMS_LONG)",
		  "Universal Transmercator (UTM)","Joint Operation Graphic (JOG)",
		  "Feature Classification (FC)", "Feature Designation Code (DSG)",
		  "Populated Place Classifictation (PC)","Primary Country Code (CC1)",
		  "First Order Admin Divistion (ADM1)","Second Order Admin Division (ADM2)",
		  "Dimension (DIM)", "Secondary Country Code (CC2)","Name Type (NT)",
		  "Language Code (LC)","SHORT_FORM","GENERIC",
		  "SORT_NAME","FULL_NAME","FULL_NAME_ND",
		  "MODIFY_DATE"};


  protected SimpleDateFormat geonet_date_format_ = new SimpleDateFormat("yyyy-MM-dd");

  
//----------------------------------------------------------------------
/**
 * Creates and initializes a DataConverter from the given filename
 *
 * @param the file to read.
 * @exception FileNotFoundException if the file cannot be found.
 * @exception UnsupportedEncodingException if UTF-8 is not supported
 */
  public GeonetDataConverter(String filename)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(new FileInputStream(filename));

//     filename.endsWith(".gz") ? this(new FileReader(filename))
//       : this(new GZInputStream(new FileInputStream(filename)));
  }
  
//----------------------------------------------------------------------
/**
 * Creates and initializes a DataConverter from the given reader.
 *
 * @param reader the reader to use.
 */
  protected GeonetDataConverter(Reader reader)
  {
    tokenizer_ = new Tokenizer(new BufferedReader(reader));
    tokenizer_.setDelimiter('\t'); 
    tokenizer_.respectEscapedCharacters(false);
  }
  
//----------------------------------------------------------------------
/**
 * Creates and initializes a DataConverter from the given
 * inputstream. The Inputstream is initialized for reading UTF-8 data.
 *
 * @param input_stream the stream to use
 * @exception UnsupportedEncodingException if UTF-8 is not supported
 */
  public GeonetDataConverter(InputStream input_stream)
    throws UnsupportedEncodingException
  {
    this(new InputStreamReader(input_stream,"UTF-8"));
  }


//----------------------------------------------------------------------
/**
 * Sets the resources to read the category names, etc. from.
 *
 * @param resources the resources.
 */
  public void setResources(Resources resources)
  {
    resources_ = resources;
  }


//----------------------------------------------------------------------
/**
 * Sets the feedback callback to be informed about progress.
 *
 * @param feedback the feedback to inform
 */
  public void setFeedback(FeedBack feedback)
  {
    feedback_ = feedback;
  }
  
//----------------------------------------------------------------------
/**
 * Creates a map that uses the geonet keys as keys and the values read
 * from the geonet file as values. This helps to retrieve special
 * values without hassling with index numbers.
 *
 * @param geonet_line the data read from the geonet file.
 */
  public static Map createKeyMap(List geonet_line)
  {
    Map map = new TreeMap();
    for(int count= 0; count < geonet_line.size(); count++)
    {
      map.put(geonet_keys_[count],geonet_line.get(count));
    }
    return(map);
  }


//----------------------------------------------------------------------
/**
 * Converts the geonet category name to GPSMap category names.
 *
 * @param geonet_cat_name
 * @return GPSMap category name
 */
  public String convertGeonetCategoryName(String geonet_cat_name)
  {
    if(cat_name_map_ == null)
    {
          // found at http://216.40.224.145/namesearch/fd_cross_ref.html
      cat_name_map_ = new TreeMap();
      cat_name_map_.put("ADMD","admin_division");
      cat_name_map_.put("ADM1","admin_division1");
      cat_name_map_.put("ADM2","admin_division2");
      cat_name_map_.put("ADM3","admin_division3");
      cat_name_map_.put("ADM4","admin_division4");
      cat_name_map_.put("AIRB","airport");
      cat_name_map_.put("AIRP","airport");
      cat_name_map_.put("AIRF","airport");
      cat_name_map_.put("AMTH","theater");
      cat_name_map_.put("ATHF","sport");
      cat_name_map_.put("BAR","bar");
      cat_name_map_.put("BCH","beach");
      cat_name_map_.put("BCHS","beach");
      cat_name_map_.put("CAVE","touristic");
      cat_name_map_.put("CLF","hypsographic");
      cat_name_map_.put("CNYN","landscape");
      cat_name_map_.put("CSNO","entertainment");
      cat_name_map_.put("CSTL","castle");
      cat_name_map_.put("CTRM","medical");
      cat_name_map_.put("CTRR","religion");
      cat_name_map_.put("FISH","leisure");
      cat_name_map_.put("FJD","landscape");
      cat_name_map_.put("FJDS","landscape");
      cat_name_map_.put("FLD","agriculture");
      cat_name_map_.put("FLDI","agriculture1");
      cat_name_map_.put("FLLS","landscape");
      cat_name_map_.put("FRM","agriculture");
      cat_name_map_.put("FRMS","agriculture");
      cat_name_map_.put("FRMT","agriculture1");
      cat_name_map_.put("FT","castle");
      cat_name_map_.put("GDN","touristic");
      cat_name_map_.put("GHSE","hotel");
      cat_name_map_.put("GRAZ","agriculture1");
      cat_name_map_.put("GYSR","landscape");
      cat_name_map_.put("HBR","harbor");
      cat_name_map_.put("HBRX","harbor");
      cat_name_map_.put("HLL","mountain");
      cat_name_map_.put("HLLS","mountain");
      cat_name_map_.put("HLLU","mountain");
      cat_name_map_.put("HLSU","mountain");
      cat_name_map_.put("HLT","traffic");
      cat_name_map_.put("HSP","medical");
      cat_name_map_.put("HSPC","medical");
      cat_name_map_.put("HSTS","historical");
      cat_name_map_.put("HTL","hotel");
      cat_name_map_.put("HUT","hotel");
      cat_name_map_.put("HUTS","hotel");
      cat_name_map_.put("INSM","military");
      cat_name_map_.put("LCTY","agriculture1");
      cat_name_map_.put("LK","lake");
      cat_name_map_.put("LKC","lake");
      cat_name_map_.put("LKI","lake");
      cat_name_map_.put("LKN","lake");
      cat_name_map_.put("LKNI","lake");
      cat_name_map_.put("LKO","lake");
      cat_name_map_.put("LKOI","lake");
      cat_name_map_.put("LKS","lake");
      cat_name_map_.put("LKSC","lake");
      cat_name_map_.put("LKSN","lake");
      cat_name_map_.put("LKSNI","lake");
      cat_name_map_.put("LKX","lake");
      cat_name_map_.put("LTHSE","touristic");
      cat_name_map_.put("MAR","harbor");
      cat_name_map_.put("MFG","industry");
      cat_name_map_.put("MFGB","industry");
      cat_name_map_.put("MFGC","industry");
      cat_name_map_.put("MILB","military");
      cat_name_map_.put("MKT","shopping");
      cat_name_map_.put("MT","mountain");
      cat_name_map_.put("MTS","mountain");
      cat_name_map_.put("MTSU","mountain");
      cat_name_map_.put("MTU","mountain");
      cat_name_map_.put("MUS","museum");
      cat_name_map_.put("NVB","military");
      cat_name_map_.put("PAL","castle");
      cat_name_map_.put("PASS","hypsographic");
      cat_name_map_.put("PIER","harbor");
      cat_name_map_.put("PGDA","religion");
      cat_name_map_.put("PKLT","parking");
      cat_name_map_.put("PKS","mountain");
      cat_name_map_.put("PK","mountain");
      cat_name_map_.put("PKS","mountain");
      cat_name_map_.put("PKSU","mountain");
      cat_name_map_.put("PK","mountain");
      cat_name_map_.put("PLAT","mountain");
      cat_name_map_.put("PO","post_office");
      cat_name_map_.put("PP","police");
      cat_name_map_.put("PPL","city");
      cat_name_map_.put("PPLA","city_capital");
      cat_name_map_.put("PPLC","city_capital1");
      cat_name_map_.put("PPLL","city1");
      cat_name_map_.put("PPLX","city_part");
      cat_name_map_.put("PRT","harbor");
      cat_name_map_.put("PRY","touristic");
      cat_name_map_.put("PYRS","touristic");
      cat_name_map_.put("QUAY","harbor");
      cat_name_map_.put("RECG","golf_course");
      cat_name_map_.put("RECR","sport");
      cat_name_map_.put("RHSE","restaurant");
      cat_name_map_.put("RK","hypsographic");
      cat_name_map_.put("RKS","hypsographic");
      cat_name_map_.put("RKFL","hypsographic");
      cat_name_map_.put("RLG","religion");
      cat_name_map_.put("RSTN","train_station");
      cat_name_map_.put("RSTP","train_station");
      cat_name_map_.put("RTE","traffic");
      cat_name_map_.put("RUIN","castle");
      cat_name_map_.put("SHRN","religion");
      cat_name_map_.put("SLP","hypsographic");
      cat_name_map_.put("SPNT","beach");
      cat_name_map_.put("STDM","sport");
      cat_name_map_.put("STM","river");
      cat_name_map_.put("STMA","river1");
      cat_name_map_.put("STMB","river1");
      cat_name_map_.put("STMC","river1");
      cat_name_map_.put("STMD","river1");
      cat_name_map_.put("STMH","river1");
      cat_name_map_.put("STMI","river1");
      cat_name_map_.put("STMIX","river1");
      cat_name_map_.put("STMM","river1");
      cat_name_map_.put("STMS","river");
      cat_name_map_.put("STMSB","river1");
      cat_name_map_.put("STMX","river1");
      cat_name_map_.put("TOWR","touristic");
      cat_name_map_.put("VAL","mountain");
      cat_name_map_.put("VALS","mountain");
      cat_name_map_.put("VLSU","mountain");
      cat_name_map_.put("ZOO","zoo");
    }
    
    String name = (String)cat_name_map_.get(geonet_cat_name);
    if(name == null)
    {

      if(Debug.DEBUG)
        Debug.println("geonet","no matching category for "+geonet_cat_name+" found, using default.");
      return(DEFAULT_CATEGORY);
    }

    return(name);
  }
  
//----------------------------------------------------------------------
/**
 * Inserts the content of the geonet file into the given location
 * marker source. It translates the category keys so they match GPSMap
 * categories.
 *
 * @param target the location marker source to insert the data.
 */
  public void insertIntoLocationMarkerSource(LocationMarkerSource target)
    throws IOException, LocationMarkerSourceException, UnsupportedOperationException
  {
    List list;
    LocationMarker marker;
    LocationMarkerCategory category;
    String category_id;
    String name;
    float latitude;
    float longitude;
    String geonet_category;

        // skip header line:
    if(tokenizer_.hasNextLine())
      list = tokenizer_.nextLine();

        // start to parse geonet data:
    while(tokenizer_.hasNextLine())
    {
      list = tokenizer_.nextLine();
      try
      {
        if(((String)list.get(17)).equals("V"))  // geonet NT variant
        {
              // ignoring variant of name
          if(Debug.DEBUG)
            Debug.println("geonet","ignored variant of name");
        }
        else
        {
          geonet_category = ((String)list.get(10)).trim(); // geonet DSG
          category_id = convertGeonetCategoryName(geonet_category); // geonet DSG
          category = new LocationMarkerCategory("",category_id);
          name = (String)list.get(22);   // geonet FULL_NAME
  
          if((Debug.DEBUG) && Debug.isEnabled("geonet_categories"))
            name = name + " ("+geonet_category+")";
          
          latitude = Float.parseFloat((String)list.get(3));
          longitude = Float.parseFloat((String)list.get(4));
  
          marker = new LocationMarker(name,latitude,longitude,category);
          target.putLocationMarker(marker);
          }
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      if((feedback_ != null) && (tokenizer_.getLineNumber() % 100 == 0))
      {
        feedback_.feedBack(new Integer(tokenizer_.getLineNumber()));
      }
        
      
//       Iterator iterator = list.iterator();
//       column_count = 0;
//       while(iterator.hasNext())
//       {
//         System.out.println(geonet_keys_[column_count]+": '"+(String)iterator.next()+"'");
//         column_count++;
//       }
//       System.out.println("----------------------------------------------------------------------");
    }
    tokenizer_.close();
  }


  public void insertIntoDatabase(String driver_name, String jdbc_url,
                                 String username, String password)
    throws SQLException, ClassNotFoundException, IOException, ParseException
  {
    JDBCUtil util = new JDBCUtil(driver_name,jdbc_url,username,password);
    util.open();
    if(!util.tableExists("GEONET_DATA"))
    {
      System.out.println("Create database GEONET_DATA:");
      String query = "CREATE CACHED TABLE geonet_data (  "
                     + "geonet_id INTEGER IDENTITY PRIMARY KEY, "
                     + "RC TINYINT, "
                     + "UFI VARCHAR(15), "
                     + "UNI VARCHAR(15), "
                     + "DD_LAT DOUBLE, "
                     + "DD_LONG DOUBLE, "
                     + "DMS_LAT VARCHAR(8), "
                     + "DMS_LONG VARCHAR(8), "
                     + "UTM VARCHAR(10), "
                     + "JOG VARCHAR(20), "
                     + "FC CHAR(1), "
                     + "DSG VARCHAR(6), "
                     + "PC INTEGER NULL, "
                     + "CC1 CHAR(2), "
                     + "ADM1 CHAR(2), "
                     + "ADM2 CHAR(2) NULL, "
                     + "DIM VARCHAR(20) NULL, "
                     + "CC2 CHAR(2) NULL, "
                     + "NT CHAR(1), "
                     + "LC CHAR(2) NULL, "
                     + "SHORT_FORM VARCHAR(60) NULL, "
                     + "GENERIC VARCHAR(60) NULL, "
                     + "SORT_NAME VARCHAR(60), "
                     + "FULL_NAME VARCHAR(60), "
                     + "FULL_NAME_ND VARCHAR(60), "
                     + "MODIFY_DATE DATE)";
      util.executeUpdate(query);
      query = "CREATE INDEX geonet_full_name ON geonet_data (FULL_NAME)";
      util.executeUpdate(query);
      query = "CREATE INDEX geonet_coords ON geonet_data (DD_LAT, DD_LONG)";
      util.executeUpdate(query);
      query = "CREATE INDEX geonet_dsg ON geonet_data (DSG)";
      util.executeUpdate(query);
      query = "CREATE INDEX geonet_full_name ON geonet_data (FULL_NAME)";
      util.executeUpdate(query);
    }

    System.out.println("Insert geonet data into database...");

        // insert data into database:
    List field_list;
    LocationMarker marker;
    LocationMarkerCategory category;
    String category_id;
    String name;
    float latitude;
    float longitude;
    String geonet_category;
    
        // skip header line:
    if(tokenizer_.hasNextLine())
      field_list = tokenizer_.nextLine();
    
        // start to parse geonet data:
    StringBuffer insert_query;
    Iterator iterator;
    PreparedStatement pstmt = util.prepareStatement("INSERT INTO geonet_data VALUES(NULL,"
                                                    +"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


    pstmt.setObject(1,"1");
    pstmt.setString(2,"ufi");
    pstmt.setObject(3,"uni");
    pstmt.setObject(4,"47.0");
    pstmt.setObject(5,"15.0");
    pstmt.setObject(6,"470000");
    pstmt.setObject(7,"150000");
    pstmt.setObject(8,"UTM");
    pstmt.setObject(9,"JOG");
    pstmt.setObject(10,"A");
    pstmt.setObject(11,"DSG");
    pstmt.setObject(12,null);
    pstmt.setObject(13,"C1");
    pstmt.setObject(14,"A1");
    pstmt.setObject(15,"A2");
    pstmt.setObject(16,"DIM");
    pstmt.setObject(17,"C2");
    pstmt.setObject(18,"N");
    pstmt.setObject(19,"LC");
    pstmt.setObject(20,"SHORT");
    pstmt.setObject(21,"GENERIC");
    pstmt.setObject(22,"SORT");
    pstmt.setObject(23,"FULL");
    pstmt.setObject(24,"FULL_D");
    Date geonet_date1 = geonet_date_format_.parse("2002-11-19");
    java.sql.Date sql_date1 = new java.sql.Date(geonet_date1.getTime());
    pstmt.setDate(25,sql_date1);

    pstmt.executeUpdate();
    
    while(tokenizer_.hasNextLine())
    {
      field_list = tokenizer_.nextLine();
//      System.out.println(field_list);

      String value;
      for(int count = 0; count <= 23; count++)
      {
        value = (String)field_list.get(count);
        if(value.length() == 0)
          pstmt.setObject(count+1,null);
        else
          pstmt.setObject(count+1,value);
      }
      Date geonet_date = geonet_date_format_.parse((String)field_list.get(24));
      java.sql.Date sql_date = new java.sql.Date(geonet_date.getTime());
      pstmt.setDate(25,sql_date);

          // finally execute the query:
      pstmt.executeUpdate();

      if((feedback_ != null) && (tokenizer_.getLineNumber() % 100 == 0))
      {
        feedback_.feedBack(new Integer(tokenizer_.getLineNumber()));
      }
    }
    
    tokenizer_.close();
    System.out.println("Optimize database...");
    util.executeUpdate("CHECKPOINT");
    util.close();
    System.out.println("finished...");
  }

  public static void main(String[] args)
  {
    if(args.length < 1)
    {
      System.out.println("Usage");
      System.out.println("GeonetDataConverter <geonet_filename>");
      System.exit(1);
    }
    
    String filename = args[0];
    String jdbc_driver = "org.hsqldb.jdbcDriver";
    String jdbc_url =  "jdbc:hsqldb:/filer/cdaller/.gpsmap/marker/geonet_db";
    String jdbc_username =  "sa";
    String jdbc_password =  "";

    try
    {
      GeonetDataConverter converter = new GeonetDataConverter(filename);
      converter.setFeedback(new FeedBack(){
          public void feedBack(Object message)
          {
            System.out.println("progress: "+message);
          }
        });
      converter.insertIntoDatabase(jdbc_driver,jdbc_url,jdbc_username,jdbc_password);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
