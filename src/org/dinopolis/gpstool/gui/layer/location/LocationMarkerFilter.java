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


package org.dinopolis.gpstool.gui.layer.location;
import java.sql.Types;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * This class is used as a filter for the location markers. The
 * getLocationMarker method of LocationMarkerSource objects accept
 * this kind of filters to choose the LocationMarkers. The key is a
 * uniquely identifier of the different filterable properties
 * (e.g. category, name, creation date, ...), whereas the operator
 * indicates the wanted selection (equals, not equals, greater than,
 * ...). The objects to match the given key with the given operation
 * can be set by the get/setValues method. So if one wants to create a
 * filter that filters entries of a given set of categories
 * (e.g. cat.equals("abc") || cat.equals("xyz"), the operation would
 * be EQUALS_OPERATION and the type would be OR_TYPE. Use KEY_FILTER
 * to combine more filters (as values) with the AND_TYPE or OR_TYPE.
 * <pre>
 *        // create a filter that does the following:
 *        // (category = 'train_station' OR category = 'airport') AND (name <> 'graz')
 *    
 *    LocationMarkerFilter cat_filter =
 *      new LocationMarkerFilter(KEY_CATEGORY, new String[]{"train_station","airport"},
 *                               OR_TYPE, EQUALS_OPERATION);
 *    LocationMarkerFilter name_filter =
 *      new LocationMarkerFilter(KEY_NAME, new String[]{"graz"}, NOT_EQUALS_OPERATION);
 *
 *    LocationMarkerFilter main_filter =
 *      new LocationMarkerFilter(KEY_FILTER,new LocationMarkerFilter[]{cat_filter,name_filter},
 *                               AND_TYPE,FILTER_OPERATION);
 *
 *        // create a prepared statement from the filter:
 *    JDBCPreparedStatementPart part = main_filter.toPreparedStatementPart();
 *    System.out.println("filter query: "+part.getQuery());
 *        // results in "(category =  ?  OR category =  ? ) AND (name <>  ? )"
 *    part.prependQuery("SELECT * FROM MARKERS WHERE ");
 *    System.out.println("full query: "+part.getQuery());
 *        // results in "SELECT * FROM MARKERS WHERE (category =  ?  OR category =  ? ) AND (name <>  ? )"
 * </pre>
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LocationMarkerFilter 
{
  public final static String KEY_FILTER = "filter";
  public final static String KEY_NAME = "name";
  public final static String KEY_DESCRIPTION = "description";
  public final static String KEY_CATEGORY = "category_id";
  public final static String KEY_IMPORTANCE = "importance";
  public final static String KEY_CREATION_DATE = "creation_date";
  public final static String KEY_CREATOR = "creator";

  
  /** all values must match the result */
  public final static int AND_TYPE = 1; 
  /** at least one of the values must match the result */
  public final static int OR_TYPE = 2; 

  protected final static int DEFAULT_TYPE = OR_TYPE;
  
  /** values must match the result */
  public final static int EQUALS_OPERATION = 1; 
  /** values must not match the result */
  public final static int NOT_EQUALS_OPERATION = 2; 
  /** the values must be less than the result */
  public final static int GREATER_THAN_OPERATION = 3; 
  /** the values must be greater than the result */
  public final static int LESS_THAN_OPERATION = 4;
  /** indicates a flexible string comparison (wildcards allowed) */
  public final static int LIKE_OPERATION = 5;
  /** indicates a filter operation (used for filters as values) */
  public final static int FILTER_OPERATION = 6;

  protected final static int DEFAULT_OPERATION = EQUALS_OPERATION;
  
  /** the key used in this filter */
  protected String key_;
  /** the boolean type used */
  int type_;
  /** the operation used in this filter to match the values */
  protected int operation_;
  /** the possible values matched with the given operation */
  protected Object[] values_;

      /** if true, ignore case in this query */
  protected boolean ignorecase_;

  protected boolean invalidated_ = true;
  protected JDBCPreparedStatementPart prep_stmt_part_;

//----------------------------------------------------------------------
/**
 * Empty Constructor
 *
 */
  public LocationMarkerFilter() 
  {
  }
  
//----------------------------------------------------------------------
/**
 * Creates a filter that compares the values for the given key with
 * the OR_TYPE and EQUALS_OPERATION.
 *
 * @param key the key
 * @param values the values
 *
 */
  public LocationMarkerFilter(String key, Object[] values) 
  {
    this(key, values, DEFAULT_TYPE, DEFAULT_OPERATION);
  }
  

//----------------------------------------------------------------------
/**
 * Creates a filter that compares the values for the given key with
 * the default type (OR)  and the given operation.
 *
 * @param key the key
 * @param values the values
 * @param operation the operation to use.
 *
 */
  public LocationMarkerFilter(String key, Object[] values, int operation) 
  {
    this(key, values, DEFAULT_TYPE, operation);
  }
  
//----------------------------------------------------------------------
/**
 * Creates a filter that compares the values for the given key with
 * the default type (OR)  and the given operation.
 *
 * @param key the key
 * @param values the values
 * @param operation the operation to use.
 * @param ignorecase if true, the filter compares case insensitive.
 *
 */
  public LocationMarkerFilter(String key, Object[] values, int operation, boolean ignorecase) 
  {
    this(key, values, DEFAULT_TYPE, operation, ignorecase);
  }
  
//----------------------------------------------------------------------
/**
 * Creates a filter that compares the values for the given key with
 * the given type and operation (and case sensitive comparison).
 *
 * @param key the key
 * @param values the values
 * @param type the boolean to use.
 * @param operation the operation to use.
 *
 */
  public LocationMarkerFilter(String key, Object[] values, int type, int operation) 
  {
    this(key,values,type,operation,false);
  }
  

//----------------------------------------------------------------------
/**
 * Creates a filter that compares the values for the given key with
 * the given type and operation.
 *
 * @param key the key
 * @param values the values
 * @param type the boolean to use.
 * @param operation the operation to use.
 * @param ignorecase if true, the filter compares case insensitive.
 *
 */
  public LocationMarkerFilter(String key, Object[] values, int type, int operation, boolean ignorecase) 
  {
    key_ = key;
    values_ = values;
    type_ = type;
    operation_ = operation;
    ignorecase_ = ignorecase;
  }
  

  
//----------------------------------------------------------------------
/**
 * Get the key.
 *
 * @return the key.
 */
  public String getKey() 
  {
    return (key_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the key.
 *
 * @param key the key.
 */
  public void setKey(String key) 
  {
    key_ = key;
  }

  
//----------------------------------------------------------------------
/**
 * Get the type.
 *
 * @return the type.
 */
  public int getType() 
  {
    return (type_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the type.
 *
 * @param type the type.
 */
  public void setType(int type) 
  {
    type_ = type;
  }
  
//----------------------------------------------------------------------
/**
 * Get the operation.
 *
 * @return the operation.
 */
  public int getOperation() 
  {
    return (operation_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the operation.
 *
 * @param operation the operation.
 */
  public void setOperation(int operation) 
  {
    operation_ = operation;
  }
  
//----------------------------------------------------------------------
/**
 * Get the values.
 *
 * @return the values.
 */
  public Object[] getValues() 
  {
    return (values_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the values.
 *
 * @param values the values.
 */
  public void setValues(Object[] values) 
  {
    values_ = values;
  }

  
//----------------------------------------------------------------------
/**
 * Returns true, if the case is ignored in this filter.
 *
 * @return if true, the case must be ignored in this filter.
 */
  public boolean isIgnoreCase() 
  {
    return (ignorecase_);
  }
  
//----------------------------------------------------------------------
/**
 * If set to true, the case must be ignored in this filter.
 *
 * @param ignorecase If set to true, the case must be ignored in this
 * filter.
 */
  public void setIgnoreCase(boolean ignorecase) 
  {
    ignorecase_ = ignorecase;
  }

//----------------------------------------------------------------------
/**
 * Invalidates a previously calculated string expression. This is
 * necessary after changing anything in the filter (values, type, or
 * operation).
 *
 */
  public void invalidateStringExpression()
  {
    invalidated_ = true;
  }

//----------------------------------------------------------------------
/**
 * Returns a string that may be used as part of a PreparedStatement
 * (in the where clause). The keys are used as column names. The
 * values to set into the PreparedStatement can be retrieved from the
 * Vector passed as a parameter. As the string is cached, any changes
 * in the filter (or filters used by this filter) imply to set the
 * expressions invalid by the use of the invalidateStringExpression()
 * method.
 *
 * @return astring that may be used as an sql query expression (in
 * the where clause).
 */
  public JDBCPreparedStatementPart toPreparedStatementPart()
  {
    if(invalidated_)
    {
      prep_stmt_part_ = addToPreparedStatementPart(new JDBCPreparedStatementPart(),this);
      invalidated_ = false;
    }
    return(prep_stmt_part_);
  }


//----------------------------------------------------------------------
/**
 * Returns a string buffer that may be used as an sql query expression
 * (in the where clause). The keys are used as column names. 
 *
 * @param buffer the stringbuffer to append the query (must not be null!). 
 * @return a string buffer holding the sql expression.
 */
  public static JDBCPreparedStatementPart addToPreparedStatementPart(JDBCPreparedStatementPart part,
                                                                     LocationMarkerFilter filter)
  {
    String type = null;
    switch(filter.getType())
    {
    case OR_TYPE:
      type = " OR ";
      break;
    case AND_TYPE:
      type = " AND ";
      break;
    }

    Object[] values = filter.getValues();
//     if(values.length == 0) // do nothing!
//       return(part);

    int sql_type;
    
    String key = filter.getKey();
    part.appendQuery("(");
    for (int value_count = 0; value_count < values.length; value_count++)
    {
      if(key.equals(KEY_FILTER))
      {
        LocationMarkerFilter next_filter = (LocationMarkerFilter)values[value_count];
        part.appendQuery("(");
        part = addToPreparedStatementPart(part, next_filter);
        part.appendQuery(")");
      }
      else
      {
        if(filter.isIgnoreCase())
        {
          part.appendQuery("UPPER(");
          part.appendQuery(key);
          part.appendQuery(")");
        }
        else
          part.appendQuery(key);
        part.appendQuery(getSQLOperation(filter.getOperation()));
            // add value placeholder:
        part.appendQuery(" ? ");
        
            // add value:
        sql_type = getSQLDataType(key);
        if(filter.isIgnoreCase() && (sql_type == Types.VARCHAR))
          part.addValue(((String)values[value_count]).toUpperCase(),sql_type);
        else
          part.addValue(values[value_count],sql_type);
      }
            // append AND or OR only if not the last:
      if(value_count < values.length-1)
        part.appendQuery(type);
    }
    part.appendQuery(")");
    return(part);
  }


//----------------------------------------------------------------------
/**
 * Returns the sql type as given in java.sql.Types for the given key
 * (e.g. if the key is set to KEY_CREATION_DATE this method returns Types.DATE).
 *
 * @return a int representing the type as specified in java.sql.Types.
 */
  public static int getSQLDataType(String key)
  {
    if(key.equals(KEY_IMPORTANCE))
      return(Types.INTEGER);
    if(key.equals(KEY_CREATION_DATE))
      return(Types.DATE);

    return(Types.VARCHAR);
  }

//----------------------------------------------------------------------
/**
 * Returns the sql type as given in java.sql.Types for the given key
 * (e.g. if the key is set to KEY_CREATION_DATE this method returns Types.DATE).
 *
 * @return a string representing the operation in sql syntax.
 */
  public static String getSQLOperation(int operation)
  {
    if(operation == EQUALS_OPERATION)
      return(" = ");
    if(operation == NOT_EQUALS_OPERATION)
      return(" <> ");
    if(operation == GREATER_THAN_OPERATION)
      return(" > ");
    if(operation == LESS_THAN_OPERATION)
      return(" < ");
    if(operation == LIKE_OPERATION)
      return(" LIKE ");
        // should never get here:
    throw new IllegalArgumentException("unknown operation in LocationMarkerFilter: "+operation);
  }


  public static void main(String[] args)
  {
        // create a filter that does the following:
        // (category = 'train_station' OR category = 'airport') AND (name <> 'graz')
    
    LocationMarkerFilter cat_filter =
      new LocationMarkerFilter(KEY_CATEGORY, new String[]{"train_station","airport"},
                               OR_TYPE, EQUALS_OPERATION);
    LocationMarkerFilter name_filter =
      new LocationMarkerFilter(KEY_NAME, new String[]{"graz"}, NOT_EQUALS_OPERATION);

    LocationMarkerFilter main_filter =
      new LocationMarkerFilter(KEY_FILTER,new LocationMarkerFilter[]{cat_filter,name_filter},
                               AND_TYPE,FILTER_OPERATION);

        // create a prepared statement from the filter:
    JDBCPreparedStatementPart part = main_filter.toPreparedStatementPart();
    System.out.println("filter query: "+part.getQuery());
        // results in "(category =  ?  OR category =  ? ) AND (name =  ? )"
    part.prependQuery("SELECT * FROM MARKERS WHERE ");
    System.out.println("full query: "+part.getQuery());
        // results in "SELECT * FROM MARKERS WHERE (category =  ?  OR category =  ? ) AND (name =  ? )"

    Vector values = part.getValues();
    Vector sql_types = part.getSQLTypes();
    for(int count = 0; count < values.size(); count++)
    {
      System.out.println("value: "+values.elementAt(count)+" is of type:"+sql_types.elementAt(count));
    }
    
  }
}
