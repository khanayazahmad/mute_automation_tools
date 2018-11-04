package com.btpb.tsu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	//migration script
	public static void scenarioMigrator(String sourcePath, String destinationPath, String listPath){
		
		SMDHandle smdHandle = new SMDHandle();
		SNFHandle snfHandle = new SNFHandle();
		
		String referencePath = "C:\\Windows\\Temp";
		String suiteName = "TempSuite~";
		
		File snfPath = new File(referencePath + "\\" + suiteName + "\\Master_Testsuite\\MUTE_workspace\\Testcases\\snf");
		File smdPath = new File(referencePath + "\\" + suiteName + "\\Master_Testsuite\\MUTE_workspace\\Testcases\\smd");
		File ssnPath = new File(referencePath + "\\" + suiteName + "\\Master_Testsuite\\MUTE_workspace\\Testcases\\ssn");
		File mlbPath = new File(referencePath + "\\" + suiteName + "\\Master_Testsuite\\MUTE_workspace\\Messages");
		
		if(snfPath.mkdirs()&&
				smdPath.mkdirs()&&
				ssnPath.mkdirs()&&
				mlbPath.mkdirs())
			System.out.println("Workspace created successfully!");
		else
			System.out.println("Failure: Workspace could not be created!");
		
		String tempPath = referencePath + "\\" + suiteName + "\\Master_Testsuite\\MUTE_workspace";


		snfHandle.snfExtractorFromSsn(sourcePath+"\\Testcases\\snf", 
										tempPath+"\\Testcases\\snf", 
										listPath);
		
		snfHandle.snfExtractorFromSnf(sourcePath+"\\Testcases\\snf", 
										tempPath+"\\Testcases\\snf");
		
		smdHandle.smdExtractor(sourcePath+"\\Testcases\\smd", 
									tempPath+"\\Testcases\\smd", 
									tempPath+"\\Testcases\\snf");
		System.out.println("\n\n\n SMD Extraction Complete... \n\n\n");
		
		ArrayList<String> oldNames=smdHandle.getOldSmdNameList(tempPath+"\\Testcases\\smd");
		ArrayList<String> newNames=smdHandle.duplicateListGenerator(destinationPath+"\\Testcases\\smd", 
				tempPath+"\\Testcases\\smd", oldNames);
		System.out.println("\n\n\n Duplicate List Generation Complete... \n\n\n");
		
		for (int i = 0; i < oldNames.size(); i++)
			if(!newNames.get(i).equals("")){
				File file = new File(tempPath+"\\Testcases\\smd\\"+oldNames.get(i));
				System.out.println(oldNames.get(i)+" deleted -> "+file.delete());
			}
		System.out.println("\n\n\n Duplicates Deleted... \n\n\n");

		
		for (int i = 0; i < oldNames.size(); i++){
			if(!newNames.get(i).equals(""))
				System.out.println(oldNames.get(i) + "	-->	" + newNames.get(i));
			else{
				newNames.set(i, oldNames.get(i));
			}
		}
		
		
		smdHandle.SnfUpdater(newNames, oldNames, tempPath+"\\Testcases\\snf");
		System.out.println("\n\n\n SNF Updated... \n\n\n");
		
		ArrayList<String> checkPath = new ArrayList<String>();
		checkPath.add(destinationPath+"\\Testcases\\smd");
		
		oldNames = smdHandle.getOldSmdNameList(tempPath+"\\Testcases\\smd");
		newNames = smdHandle.generateNewSmdNameListWithReference(tempPath+"\\Testcases\\smd",checkPath);
		System.out.println("\n\n\n New List with updated numbers created... \n\n\n");
		
		
		smdHandle.SnfUpdater(newNames, oldNames, tempPath+"\\Testcases\\snf");
		System.out.println("\n\n\n SNF Updated... \n\n\n");
		
		smdHandle.SmdNameChanger(newNames, oldNames, tempPath+"\\Testcases\\smd");
		System.out.println("\n\n\n SMD Updated... \n\n\n");
		
		File sourceLib[] = (new File(tempPath+"\\Testcases\\smd")).listFiles();

		for(int i=0;i<sourceLib.length;i++){
			System.out.println("Copying "+ sourceLib[i].getName() + " from " + " " + sourcePath + " ...");
			try {
				Files.copy(sourceLib[i].toPath(),new File(destinationPath + "\\Testcases\\smd\\"+sourceLib[i]).toPath(),
																						StandardCopyOption.REPLACE_EXISTING);
				System.out.println(sourceLib[i].getName()+" copied successfully!");

			} catch (IOException e) {

				e.printStackTrace();
				System.out.println("Failure: "+sourceLib[i].getName()+" could not be copied");
			}
			
		}
		
		File sourceSnf[] = (new File(tempPath+"\\Testcases\\snf")).listFiles();

		for(int i=0;i<sourceSnf.length;i++){
			System.out.println("Copying "+ sourceSnf[i].getName() + " from " + " " + sourcePath + " ...");
			try {
				Files.copy(sourceSnf[i].toPath(),new File(destinationPath + "\\Testcases\\snf\\"+sourceSnf[i]).toPath(),
																						StandardCopyOption.REPLACE_EXISTING);
				System.out.println(sourceSnf[i].getName()+" copied successfully!");

			} catch (IOException e) {

				e.printStackTrace();
				System.out.println("Failure: "+sourceLib[i].getName()+" could not be copied");
			}
			
		}
		
		(new File(referencePath + "\\" + suiteName)).delete();
		System.out.println("-------testsuite migration completed!");
		
	}

	public static void smdNumberingProvider(String sourcePath, String ...checkPath ){
		
		
		SMDHandle smdHandle = new SMDHandle();
		
		ArrayList<String> checkPathList = new ArrayList<String>(Arrays.asList(checkPath));

		
		ArrayList<String> oldNames = smdHandle.getOldSmdNameList(sourcePath+"\\Testcases\\smd");
		ArrayList<String> newNames = smdHandle.generateNewSmdNameListWithReference(sourcePath+"\\Testcases\\smd",checkPathList);
		

		smdHandle.SnfUpdater(newNames, oldNames, sourcePath+"\\Testcases\\snf");
		smdHandle.SmdNameChanger(newNames, oldNames, sourcePath+"\\Testcases\\smd");
	}
	
	public static void morphTestsuite(){
		
		int mode = 1;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Select from the following forms:\n1. Random Address\n2. Resolvable Private Address with Public Address\n3. Resolvable Private Address with Random Address\n4. Non Resolvable Private Address ");
		try {
			mode = Integer.parseInt(br.readLine());
			TestsuiteMorphingTool r1 = new TestsuiteMorphingTool(mode);
			System.out.println("Enter Random Address for IUT: ");
			r1.setRANDOM_ADDRESS_INF1(br.readLine());
			System.out.println("Enter Random Address for Tester: ");
			r1.setRANDOM_ADDRESS_INF2(br.readLine());
			System.out.println("Enter Path of SVN checkout of Testsuite: ");
			r1.setSourcePath(br.readLine()+"\\MUTE_workspace");
			r1.setSasraCounter(1);
			r1.setSraCounter(1);
			System.out.println("Enter path where the Testsuite is to be Generated: ");
			r1.createWorkspace(br.readLine());
			System.out.println("Enter the path of File having the list of Scenarios to be morphed: ");
			r1.generateTestSuite(br.readLine());
	        r1.revertChangesAndCleanUp();
	        br.close();
		} catch (NumberFormatException | IOException e) {

			e.printStackTrace();
		}
	
	}
	
	public static void main(String[] args) {
		
		String choice="";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do{
			try{
				String input;
				System.out.println("Select the tool to be used: ");
				System.out.println("/scm -> Scenario Migrator");
				System.out.println("/snp -> SMD Number Provider");
				System.out.println("/tsm -> Testsuite Morphing ");
				System.out.println("Type a choice or type exit to exit");
				choice = br.readLine();
				switch(choice){
				case "/scm":System.out.println("Enter source path, destination path and path of list consisting of scenarios to be migrated: ");
							scenarioMigrator(br.readLine(), br.readLine(), br.readLine());
					break;
				case "/snp":System.out.println("Enter path of workspace you want to be renumbered\n"
							+" and the reference snf folder path: ");
							smdNumberingProvider(br.readLine(),br.readLine());
					break;
				case "/tsm":morphTestsuite();
					break;
				case "exit":System.out.println("Logging off.... ");
					break;
				default:System.out.println("Please select a valid choice... ");
				}
			}catch(Exception e){
				System.out.println("Input could not be processed! Try again..");
				e.printStackTrace();
				continue;
			}
		}while(!choice.contains("exit"));
		
	
	}


}
