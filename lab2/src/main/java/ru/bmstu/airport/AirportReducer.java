package ru.bmstu.airport;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class AirportReducer extends Reducer<AirportWritableComparable, Text, Text, Text> {
  @Override
  protected void reduce(AirportWritableComparable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    Iterator<Text> iter = values.iterator();

    Text airportName = new Text("Airport name: " + iter.next().toString());

    if (iter.hasNext()) {
      int i = 0;
      float curr;
      float mi = 0.0f;
      float ma = 0.0f;
      float sum = 0.0f;

      while (iter.hasNext()) {
        curr = Float.parseFloat(iter.next().toString());

        if (curr < mi || i==0)
          mi = curr;
        else if (curr > ma)
          ma = curr;

        sum += curr;
        i++;
      }

      sum /= i;
      Text stat = new Text("Stats delays time: Min: " + Float.toString(mi) + " , Max: " + Float.toString(ma) + " , Average: " + Float.toString(sum));
      context.write(airportName, stat);
    }
  }
}
