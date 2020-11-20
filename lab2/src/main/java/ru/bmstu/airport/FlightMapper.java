package ru.bmstu.airport;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlightMapper extends Mapper<LongWritable, Text, AirportWritableComparable, Text> {
  private static int DEST_AIRPORT_ID = 14;
  private static int DELAY_TIME = 18;

  private static float getFloat(String col) {
    if (!col.equals(""))
      return Float.parseFloat(col);
    else
      return 0.0f;
  }

  @Override
  protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
    String[] cols = value.toString().split(",");
    if (key.get()>0) {
      float delayTime = getFloat(cols[DELAY_TIME]);
      if (delayTime > 0.0f) {
        int destAirportID = Integer.parseInt(cols[DEST_AIRPORT_ID]);
        AirportWritableComparable newKey = new AirportWritableComparable(destAirportID,1);
        context.write(newKey, new Text(cols[DELAY_TIME]));
      }
    }
  }
}
