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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.dinopolis.gpstool.gui.layer.location.LocationMarker;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;

import java.io.FileNotFoundException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.sql.DatabaseMetaData;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;

//----------------------------------------------------------------------
/**
 * This class can be used as an abstraction layer for different sql databases.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class JDBCUtil 
{
  String driver_name_;
  String jdbc_url_;
  String username_;
  String password_;
  Connection db_connection_;
  Statement db_statement_;
  boolean debug_ = true;
  
//----------------------------------------------------------------------
/**
 * Constructs a location marker source that reads from a jdbc
 * connection.
 *
 * @param driver_name the name of the jdbc driver to use.
 * @param jdbc_url the url to use.
 * @param username the username to use.
 * @param password the password to use.
 */
  public JDBCUtil(String driver_name, String jdbc_url,
                  String username, String password)
  {
    driver_name_ = driver_name;
    jdbc_url_ = jdbc_url;
    username_ = username;
    password_ = password;
  }

//----------------------------------------------------------------------
/**
 * Set debug to true to print information on stdout.
 *
 * @param debug the debug.
 */
  public void setDebug(boolean debug) 
  {
    debug_ = debug;
  }
  
  public void open()
    throws ClassNotFoundException, SQLException
  {
      Class.forName(driver_name_);
          // Connect to the database
      db_connection_ = DriverManager.getConnection(jdbc_url_,username_,password_);
      db_statement_ = db_connection_.createStatement();
  }

  public void close()
    throws SQLException
  {
    db_connection_.close();
  }

//----------------------------------------------------------------------
/**
 * Queries the database for all tables.
 *
 * @return a vector containing the names of all tables.
 * @exception SQLException if the jdbc driver used throws an SQLException
 */
  
  public Vector getTables()
    throws SQLException
  {
    DatabaseMetaData db_meta_data = db_connection_.getMetaData();
    ResultSet rs;
    Vector tables = new Vector();
    rs = db_meta_data.getTables(null,null,null,new String[]{"TABLE"});
    while(rs.next())
      tables.addElement(rs.getString("TABLE_NAME"));
    return(tables);
  }

//----------------------------------------------------------------------
/**
 * Returns true if the given tablename exists. Comparison is case
 * insensitive!
 *
 * @return true if the given tablename exists.
 * @exception SQLException if the jdbc driver used throws an SQLException
 */
  
  public boolean tableExists(String tablename)
    throws SQLException
  {
    Vector tables = getTables();
    Iterator iterator = tables.iterator();
    tablename = tablename.toLowerCase();
    while(iterator.hasNext())
    {
      if(((String)iterator.next()).toLowerCase().equals(tablename))
        return(true);
    }
    return(false);
  }


//----------------------------------------------------------------------
/**
 * Prints the result of a reader containing sql statements. The '#'
 * character indicates that the rest of the line is to be taken as a
 * comment. All sql statements are separated by ';'. An sql statement
 * may be written in two or more lines.
 *
 * @exception SQLException if the jdbc driver used throws an SQLException
 */
  public void executeSQLFile(String filename)
    throws SQLException, FileNotFoundException
  {
    executeSQL(new FileReader(filename));
  }
  
