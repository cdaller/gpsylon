package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/*
 * This class describes the correction of the quarz depending on the
 * temperature.
 * These garmin packages are sent as part of a record and have the package id
 * 39 (0x27). They hold the following data:
 * <ul>
 * <li>Temperatur: 2 byte (signed), from -34 to 85 degrees</li>
 * <li>2 bytes: unknown</li>
 * <li>8 byte double (e.g 90 242 251 105 66 56 111 65 -> 16.368.147,312 Hz) 
 * average at the given temperature.
 * <li>8 byte double (102 159 26 47 56 56 111 65 -> 16.368.065,472 Hz) 
 * real value at the given temperatur. 16.368.065,472 is equal to zero value
 * (means that the gps device was never switched on at this temperature.</li>
 * <li>2 bytes: unknown</li>
 * <li>2 bytes: unknown</li>
 * </ul>
 */

public class GarminTemperaturCorrection
{

	double[] average_freq = new double[120];
  double[] real_freq = new double[120];
  
//----------------------------------------------------------------------
/**
 * Default Construtor.
 */
  public GarminTemperaturCorrection()
  {
  }

//----------------------------------------------------------------------
/**
 * Create a new Garmin Temperatur Correction object by the use of the first
 * data package.
 * @param garmin_package the data package
 */

  public GarminTemperaturCorrection(GarminPackage garmin_package)
  {
    if(Debug.DEBUG && Debug.isEnabled("garmin_temp_correction"))
      Debug.println("garmin_temp_correction","temp correction: "+garmin_package);
		addData(garmin_package);
  }

//----------------------------------------------------------------------
/**
 * Add a new temperatur value using the given garmin package.
 * @param garmin_package the data package holding the next temperatur
 *correction value.
 */
  public void addData(GarminPackage garmin_package)
  {
    if(Debug.DEBUG && Debug.isEnabled("garmin_temp_correction"))
      Debug.println("garmin_temp_correction","next data package: "+garmin_package);
		int temperatur = garmin_package.getNextAsSignedInt();
		int unknown = garmin_package.getNextAsWord();
		double freq1 = garmin_package.getNextAsDouble();
		double freq2 = garmin_package.getNextAsDouble();
//		System.out.println("temp="+temperatur+", freq1:" + freq1+", freq2:"+freq2);
		average_freq[temperatur+34] = freq1;
		real_freq[temperatur+34] = freq2;
  }

	public double[] getAverageFrequencies()
	{
		return(average_freq);
	}

  
	public double[] getRealFrequencies()
	{
		return(real_freq);
	}

  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminTemperaturCorrection[");
    buffer.append("]");
    return(buffer.toString());
  }

	public static void main(String[] args)
	{
		try
		{
			if(args.length < 1)
			{
				System.out.println("need to give a filename to read package data from!");
				return;
			}
			
			org.dinopolis.util.io.Tokenizer tokenizer = 
				new org.dinopolis.util.io.Tokenizer(new java.io.FileInputStream(args[0]));
			tokenizer.setDelimiters(" ");
			java.util.List tokens;
			tokens = tokenizer.nextLine();
			GarminPackage first = new GarminPackage(39,tokens.size());
			for(int index = 0; index < tokens.size(); index++)
			{
				first.put(Integer.parseInt((String)tokens.get(index)));
			}
			GarminTemperaturCorrection temp_correction = new GarminTemperaturCorrection(first);
			while(tokenizer.hasNextLine())
			{
				tokens = tokenizer.nextLine();
				GarminPackage data = new GarminPackage(39,tokens.size());
				for(int index = 0; index < tokens.size(); index++)
				{
					data.put(Integer.parseInt((String)tokens.get(index)));
				}
				temp_correction.addData(data);
			}

					// output:
			double[] average_freq = temp_correction.getAverageFrequencies();
			double[] real_freq = temp_correction.getRealFrequencies();
			for(int count = 0; count < average_freq.length; count++)
				System.out.println((count-34)+", " + average_freq[count]+", "+real_freq[count]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
