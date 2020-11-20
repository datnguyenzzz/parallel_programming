package ru.bmstu.airport;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AirportWritableComparable implements WritableComparable {
  private int airportID;
  private int flag;

  public AirportWritableComparable() {}

  public AirportWritableComparable(int airportID, int flag) {
    this.airportID = airportID;
    this.flag = flag;
  }

  public int getAirportID() {
    return airportID;
  }

  @Override
  public int compareTo(Object o) {
    AirportWritableComparable other = (AirportWritableComparable)o;

    if (this.airportID > other.airportID)
      return 1;
    else if (this.airportID < other.airportID)
      return -1;

    if (this.flag > other.flag)
      return 1;
    else if (this.flag < other.flag)
      return -1;

    return 0;
  }

  public int compareToAirportID(Object o) {
    AirportWritableComparable other = (AirportWritableComparable)o;

    if (this.airportID > other.airportID)
      return 1;
    else if (this.airportID < other.airportID)
      return -1;

    return 0;
  }


  @Override
  public void write(DataOutput dataOutput) throws IOException {
    dataOutput.writeInt(airportID);
    dataOutput.writeInt(flag);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    airportID = dataInput.readInt();
    flag = dataInput.readInt();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;
    AirportWritableComparable other = (AirportWritableComparable)o;

    if (airportID != other.airportID) return false;
    return flag == other.flag;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return "AirportWritableComparable{" +
           "airportID=" + airportID +
           ", flag=" + flag +
           "}";
  }

}
