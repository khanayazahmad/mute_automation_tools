package com.btpb.tsu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MLBHandle {
	
	public static HashMap<String,HashMap<String,ArrayList<String>>> getUsedVariantsForCommands(String smdPath){
		
		HashMap<String,HashMap<String,ArrayList<String>>> cmdVariants = new HashMap<String,HashMap<String,ArrayList<String>>>();
		File smdDir = new File(smdPath);
		ArrayList<File> listOfSMD = new ArrayList<File>(Arrays.asList(smdDir.listFiles()));
		String line;
		
		for(File smd:listOfSMD){
			try {
				BufferedReader br = new BufferedReader(new FileReader(smd));
				while((line = br.readLine()) != null){
					if(line.trim().startsWith("%"))
						continue;
					if(line.trim().startsWith("SEND")&&line.trim().endsWith(",")){
						String command = line.split(",")[1].trim();
						String variant = line.split(",")[2].trim();
						if(cmdVariants.containsKey(command)){
							if(cmdVariants.get(command).containsKey(variant)){
								if(cmdVariants.get(command).get(variant).contains(smd.getName())){
									
								}else{
									cmdVariants.get(command).get(variant).add(smd.getName());
								}
							}else{
								cmdVariants.get(command).put(variant, new ArrayList<String>(Arrays.asList(smd.getName())));
							}
						}else{
							cmdVariants.put(command, (new HashMap<String,ArrayList<String>>()));
							cmdVariants.get(command).put(variant, new ArrayList<String>(Arrays.asList(smd.getName())));
						}
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(String cmd:cmdVariants.keySet()){
			System.out.println("cmd: "+cmd+"	--> ");
			for(String var:cmdVariants.get(cmd).keySet()){
				System.out.println(var+"		-->		"+cmdVariants.get(cmd).get(var));
				
			}
			System.out.println();
		}
		
		return cmdVariants;
		
	}

	public static HashMap<String,HashMap<String,ArrayList<String>>> getUsedVariantsForEvents(String smdPath){
		
		HashMap<String,HashMap<String,ArrayList<String>>> evtVariants = new HashMap<String,HashMap<String,ArrayList<String>>>();
		File smdDir = new File(smdPath);
		ArrayList<File> listOfSMD = new ArrayList<File>(Arrays.asList(smdDir.listFiles()));
		String line;
		
		for(File smd:listOfSMD){
			try {
				BufferedReader br = new BufferedReader(new FileReader(smd));
				while((line = br.readLine()) != null){
					if(line.trim().startsWith("%"))
						continue;
					if((line.trim().startsWith("WAIT")||line.trim().startsWith("COMPARE"))&&line.trim().endsWith(",")){
						String event = line.split(",")[1].trim();
						String variant = line.split(",")[2].trim();
						if(evtVariants.containsKey(event)){
							if(evtVariants.get(event).containsKey(variant)){
								if(evtVariants.get(event).get(variant).contains(smd.getName())){
									
								}else{
									evtVariants.get(event).get(variant).add(smd.getName());
								}
							}else{
								evtVariants.get(event).put(variant, new ArrayList<String>(Arrays.asList(smd.getName())));
							}
						}else{
							evtVariants.put(event, (new HashMap<String,ArrayList<String>>()));
							evtVariants.get(event).put(variant, new ArrayList<String>(Arrays.asList(smd.getName())));
						}
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(String cmd:evtVariants.keySet()){
			System.out.println("cmd: "+cmd+"	--> ");
			for(String var:evtVariants.get(cmd).keySet()){
				System.out.println(var+"		-->		"+evtVariants.get(cmd).get(var));
				
			}
			System.out.println();
		}
		
		return evtVariants;
		
	}

	public static int addByteDump(File mlb, String command, String byteDump){
		long offset = 0 ;
		
		BufferedReader brmlb;
		try {
			brmlb = new BufferedReader(new FileReader(mlb));
			Object[] o = brmlb.lines().toArray();
			String lines = String.join("\r\n", Arrays.copyOf(o, o.length, String[].class));

			int lastcomma =lines.lastIndexOf(",", lines.lastIndexOf(",",lines.indexOf(":\r\n",lines.indexOf(":",lines.indexOf(command+":"))+1))-1);
			int lastline = lines.lastIndexOf("\n",lastcomma-1);
			int var = Integer.parseInt(lines.substring(lastline, lastcomma).trim())+1;
			offset = lines.indexOf("\n", lines.indexOf("\n", lines.lastIndexOf(",",lines.indexOf(":\r\n",lines.indexOf(":",lines.indexOf(command+":"))+1)))) + 1;
			

			insert(mlb.getAbsolutePath(),offset,("\r\n    "+var+","+byteDump+"\r\n").getBytes());
			return var;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}

	public static void insert(String filename, long offset, byte[] content) throws Exception {
		  RandomAccessFile r = new RandomAccessFile(new File(filename), "rw");
		  RandomAccessFile rtemp = new RandomAccessFile(new File(filename + "~"), "rw");
		  long fileSize = r.length();
		  FileChannel sourceChannel = r.getChannel();
		  FileChannel targetChannel = rtemp.getChannel();
		  sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
		  sourceChannel.truncate(offset);
		  r.seek(offset);
		  r.write(content);
		  long newOffset = r.getFilePointer();
		  targetChannel.position(0L);
		  sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
		  sourceChannel.close();
		  targetChannel.close();
		}
	
	public static void main(String[] args) {
		System.out.println("COMMANDS____________________________________________________");
		getUsedVariantsForCommands("D:\\TSU\\AE\\MUTE_workspace\\Testcases\\smd");
		System.out.println("\n\nEVENTS____________________________________________________");
		getUsedVariantsForEvents("D:\\TSU\\AE\\MUTE_workspace\\Testcases\\smd");

	}
}
