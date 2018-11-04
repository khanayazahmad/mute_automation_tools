package com.btpb.tsu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;

public class SMDHandle {
	
	public ArrayList<String> getOldSmdNameList(String smdPath){
		
		File folder = new File(smdPath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> listOfOldSmdNames = new ArrayList<String>();
		
		for (File currentFile : listOfFiles) {

			listOfOldSmdNames.add(currentFile.getName());
		}
		
		return listOfOldSmdNames;
	}

	public ArrayList<String> generateNewSmdNameListWithoutReference(ArrayList<String> listOfOldSmdNames){
		
		ArrayList<String> listOfNewSmdNames = new ArrayList<String>();
		String previousCmdName = "";
		int count = 0;
		
		for (String name : listOfOldSmdNames) {

			try {
				Integer.parseInt(name.split("_")[name.split("_").length - 1].split("\\.")[0]);
			} catch (Exception e) {
				System.out.println(e);
				listOfNewSmdNames.add(name);
				continue;
			}

			String currentCmdName = (name.split("_")[1].contains("0x"))?name.split("_")[0]:name.split("_")[0] + "_" + name.split("_")[1];
			if (currentCmdName.equals(previousCmdName))
				count++;
			else {
				previousCmdName = currentCmdName;
				count = 1;
			}
			String[] nameArr = name.split("_");
			nameArr[nameArr.length - 1] = "" + count;
			while (nameArr[nameArr.length - 1].length() != 4)
				nameArr[nameArr.length - 1] = "0" + nameArr[nameArr.length - 1];
			nameArr[nameArr.length - 1] += ".smd";
			name = nameArr[0];
			for (int i = 1; i < nameArr.length; i++) {
				name += "_" + nameArr[i];
			}
			listOfNewSmdNames.add(name);
		}
		
		return listOfNewSmdNames;
	}
	
	public ArrayList<String> generateNewSmdNameListWithReference(String sourceSmdPath, ArrayList<String> smdCheckPaths){
		
		File sourceDir=new File(sourceSmdPath);
		ArrayList<File> listOfSmd=new ArrayList<File>(Arrays.asList(sourceDir.listFiles()));
		
		ArrayList<String> listOfNewSmdNames = new ArrayList<String>();

		HashMap<String,Integer> cmdMap = new HashMap<String,Integer>();
 		
		String cmdName = "";
		int max=1;
		int flag=0;
		
		for(File f:listOfSmd){
			cmdName=(f.getName().split("_")[1].contains("0x")||f.getName().split("_")[0].equals("CONFIG")||f.getName().split("_")[0].equals("DELAY"))?f.getName().split("_")[0]:f.getName().split("_")[0]+"_"+f.getName().split("_")[1];
			flag=0;
			for(int i=0;i<smdCheckPaths.size();i++){
				File checkDir=new File(smdCheckPaths.get(i));
				ArrayList<File> listOfOldSmd=new ArrayList<File>(Arrays.asList(checkDir.listFiles()));
				if(listOfOldSmd.contains(f)){
					System.out.println("Duplicate Found. File deleted : "+f.getName());
					f.delete();
					break;
				}
				else if(cmdMap.containsKey(cmdName)){
					String name=f.getName();
					String[] nameArr = name.split("_");
					nameArr[nameArr.length - 1] = "" + (cmdMap.get(cmdName)+1);
					cmdMap.put(cmdName, (cmdMap.get(cmdName)+1));
					while (nameArr[nameArr.length - 1].length() != 4)
						nameArr[nameArr.length - 1] = "0" + nameArr[nameArr.length - 1];
					nameArr[nameArr.length - 1] += ".smd";
					name = nameArr[0];
					for (int j = 1; j < nameArr.length; j++) {
						name += "_" + nameArr[j];
					}
					listOfNewSmdNames.add(name);
					break;
				}
				else{
					for(File cf:listOfOldSmd){
						if(cmdName.equals((cf.getName().split("_")[1].contains("0x")||cf.getName().split("_")[0].equals("CONFIG")||cf.getName().split("_")[0].equals("DELAY"))?cf.getName().split("_")[0]:cf.getName().split("_")[0]+"_"+cf.getName().split("_")[1])){
							int number = Integer.parseInt(cf.getName().split("_")[cf.getName().split("_").length - 1].split("\\.")[0]);
							max=(max>number)?max:number;
						
						}
						
					}
					flag=1;
				}
			}
			
			if(flag==1){
				cmdMap.put(cmdName, max);
				max=1;
				System.out.println(cmdName+"-->"+cmdMap.get(cmdName));
				String name=f.getName();
				String[] nameArr = name.split("_");
				nameArr[nameArr.length - 1] = "" + (cmdMap.get(cmdName)+1);
				cmdMap.put(cmdName, (cmdMap.get(cmdName)+1));
				while (nameArr[nameArr.length - 1].length() != 4)
					nameArr[nameArr.length - 1] = "0" + nameArr[nameArr.length - 1];
				nameArr[nameArr.length - 1] += ".smd";
				name = nameArr[0];
				for (int j = 1; j < nameArr.length; j++) {
					name += "_" + nameArr[j];
				}
				listOfNewSmdNames.add(name);
			}
		}
		
		return listOfNewSmdNames;
		
	}
	
	public void SnfUpdater(ArrayList<String> listOfNewSmdNames, ArrayList<String> listOfOldSmdNames, String snfFolderPath) {

		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		int lineNumber = 1;
		File snfFolder = new File(snfFolderPath);
		File[] listOfSnfFiles = snfFolder.listFiles();
		
		for (File currentSnf : listOfSnfFiles) {
			try {

				fr = new FileReader(currentSnf);
				br = new BufferedReader(fr);
				
			} catch (FileNotFoundException e1) {
				System.out.println("Error: File not found: "+currentSnf.getName());
				continue;
			}

			String line, newFileContent = "";
			try {
				
				lineNumber = 1;
				while ((line = br.readLine()) != null) {
					
					lineNumber++;
					for (int i = 0; i < listOfOldSmdNames.size(); i++) {
						if (line.trim().equals(listOfOldSmdNames.get(i))) {
							line = listOfNewSmdNames.get(i);
							break;
						}

					}
					newFileContent += line + "\n";
				}
				
				fw = new FileWriter(currentSnf);
				fw.write(newFileContent);
				fw.close();
				
			} catch (Exception e) {
				
				System.out.println("Error: Unsuccessful change in file : " + currentSnf.getName() + " at line number " + lineNumber);
				e.printStackTrace();
				continue;
			}

		}

	}
	
	public void SmdNameChanger(ArrayList<String> listOfNewSmdNames, ArrayList<String> listOfOldSmdNames, String smdFolderPath){
		
		while (!listOfOldSmdNames.isEmpty()) {
			for (int i = 0; i < listOfOldSmdNames.size(); i++) {
				
				File oldfile = new File(smdFolderPath + "\\" + listOfOldSmdNames.get(i));
				File newfile = new File(smdFolderPath + "\\" + listOfNewSmdNames.get(i));

				if (oldfile.renameTo(newfile)) {

					listOfOldSmdNames.remove(i);
					listOfNewSmdNames.remove(i);
				} else {
					
					System.out.println("Failure: Rename failed for : " + listOfOldSmdNames.get(i)+" -> "+listOfNewSmdNames.get(i));
					continue;
				}
			}

		}
	}
	
	public void smdExtractor(String sourcePathForSmd, String destinationPathForSmd, String snfPath){
		File snfDirectory=new File(snfPath);
		ArrayList<File> listOfSnfFiles=new ArrayList<File>(Arrays.asList(snfDirectory.listFiles()));
		
		File sourceDirectoryForSmd=new File(sourcePathForSmd);
		ArrayList<File> listOfSourceSmdFiles=new ArrayList<File>(Arrays.asList(sourceDirectoryForSmd.listFiles()));
		
		for(File snf:listOfSnfFiles){
			String line;
			try {
				BufferedReader br=new BufferedReader(new FileReader(snf));
				while((line=br.readLine())!=null)
				{
					if(line.trim().startsWith("%"))
						continue;

					if(line.contains(".smd")){
						if(listOfSourceSmdFiles.contains(new File(sourcePathForSmd+"\\"+line.trim()))){
							System.out.println("Found");
							File from=new File(sourcePathForSmd+"\\"+line.trim());
							File to=new File(destinationPathForSmd+"\\"+line.trim());
							String l,content="";
							BufferedReader fr=new BufferedReader(new FileReader(from));
							while((l=fr.readLine())!=null)
								content+=l+"\n";
							FileWriter fw=new FileWriter(to);
							fw.write(content);
							fr.close();
							fw.close();
						}
						else{
							System.out.println("Error: SMD	"+line+"	in SNF	"+snf.getName()+" not found in Path " 
																										+sourcePathForSmd);
						}
					}
				}
				br.close();
			} catch (IOException e) {		
				System.out.println("Failure: " + snf.getName() + " could not be opened.");
				continue;
			}
			
			
		}

	}
	
	public String generateCode(String file){
		String line, code="", prevState = "";
		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(file)));
			code = "";
			HashMap<String, Boolean> states=new HashMap<String, Boolean>();		
			while((line=br.readLine())!=null){
				if(line.trim().startsWith("%")||line.trim()==null)
					continue;
				if(line.trim().endsWith(":")){
					prevState = line.trim().split(":")[0];
					states.put(prevState, false);
					continue;	
				}
			}
			for(String S:states.keySet()){
				System.out.println(S+"-->"+states.get(S));
			}
			
