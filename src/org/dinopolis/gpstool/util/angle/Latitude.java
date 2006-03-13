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


/*
 * Geotools - OpenSource mapping toolkit
 * (C) 2002, Centre for Computational Geography
 * (C) 2001, Institut de Recherche pour le Developpement
 * (C) 1999, Fisheries and Oceans Canada
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Contacts:
 *     UNITED KINGDOM: James Macgill
 *             mailto:j.macgill@geog.leeds.ac.uk
 *
 *     FRANCE: Surveillance de l'Environnement Assistee par Satellite
 *             Institut de Recherche pour le Developpement / US-Espace
 *             mailto:seasnet@teledetection.fr
 *
 *     CANADA: Observatoire du Saint-Laurent
 *             Institut Maurice-Lamontagne
 *             mailto:osl@osl.gc.ca
 */
package org.dinopolis.gpstool.util.angle;


/***
 * A latitude angle. Positive latitudes are North, while negative
 * latitudes are South. This class has no direct OpenGIS equivalent.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 *
 * @see Longitude
 * @see AngleFormat
 */
public final class Latitude extends Angle {
    /***
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -4496748683919618976L;
   
    /***
     * Minimum legal value for latitude (-90?).
     */
    public static final double MIN_VALUE = -90;
   
    /***
     * Maximum legal value for latitude (+90?).
     */
    public static final double MAX_VALUE = +90;
   
    /***
     * Contruct a new latitude with the specified value.
     *
     * @param theta Angle in degrees.
     */
    public Latitude(final double theta) {
        super(theta);
    }
   
    /***
     * Constructs a newly allocated <code>Latitude</code> object that
     * represents the latitude value represented by the string.   The
     * string should represents an angle in either fractional degrees
     * (e.g. 45.5?) or degrees with minutes and seconds (e.g. 45?30').
     * The hemisphere (N or S) is optional (default to North).
     *
     * @param  source A string to be converted to a <code>Latitude</code>.
     * @throws NumberFormatException if the string does not contain a parsable latitude.
     */
    public Latitude(final String source) throws NumberFormatException {
        super(source);
    }
}