//----------------------------------------------------------------------
/**
 * Prints the result of a file containing sql statements. The '#'
 * character indicates that the rest of the line is to be taken as a
 * comment. All sql statements are separated by ';'. An sql statement
 * may be written in two or more lines.
 *
 * @exception SQLException if the jdbc driver used throws an SQLException
 */

  public void executeSQL(Reader sql_reader)
    throws SQLException
  {
    try
    {
      BufferedReader reader = new BufferedReader(sql_reader);

      StreamTokenizer tokenizer = new StreamTokenizer(reader);
      tokenizer.resetSyntax();
      tokenizer.wordChars(0x0021,0x00ff);
      tokenizer.whitespaceChars('\t','\t');   // separate params
      tokenizer.whitespaceChars(' ',' ');     // separate params
      tokenizer.ordinaryChar(';');
      tokenizer.quoteChar ('\"');             // quote params
      tokenizer.quoteChar ('\'');             // quote params
      tokenizer.commentChar ('#');            // comments
      tokenizer.eolIsSignificant(false);

      int code;

      StringBuffer statement = new StringBuffer();

  out:
      while(true)
      {
        code = tokenizer.nextToken();
        switch(code)
        {
        case ';':
          printSQLResult(statement.toString());
//          System.out.println("STMT: "+statement);
          statement = new StringBuffer();
          break;

        case StreamTokenizer.TT_EOF:
          break out;

        case StreamTokenizer.TT_WORD:
          statement.append(tokenizer.sval);
          statement.append(" ");
          break;
        case '\"':
          statement.append("\"").append(tokenizer.sval).append("\"");
          statement.append(" ");
          break;
        case '\'':
          statement.append("\'").append(tokenizer.sval).append("\'");
          statement.append(" ");
          break;
        case StreamTokenizer.TT_EOL:
              // ignore new lines
          break;
        default:
          System.err.println("unknown token (code 0x0"+Integer.toHexString(code)
                             +") string="+tokenizer.sval+", line "+tokenizer.lineno());
        }
      }

      reader.close();
      if(statement.length() > 0)
        System.err.println("WARNING: Last sql statement in file was not executed due to missing ';' at the end!");
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Tries to increase the performance of the database. In case of an
 * hsqldb database, the sql command "CHECKPOINT" is used, otherwise
 * nothing is done for the moment.
 *
 * @exception SQLException if the jdbc driver used throws an SQLException
 */

  public void optimizeDatabase()
    throws SQLException
  {
    if(driver_name_.indexOf("hsqldb") > 0)
    {
      executeUpdate("CHECKPOINT");
    }
  }

//----------------------------------------------------------------------
/**
 * Prints the result of an sql query. If the query is a select query,
 * the resulting ResultSet is printed (with header), otherwise the
 * number of modified rows are printed.
 *
 * @exception SQLException if the jdbc driver used throws an SQLException
 */
  
  public void printSQLResult(String query)
    throws SQLException
  {
    if(debug_)
      System.out.println("Executing sql query: '"+query+"'");
    if(query.toLowerCase().trim().startsWith("select"))
    {
      ResultSet rs = executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
          // print headers:
      for(int col_count = 1; col_count <= rsmd.getColumnCount(); col_count++)
      {
        if(debug_)
          System.out.println("  NAME: " +rsmd.getColumnName(col_count)
                             + " TYPE: " +rsmd.getColumnType(col_count));
      }
      if(debug_)
        System.out.println("=====================");
          // print content:
      while(rs.next())
      {
        for(int col_count = 1; col_count <= rsmd.getColumnCount(); col_count++)
        {
          if(debug_)
            System.out.println("    "+rsmd.getColumnName(col_count)+": "+rs.getString(col_count));
        }
        if(debug_)
          System.out.println("---------------");
      }
    }
    else
    {
      int result = executeUpdate(query);
      if(debug_)
        System.out.println(result + " row(s) updated.");
    }
  }
  
//----------------------------------------------------------------------
/**
 * Executes an executeUpdate call on the db_statement_ and takes care,
 * that multiple accesses to db_statements_ is handled correctly.
 *
 * @exception SQLException if the jdbc driver used throws an SQLException
 */

  public  int executeUpdate(String sql)
    throws SQLException
  {
    if(debug_)
      System.err.println("executeUpdate: "+sql);
    synchronized(db_statement_)
    {
      return(db_statement_.executeUpdate(sql));
    }
  }

  
//----------------------------------------------------------------------
/**
 * Executes an executeQuery call on the db_statement_ and takes care,
 * that multiple accesses to db_statements_ is handled correctly.
 *
 * @return the result set sesulting from the executeQuery call.
 * @exception SQLException if the jdbc driver used throws an SQLException
 */
  public ResultSet executeQuery(String sql)
    throws SQLException
  {
    if(debug_)
      System.err.println("executeQuery: "+sql);
    synchronized(db_statement_)
    {
      return(db_statement_.executeQuery(sql));
    }
  }


  public PreparedStatement prepareStatement(String query)
    throws SQLException
  {
    return(db_connection_.prepareStatement(query));
  }
  

  public static void main(String[] args)
  {
    String jdbc_driver = "org.hsqldb.jdbcDriver";
    String jdbc_url =  "jdbc:hsqldb:/filer/cdaller/.gpsmap/marker/testdb";
    String jdbc_username =  "sa";
    String jdbc_password =  "";
    
    JDBCUtil jdbc_util = new JDBCUtil(jdbc_driver,jdbc_url,jdbc_username,jdbc_password);

    try
    {
      System.out.println("open");
      jdbc_util.open();
      System.out.println("check for table MARKERS:");
      
      if(!jdbc_util.tableExists("MARKERS"))
      {
        System.out.println("execute script:");
        jdbc_util.executeSQLFile("/filer/cdaller/cvs/dinopolis/auxiliary/org/dinopolis/gpstool/create_location_hsql.sql");
      }
      else
      {
        System.out.println("exists!");
      }
      jdbc_util.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}





