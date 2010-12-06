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
//		getUserProbDistribution();
//		getBayesEstimate();
//		countAllUsers();
		getTopicDistribution();
	}
	private static void getTopicDistribution() throws IOException {
		topicDistribution("C:/ALTData/Run4/ExternalUserProbDistribution4.txt", "C:/ALTData/TopicDistributions/Run4TestTopicDist.txt");
		topicDistribution("C:/ALTData/Run4/UserProbDistribution4.txt", "C:/ALTData/TopicDistributions/Run4TrainTopicDist.txt");
		topicDistribution("C:/ALTData/Port/UserProbDistributionPort.txt", "C:/ALTData/TopicDistributions/RunTrainPortTopicDist.txt");
		topicDistribution("C:/ALTData/Minn/UserProbDistributionMinn.txt", "C:/ALTData/TopicDistributions/RunTrainMinnTopicDist.txt");
		topicDistribution("C:/ALTData/Aus/UserProbDistributionAus.txt", "C:/ALTData/TopicDistributions/RunTrainAusTopicDist.txt");
		topicDistribution("C:/ALTData/RunXaa/UserProbDistributionXaa.txt", "C:/ALTData/TopicDistributions/RunTrainXaaTopicDist.txt");
		topicDistribution("C:/ALTData/XaaIndia/XaaProbForIndianModel.txt", "C:/ALTData/TopicDistributions/XaaIndianTopicDist.txt");
		topicDistribution("C:/ALTData/XaaPortland/XaaProbForPortlandModel.txt", "C:/ALTData/TopicDistributions/XaaPortTopicDist.txt");
		topicDistribution("C:/ALTData/XaaMinnesota/XaaProbForMinnesotaModel.txt", "C:/ALTData/TopicDistributions/XaaMinnTopicDist.txt");
	}
	private static void topicDistribution(String readFile, String writeFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(readFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
		String line;
		String[] splitS;
		int numTopics =50;
		double[][] userProbDist = new double[150000][numTopics];
		double[] topicNumbers = new double[numTopics];
		int i=0;
		while((line = br.readLine())!=null){
			if ((line.contains("#"))){
				for(int j=0; j<numTopics; ++j){
					userProbDist[i][j] = 0;
				}
				continue;
			}
			splitS = line.split("\t");
			for(int j=0; j<splitS.length; ++j){
				userProbDist[i][j] = Double.parseDouble(splitS[j]);
			}
			++i;
		}
		
		for(int j=0; j<i; ++j){
			for(int k=0; k<numTopics;  ++k){
				if(userProbDist[j][k]>=0.1){
					++topicNumbers[k];
				}
			}
		}
		
		for(int j=0; j<numTopics; ++j){
			bw.write(topicNumbers[j]+"\n");
		}
		System.out.println(i);
		bw.flush();
		bw.close();
		
	}
	private static void countAllUsers() throws IOException, ClassNotFoundException{
		countUsers("C:/ALTData/Run4/ExternalUserProbDistribution4.txt", "C:/ALTData/UserCounts/Run4TestCount.txt");
		countUsers("C:/ALTData/Run4/UserProbDistribution4.txt", "C:/ALTData/UserCounts/Run4TrainCount.txt");
		countUsers("C:/ALTData/Port/UserProbDistributionPort.txt", "C:/ALTData/UserCounts/RunTrainPortCount.txt");
		countUsers("C:/ALTData/Minn/UserProbDistributionMinn.txt", "C:/ALTData/UserCounts/RunTrainMinnCount.txt");
		countUsers("C:/ALTData/Aus/UserProbDistributionAus.txt", "C:/ALTData/UserCounts/RunTrainAusCount.txt");
		countUsers("C:/ALTData/RunXaa/UserProbDistributionXaa.txt", "C:/ALTData/UserCounts/RunTrainXaaCount.txt");
		countUsers("C:/ALTData/XaaIndia/XaaProbForIndianModel.txt", "C:/ALTData/UserCounts/XaaIndianCount.txt");
		countUsers("C:/ALTData/XaaPortland/XaaProbForPortlandModel.txt", "C:/ALTData/UserCounts/XaaPortCount.txt");
		countUsers("C:/ALTData/XaaMinnesota/XaaProbForMinnesotaModel.txt", "C:/ALTData/UserCounts/XaaMinnCount.txt");
	}
	private static void countUsers(String readFile, String writeFile)throws IOException, ClassNotFoundException{
		BufferedReader br = new BufferedReader(new FileReader(readFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
		String line;
		String[] splitS;
		int numTopics =50;
		double[][] userProbDist = new double[150000][numTopics];
		double[] userNumbers = new double[numTopics+1];
		int i=0;
		while((line = br.readLine())!=null){
			if ((line.contains("#"))){
				for(int j=0; j<numTopics; ++j){
					userProbDist[i][j] = 0;
				}
				continue;
			}
			splitS = line.split("\t");
			for(int j=0; j<splitS.length; ++j){
				userProbDist[i][j] = Double.parseDouble(splitS[j]);
			}
			++i;
		}
		
		int countTopics = 0;
		for(int j=0; j<i; ++j){
			countTopics = 0;
			for(int k=0; k<numTopics;  ++k){
				if(userProbDist[j][k]>=0.1){
					++countTopics;
				}
			}
			++userNumbers[countTopics];
		}
		
		for(int j=0; j<numTopics+1; ++j){
			bw.write(userNumbers[j]+"\n");
		}
		System.out.println(i);
		bw.flush();
		bw.close();
	}
	private static double getMostFrequentValue(double[] ds) {
		int[] count = new int[ds.length];
		for(int i=0;i<ds.length-1;++i){
			for(int j=0;j<ds.length-1;++j){
				if(i!=j && ds[i]==ds[j]){
					++count[i];
				}
			}
		}
		int maxIndex=0;
		for(int i=0;i<ds.length-1;++i){
			if(count[i] > maxIndex){
				maxIndex = i;
			}
		}
		return ds[maxIndex];
	}
	private static void getBayesEstimate() throws IOException, ClassNotFoundException{
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/ALTData/XaaMinnesota/XaaProbForMinnesotaModel.txt"));
		LDABase lbase = new LDABase("C:/ALTData/Minn.txt");
		BufferedReader br = new BufferedReader(new FileReader("C:/ALTData/xaa"));
		FileInputStream fr = new FileInputStream("C:/ALTData/Minn/ldaobjectMinn.obj");
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
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:/ALTData/UserProbDistributionXaa.txt"));
		LDABase lbase = new LDABase("C:/ALTData/xaa");
		lbase.startEpochs();
		for(int i=0; i<lbase.sample.numDocuments(); i++){
            for(int j=0;j< lbase.sample.numTopics();j++){
                bw.write(lbase.sample.documentTopicProb(i, j) + "\t");
            }
            bw.write("\n");
        }
		bw.flush();
		bw.close();
		FileOutputStream fs = new FileOutputStream("C:/ALTData/ldaobjectXaa.obj");
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
