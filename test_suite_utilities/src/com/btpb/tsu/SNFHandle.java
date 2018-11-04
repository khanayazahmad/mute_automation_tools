package com.btpb.tsu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SNFHandle {
	
	public ArrayList<String> getOldSmdNameList(String smdPath){
		
		File folder = new File(smdPath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> listOfOldSnfNames = new ArrayList<String>();
		
		for (File currentFile : listOfFiles) {

			listOfOldSnfNames.add(currentFile.getName());
		}
		
		return listOfOldSnfNames;
	}
	
	public void snfExtractorFromSsn(String sourcePathForSnf, String destinationPathForSnf, String listPath){
		File ssnDirectory=new File(listPath);
		ArrayList<File> listOfSsnFiles=new ArrayList<File>(Arrays.asList(ssnDirectory.listFiles()));
		
		File sourceDirectoryForSnf=new File(sourcePathForSnf);
		ArrayList<File> listOfSourceSnfFiles=new ArrayList<File>(Arrays.asList(sourceDirectoryForSnf.listFiles()));
		
		for(File ssn:listOfSsnFiles){
			String line;
			try {
				BufferedReader br=new BufferedReader(new FileReader(ssn));
				while((line=br.readLine())!=null)
				{
					if(line.trim().startsWith("%"))
						continue;

					if(line.contains(".snf")){
						if(listOfSourceSnfFiles.contains(new File(sourcePathForSnf+"\\"+line.trim()))){
							System.out.println("Found");
							File from=new File(sourcePathForSnf+"\\"+line.trim());
							File to=new File(destinationPathForSnf+"\\"+line.trim());
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
							System.out.println("Error: SNF	"+line+"	in SSN	"+ssn.getName()+" not found in Path " 
																										+sourcePathForSnf);
						}
					}
				}
				br.close();
			} catch (IOException e) {		
				System.out.println("Failure: " + ssn.getName() + " could not be opened.");
				continue;
			}
	
		}

	}
	
	public void snfExtractorFromSnf(String sourcePathForSnf, String destinationPathForSnf){
		
		File snfDirectory=new File(destinationPathForSnf);
		ArrayList<File> listOfSnfFiles=new ArrayList<File>(Arrays.asList(snfDirectory.listFiles()));

		
		File sourceDirectoryForSnf=new File(sourcePathForSnf);
		ArrayList<File> listOfSourceSnfFiles=new ArrayList<File>(Arrays.asList(sourceDirectoryForSnf.listFiles()));
		
		for(int i=0;i<listOfSnfFiles.size();i++){
			File snf = listOfSnfFiles.get(i);
			String line;
			try {
				BufferedReader br=new BufferedReader(new FileReader(snf));
				while((line=br.readLine())!=null)
				{
					if(line.trim().startsWith("%"))
						continue;
					if(line.contains(".snf")){
						if(listOfSourceSnfFiles.contains(new File(sourcePathForSnf+"\\"+line.trim()))){
							System.out.println("Found");
							File from=new File(sourcePathForSnf+"\\"+line.trim());
							File to=new File(destinationPathForSnf+"\\"+line.trim());
							String l,content="";
							BufferedReader fr=new BufferedReader(new FileReader(from));
							while((l=fr.readLine())!=null)
								content+=l+"\n";
							FileWriter fw=new FileWriter(to);
							fw.write(content);
							fr.close();
							fw.close();
							listOfSnfFiles.add(to);
						}
						else{
							System.out.println("Error: SNF	"+line+"	in SNF	"+snf.getName()+" not found in Path " 
																										+sourcePathForSnf);
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
}
