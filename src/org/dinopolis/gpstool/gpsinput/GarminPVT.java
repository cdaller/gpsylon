package org.dinopolis.gpstool.gpsinput;

public class GarminPVT
{
  protected float alt_;
  protected float epe_;
  protected float eph_;
  protected float epv_;
  protected int fix_;
  protected double tow_;
  protected double lat_;
  protected double lon_;
  protected float east_;
  protected float north_;
  protected float up_;
  protected float msl_height_;
  protected int leap_seconds_;
  protected int wn_days_;

  public GarminPVT()
  {
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminPVT[");
    buffer.append("alt=").append(alt_).append(", ");
    buffer.append("epe=").append(epe_).append(", ");
    buffer.append("eph=").append(eph_).append(", ");
    buffer.append("epv=").append(epv_).append(", ");
    buffer.append("fix=").append(fix_).append(", ");
    buffer.append("tow=").append(tow_).append(", ");
    buffer.append("lat=").append(lat_).append(", ");
    buffer.append("lon=").append(lon_).append(", ");
    buffer.append("east=").append(east_).append(", ");
    buffer.append("north=").append(north_).append(", ");
    buffer.append("up=").append(up_).append(", ");
    buffer.append("msl_height=").append(msl_height_).append(", ");
    buffer.append("leap_seconds=").append(leap_seconds_).append(", ");
    buffer.append("wn_days=").append(wn_days_);
    buffer.append("]");
    return(buffer.toString());
  }
}
