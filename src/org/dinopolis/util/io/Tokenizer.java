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

package org.dinopolis.util.io;

import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;

//----------------------------------------------------------------------
/**
   
 * This tokenizer merges the benefits of the java.lang.StringTokenizer
 * class and the java.io.StreamTokenizer class. It provides a low
 * level and a high level interface to the tokenizer. The low level
 * interface consists of the method pair nextToken() and getWord(),
 * where the first returns the type of token in the parsing process,
 * and the latter returns the String element itself.
 * <p>
 * The high level interface consists of the methods hasNextLine() and
 * nextLine(). They use the low level interface to parse the data line
 * by line and create a list of strings from it.  <p> It is unsure, if
 * it is wise to mix the usage of the high and the low level
 * interface. For normal usage, the high level interface should be
 * more comfortable to use and does not provide any drawbacks.
 * <p>
 * The advantages compared to the StreamTokenizer class are: Unlike
 * the StreamTokenizer, this Tokenizer class returns the delimiters as
 * tokens and therefore may be used to tokenize e.g. comma separated
 * files with empty fields (the StreamTokenizer handles multiple
 * delimiters in a row like one delimiter).
 * <p>
 * The tokenizer respect quoted words, so the delimiter is ignored if
 * inside quotes. And it may handle escaped characters (like an
 * escaped quote character, or an escaped new line). So the line
 * <code>eric,"he said \"great!\""</code> returns <code>eric</code>
 * and <code>he said "great"</code> as words.
 * <p>
 * The design of the
 * Tokenizer allows to get empty columns as well as treat multiple
 * delimiters in a row as one delimiter. For the first approach
 * trigger the values on every DELIMITER and EOF token whereas for the
 * second, trigger only on WORD tokens.
 * <p>
 * If one wants to be informed about empty words as well, use the
 * Tokenizer like in the following code fragment:
 *  <pre>
 *   String word = "";
 *   int token;
 *   while((token = tokenizer.nextToken()) != Tokenizer.EOF)
 *   {
 *     switch(token)
 *     {
 *     case Tokenizer.EOL:
 *       System.out.println("word: "+word);
 *       word = "";
 *       System.out.println("-------------");
 *       break;
 *     case Tokenizer.WORD:
 *       word = tokenizer.getWord();
 *       break;
 *     case Tokenizer.QUOTED_WORD:
 *       word = tokenizer.getWord() + " (quoted)";
 *       break;
 *     case Tokenizer.DELIMITER:
 *       System.out.println("word: "+word);
 *       word = "";
 *       break;
 *     default:
 *       System.err.println("Unknown Token: "+token);
 *     }
 *   }
 * </pre>
 * In this example, if the delimiter is set to a comma, a line like
 * <code>column1,,,column4</code> would be treated correctly.
 * <p>
 * The following example shows the usage of the tokenizer, if empty
 * fields can be ignored:
 * <pre>
 *  int token;
 *  while((token = tokenizer.nextToken()) != Tokenizer.EOF)
 *  {
 *    switch(token)
 *    {
 *    case Tokenizer.EOL:
 *      System.out.println("-------------");
 *      break;
 *    case Tokenizer.WORD:
 *      System.out.println("word: "+tokenizer.getWord());
 *      break;
 *    case Tokenizer.QUOTED_WORD:
 *      System.out.println("quoted word: "+tokenizer.getWord());
 *      break;
 *    case Tokenizer.DELIMITER:
 *      break;
 *    default:
 *      System.err.println("Unknown Token: "+token);
 *    }
 *  }
 * </pre>
 * Using a star as delimiter, the line <code>column1***column2<code>
 * could be correctly tokenized.
 * <p> 
 * For simplicity reasons, this implementation only supports a single
 * character as delimiter, extending subclasses may provide additional
 * functionality by overriding the isDelimiter method.
 * <p>
 * This tokenizer uses the LF character as end of line characters. It
 * ignores any CR characters, so it can be used in windows
 * environments as well.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class Tokenizer 
{
  protected PushbackReader reader_;
  protected StringBuffer buffer_;
  protected int delimiter_ = ',';
  protected int escape_char_ = '\\';
  protected int quote_char_ = '"';

  protected boolean escape_mode_ = false;

  protected boolean eol_is_significant_ = true;
  protected boolean respect_escaped_chars_ = false;
  protected boolean respect_quoted_words_ = true;

  protected int line_count_ = 1;

  protected boolean eof_reached_ = false;

  protected int last_token_ = NOT_STARTED;
  
  public static final int EOF = -1;
  public static final int EOL = 0;
  public static final int WORD = 1;
  public static final int QUOTED_WORD = 2;
  public static final int DELIMITER = 3;
  public static final int ERROR = 4;
  public static final int NOT_STARTED = 5;


//----------------------------------------------------------------------
/**
 * Creates a tokenizer that reads from the given string. It uses the
 * comma as delimiter, does not respect escape characters but respects
 * quoted words.
 *
 * @param string the string to read from.
 */
  public Tokenizer(String string)
  {
    this(new StringReader(string));
  }
  
