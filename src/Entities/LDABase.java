package Entities;

import com.aliasi.cluster.LatentDirichletAllocation;
import com.aliasi.tokenizer.*;
import com.aliasi.symbol.*;
import java.io.*;
import java.util.*;
import Tests.*;

public class LDABase implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -530485173822375971L;
	int minTokenCount = 3;
	short numTopics = 50;
	double topicPrior = 0.02;
	double wordPrior = 0.001;
	public int burninEpochs = 100;
	public int sampleLag = 1;
	public int numSamples = 2000;
	long randomSeed = 6474835;
	public CharSequence[] articleTexts;
	public SymbolTable symbolTable;
	public LatentDirichletAllocation lda;
	public int[][] docTokens;
	public LatentDirichletAllocation.GibbsSample sample ;
	private static final String[] STOPWORD_LIST = setStopWordList(UnitTests.DATA_PATH + "stopwords.txt");
	private static final Set<String> STOPWORD_SET =  new HashSet<String>(Arrays.asList(STOPWORD_LIST));
	
	public LDABase(String corpusFile)
	{
		this.articleTexts = readCorpus(corpusFile);
		symbolTable = new MapSymbolTable();
		docTokens = LatentDirichletAllocation
		.tokenizeDocuments(articleTexts,WORMBASE_TOKENIZER_FACTORY,symbolTable,minTokenCount);
	}
	
	public void startEpochs() throws IOException{

		System.out.println("Number of unique words above count threshold=" + symbolTable.numSymbols());

		int numTokens = 0;
		for (int[] tokens : docTokens)
			numTokens += tokens.length;
		System.out.println("Tokenized.  #Tokens After Pruning=" + numTokens);

		LdaReportingHandler handler = new LdaReportingHandler(symbolTable);
		sample = LatentDirichletAllocation.gibbsSampler(docTokens,

				numTopics,
				topicPrior,
				wordPrior,

				burninEpochs,
				sampleLag,
				numSamples,

				new Random(randomSeed),
				handler);
		lda = sample.lda();
		FileOutputStream fs = new FileOutputStream("C:/ALTData/ldaobject.obj");
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(lda);
		os.close();
		int maxWordsPerTopic = 20;
		int maxTopicsPerDoc = 50;
		boolean reportTokens = true;
//		handler.fullReport(sample,maxWordsPerTopic,maxTopicsPerDoc,reportTokens);
		handler.fullMachineReport(sample, maxWordsPerTopic, numTopics, reportTokens);
	}

	public int[] getDocumentTokens(String docString){
//		docString = docString.substring(docString.indexOf("~"));
//		docString = docString.replaceAll("\\~", " ");
//		docString = docString.replaceAll("null", " ");
		docString = docString.replaceAll("\\p{Punct}+", " ").trim();
		int[] docTokens = LatentDirichletAllocation.tokenizeDocument(docString, 
				WORMBASE_TOKENIZER_FACTORY, symbolTable);
		return docTokens;
	}
	private CharSequence[] readCorpus(String corpusFile) {
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(corpusFile));
			String line="";
			List<CharSequence> articleTextList = new ArrayList<CharSequence>(15000);

			while((line = input.readLine()) != null){
//				line = line.substring(line.indexOf("~"));
//				line = line.replaceAll("\\~", " ");
//				line = line.replaceAll("null", " ");
				line = line.toLowerCase();
				line = line.replaceAll("\\p{Punct}+", " ");
				articleTextList.add(line);
			}
			return articleTextList.<CharSequence>toArray(new CharSequence[articleTextList.size()]);
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	static boolean validStem(String stem) {
		if (stem.length() < 2) return false;
		for (int i = 0; i < stem.length(); ++i) {
			char c = stem.charAt(i);
			for (int k = 0; k < VOWELS.length; ++k)
				if (c == VOWELS[k])
					return true;
		}
		return false;
	}

	static String[] setStopWordList(String filename){
		try{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = "";
			String stopString = "";
			while((line=br.readLine())!=null){
				stopString += line.trim()+"|";
			}
			String[] temp = stopString.split("\\|");
			return temp;
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	static final TokenizerFactory wormbaseTokenizerFactory() {
		TokenizerFactory factory = BASE_TOKENIZER_FACTORY;
		factory = new NonAlphaStopTokenizerFactory(factory);
		factory = new LowerCaseTokenizerFactory(factory);
		factory = new EnglishStopTokenizerFactory(factory);
		factory = new StopTokenizerFactory(factory,STOPWORD_SET);
		factory = new StemTokenizerFactory(factory);
		factory = new StopTokenizerFactory(factory,STOPWORD_SET);
		return factory;
	}

//	static final TokenizerFactory BASE_TOKENIZER_FACTORY
//	= new RegExTokenizerFactory("[\\x2Da-zA-Z0-9]+"); // letter or digit or hyphen (\x2D)
	static final TokenizerFactory BASE_TOKENIZER_FACTORY
	= new RegExTokenizerFactory("[a-zA-Z0-9]+"); // letter or digit or hyphen (\x2D)

	static final char[] VOWELS
	= new char[] { 'a', 'e', 'i', 'o', 'u', 'y' };




	static final TokenizerFactory WORMBASE_TOKENIZER_FACTORY
	= wormbaseTokenizerFactory();


	// removes tokens that have no letters
	static class NonAlphaStopTokenizerFactory extends ModifyTokenTokenizerFactory {
		static final long serialVersionUID = -3401639068551227864L;
		public NonAlphaStopTokenizerFactory(TokenizerFactory factory) {
			super(factory);
		}
		public String modifyToken(String token) {
			return stop(token) ? null : token;
		}
		public boolean stop(String token) {
			if (token.length() < 2) return true;
			for (int i = 0; i < token.length(); ++i)
				if (Character.isLetter(token.charAt(i)))
					return false;
			return true;
		}
	}

	static class StemTokenizerFactory extends ModifyTokenTokenizerFactory {
		static final long serialVersionUID = -6045422132691926248L;
		public StemTokenizerFactory(TokenizerFactory factory) {
			super(factory);
		}
		static final String[] SUFFIXES = new String[] {
			"ss", "ies", "sses", "s", "ing", "ed" // s must be last as its weaker
		};
		public String modifyToken(String token) {
			for (String suffix : SUFFIXES) {
				if (token.endsWith(suffix)) {
					String stem = token.substring(0,token.length()-suffix.length());
					return validStem(stem) ? stem : token;
				}
			}
			return token;
		}
	}
}