			br.close();
			br=new BufferedReader(new FileReader(new File(file)));
			while((line=br.readLine())!=null){

				if(line.trim().startsWith("%")||line.trim()==null)
					continue;
				if(line.trim().endsWith(":")){
					//System.out.println("file:	"+file+"	line:	"+line.trim().split(":")[0]);
					
					if(states.get(line.trim().split(":")[0])){
						while((line=br.readLine())!=null&&!(line.trim().endsWith(":")));

					}
					continue;
				}
				if(line.contains("START_TC")||line.contains("END_TC"))
					continue;
				if(line.contains("LOG")){
					br.readLine();
					continue;
				}
					
				if(line.contains("HCI_READ_LOCAL_VERSION_INFO")){
					while((line=br.readLine())!=null&&!(line.trim().endsWith(":")));
				}
				else{

					for(int j=0;j<states.keySet().size();j++){
						if(line.split(" "+(String) states.keySet().toArray()[j]).length>1)
							line=line.split(" "+(String) states.keySet().toArray()[j])[0]
									+line.split(" "+(String) states.keySet().toArray()[j])[1];
						else if(line.split(","+(String) states.keySet().toArray()[j]).length>1)
							line=line.split(","+(String) states.keySet().toArray()[j])[0]
									+","+line.split(","+(String) states.keySet().toArray()[j])[1];
	
	
					}
		line = line.trim().replaceAll(" ", "");
					line = line.trim().replaceAll("\n", "");
					line = line.trim().replaceAll("\t", "");
					code+=line;
					System.out.println(line);
					
				}
			}
			br.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File: "+file+"	Code:	"+code);
		return code;
	}
	
