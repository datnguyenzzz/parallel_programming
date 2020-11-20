package ru.bmstu.SparkApp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

/**
 * Hello world!
 *
 */
public class App
{

    private static final int ORIGIN_AIRPORT_ID = 11;
    private static final int DEST_AIRPORT_ID = 14;
    private static final int DELAY_TIME = 18;
    private static final int CANCELLED = 19;

    private static final float IS_CANCELLED = 1.0f;

    private static final String DELI_COMMA = ",";
    private static final String DELI_QUOTE = "\"";
    private static final String DELI_NAN = "";

    private static final String PATH_TO_FLIGHT_FILE = "input/664600583_T_ONTIME_sample.csv";
    private static final String PATH_TO_AIRPORT_FILE = "input/L_AIRPORT_ID.csv";
    private static final String PATH_TO_OUTPUT_FILE = "hdfs://localhost:9000/user/datng/output_lab3";

    public static void main( String[] args )
    {
        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> flightFile = sc.textFile(PATH_TO_FLIGHT_FILE);
        JavaRDD<String> airportFile = sc.textFile(PATH_TO_AIRPORT_FILE);

        JavaPairRDD<Integer, String> airportData = airportFile
                .filter(s -> !s.contains("Code"))
                .mapToPair(s -> {
                    s = s.replace(DELI_QUOTE,DELI_NAN); //return row in db
                    int indexComma = s.indexOf(DELI_COMMA);
                    return new Tuple2<>(
                          Integer.valueOf(s.substring(0, indexComma)), //code
                          s.substring(indexComma+1, s.length()) //Description
                    );
                });

        JavaPairRDD<Tuple2<Integer,Integer>, FlightSerializable> flightData = flightFile
                .filter(s -> !s.contains("YEAR"))
                .mapToPair(s -> {
                      String[] cols = s.split(DELI_COMMA);
                      int originAirportId = Integer.parseInt(cols[ORIGIN_AIRPORT_ID]);
                      int destAirportId = Integer.parseInt(cols[DEST_AIRPORT_ID]);
                      float delayTime = (cols[DELAY_TIME].equals(DELI_NAN)) ? 0.0f : Float.parseFloat(cols[DELAY_TIME]);
                      float cancelled = Float.parseFloat(cols[CANCELLED]);

                      return new Tuple2<> (
                          new Tuple2<> (
                              originAirportId, destAirportId
                          ),
                          new FlightSerializable(originAirportId,destAirportId,delayTime,cancelled)
                      );
                });

        //StatCount -> listing delay time
        JavaPairRDD<Tuple2<Integer,Integer>,String> flightResult = flightData //flight <-> flightSerializable
                .combineByKey(flight -> new StatCount(1, flight.get_cancelled() == IS_CANCELLED ? 1 : 0, flight.get_delay_time() > 0.0f ? 1 : 0, flight.get_delay_time()),
                              (stat,flight) -> StatCount.add_value(stat, flight.get_cancelled() == IS_CANCELLED, flight.get_delay_time() > 0.0f, flight.get_delay_time()),
                              StatCount::add)
                .mapToPair(s -> new Tuple2<>(s._1() , StatCount.output(s._2())));

        final Broadcast<Map<Integer,String>> airportBroadcaster = sc.broadcast(airportData.collectAsMap());

        JavaRDD<String> result = flightResult
                .map(a -> {
                    Map<Integer,String> O_D_airportId = airportBroadcaster.value();
                    Tuple2<Integer,Integer> key = a._1(); //stat name
                    String value = a._2(); //stat flight

                    String originAirportName = O_D_airportId.get(key._1());
                    String destAirportName = O_D_airportId.get(key._2());

                    return "From " + originAirportName + " To " + destAirportName + "\t" + value;
                });

        result.saveAsTextFile(PATH_TO_OUTPUT_FILE);
    }
}
