package jdamasceno.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.math.NamedVector;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class Snippet {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path pathDictionary = new Path(System.getProperty("user.home") + "/dados/tweeter-analytics/kmeans-cluster/clusteredPoints/part-m-00000");
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, pathDictionary, conf);

		Mongo mongo = new Mongo();
		DB tweetsDB = mongo.getDB("tweets");
		DBCollection tweetsCollection = tweetsDB.getCollection("tweets");
		
		
		IntWritable key = new IntWritable();
		WeightedVectorWritable value = new WeightedVectorWritable();
		while(reader.next(key,value)) {
		  String tweetId = ((NamedVector) value.getVector()).getName();
		  String clusterId = key.toString();
		  
		  DBObject searchById = new BasicDBObject("_id", new ObjectId(tweetId));
		  DBObject found = tweetsCollection.findOne(searchById);
		  
		  System.out.println(found.get("message") +" ----> " + clusterId );
		}
		
		mongo.close();
	}
}

