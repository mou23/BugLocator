package buglocator;

import java.io.File;

import buglocator.property.Property;

public class BugLocator {
	public static void main(String[] args) {
		 args = new String[12];
		 args[0]="-b";
		 args[1]="D:/FL/dataset/aspectj-updated-data.xml";
		 args[2]="-s";
		 args[3]="D:/FL/dataset/aspectj";
		 args[4]="-a";
		 args[5]="0.3";
		 args[6]="-w";
		 args[7]="D:/FL/dataset/temp";
		 args[8]="-n";
		 args[9]="aspectj_run_modified";
		 args[10]="-i";
		 args[11]="non-bugs-id-aspectj.txt";

		try {
//			if (args.length == 0)
//				throw null;

			boolean isLegal = parseArgs(args);
			if (!isLegal)
				throw null;
			
			Core core = new Core();
			core.process();

		} catch (Exception ex) {
			showHelp();
		}
	}

	private static void showHelp() {
		String usage = "--------------\nUsage:java -jar BugLocator [-options] \r\n" + "where options must include:\r\n"
				+ "-b\tindicates the bug information file\r\n" + "-s\tindicates the source code directory\r\n"
				+ "-a\tindicates the alpha value for combining vsmScore and simiScore\r\n"
				+ "-w\tindicates the working directory\r\n"
				+ "-n\tindicates the working name (this uses for result file name.)\r\n"
				+ "-i\tindicates the file name containing invalid bug ids\r\n"
				+ "  \tOn the below of the {working directory}\r\n"
				+ "  \tThis program will make temp directory : BugLocator_{working name}\\\r\n"
				+ "  \t                and final result file : BugLocator_{working name}_output.txt";
		System.out.println(usage);
	}

	/**
	 * �Է� �Ķ���͸� �Ľ��Ͽ� Property ��ü ����
	 * @param args
	 * @return
	 */
	private static boolean parseArgs(String[] args) {
		String bugFilePath = "";
		String sourceCodeDir = "";
		String alphaStr = "";
		float alpha = 0.0F;
		String outputFile = "";
		String workingPath = "";
		String workingName = "";
		String invalidBugFile = "";

		// parsing the parameters
		int i = 0;
		while (i < args.length - 1) {
			if (args[i].equals("-b")) {
				i++;
				bugFilePath = args[i];
				bugFilePath = bugFilePath.replace("\\", "/");
				bugFilePath = bugFilePath.replace("//", "/");
			} else if (args[i].equals("-s")) {
				i++;
				sourceCodeDir = args[i];
				sourceCodeDir = sourceCodeDir.replace("\\", "/");
				sourceCodeDir = sourceCodeDir.replace("//", "/");
			} else if (args[i].equals("-a")) {
				i++;
				alphaStr = args[i];
			} else if (args[i].equals("-w")) {
				i++;
				workingPath = args[i];
				workingPath = workingPath.replace("\\", "/");
				workingPath = workingPath.replace("//", "/");
			} else if (args[i].equals("-n")) {
				i++;
				workingName = args[i];
			} else if (args[i].equals("-i")) {
				i++;
				invalidBugFile = args[i];
			}
			i++;
		}

		// check invalid input
		boolean isLegal = true;
		if ((bugFilePath.equals("")) || (bugFilePath == null)) {
			isLegal = false;
			System.out.println("you must indicate the bug information file");
		}
		if ((sourceCodeDir.equals("")) || (sourceCodeDir == null)) {
			isLegal = false;
			System.out.println("you must indicate the source code directory");
		}
		if ((!alphaStr.equals("")) && (alphaStr != null)) {
			try {
				alpha = Float.parseFloat(alphaStr);
			} catch (Exception ex) {
				isLegal = false;
				System.out.println("-a argument is ilegal,it must be a float value");
			}
		}
		if ((workingPath.equals("")) || (workingPath == null)) {
			isLegal = false;
			System.out.println("you must indicate the working directory (temp directory)");
		}

		if ((workingName.equals("")) || (workingName == null)) {
			isLegal = false;
			System.out.println("you must indicate the working name (for result file or directory)");
		}
		
		//File System check (minimum 2GB) // Is working in windows ...?
		File file = new File(System.getProperty("user.dir"));
		if (file.getFreeSpace() / 1024L / 1024L / 1024L < 2L) {
			System.out.println(
					"Not enough free disk space, please ensure your current disk space are bigger than 2G.");
			isLegal = false;
		}
		if ((invalidBugFile.equals("")) || (invalidBugFile == null)) {
			isLegal = false;
			System.out.println("you must indicate the file name containing invalid bug ids");
		}
		
		//Check this state.
		if (!isLegal) return isLegal;

		// prepare working directory and create properties. 
		// make workingPath
		if (workingPath.endsWith("/") == false) workingPath += "/";
		workingPath += "BugLocator_" + workingName + "/";
		
		//make outputFile path.
		File dir = new File(workingPath);
		if (!dir.exists())
			dir.mkdirs();
		outputFile = workingPath.substring(0, workingPath.length() - 1) + "_output.txt";

		Property.createInstance(workingName.toUpperCase(), bugFilePath, sourceCodeDir, workingPath, alpha, outputFile, invalidBugFile);
		// dir.getAbsolutePath() ==> workingPath

		return isLegal;
	}
}