//	int compareAndResolveByteDump(File smd, String pathSourceMLB, String pathDestinationMLB){
//	
//		String line;
//		boolean equal = true;
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(smd));
//			while((line=br.readLine())!=null){
//				
//				if(line.contains("SEND")){
//				String cmdLine="";
//				String cmd = line.split(",")[1].trim()+":";
//				String var = line.split(",")[2].trim();
//				BufferedReader cmdSourceLib = new BufferedReader(new FileReader(new File(
//						pathSourceMLB+"\\LE_cmd_msg.mlb")));
//				BufferedReader cmdDestLib = new BufferedReader(new FileReader(new File(
//						pathSourceMLB+"\\LE_cmd_msg.mlb")));
//				while((cmdLine=cmdLib.readLine()) != null&&!cmdLine.trim().equals(cmd));
//				if(cmdLine == null){
//					System.out.println("Command "+cmd+ "not found in smd "+file);
//					
//				}else{
//					while((cmdLine=cmdLib.readLine()) != null&&!cmdLine.trim().startsWith(var));
//					if(cmdLine == null){
//						System.out.println("Variant "+var+" for "+cmd+ "not found in smd "+file);
//						
//					}else{
//						line += cmdLine.split(",")[2];
//						if(cmdLine.endsWith("_")){
//							while((cmdLine=cmdLib.readLine())!=null&&cmdLine.endsWith("_"))
//								line += cmdLine.trim();
//							if(cmdLine!=null)
//								line += cmdLine.trim();
//							
//						}
//
//					}
//
//				}
//			}
//			else if(line.contains("WAIT")||line.contains("IGNORE")
//					||line.contains("IGNORE_CANCEL")||line.contains("COMPARE")){
//				String cmdLine="";
//				String cmd = line.split(",")[1].trim()+":";
//				String var = line.split(",")[2].trim();
//				BufferedReader cmdLib = new BufferedReader(new FileReader(new File(
//						file.split("Testcases")[0]+"Messages//LE_evt_msg.mlb")));
//				while((cmdLine=cmdLib.readLine()) != null&&!cmdLine.trim().equals(cmd));
//				if(cmdLine == null){
//					System.out.println("Command "+cmd+ "not found in smd "+file);
//					
//				}else{
//					while((cmdLine=cmdLib.readLine()) != null&&!cmdLine.trim().startsWith(var));
//					if(cmdLine == null){
//						System.out.println("Variant "+var+" for "+cmd+ "not found in smd "+file);
//						
//					}else{
//						line += cmdLine.split(",")[2];
//						if(cmdLine.endsWith("_")){
//							while((cmdLine=cmdLib.readLine())!=null&&cmdLine.endsWith("_"))
//								line += cmdLine.trim();
//							if(cmdLine!=null)
//								line += cmdLine.trim();
//							
//						}
//
//					}
//
//				}
//			}
//	
//			}
//			
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		
//		
//		
//		return 0;
//	}
	
	public ArrayList<String> duplicateListGenerator(String toPath, String fromPath, ArrayList<String> oldNames){
		
		File toDir = new File(toPath);
		ArrayList<File> checkList=new ArrayList<File>(Arrays.asList(toDir.listFiles()));
		ArrayList<String> newNames=new ArrayList<String>();
		String prevName="";
		for(int i=0;i<oldNames.size();i++){
			prevName="";
			for(int j=0;j<checkList.size();j++){
				
				if((oldNames.get(i).substring(0, oldNames.get(i).lastIndexOf("_")).equals(checkList.get(j).getName().substring(0, checkList.get(j).getName().lastIndexOf("_"))))
						&&(generateCode(fromPath+"\\"+oldNames.get(i)).equals(generateCode(toPath+"\\"+checkList.get(j).getName())))){
					
					prevName = checkList.get(j).getName();
					checkList.remove(j--);
					
				}
				
			}
			newNames.add(prevName);
		}
		
		for(String s:newNames)
			System.out.println(s);
		return newNames;
		
	}
	

}
