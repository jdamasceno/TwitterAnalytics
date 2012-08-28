package jdamasceno.mahout.clustering;

import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.lucene.analysis.*;
import org.apache.mahout.clustering.*;
import org.apache.mahout.clustering.canopy.*;
import org.apache.mahout.clustering.classify.*;
import org.apache.mahout.clustering.kmeans.*;
import org.apache.mahout.common.*;
import org.apache.mahout.common.distance.*;
import org.apache.mahout.vectorizer.*;
import org.apache.mahout.vectorizer.tfidf.*;

public class AgrupadorDeTweets {
	public static void main(String args[]) throws Exception {
		int minSupport = 2;
		int minDf = 5;
		int maxDFPercent = 95;
		int maxNGramSize = 2;
		int minLLRValue = 50;
		int reduceTasks = 1;
		int chunkSize = 200;
		int norm = 2;
		boolean sequentialAccessOutput = true;
		String inputDir = "tmp";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		String outputDir = "clusters";
		HadoopUtil.delete(conf, new Path(outputDir));
		Path tokenizedPath = new Path(outputDir,
				DocumentProcessor.TOKENIZED_DOCUMENT_OUTPUT_FOLDER);
		MyAnalyzer analyzer = new MyAnalyzer();
		DocumentProcessor.tokenizeDocuments(new Path(inputDir), analyzer
				.getClass().asSubclass(Analyzer.class), tokenizedPath, conf);
		DictionaryVectorizer.createTermFrequencyVectors(tokenizedPath,
				new Path(outputDir), "dicionario", conf, minSupport,
				maxNGramSize, minLLRValue, 2, true, reduceTasks, chunkSize,
				sequentialAccessOutput, false);
		Pair<Long[], List<Path>> pair = TFIDFConverter.calculateDF(new Path(
				inputDir), new Path(outputDir), conf, 5);
		TFIDFConverter.processTfIdf(new Path(outputDir,
				DictionaryVectorizer.DOCUMENT_VECTOR_OUTPUT_FOLDER), new Path(
				outputDir), conf, pair, chunkSize, minDf, maxDFPercent, false,
				true, sequentialAccessOutput, reduceTasks);
		Path vectorsFolder = new Path(outputDir, "tfidf-vectors");
		Path canopyCentroids = new Path(outputDir, "canopy-centroids");
		Path clusterOutput = new Path(outputDir, "clusters");
		CanopyDriver.run(vectorsFolder, canopyCentroids,
				new EuclideanDistanceMeasure(), 250, 120, false, 10, false);
		KMeansDriver.run(conf, vectorsFolder, new Path(canopyCentroids,
				"clusters-0"), clusterOutput, new TanimotoDistanceMeasure(),
				0.01, 20, true, 10, false);
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(
				clusterOutput + Cluster.CLUSTERED_POINTS_DIR + "/part-00000"),
				conf);
		IntWritable key = new IntWritable();
		WeightedVectorWritable value = new WeightedVectorWritable();
		while (reader.next(key, value)) {
			System.out.println(key.toString() + " belongs to cluster "
					+ value.toString());
		}
		reader.close();
	}
}