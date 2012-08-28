package jdamasceno.mahout.clustering;

import java.io.*;
import java.util.regex.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.*;

public class MyAnalyzer extends Analyzer {
	private final Pattern alphabets = Pattern.compile("[a-z]+");

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new StandardTokenizer(Version.LUCENE_36, reader);
		result = new StandardFilter(result);
		result = new LowerCaseFilter(result);
		result = new StopFilter(true, result, StandardAnalyzer.STOP_WORDS_SET);
		TermAttribute termAtt = (TermAttribute) result
				.addAttribute(TermAttribute.class);
		StringBuilder buf = new StringBuilder();
		try {
			while (result.incrementToken()) {
				if (termAtt.termLength() < 3)
					continue;
				String word = new String(termAtt.termBuffer(), 0,
						termAtt.termLength());
				Matcher m = alphabets.matcher(word);
				if (m.matches()) {
					buf.append(word).append(" ");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new WhitespaceTokenizer(Version.LUCENE_36, new StringReader(
				buf.toString()));
	}
}