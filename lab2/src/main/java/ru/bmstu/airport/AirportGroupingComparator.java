package ru.bmstu.airport;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class AirportGroupingComparator extends WritableComparator {
  protected AirportGroupingComparator() {
    super(AirportWritableComparable.class, true);
  }

  @Override
  public int compare(WritableComparable a, WritableComparable b) {
    AirportWritableComparable t1 = (AirportWritableComparable) a;
    AirportWritableComparable t2 = (AirportWritableComparable) b;
    return t1.compareToAirportID(t2);
  }
}
