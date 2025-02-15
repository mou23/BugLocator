package buglocator;

import java.util.ArrayList;

import buglocator.bug.Bug;
import buglocator.bug.BugCorpusCreator;
import buglocator.bug.BugSimilarity;
import buglocator.bug.BugVector;
import buglocator.bug.SimilarityDistribution;
import buglocator.evaluation.Evaluation;
import buglocator.property.Property;
import buglocator.sourcecode.CodeCorpusCreator;
import buglocator.sourcecode.CodeVectorCreator;
import buglocator.sourcecode.Indexer;
import buglocator.sourcecode.LenScore;
import buglocator.sourcecode.Similarity;
import buglocator.utils.CommitCheckout;


public class Core
{
	public void process()
	{
		ArrayList<ArrayList<Bug>> arrayListOfBugs = new BugReportProcessor().getBugReports();
		
		for (int i = 0; i < arrayListOfBugs.size(); i++) {
			ArrayList<Bug> bugs = arrayListOfBugs.get(i);
			Property.getInstance().BugReportCount = bugs.size();
			if(i==0)
				Property.getInstance().WorkDir = Property.getInstance().WorkDir + String.valueOf(i);
			else
				Property.getInstance().WorkDir = Property.getInstance().WorkDir.replaceAll("\\d+",String.valueOf(i));
			try
			{
				System.out.println("create bug corpus...");
				new BugCorpusCreator().create(bugs);
			}
			catch (Exception localException1) {
				localException1.printStackTrace();
				return;
			}
			
			try
			{
				System.out.println("create bug vector...");
				new BugVector().create();
			}
			catch (Exception localException2) {
				localException2.printStackTrace();
				return;
			}
//	
			try
			{
				System.out.println("compute bug similarity...");
				new BugSimilarity().computeSimilarity();
			}
			catch (Exception localException3) {
				localException3.printStackTrace();
				return;
			}
//	
			try
			{
				Property.getInstance().CommitHash = bugs.get(bugs.size()-1).getBugCommit();
				System.out.println("checkout to one commit before fix...");
				new CommitCheckout().create();
			}
			catch (Exception z) {
				z.printStackTrace();
				return;
			}
//	
			try
			{
				System.out.println("create code corpus...");
				new CodeCorpusCreator().create();
			}
			catch (Exception localException4) {
				localException4.printStackTrace();
				return;
			}
//			
			try
			{
				System.out.println("compute SimiScore...");
				new SimilarityDistribution().distribute();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
//			
			try
			{
				System.out.println("create index...");
				new Indexer().index();
			}
			catch (Exception localException5) {}
	
			try
			{
				System.out.println("create vector...");
				new CodeVectorCreator().create();
			}
			catch (Exception localException6) {}
	
			try
			{
				System.out.println("compute VSMScore...");
				new Similarity().compute();
			}
			catch (Exception localException7) {}
	
			try
			{
				System.out.println("compute LengthScore...");
				new LenScore().computeLenScore();
			}
			catch (Exception localException8) {}
			
			try
			{
				System.out.println("evaluate...");
				new Evaluation().evaluate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}
		System.out.println("finished");
	}
}
