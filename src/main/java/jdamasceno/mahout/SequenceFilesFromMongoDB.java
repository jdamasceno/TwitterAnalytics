package jdamasceno.mahout;

import java.util.List;

import jdamasceno.stemmer.Stemmer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.text.ChunkedWriter;

import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.support.PTStemmerUtilities;

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
		Stemmer stemmer = createStemmer();

		while (!tweets.isEmpty()) {

			for (DBObject object : tweets) {
				String tweet = object
						.get("message").toString();

				String stemmedTweet = stem(tweet, stemmer);

				writer.write(object.get("_id").toString(), stemmedTweet);
			}

			tweets = tweetsCollection.find().skip(PAGE_SIZE * ++page)
					.limit(PAGE_SIZE).toArray();
		}

		writer.close();
		mongo.close();
		System.out.println("SequenceFile generated ");
		return 0;
	}

	private Stemmer createStemmer() throws PTStemmerException {
		Stemmer stemmer = new Stemmer();
		stemmer.enableCaching(100000);
		stemmer.setStopWords(PTStemmerUtilities.fileToSet("stemmer/stopwords.txt"));
		stemmer.ignore(PTStemmerUtilities.fileToSet("stemmer/namedEntities.txt"));
		return stemmer;
	}

	private String stem(String tweet, Stemmer stemmer) throws PTStemmerException {
		
		String[] stems = stemmer.getPhraseStems(tweet);
		StringBuilder sb = new StringBuilder();
		
		for (String stem : stems) {
			String wordStemmed = PTStemmerUtilities.removeDiacritics(stem);
			sb.append(wordStemmed).append(" ");
		}
		
		System.out.println(sb.toString());
		
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new SequenceFilesFromMongoDB(), args);
	}
}
