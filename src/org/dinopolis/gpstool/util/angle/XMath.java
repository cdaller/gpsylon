/*
 * Geotools - OpenSource mapping toolkit
 * (C) 2002, Centre for Computational Geography
 * (C) 2001, Institut de Recherche pour le D�veloppement
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
 *     FRANCE: Surveillance de l'Environnement Assist�e par Satellite
 *             Institut de Recherche pour le D�veloppement / US-Espace
 *             mailto:seasnet@teledetection.fr
 *
 *     CANADA: Observatoire du Saint-Laurent
 *             Institut Maurice-Lamontagne
 *             mailto:osl@osl.gc.ca
 */

package org.dinopolis.gpstool.util.angle;

// Miscellaneous
import java.lang.Math;
import java.text.ChoiceFormat;


/**
 * Simple mathematical functions. Some of these functions will
 * be removed if JavaSoft provide a standard implementation
 * or fix some issues in Bug Parade:<br>
 * <ul>
 *   <li><a href="http://developer.java.sun.com/developer/bugParade/bugs/4074599.html">Implement log10 (base 10 logarithm)</a></li>
 *   <li><a href="http://developer.java.sun.com/developer/bugParade/bugs/4358794.html">implement pow10 (power of 10) with optimization for integer powers</a>/li>
 *   <li><a href="http://developer.java.sun.com/developer/bugParade/bugs/4461243.html">Math.acos is very slow</a></li>
 * </ul>
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public final class XMath
{
    /**
     * Natural logarithm of 10.
     * Approximately equal to 2.302585.
     */
    public static final double LN10=2.3025850929940456840179914546844;
    
    /**
     * Table of some integer powers of 10. Used
     * for fast computation of {@link #pow10(int)}.
     */
    private static final double[] POW10 = {
        1E+00, 1E+01, 1E+02, 1E+03, 1E+04, 1E+05, 1E+06, 1E+07, 1E+08, 1E+09,
        1E+10, 1E+11, 1E+12, 1E+13, 1E+14, 1E+15, 1E+16, 1E+17, 1E+18, 1E+19,
        1E+20, 1E+21, 1E+22, 1E+23
    };

    /**
     * Do not allow instantiation of this class.
     */
    private XMath() {
    }

    /**
     * Combute the cubic root of the specified value. This is method will be removed if
     * <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4633024.html">RFE
     * 4633024</A> is implemented.
     */
    public static double cbrt(final double x) {
        return Math.pow(x, 1.0/3);
    }
    
    /**
     * Compute the hypotenuse (<code>sqrt(x�+y�)</code>).
     */
    public static double hypot(final double x, final double y) {
        return Math.sqrt(x*x + y*y);
    }

    /**
     * Compute the logarithm in base 10. See
     * http://developer.java.sun.com/developer/bugParade/bugs/4074599.html.
     */
    public static double log10(final double x) {
        return Math.log(x)/LN10;
    }
    
    /**
     * Compute 10 power <var>x</var>.
     */
    public static double pow10(final double x) {
        final int ix=(int) x;
        if (ix==x) {
            return pow10(ix);
        } else {
            return Math.pow(10, x);
        }
    }
    
    /**
     * Compute <var>x</var> to the power of 10. This computation is very fast
     * for small power of 10 but has some rounding error issues (see
     * http://developer.java.sun.com/developer/bugParade/bugs/4358794.html).
     */
    public static double pow10(final int x) {
        if (x>=0) {
            if (x<POW10.length) {
                return POW10[x];
            }
        } else if (x!=Integer.MIN_VALUE) {
            final int nx = -x;
            if (nx<POW10.length) {
                return 1/POW10[nx];
            }
        }
        try {
            /*
             * Note: Method 'Math.pow(10,x)' has rounding errors: it doesn't
             *       always return the closest IEEE floating point
             *       representation. Method 'Double.parseDouble("1E"+x)' gives
             *       as good or better numbers for ALL integer powers, but is
             *       much slower.  The difference is usually negligible, but
             *       powers of 10 are a special case since they are often
             *       used for scaling axes or formatting human-readable output.
             *       We hope that the current workaround is only temporary.
             *       (see http://developer.java.sun.com/developer/bugParade/bugs/4358794.html).
             */
            return Double.parseDouble("1E"+x);
        } catch (NumberFormatException exception) {
            return StrictMath.pow(10, x);
        }
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is null or <code>NaN</code> and
     *    +1 if <var>x</var> is positive.
     */
    public static int sgn(final double x) {
        if (x>0) return +1;
        if (x<0) return -1;
        else     return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is null or <code>NaN</code> and
     *    +1 if <var>x</var> is positive.
     */
    public static int sgn(final float x) {
        if (x>0) return +1;
        if (x<0) return -1;
        else     return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is null and
     *    +1 if <var>x</var> is positive.
     */
    public static int sgn(long x) {
        if (x>0) return +1;
        if (x<0) return -1;
        else     return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is null and
     *    +1 if <var>x</var> is positive.
     */
    public static int sgn(int x) {
        if (x>0) return +1;
        if (x<0) return -1;
        else     return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is null and
     *    +1 if <var>x</var> is positive.
     */
    public static short sgn(short x) {
        if (x>0) return (short) +1;
        if (x<0) return (short) -1;
        else     return (short)  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is null and
     *    +1 if <var>x</var> is positive.
     */
    public static byte sgn(byte x) {
        if (x>0) return (byte) +1;
        if (x<0) return (byte) -1;
        else     return (byte)  0;
    }

    /**
     * Finds the least float greater than d (if positive == true),
     * or the greatest float less than d (if positive == false).
     * If NaN, returns same value. This code is an adaptation of
     * {@link java.text.ChoiceFormat#nextDouble}.
     */
    private static float next(final float f, final boolean positive)
    {
        final int SIGN             = 0x80000000;
        final int POSITIVEINFINITY = 0x7F800000;

        // Filter out NaN's
        if (Float.isNaN(f)) {
            return f;
        }

        // Zero's are also a special case
        if (f == 0f) {
            final float smallestPositiveFloat = Float.intBitsToFloat(1);
            return (positive) ? smallestPositiveFloat : -smallestPositiveFloat;
        }

        // If entering here, d is a nonzero value.
        // Hold all bits in a int for later use.
        final int bits = Float.floatToIntBits(f);

        // Strip off the sign bit.
        int magnitude = bits & ~SIGN;

        // If next float away from zero, increase magnitude.
        // Else decrease magnitude
        if ((bits > 0) == positive) {
            if (magnitude != POSITIVEINFINITY) {
                magnitude++;
            }
        } else {
            magnitude--;
        }

        // Restore sign bit and return.
        final int signbit = bits & SIGN;
        return Float.intBitsToFloat(magnitude | signbit);
    }

    /**
     * Finds the least float greater than <var>f</var>.
     * If <code>NaN</code>, returns same value.
     */
    public static float next(final float f) {
        return next(f, true);
    }

    /**
     * Finds the greatest float less than <var>f</var>.
     * If <code>NaN</code>, returns same value.
     */
    public static float previous(final float f) {
        return next(f, false);
    }

    /**
     * Finds the least double greater than <var>f</var>.
     * If <code>NaN</code>, returns same value.
     *
     * @see java.text.ChoiceFormat#nextDouble
     */
    public static double next(final double f) {
        return ChoiceFormat.nextDouble(f);
    }

    /**
     * Finds the greatest double less than <var>f</var>.
     * If <code>NaN</code>, returns same value.
     *
     * @see java.text.ChoiceFormat#previousDouble
     */
    public static double previous(final double f) {
        return ChoiceFormat.previousDouble(f);
    }
}
