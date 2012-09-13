package jdamasceno.stemmer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.implementations.OrengoStemmer;

public class Stemmer extends OrengoStemmer {

	private Set<String> stopWords = new HashSet<String>();
	
	public Stemmer() throws PTStemmerException {
		super();
	}

	public String[] getPhraseStems(String phrase) {
		String[] res = phrase.split(" ");
		List<String> ret = new LinkedList<String>();
		
		for (int i = 0; i < res.length; i++) {
			if (!stopWords.contains(res[i].toLowerCase()) && res[i].length() > 1) {
				ret.add(getWordStem(res[i]));
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	public Set<String> getStopWords() {
		return stopWords;
	}

	public void setStopWords(Set<String> stopWords) {
		this.stopWords = stopWords;
	}
}