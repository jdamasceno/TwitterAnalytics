package jdamasceno.hadoop;

import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

public class Snippet {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path pathDictionary = new Path("/usr/local/Cellar/mahout/bin/tmp5/dictionary.file-0");
		
		SequenceFile.Reader read = new SequenceFile.Reader(fs, pathDictionary, conf);
		IntWritable dicKey = new IntWritable();
		Text text = new Text();
		HashMap dictionaryMap = new HashMap();
		while (read.next(text, dicKey)) {
			dictionaryMap.put(Integer.parseInt(dicKey.toString()), text.toString());
		}
		read.close();
		
		Path pathTfidf = new Path("/usr/local/Cellar/mahout/bin/tmp5/tfidf-vectors/part-r-00000");
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, pathTfidf, conf);
		Text key = new Text();
		VectorWritable value = new VectorWritable();
		while (reader.next(key, value)) {
			NamedVector namedVector = (NamedVector)value.get();
			RandomAccessSparseVector vect = (RandomAccessSparseVector)namedVector.getDelegate();
			
			for( Element  e : vect ){
				System.out.println("Token: "+dictionaryMap.get(e.index())+", TF-IDF weight: " + e.get()) ;
			}
		}
		reader.close();
		
	}
}

