package buglocator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import buglocator.bug.Bug;
import buglocator.property.Property;

public class BugReportProcessor {
	
	public Date makeTime(String time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(time);
		} catch (Exception e) {
			long ltime = Long.parseLong(time);
			date = new Date(ltime);
		}
		return date;
	}
	
	public ArrayList<ArrayList<Bug>> getBugReports() {
		ArrayList<Bug> bugs = parseXML();
		int totalSize = bugs.size();
        int splitIndex = (int) Math.ceil(totalSize * 0.6);
        
        List<Bug> firstPart = bugs.subList(0, splitIndex);
        List<Bug> secondPart = bugs.subList(splitIndex, totalSize);

        ArrayList<Bug> training_bugs = new ArrayList<>(firstPart);
        ArrayList<Bug> test_bugs = new ArrayList<>(secondPart);
        System.out.println("size of test bugs: " + test_bugs.size());
        
        ArrayList<ArrayList<Bug>> arrayListOfBugs = new ArrayList<>();
        
        for (Bug bug : test_bugs) {
            ArrayList<Bug> newList = new ArrayList<>(training_bugs); 
            newList.add(bug);
            arrayListOfBugs.add(newList);
        }
        
		return arrayListOfBugs;
	}
	
	private ArrayList<Bug> parseXML() {
		ArrayList<Bug> list = new ArrayList<Bug>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(Property.getInstance().BugFilePath);
			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();

			// Get the list of tables in the database
			NodeList tables = root.getElementsByTagName("table");
			System.out.println("number of reports " + tables.getLength());
			for (int i = 0; i < tables.getLength(); i++) {
				Node tableNode = tables.item(i);
				if (tableNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				NodeList columns = tableNode.getChildNodes();
				Bug bug = new Bug();
				String files = "";

				for (int j = 0; j < columns.getLength(); j++) {
					Node columnNode = columns.item(j);
					if (columnNode.getNodeType() != Node.ELEMENT_NODE)
						continue;

					Element columnElement = (Element) columnNode;
					String columnName = columnElement.getAttribute("name");

					switch (columnName) {
						case "bug_id":
							bug.setBugId(columnElement.getTextContent());
							break;
						case "summary":
							bug.setBugSummary(columnElement.getTextContent());
							break;
						case "description":
							bug.setBugDescription(columnElement.getTextContent());
							break;
						case "report_timestamp":
							bug.setOpenDate(Long.parseLong(columnElement.getTextContent()));
							break;
						case "commit_timestamp":
							bug.setFixDate(Long.parseLong(columnElement.getTextContent()));
							break;
						case "files":
							files = columnElement.getTextContent();
							break;
						case "commit":
							bug.setBugCommit(columnElement.getTextContent());
							Property.getInstance().CommitHash = columnElement.getTextContent();
							break;
					}
				}

				if (!files.isEmpty()) {
					String[] fixedFiles = files.split("\\.java");
					for (String file : fixedFiles) {
						if(file.length()>0)
							bug.addFixedFile(file.trim()+".java");
					}
					System.out.println(bug.getBugId());
					System.out.println(bug.getFixedFileSet());
				}
				else {
					System.out.println("no fixed file found!");
				}

				list.add(bug);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		list.sort((bug1, bug2) -> Long.compare(bug1.getFixDate(), bug2.getFixDate()));
		
		return list;
	}
}
