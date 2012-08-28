package jdamasceno.mahout;

import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.text.ChunkedWriter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class SequenceFilesFromMongoDB extends AbstractJob {

	private static final int PAGE_SIZE = 5000;

	public int run(String[] args) throws Exception {

		int chunkSize = 64;

		Mongo mongo = new Mongo();
		DB tweetsDB = mongo.getDB("tweets");
		DBCollection tweetsCollection = tweetsDB.getCollection("tweets");

		List<DBObject> tweets = tweetsCollection.find().limit(PAGE_SIZE)
				.toArray();

		int page = 0;

		String dirUsuario = System.getProperty("user.home")
				+ "/dados/tweeter-analytics/sequence-files-mongodb";
		ChunkedWriter writer = new ChunkedWriter(getConf(), chunkSize,
				new Path(dirUsuario));

		while (!tweets.isEmpty()) {

			for (DBObject object : tweets) {
				writer.write(object.get("_id").toString(), object
						.get("message").toString());
			}

			tweets = tweetsCollection.find().skip(PAGE_SIZE * ++page)
					.limit(PAGE_SIZE).toArray();
		}

		writer.close();
		mongo.close();
		System.out.println("SequenceFile generated");
		return 0;
	}

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new SequenceFilesFromMongoDB(), args);
	}
}
