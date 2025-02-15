package buglocator.property;

public class Property {
	

	private static Property p = null;
	
	public static void createInstance(String projectName, String bugFilePath, String sourceCodeDir, String workDir, float alpha, String outputFile, String invalidBugFile) {
		if (p == null)
			p = new Property(projectName, bugFilePath, sourceCodeDir, workDir, alpha, outputFile, invalidBugFile);
	}
	
	public static Property getInstance() {
		return p;
	}
	
	private Property(String projectName, String bugFilePath, String sourceCodeDir, String workDir, float alpha, String outputFile, String invalidBugFile) {
		this.ProjectName = projectName;
		this.BugFilePath = bugFilePath;
		this.SourceCodeDir = sourceCodeDir;
		this.WorkDir = workDir;
		this.Alpha = alpha;
		this.OutputFile = outputFile;

		this.CommitHash = null;
		this.InvalidBugFile = invalidBugFile;
		this.Separator = System.getProperty("file.separator");
		this.LineSeparator = System.getProperty("line.separator");
	}
	

	public final String Separator;
	public final String LineSeparator;
	
	public final String ProjectName;
	public final String BugFilePath;
	public final String SourceCodeDir;
	public String WorkDir;
	public final float Alpha;
	public final String OutputFile;

	public String CommitHash;
	public final String InvalidBugFile;
	
	public int FileCount;
	public int WordCount;
	public int BugReportCount;
	public int BugTermCount;
	
}