//----------------------------------------------------------------------
/**
 * Creates a tokenizer that reads from the given string. It uses the
 * comma as delimiter, does not respect escape characters but respects
 * quoted words.
 *
 * @param string the string to read from.
 */
  public Tokenizer(InputStream in_stream)
  {
    this(new InputStreamReader(in_stream));
  }
  
//----------------------------------------------------------------------
/**
 * Creates a tokenizer that reads from the given reader. It uses the
 * comma as delimiter, does not respect escape characters but respects
 * quoted words.
 *
 * @param reader the reader to read from.
 */
  public Tokenizer(Reader reader)
  {
    reader_ = new PushbackReader(reader,2);
    buffer_ = new StringBuffer();
  }

//----------------------------------------------------------------------
/**
 * Set the delimiter character. The default is the comma.
 *
 * @param delimiter_char the delimiter character.
 */
  public void setDelimiter(int c)
  {
    delimiter_ = c;
  }

//----------------------------------------------------------------------
/**
 * Get the delimiter character.
 *
 * @return the delimiter character.
 */
  public int getDelimiter()
  {
    return(delimiter_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the escape character. The default is the backslash.
 *
 * @param escape_char the escape character.
 */
  public void setEscapeChar(int c)
  {
    escape_char_ = c;
  }

//----------------------------------------------------------------------
/**
 * Get the escape character.
 *
 * @return the escape character.
 */
  public int getEscapeChar()
  {
    return(escape_char_);
  }
  
//----------------------------------------------------------------------
/**
 * If escape characters should be respected, set the param to
 * <code>true</code>. The default is to ignore escape characters.
 *
 * @param respect_escaped If escape characters should be respected,
 * set the param to <code>true</code>.
 */
  public void respectEscapedCharacters(boolean respect_escape)
  {
    respect_escaped_chars_ = respect_escape;
  }

//----------------------------------------------------------------------
/**
 * Returns <code>true</code>, if escape character is respected.
 *
 * @return <code>true</code>, if escape character is respected.
 */
  public boolean respectEscapedCharacters()
  {
    return(respect_escaped_chars_);
  }

//----------------------------------------------------------------------
/**
 * Get the quote character.
 *
 * @return the quote character.
 */
  public int getQuoteChar() 
  {
    return (quote_char_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the quote character. The default is the double quote.
 *
 * @param quote_char the quote character.
 */
  public void setQuoteChar(int quote_char) 
  {
    quote_char_ = quote_char;
  }
  
//----------------------------------------------------------------------
/**
 * If quoted words should be respected, set the param to
 * <code>true</code>. The default is to respect quoted words.
 *
 * @param respect_quotes If quoted words should be respected,
 * set the param to <code>true</code>.
 */
  public void respectQuotedWords(boolean respect_quotes)
  {
    respect_quoted_words_ = respect_quotes;
  }

//----------------------------------------------------------------------
/**
 * Returns <code>true</code>, if quoted words are respected.
 *
 * @return <code>true</code>, if quoted words are respected.
 */
  public boolean respectQuotedWords()
  {
    return(respect_quoted_words_);
  }

//----------------------------------------------------------------------
/**
 * If set to <code>true</code> the end of line is signaled by the EOL
 * token.  If set to <code>false</code> end of line is treated as a
 * normal delimiter. The default value is true;
 *
 * @param significant if the end of line is treated as a special token
 * or as a delimiter.
 */
  public void eolIsSignificant(boolean significant)
  {
    eol_is_significant_ = significant;
  }

//----------------------------------------------------------------------
/**
 * Returns <code>true</code>, if in case of an end of line detected,
 * an EOL token is returned. If <code>false</code>, the end of line is
 * treated as a normal delimiter.
 *
 * @return <code>true</code>, if in case of an end of line detected,
 * an EOL token is returned. If <code>false</code>, the end of line is
 * treated as a normal delimiter.
 */
  public boolean isEolSignificant()
  {
    return(eol_is_significant_);
  }

  
//----------------------------------------------------------------------
/**
 * Returns the current line number of the reader.
 *
 * @return the current line number of the reader.
 */
  public int getLineNumber()
  {
    return(line_count_);
  }

//----------------------------------------------------------------------
/**
 * Returns the value of the token. If the token was of the type WORD,
 * the word is returned.
 *
 * @return the value of the token. 
 */
  public String getWord()
  {
    return(buffer_.toString()); 
  }

//----------------------------------------------------------------------
/**
 * Returns the last token that was returned from the nextToken() method.
 *
 * @return the last token. 
 */
  public int getLastToken()
  {
    return(last_token_);
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the given character is seen as a delimiter. This
 * method respects escape_mode, so if the escape character was found
 * before, it has to act accordingly (usually, return false, even if
 * the character is a delimiter).
 *
 * @return true, if the given character is seen as a delimiter.
 */
  protected boolean isDelimiter(int c)
  {
        // check for escape mode:
    if(escape_mode_)
      return(false);

    return(c == delimiter_);
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the given character is seen as a quote
 * character. This method respects escape_mode, so if the escape
 * character was found before, it has to act accordingly (usually,
 * return false, even if the character is a quote character).
 *
 * @return true, if the given character is seen as a quote character.
 */
  protected boolean isQuoteChar(int c)
  {
    if(!respect_quoted_words_)
      return(false);
    
        // check for escape mode:
    if(escape_mode_)
      return(false);

    return(c == quote_char_);
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the given character is seen as a escape
 * character. This method respects escape_mode, so if the escape
 * character was found before, it has to act accordingly (usually,
 * return false, even if the character is a escape character).
 *
 * @return true, if the given character is seen as a escape character.
 */
  protected boolean isEscapeChar(int c)
  {
    if(!respect_escaped_chars_)
      return(false);
    
        // check for escape mode:
    if(escape_mode_)
      return(false);

    return(c == escape_char_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns true, if the given character is seen as a end of line
 * character. This method respects end of line_mode, so if the end of
 * line character was found before, it has to act accordingly
 * (usually, return false, even if the character is a end of line
 * character).
 *
 * @return true, if the given character is seen as a end of line
 * character.
 */
  protected boolean isEndOfLine(int c)
  {
        // check for escape mode:
    if(escape_mode_)
    {
      if(c == '\n')   // add line count, even if in escape mode!
        line_count_++;
      return(false);
    }
    if(c == -1)
      eof_reached_ = true;
    
    return((c=='\n') || (c=='\r') || (c == -1));
  }

//----------------------------------------------------------------------
/**
 * Closes the tokenizer (and the reader is uses internally).
 *
 * @exception IOException if an error occured.
 */
  public void close()
    throws IOException
  {
    reader_.close();
  }

//----------------------------------------------------------------------
/**
 * Reads and returns the next character from the reader and checks for
 * the escape character. If an escape character is read, a flag is set
 * and the next character is read. A newline following the escape
 * character is ignored.
 *
 * @return the next character.
 * @exception IOException if an error occured.
 */
  protected int readNextChar()
    throws IOException
  {
    int next_char = reader_.read();
    if(escape_mode_)
    {
      escape_mode_ = false;
    }
    else
    {
      if(isEscapeChar(next_char))
      {
            // ignore escape char itself:
        next_char = reader_.read();

            // check for newline and ignore it:
        if(isEndOfLine(next_char))
        {
          line_count_++;
          next_char = reader_.read();
              // ignore CR:
          if(next_char == '\r')
          {
            next_char = readNextChar(); 
          }
        }
        escape_mode_ = true;
      }
    }
        // ignore CR:
    if(next_char == '\r')
    {
      next_char = readNextChar(); 
    }
    return(next_char);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the next token from the reader. The token's value may be
 * WORD, QUOTED_WORD, EOF, EOL, or DELIMITER. In the case or WORD or
 * QUOTED_WORD the actual word can be obtained by the use of the
 * getWord method.
 *
 * @return the next token.
 * @exception IOException if an error occured.
 */
  public int nextToken()
    throws IOException
  {
    buffer_.setLength(0);

    int next_char;
    next_char = readNextChar();

        // handle EOF:
    if(eof_reached_)
    {
      last_token_ = EOF;
      return(EOF);
    }

        // handle EOL:
    if(isEndOfLine(next_char))
    {
      line_count_++;
      if(eol_is_significant_)
      {
        last_token_ = EOL;
        return(EOL);
      }
      else
      {
        last_token_ = DELIMITER;
        return(DELIMITER);
      }
    }

        // handle DELIMITER
    if(isDelimiter(next_char))
    {
      last_token_ = DELIMITER;
      return(DELIMITER);
    }
    
        // handle quoted words:
    if(isQuoteChar(next_char))
    {
      while(true)
      {
        next_char = readNextChar();
        if(isEndOfLine(next_char))
        {
          last_token_ = ERROR;
          return(ERROR);
        }
        else
        {
          if(isQuoteChar(next_char))
          {
            last_token_ = QUOTED_WORD;
            return(QUOTED_WORD);
          }

              // no special char, then append to buffer:
          buffer_.append((char)next_char);
        }
      }
    }

        // handle 'normal' words:
    while(true)
    {
      buffer_.append((char)next_char);
      next_char = readNextChar();
      if(isDelimiter(next_char) || isEndOfLine(next_char))
      {
        reader_.unread(next_char);
        last_token_ = WORD;
        return(WORD);
      }
    }
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the tokenizer can return another line. 
 *
 * @return true, if the tokenizer can return another line. 
 * @exception IOException if an error occured.
 */
  public boolean hasNextLine()
    throws IOException
  {
    if(last_token_ == EOF)
      return(false);

    if((last_token_ == EOL) || (last_token_ == NOT_STARTED))
    {
      int next_char = readNextChar();
      if(next_char == -1)
        return(false);

      reader_.unread(next_char);
    }
    return(true);
  }


//----------------------------------------------------------------------
/**
 * Returns a list of elements (Strings) from the next line of the
 * tokenizer. If there are multiple delimiters without any values in
 * between, empty (zero length) strings are added to the list. They
 * may be removed by the use of the removeZeroLengthElements method.
 *
 * @return a list of elements (Strings) from the next line of the
 * tokenizer.
 * @exception IOException if an error occured.
 */
  public List nextLine()
    throws IOException
  {
    int token = nextToken();
    Vector list = new Vector();
    String word = "";
    while(token != Tokenizer.EOF)
    {
      switch(token)
      {
        case Tokenizer.WORD:
          word = getWord();
          break;
        case Tokenizer.QUOTED_WORD:
          word = getWord();
          break;
        case Tokenizer.DELIMITER:
          list.add(word);
          word = "";
          break;
        case Tokenizer.EOL:
          list.add(word);
          return(list);
        default:
          System.err.println("Unknown Token: "+token);
      }
      token = nextToken();
    }
    return(list);
  }

//----------------------------------------------------------------------
/**
 * This helper method removes all zero length elements from the given
 * list and returns it.
 *
 * @return the list where all zero length elements are remove.
 */
  public static List removeZeroLengthElements(List list)
  {
    Iterator iterator = list.iterator();
    String value;
    while(iterator.hasNext())
    {
      value = (String)iterator.next();
      if(value.length() == 0)
        iterator.remove();
    }
    return(list);
  }

  protected static void testLowLevel(String[] args)
  {
    try
    {
      String filename;
      if(args.length > 0)
        filename = args[0];
      else
        filename = "/filer/cdaller/tmp/test.csv";
      
      Tokenizer tokenizer = new Tokenizer(new BufferedReader(new FileReader(filename)));
//      Tokenizer tokenizer = new Tokenizer("column1,\"quoted column2\",column3\\, with quoted comma");
      tokenizer.setDelimiter(',');
//      tokenizer.eolIsSignificant(false);
      tokenizer.respectEscapedCharacters(true);
      tokenizer.respectQuotedWords(true);
      
      int token;
      while((token = tokenizer.nextToken()) != Tokenizer.EOF)
      {
        switch(token)
        {
        case Tokenizer.EOL:
          System.out.println("------------- ");
          break;
        case Tokenizer.WORD:
          System.out.println("line" +tokenizer.getLineNumber() +" word: "+tokenizer.getWord());
          break;
        case Tokenizer.QUOTED_WORD:
          System.out.println("line" +tokenizer.getLineNumber() +" quoted word: "+tokenizer.getWord());
          break;
        case Tokenizer.DELIMITER:
          System.out.println("delimiter");
          break;
        default:
          System.err.println("Unknown Token: "+token);
        }
      }
      tokenizer.close();
    }
    catch(Exception ioe)
    {
      ioe.printStackTrace();
    }
  }


  protected static void testHighLevel(String[] args)
  {
    try
    {
      String filename;
      if(args.length > 0)
        filename = args[0];
      else
        filename = "/filer/cdaller/tmp/test.csv";
      
      Tokenizer tokenizer = new Tokenizer(new BufferedReader(new FileReader(filename)));
//      Tokenizer tokenizer = new Tokenizer("column1,\"quoted column2\",column3\\, with quoted comma");
      tokenizer.setDelimiter(',');
//      tokenizer.eolIsSignificant(false);
      tokenizer.respectEscapedCharacters(true);
      tokenizer.respectQuotedWords(true);

      List list;
      while(tokenizer.hasNextLine())
      {
        list = tokenizer.nextLine();
        System.out.println("List: "+list);
        System.out.println("List w/o zero length elements: "+removeZeroLengthElements(list));
        System.out.println("--");
      }
      
    }
    catch(Exception ioe)
    {
      ioe.printStackTrace();
    }
  }

  protected static void testGeonetUTF8(String[] args)
  {
    try
    {
      String filename;
      if(args.length > 0)
        filename = args[0];
      else
        filename = "/filer/cdaller/tmp/test.csv";

      Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
      
      Tokenizer tokenizer = new Tokenizer(reader);
//      Tokenizer tokenizer = new Tokenizer("column1,\"quoted column2\",column3\\, with quoted comma");
      tokenizer.setDelimiter('\t');
//      tokenizer.eolIsSignificant(false);
      tokenizer.respectEscapedCharacters(true);
      tokenizer.respectQuotedWords(true);

      List list;
      while(tokenizer.hasNextLine())
      {
        list = tokenizer.nextLine();
        System.out.println("Name: "+list.get(20));
      }
      
    }
    catch(Exception ioe)
    {
      ioe.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
//    testLowLevel(args);
//    testHighLevel(args);
    testGeonetUTF8(args);
  }
}


