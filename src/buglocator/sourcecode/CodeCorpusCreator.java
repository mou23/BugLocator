package buglocator.sourcecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.TreeSet;
import buglocator.property.Property;
import buglocator.sourcecode.ast.Corpus;
import buglocator.sourcecode.ast.FileDetector;
import buglocator.sourcecode.ast.FileParser;
import buglocator.utils.Stem;
import buglocator.utils.Stopword;

public class CodeCorpusCreator
{
	private final String workDir = Property.getInstance().WorkDir;
	private final String codePath = Property.getInstance().SourceCodeDir;
	private final String pathSeparator = Property.getInstance().Separator;
	private final String lineSeparator = Property.getInstance().LineSeparator;
	private final String projectName = Property.getInstance().ProjectName;
	
	public CodeCorpusCreator() throws IOException, ParseException
	{}
	
	/**
	 * ���� �Լ�.
	 * @throws Exception
	 */

	public void create() throws Exception {
		int count = 0;
		TreeSet<String> nameSet = new TreeSet<String>();

		// File listing
		FileDetector detector = new FileDetector("java"); // java file filter
		File[] files = detector.detect(codePath);

		// Preparing output file
		FileWriter writeCorpus = new FileWriter(workDir + pathSeparator + "CodeCorpus.txt");
		FileWriter writer = new FileWriter(workDir + pathSeparator + "ClassName.txt");

		// Make corpus for each file
		for (File file : files) {
			Corpus corpus = this.create(file);	// Corpus creation
			if (corpus == null) continue;

			// File filtering
			String fullPath = file.getPath(); // Get the full path
			fullPath = fullPath.trim().replace("\\", "/");
//			System.out.println(fullPath+ " "+codePath);
			// Remove the prefix, e.g., "bundles/org.eclipse.e4.ui.workbench/src/"
			if (fullPath.startsWith(codePath)) {
				fullPath = fullPath.substring(codePath.length()); // Remove the prefix
			}

			// Normalize path separators
//			fullPath = fullPath.trim().replace("\\", "/");

			if (nameSet.contains(fullPath)) continue;

			// Write file
			writer.write(count + "\t" + fullPath + this.lineSeparator);
			writeCorpus.write(fullPath + "\t" + corpus.getContent() + this.lineSeparator);
			writer.flush();
			writeCorpus.flush();

			// Update filter
			nameSet.add(fullPath);
			count++;
		}

		Property.getInstance().FileCount = count;
		writeCorpus.close();
		writer.close();
	}



	/**
	 * �� ���Ͽ� ���ؼ� corpus�� ����
	 * @param file
	 * @return
	 */
	public Corpus create(File file) {
		FileParser parser = new FileParser(file);
		
		//������ ��Ű�� ���� ���
		String fileName = parser.getPackageName();
		if (fileName.trim().equals("")) {
			fileName = file.getName();
		} else {
			fileName = fileName + "." + file.getName();
		}
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		
		//content�� �и��Ͽ� stemming, removing stopwords ����
		String[] content = parser.getContent();
		StringBuffer contentBuf = new StringBuffer();
		for (String word : content) {	//camel case �и� tokenize�� content����.
			String stemWord = Stem.stem(word.toLowerCase());
			if ((!Stopword.isKeyword(word)) && (!Stopword.isEnglishStopword(word)))
			{
				contentBuf.append(stemWord);
				contentBuf.append(" ");
			}
		}
		String sourceCodeContent = contentBuf.toString();
		
		//Ŭ������, �޼ҵ�� ���ؼ� ������ corpus�� �ѹ� �� ����.
		String[] classNameAndMethodName = parser.getClassNameAndMethodName();
		StringBuffer nameBuf = new StringBuffer();
		
		for (String word: classNameAndMethodName) {			
			String stemWord = Stem.stem(word.toLowerCase());
			nameBuf.append(stemWord);
			nameBuf.append(" ");
		}
		String names = nameBuf.toString();
		
		//corpus��ü ����.
		Corpus corpus = new Corpus();
		corpus.setJavaFilePath(file.getAbsolutePath());
		corpus.setJavaFileFullClassName(fileName);
		corpus.setContent(sourceCodeContent + " " + names);	//content���� �� corpus�� ����.
		return corpus;
	}
}
