package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import Entities.LDABase;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/ALTData/UserProbDistribution.txt"));
		LDABase lbase = new LDABase("C:/ALTData/linkTexts.txt");
		lbase.startEpochs();
		for(int i=0; i<lbase.sample.numDocuments(); i++){
            for(int j=0;j< lbase.sample.numTopics();j++){
                bw.write(lbase.sample.documentTopicProb(i, j) + "\t");
            }
            bw.write("\n");
        }
		bw.flush();
		bw.close();
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
