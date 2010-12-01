package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Random;

import com.aliasi.cluster.LatentDirichletAllocation;

import Entities.LDABase;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		getUserProbDistribution();
		getBayesEstimate();
	}
	
	private static void getBayesEstimate() throws IOException, ClassNotFoundException{
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/ALTData/ExternalUserProbDistribution4.txt"));
		LDABase lbase = new LDABase("C:/ALTData/Train.txt");
		BufferedReader br = new BufferedReader(new FileReader("C:/ALTData/TestIndians.txt"));
		FileInputStream fr = new FileInputStream("C:/ALTData/ldaobject4.obj");
		ObjectInputStream os = new ObjectInputStream(fr);
		lbase.lda = (LatentDirichletAllocation) os.readObject();
//		BufferedReader br2 = new BufferedReader(new FileReader("C:/ALTData/UserProbDistribution.txt"));
		String line;
		String[] splitS;
//		double[][] seedProbDistrbution = new double[lbase.sample.numDocuments()][lbase.sample.numTopics()];
//		int i=0;
//		while((line = br2.readLine())!=null){
//			splitS = line.split("\t");
//			for(int j=0;j< splitS.length;j++){
//				seedProbDistrbution[i][j] = Double.parseDouble(splitS[j]);
//			}
//			i++;
//		}
		double[] bayesEstimate;
		int[] docTokens;
		while((line = br.readLine())!=null){
			splitS = line.split("\t", 2);
			docTokens = lbase.getDocumentTokens(splitS[1]);
			if(docTokens.length==0){ 
				bw.write("#\n");
                continue;
            }
            bayesEstimate = lbase.lda.bayesTopicEstimate(docTokens, 100, lbase.burninEpochs, lbase.sampleLag, new Random());
            for(int j=0;j<bayesEstimate.length;j++){
                if(bayesEstimate[j]==0){
                    bayesEstimate[j]=0.0000000001;
                }
            }
			for(int j =0; j<bayesEstimate.length;j++){
				bw.write(bayesEstimate[j] + "\t");
			}
			bw.write("\n");
		}
		bw.flush();
		bw.close();
	}
	
	private static void getUserProbDistribution() throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/ALTData/UserProbDistribution4.txt"));
		LDABase lbase = new LDABase("C:/ALTData/Train.txt");
		lbase.startEpochs();
		for(int i=0; i<lbase.sample.numDocuments(); i++){
            for(int j=0;j< lbase.sample.numTopics();j++){
                bw.write(lbase.sample.documentTopicProb(i, j) + "\t");
            }
            bw.write("\n");
        }
		bw.flush();
		bw.close();
		FileOutputStream fs = new FileOutputStream("C:/ALTData/ldaobject4.obj");
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(lbase.lda);
		os.close();
	}
	private static void getLinks() throws IOException{
		BufferedReader brTweets = new BufferedReader(new FileReader("C:/ALTData/Indians/Indians.txt"));
		BufferedReader brUsers = new BufferedReader(new FileReader("C:/ALTData/res.txt"));
		BufferedWriter bwTweets = new BufferedWriter(new FileWriter("C:/ALTData/linkTexts.txt"));
		BufferedWriter bwLinks = new BufferedWriter(new FileWriter("C:/ALTData/links.txt"));
		Hashtable<String, String> tweetsTable = new Hashtable<String, String>();
		String line;
		
		while((line=brTweets.readLine()) != null){
			String[] splitS = line.split("\t",2);
			tweetsTable.put(splitS[0], splitS[1]);
		}
		
		while((line=brUsers.readLine()) != null){
			String[] splitS = line.split("\t");
			String follower = splitS[0];
			for(int i=1; i< splitS.length;i++){
				bwLinks.write(follower + "\t" + splitS[i] + "\n");
			}
			bwTweets.write(tweetsTable.get(follower)+ "\n");
		}
		
		bwLinks.flush();
		bwTweets.flush();
		System.out.println("Yay. Data constructed for LDA Model.");
	}

}
