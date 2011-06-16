package br.ufpa.linc.xflow.core.processors.callgraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import br.ufpa.linc.xflow.data.entities.FileDependencyObject;

public class StructuralCouplingIdentifier {

	public static List<Integer> calcStructuralCoupling(FileDependencyObject a, FileDependencyObject b){
		
		String sourceCodeofA = a.getFile().getSourceCode();
		String sourceCodeofB = b.getFile().getSourceCode();
		
		//If at least of the files is empty, there is nothing to be done
		if (sourceCodeofA == null || sourceCodeofB == null){
			LinkedList<Integer> noCalls = new LinkedList<Integer>();
			noCalls.add(0);
			noCalls.add(0);
			return noCalls;
		}
		//Calculation of references is needed
		else{
			try {
				//Creates and fills temp files
				String tmpFilepathForA = "/tmp/A.java";
				String tmpFilepathForB = "/tmp/B.java";
				createAndFillFile(tmpFilepathForA, sourceCodeofA);			
				createAndFillFile(tmpFilepathForB, sourceCodeofB);
				
				//Calculates structural coupling (counts calls from A to B and vice-versa)
				List<Integer> calls = countCallsBetweenPairOfFiles(tmpFilepathForA, tmpFilepathForB, 
						a.getFilePath(), b.getFilePath());

				return calls;
							
			} catch (Exception e) {
				System.out.println("Unable to calculate structural coupling");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return null;
			}	
		}
	}
	
	public static boolean checkStructuralCoupling(FileDependencyObject a, FileDependencyObject b){
		
		String sourceCodeofA = a.getFile().getSourceCode();
		String sourceCodeofB = b.getFile().getSourceCode();
		
		//If at least of the files is empty, there is nothing to be done
		if (sourceCodeofA == null || sourceCodeofB == null){
			return false;
		}
		//Calculation of references is needed
		else{
			try {
				//Creates and fills temp files
				String tmpFilepathForA = "/tmp/A.java";
				String tmpFilepathForB = "/tmp/B.java";
				createAndFillFile(tmpFilepathForA, sourceCodeofA);			
				createAndFillFile(tmpFilepathForB, sourceCodeofB);
				
				//Calculates structural coupling (counts calls from A to B and vice-versa)
				return checkCallsBetweenPairOfFiles(tmpFilepathForA, tmpFilepathForB, a.getFilePath(), b.getFilePath());

			} catch (Exception e) {
				System.out.println("Unable to calculate structural coupling");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return false;
			}	
		}
	}

	private static void createAndFillFile(String filepath, String sourceCode) throws IOException{
		//Creates and fills first temp file
		PrintWriter outA
		   = new PrintWriter(new BufferedWriter(new FileWriter(filepath)));
		outA.append(sourceCode);
		outA.close();
	}
		
	private static List<Integer> countCallsBetweenPairOfFiles(String filepathA, 
			String filepathB, String fa, String fb) throws IOException{
		
		LinkedList<Integer> linkedList = new LinkedList<Integer>();
		Pattern modulePattern = Pattern.compile("module .*");
		Pattern functionCallPattern = Pattern.compile("uses function.*defined in.*");
		String currentModule = null;
		int refs = 0;
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("D:\\cygwin\\home\\Francisco\\doxyparse\\bin\\doxyparse" + " " + filepathA + " " + filepathB);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		while((line = bufferedReader.readLine()) != null){
			line = line.trim();
			//Found a module
			if (modulePattern.matcher(line).matches()){
				//Found the first module
				if (currentModule == null){
					currentModule = line.replace("module ", "");
				}
				//Found the second module and it's not an inner class
				else if (currentModule != null && !line.contains(currentModule + "::")){
					currentModule = line.replace("module ", "");
					linkedList.addLast(refs);
					refs = 0;
				}
			}
			//Found a function call
			else if(functionCallPattern.matcher(line).matches()){
				String[] words = line.trim().split(" ");
				String referredModule = words[words.length-1];
				if(!currentModule.equals(referredModule)){
					refs++;
				}
			}
		}
		linkedList.addLast(refs);
		
		//Due to possible aspect classes
		if(linkedList.size() == 1){
			System.out.println("Aspect class detected");
			System.out.println(fa);
			System.out.println(fb);
			linkedList.add(0);
		}
		
		bufferedReader.close();
		process.destroy();
		return linkedList;
	}
	
	private static boolean checkCallsBetweenPairOfFiles(String filepathA, 
			String filepathB, String fa, String fb) throws IOException{
		
		LinkedList<Integer> linkedList = new LinkedList<Integer>();
		Pattern modulePattern = Pattern.compile("module .*");
		Pattern functionCallPattern = Pattern.compile("uses function.*defined in.*");
		String currentModule = null;
		int refs = 0;
		
		filepathA = "/cygdrive/d"+filepathA;
		filepathB = "/cygdrive/d"+filepathB;
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("D:\\cygwin\\home\\Francisco\\doxyparse\\bin\\doxyparse" + " " + filepathA + " " + filepathB);
//		Process process = runtime.exec("doxyparse" + " " + filepathA + " " + filepathB);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		while((line = bufferedReader.readLine()) != null){
			line = line.trim();
			//Found a module
			if (modulePattern.matcher(line).matches()){
				//Found the first module
				if (currentModule == null){
					currentModule = line.replace("module ", "");
				}
				//Found the second module and it's not an inner class
				else if (currentModule != null && !line.contains(currentModule + "::")){
					currentModule = line.replace("module ", "");
					linkedList.addLast(refs);
					refs = 0;
				}
			}
			//Found a function call
			else if(functionCallPattern.matcher(line).matches()){
				String[] words = line.trim().split(" ");
				String referredModule = words[words.length-1];
				if(!currentModule.equals(referredModule)){
					refs++;
					bufferedReader.close();
					process.destroy();
					return true;
				}
			}
		}

		return false;
	}
}