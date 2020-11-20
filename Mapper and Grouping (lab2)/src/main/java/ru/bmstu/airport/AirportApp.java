package ru.bmstu.airport;
//file system
import org.apache.hadoop.fs.Path;
//io
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
//io format job
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * Hello world!
 *
 */
public class AirportApp
{
    public static void main( String[] args ) throws Exception {
      if (args.length!=3) {
        System.err.println("Syntax: <input flight> <input airport> <output>");
        System.exit(-1);
      }

      Job job = Job.getInstance();
      job.setJarByClass(AirportApp.class);
      job.setJobName("Flight app");
      MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, FlightMapper.class);
      MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, AirportMapper.class);

      FileOutputFormat.setOutputPath(job, new Path(args[2]));

      job.setReducerClass(AirportReducer.class);
      job.setPartitionerClass(AirportPartitioner.class);
      job.setGroupingComparatorClass(AirportGroupingComparator.class);

      job.setMapOutputKeyClass(AirportWritableComparable.class);
      job.setMapOutputValueClass(Text.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(Text.class);

      job.setNumReduceTasks(2);
      System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
