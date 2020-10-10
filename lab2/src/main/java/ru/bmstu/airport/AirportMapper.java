package ru.bmstu.airport;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, AirportWritableComparable, Text> {
  private static int AIRPORT_ID = 0;
  private static int AIRPORT_NAME = 1;

  @Override
  protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
    String[] cols = value.toString().split("\",");
    if (key.get()>0) {
      Text airportName = new Text(cols[AIRPORT_NAME].replaceAll("\"",""));
      int airportID = Integer.parseInt(cols[AIRPORT_ID].replaceAll("\"",""));
      AirportWritableComparable newKey = new AirportWritableComparable(airportID,0);
      context.write(newKey, airportName);
    }
  }
}
