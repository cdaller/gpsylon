/**
 *
 */
package org.dinopolis.gpstool.plugin.googlemap.util;

/* translated from php to java by Dave Sant
 quakr.net
 neenar.com
 david.sant@yahoo.co.uk
 http://www.quakr.net/java/gmaps/Tile.java
 */
public class Tile
{
  // The point (x,y) for this tile
  private SimplePoint point;
  // The coord (lat,lon) for this tile
  //private Point co;
  private SimplePoint coordinates;
  // Zoom level for this tile
  private int zoomLevel;
  // ...Constants...
  //private double PI = 3.1415926535;
  private int tileSize = 256;
  private float pixelsPerLonDegree[] = new float[18];
  private float pixelsPerLonRadian[] = new float[18];
  private int numTiles[] = new int[18];
  private SimplePoint bitmapOrigo[] = new SimplePoint[18];
  private int tileWidth = 256;
  private double twoPi;
  private double Wa;

  // Fill in the constants array
  private void fillInConstants()
  {
    this.twoPi = 2 * Math.PI;
    this.Wa = Math.PI / 180;
    for (int zoomLevel = 17; zoomLevel >= 0; --zoomLevel)
    {
      float f1 = (this.tileWidth / 360f);
      this.pixelsPerLonDegree[zoomLevel] = f1;
      this.pixelsPerLonRadian[zoomLevel] = (float) (this.tileWidth / this.twoPi);
      int e = this.tileWidth / 2;
      this.bitmapOrigo[zoomLevel] = new SimplePoint(e, e);
      this.numTiles[zoomLevel] = (this.tileWidth / 256);
      this.tileWidth *= 2;
    }
  }

  public Tile(double latitude, double longitude, int zoomLevel)
  {
    this.fillInConstants();
    this.zoomLevel = zoomLevel;
    this.point = this.getTileCoordinate(latitude, longitude, zoomLevel);
    this.coordinates = this.getLatLong(latitude, longitude, zoomLevel);
  }

  public SimplePoint getTileCoord()
  {
    return this.point;
  }

  public SimplePoint getTileLatLong()
  {
    return this.coordinates;
  }

  private String getKeyholeString()
  {
    String s = "";
    int myX = this.point.getX();
    int myY = this.point.getY();

    for (int i = 17; i > this.zoomLevel; i--)
    {
      double rx = (myX % 2);
      myX = (int) Math.floor(myX / 2);
      double ry = (myY % 2);
      myY = (int) Math.floor(myY / 2);
      s = this.getKeyholeDirection(rx, ry) + s;
    }
    return 't' + s;
  }

  private String getKeyholeDirection(double x, double y)
  {
    if (x == 1)
    {
      if (y == 1)
      {
        return "s";
      } else if (y == 0)
      {
        return "r";
      }
    } else if (x == 0)
    {
      if (y == 1)
      {
        return "t";
      } else if (y == 0)
      {
        return "q";
      }
    }

    return "";
  }

  private SimplePoint getBitmapCoordinate(double a, double b, int zoom)
  {
    SimplePoint resultPoint = new SimplePoint(0, 0);

    double pixelPerLonDeg = this.pixelsPerLonDegree[zoom];
    SimplePoint sp = (SimplePoint) this.bitmapOrigo[zoom];

    int newX = (int) Math.floor(sp.getX() + (b * pixelPerLonDeg));

    resultPoint.setX(newX);
    double e = Math.sin(a * this.Wa);

    if (e > 0.9999)
    {
      e = 0.9999;
    }

    if (e < -0.9999)
    {
      e = -0.9999;
    }

    double pixelPerLonRad = this.pixelsPerLonRadian[zoom];

    int newY = (int) Math.floor(sp.getY() + 0.5 * Math.log((1 + e) / (1 - e)) * -1 * (pixelPerLonRad));
    resultPoint.setY(newY);
    return resultPoint;
  }

  private SimplePoint getTileCoordinate(double a, double b, int c)
  {
    SimplePoint d = this.getBitmapCoordinate(a, b, c);
    d.setX((int) (Math.floor(d.getX() / this.tileSize)));
    d.setY((int) (Math.floor(d.getY() / this.tileSize)));

    return d;
  }

  private SimplePoint getLatLong(double a, double b, int c)
  {
    SimplePoint d = new SimplePoint(0, 0);
    SimplePoint e = this.getBitmapCoordinate(a, b, c);
    a = e.getX();
    b = e.getY();

    SimplePoint sp = (SimplePoint) this.bitmapOrigo[c];
    double thispplon = this.pixelsPerLonDegree[c];
    double thispplonrad = this.pixelsPerLonRadian[c];
    int newX = (int) ((a - sp.getX()) / thispplon);
    d.setX(newX);

    double e2 = ((b - sp.getY()) / (-1 * thispplonrad));
    d.setY((int) ((2 * Math.atan(Math.exp(e2)) - Math.PI / 2) / this.Wa));
    return d;
  }
}
