package edu.purdue.cs.ipr.instrument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Collections;

import javax.swing.JOptionPane;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.purdue.cs.ipr.file.FileUtils;
import edu.purdue.cs.ipr.instrument.InstruThread;
import edu.purdue.cs.ipr.visitors.analysis.CloneVisitor;
import junit.framework.TestCase;

public class CloneInstrument {
	static final String template = "/home/troy/SysAssure/code/Grafter/Grafter/src/main/resources/template/TestTracker.template";
	int id;
	String path;
	int start;
	int end;
	private int start_new;
	private int end_new;

	public static ArrayList<Boolean> success;

	public CloneInstrument(int id, String path, int start, int end) {
		this.id = id;
		this.path = path;
		this.start = start;
		this.end = end;
		this.start_new = start;
		this.end_new = end;
	}

	public static void instru(String directoryPath, String filepath, int linenumber, String[] patches, String testName,
			String testPath, String moduleName, String methodName) {
		System.out.println("[IPR] Begin parallel debugging...");
		String projectName = directoryPath.split("/")[directoryPath.split("/").length - 1];
		FileUtils.emptyDir(System.getProperty("user.home") + "/.ipr/" + projectName);

		// success is an array that records whether our tests run sucessfully
		success = new ArrayList<>(10);
		for (int i = 0; i < 10; i++) {
			success.add(false);
		}
		ArrayList<InstruThread> threads = new ArrayList<>();
		for (int i = 0; i <= patches.length; i++) {
			String source = directoryPath;
			File srcDir = new File(source);
			String destination = System.getProperty("user.home") + "/.ipr/" + projectName + "/" + Integer.toString(i);
			File destDir = new File(destination);

			try {
				org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir);
				if (i == 0) {
					System.out.println("[IPR] Project with buggy line has been created.");
				} else {
					System.out.println("[IPR] Project with patch No." + Integer.toString(i) + " has been created.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			String newFilePath = filepath.replaceFirst(directoryPath, destination);

			if (i == 0) {
				InstruThread thread = new InstruThread(newFilePath, linenumber, "", testName, destination,
						moduleName, methodName, directoryPath, i);
				thread.start();
				threads.add(thread);
			} else {
				InstruThread thread = new InstruThread(newFilePath, linenumber, patches[i - 1], testName, destination,
						moduleName, methodName, directoryPath, i);
				thread.start();
				threads.add(thread);
			}

		}

		// should wait for all threads
		for (InstruThread each : threads) {
			try {
				each.join();
			} catch (InterruptedException e) {
				System.out.println("Interrupted when InstruThread.join()");
			}
		}
		// delete everything in our test folder
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(new File(directoryPath + "IPRkkk"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[IPR] Finished parallel debugging.");

	}

	public static String insert_print(String code, ArrayList<ArrayList<String>> vars, int linenumber, String filepath) {
		String result = "";
		int lastIndex = 0;

		String lineSeparator = System.getProperty("line.separator");
		String[] cc = code.split(lineSeparator);
		// add import statement
		int lineToInsertImport = -1;
		int inComment = 0;
		for (int i = 0; i < cc.length; i++) {
			if (cc[i].contains("/*")) {
				inComment = 1;
			}
			if (cc[i].contains("*/")) {
				inComment = 0;
			}
			// if in comment, we want to skip this line
			if (inComment == 1) {
				continue;
			}

			if (cc[i].contains("package") || cc[i].contains("import")) {
				lineToInsertImport = i;
			}
			if (cc[i].contains("public") || cc[i].contains("class")) {
				break;
			}
		}
		String outputFileName = "\""
				+ filepath.substring(0, filepath.lastIndexOf("/") + 1)
				+ "iprOutput" + Integer.toString(linenumber) + ".txt" + "\"";
		lineToInsertImport++;
		cc[lineToInsertImport] = "import com.thoughtworks.xstream.XStream;"
				+ "import com.thoughtworks.xstream.io.xml.DomDriver;import java.io.*;"
				+ cc[lineToInsertImport];

		ArrayList<Integer> lineIndices = new ArrayList<Integer>();
		for (String each : vars.get(0)) {
			lineIndices.add(Integer.parseInt(each.split(":")[1]));
		}
		for (String each : vars.get(1)) {
			lineIndices.add(Integer.parseInt(each.split(":")[1]));
		}
		for (String each : vars.get(2)) {
			lineIndices.add(Integer.parseInt(each.split(":")[1]));
		}
		for (String each : vars.get(3)) {
			lineIndices.add(Integer.parseInt(each.split(":")[1]));
		}

		int minLineIndex = Collections.min(lineIndices);

		// create and redirect the system out to a file named iprOutput.txt
		cc[minLineIndex - 2] = cc[minLineIndex - 2] + "File new_file = new File(" + outputFileName + ");";
		cc[minLineIndex - 2] = cc[minLineIndex - 2] +
				"if (!new_file.exists()) { try {new_file.createNewFile();} catch(Exception e) {System.out.println(\"cannot create iprOutput.txt\");} }";
		cc[minLineIndex - 2] = cc[minLineIndex - 2] + "FileWriter IPRfw0 = null;";
		cc[minLineIndex - 2] = cc[minLineIndex - 2] + "try {  IPRfw0 = new FileWriter(" + outputFileName
				+ ", true); } catch (Exception e) {System.out.println(\"cannot create fileWriter\");}";
		cc[minLineIndex - 2] = cc[minLineIndex - 2]
				+ "try { IPRfw0.write(\"<entry>\" + System.getProperty(\"line.separator\"));} catch (Exception e) {System.out.println(\"cannot write to fileWriter\");}"
				+ "try { IPRfw0.close(); } catch (Exception e) {System.out.println(\"cannot close file writer\");} IPRfw0 = null;";


		// variableTypes is the dictionary that we use to look up the types of
		// certain variables
		HashMap<String, String> variableTypes = new HashMap<>();
		try {
			Scanner sc = new Scanner(new File("./typeInfo.txt"));
			while (sc.hasNext()) {
				// System.out.println(sc.nextLine());
				String[] pair = sc.nextLine().split(":::");
				variableTypes.put(pair[0], pair[1]);
				}
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("variableTypes csv file unreadable");
				return "";
		}
		 

		// create the inserted lines
		// vars[0] is used variable list; vars[1] is defined variable list
		for (String each : vars.get(0)) {
			String name = each.split(":")[0];
			int line = Integer.parseInt(each.split(":")[1]);
			// name = name + "," + Integer.toString(line);
			if (line - 2 > lastIndex) {
				lastIndex = line - 2;
			}
			String type = variableTypes.get(name);
			if (type != null) {
				continue;
			}
			cc[line - 2] = cc[line - 2] + "try { FileWriter IPRfw = null; XStream xstream = new XStream(new DomDriver());" 
				+ "try { IPRfw = new FileWriter(" + outputFileName
				+ ", true); } catch (Exception e) {System.out.println(\"cannot create fileWriter\");}"
				+ "String s = xstream.toXML(" + name 
				+ ");"
				+ "if (s.length() < 300)"
				+ "{IPRfw.write(\""
				+ Integer.toString(line) + "," + "used,"
				+ name + "," + "\"+ " + "\"\\\"\"+"
				+ "s.replaceAll(\"\\\"\", \"\")" + "+\"\\\"\""
				+ "+ System.getProperty(\"line.separator\"));}"
				+ "try { IPRfw.close(); } catch (Exception e) {System.out.println(\"cannot close file writer\");}"
				+ "IPRfw = null; xstream = null;} catch(Exception e) {System.out.println(\"XStream cannnot serialize\");}";
		
			 if (!cc[line - 1].contains("return")) {
					cc[line - 1] = cc[line - 1] + "try { FileWriter IPRfw = null; XStream xstream = new XStream(new DomDriver());" 
					+ "try { IPRfw = new FileWriter(" + outputFileName
					+ ", true); } catch (Exception e) {System.out.println(\"cannot create fileWriter\");}"
					+ "String s = xstream.toXML(" + name 
					+ ");"
					+ "if (s.length() < 300)"
					+ "{IPRfw.write(\""
					+ Integer.toString(line) + "," + "defined,"
					+ name + "," + "\"+ " + "\"\\\"\"+"
					+ "s.replaceAll(\"\\\"\", \"\")" + "+\"\\\"\""
					+ "+ System.getProperty(\"line.separator\"));}"
					+ "try { IPRfw.close(); } catch (Exception e) {System.out.println(\"cannot close file writer\");}"
					+ "IPRfw = null; xstream = null;} catch(Exception e) {System.out.println(\"XStream cannnot serialize\");}";
					if (line - 1 > lastIndex) {
					lastIndex = line - 1;
				}
			}
			 
		}
		for (String each : vars.get(1)) {
			// if our patch is a return statement, we don't want to insert print after
			// return
			String name = each.split(":")[0];
			int line = Integer.parseInt(each.split(":")[1]);
			// name = name + "," + Integer.toString(line);
			if (!cc[line - 1].contains("return")) {
				cc[line - 1] = cc[line - 1] + "try { FileWriter IPRfw = null; XStream xstream = new XStream(new DomDriver());" 
				+ "try { IPRfw = new FileWriter(" + outputFileName
					+ ", true); } catch (Exception e) {System.out.println(\"cannot create fileWriter\");}"
					+ "String s = xstream.toXML(" + name 
					+ ");"
					+ "if (s.length() < 300)"
					+ "{IPRfw.write(\""
					+ Integer.toString(line) + "," + "defined,"
					+ name + "," + "\"+ " + "\"\\\"\"+"
					+ "s.replaceAll(\"\\\"\", \"\")" + "+\"\\\"\""
					+ "+ System.getProperty(\"line.separator\"));}"
					+ "try { IPRfw.close(); } catch (Exception e) {System.out.println(\"cannot close file writer\");}"
					+ "IPRfw = null; xstream = null;} catch(Exception e) {System.out.println(\"XStream cannnot serialize\");}";
				if (line - 1 > lastIndex) {
					lastIndex = line - 1;
				}
			}
		}

		 // handle infix expressions: insert a printStatement only once (before the target line)
		// int i = 0;
		// for (String each : vars.get(2)) {
		// 	String name = each.split(":")[0];
		// 	int line = Integer.parseInt(each.split(":")[1]);
		// 	String type = variableTypes.get(name);
		// 	System.out.println(name + " " + type);
		// 	if (type == null) {
		// 		continue;
		// 	}
			
		// 	if (line - 2 > lastIndex) {
		// 		lastIndex = line - 2;
		// 	}
		// 	cc[line - 2] = cc[line - 2] + type + " iprTemp" + i + " = " + name + ";";
		// 	cc[line - 2] = cc[line - 2] + "try { " + "IPRfw.write(\"before"
		// 	+ Integer.toString(line)
		// 	+ "," + "infix,"
		// 	+ name + "," + "\"+ " + "\"\\\"\"+" + "xstream.toXML(" + "iprTemp" + i
		// 	+ ").toString().replaceAll(\"\\\"\", \"\")" + "+\"\\\"\""
		// 	+ "+ System.getProperty(\"line.separator\"));" +
		// 	"} catch(Exception e) {System.out.println(\"XStream cannnot serialize\");}";
		// 	// update our patch string
		// 	cc[line - 1].replaceFirst(each, "iprTemp" + i);
		// 	i++;
		// }
		// infix expressions are not needed


		// handle methodCalls: insert a printStatement before the target line
		// assumption: calling the method(s) more than once does not alter the overall
		// behavior
		for (String each : vars.get(3)) {
			String name = each.split(":")[0];
			int line = Integer.parseInt(each.split(":")[1]);
			// name = name + "," + Integer.toString(line);
			String type = variableTypes.get(name);
			if (type != null) {
				continue;
			}
			if (line - 2 > lastIndex) {
				lastIndex = line - 2;
			}
			if (name.contains("assert")) {
				continue;
			}
			String tempname = "\\\"" + name.replaceAll("\"","&quote;") + "\\\"";
			cc[line - 2] = cc[line - 2] + "try { FileWriter IPRfw = null; XStream xstream = new XStream(new DomDriver());" 
					+ "try { IPRfw = new FileWriter(" + outputFileName
					+ ", true); } catch (Exception e) {System.out.println(\"cannot create fileWriter\");}"
					+ "String s = xstream.toXML(" + name 
					+ ");"
					+ "if (s.length() < 300)"
					+ "{IPRfw.write(\""
					+ Integer.toString(line)
					+ "," + "method,"
					+ "\"+\"" + tempname + "\"+\"" + "," + "\"+ \"\\\"\"+"
					+ "s.replaceAll(\"\\\"\", \"\")" + "+\"\\\"\""
					+ "+ System.getProperty(\"line.separator\"));}"
					+ "try { IPRfw.close(); } catch (Exception e) {System.out.println(\"cannot close file writer\");}"
					+ "IPRfw = null; xstream = null;} catch(Exception e) {System.out.println(\"XStream cannnot serialize\");}";
		}

		// cc[lastIndex] = cc[lastIndex]
		// 		+ "try { FileWriter IPRfw = null;" 
		// 		+ "try { IPRfw = new FileWriter(" + outputFileName
		// 		+ ", true); } catch (Exception e) {System.out.println(\"cannot create fileWriter\");}"
		// 		+ "IPRfw.write(\"<exit>\" + System.getProperty(\"line.separator\"));"
		// 		+ "try { IPRfw.close(); } catch (Exception e) {System.out.println(\"cannot close file writer\");}"
		// 		+ "IPRfw = null; } catch(Exception e) {System.out.println(\"XStream cannnot serialize\");}";

		for (String each : cc) {
			result += each + lineSeparator;
		}

		return result;
	}

	// preprocessTrace is used before showDiff(), they basically have the same
	// function,
	// but preprocessTrace() only insert print statments into the targeted file
	public static boolean preprocessTrace(String filepath, int linenumber) {
		ArrayList<ArrayList<String>> vars;
		String patch;
		try {
			vars = CloneVisitor.parseSnipCode(filepath, linenumber);
			patch = FileUtils.grepLine(filepath, linenumber);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("unable to perform CloneVisitor.parseSnipCode");
			return false;
		}

		if (vars.size() == 0) {
			System.out.println("no variable used or defined");
			return false;
		}

		String code;
		try {
			code = FileUtils.readFileToString(filepath);
		} catch (IOException e) {
			System.out.println("unable to perform FileUtils.readFileToString");
			return false;
		}

		code = insert_print(code, vars, linenumber, filepath);
		if (code.length() == 0) {
			return false;
		}

		// change the name of the original file
		File backup = new File(filepath + ".bak");
		File old_file = new File(filepath);
		boolean ifRename = old_file.renameTo(backup);
		if (ifRename) {
			// System.out.println("rename sucess");
		}

		// create a new file with the same name
		File new_file = new File(filepath);
		// testfilepath is the location that our new clone files are located
		try {
			// make sure we have another folder for these altered java files
			new_file.createNewFile();
		} catch (IOException e) {
			System.out.println("unable to perform createNewFile");
			e.printStackTrace();
			return false;
		}

		/*
		 * // if our target line is a if statement, we want to insert the print
		 * statements
		 * // after the whole if block, instead of the line after the if condition
		 * int ifEndLine = Integer.valueOf(vars.get(4).get(0)).intValue();
		 * if (ifEndLine != -1) {
		 * // if we do encounter a If statement
		 * for (int k = linenumber; k <= ifEndLine - 1; k++) {
		 * code += cc[k] + lineSeparator;
		 * }
		 * for (String each : addedAfter) {
		 * code += each + lineSeparator;
		 * }
		 * for (int k = ifEndLine; k < cc.length; k++) {
		 * code += cc[k] + lineSeparator;
		 * }
		 * } else {
		 * // if we do not encounter a If statement
		 * for (String each : addedAfter) {
		 * code += each + lineSeparator;
		 * }
		 * code += after;
		 * }
		 */

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new_file));
			bw.write(code);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
		}
		return true;
	} // preprocessTrace()

	public static boolean showDiff(String filepath, int linenumber, String patch, String testName, String testPath,
			String moduleName, String methodName) {

		if (patch != "") {
			try {
				FileUtils.rewriteStringToFile(patch, linenumber, filepath);
			} catch (IOException e) {
				System.out.println("exception when rewriting the file");
				return false;
			}
		}

		// get variables needed from CloneVisitor.parseSnipCode
		ArrayList<ArrayList<String>> vars;
		try {
			vars = CloneVisitor.parseSnipCode(filepath, linenumber);
		} catch (IOException e) {
			System.out.println("unable to perform CloneVisitor.parseSnipCode");
			return false;
		}

		if (vars.size() == 0) {
			System.out.println("no variable used or defined");
			return false;
		}

		String code;
		try {
			code = FileUtils.readFileToString(filepath);
		} catch (IOException e) {
			System.out.println("unable to perform FileUtils.readFileToString");
			return false;
		}

		code = insert_print(code, vars, linenumber, filepath);
		if (code.length() == 0) {
			return false;
		}

		// change the name of the original file
		File backup = new File(filepath + ".bak");
		File old_file = new File(filepath);
		boolean ifRename = old_file.renameTo(backup);
		if (ifRename) {
			// System.out.println("rename sucess");
		}

		// create a new file with the same name
		File new_file = new File(filepath);
		// testfilepath is the location that our new clone files are located
		try {
			// make sure we have another folder for these altered java files
			new_file.createNewFile();
		} catch (IOException e) {
			System.out.println("unable to perform createNewFile");
			e.printStackTrace();
			return false;
		}

		/*
		 * // if our target line is a if statement, we want to insert the print
		 * statements
		 * // after the whole if block, instead of the line after the if condition
		 * int ifEndLine = Integer.valueOf(vars.get(4).get(0)).intValue();
		 * if (ifEndLine != -1) {
		 * // if we do encounter a If statement
		 * for (int k = linenumber; k <= ifEndLine - 1; k++) {
		 * code += cc[k] + lineSeparator;
		 * }
		 * for (String each : addedAfter) {
		 * code += each + lineSeparator;
		 * }
		 * for (int k = ifEndLine; k < cc.length; k++) {
		 * code += cc[k] + lineSeparator;
		 * }
		 * } else {
		 * // if we do not encounter a If statement
		 * for (String each : addedAfter) {
		 * code += each + lineSeparator;
		 * }
		 * code += after;
		 * }
		 */

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new_file));
			bw.write(code);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		// we should run this new clone file

		ProcessBuilder processBuilder = new ProcessBuilder();
		if (moduleName.equals("")) {
			processBuilder.command("./myscript.sh", testPath, testName,
					methodName);
		} else {
			processBuilder.command("./myscript.sh", testPath, moduleName,
					testName, methodName);
		}
		Process process;
		try {
			process = processBuilder.start();
			boolean pass = process.waitFor(10, TimeUnit.MINUTES);
			if (!pass) {
				process.destroy();
				throw new Exception();
			}

		} catch (Exception e) {
			// e.printStackTrace();
			File delete = new File(filepath);
			File originalPath = new File(filepath);
			if (delete.exists()) {
				delete.delete();
				File old = new File(filepath + ".bak");
				ifRename = old.renameTo(originalPath);
				if (ifRename) {
					// System.out.println("rename(back) sucess failed");
				}
			}
			return false;
		}
		try {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					// System.out.println(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// end---running code

		/*
		 * // after we run the new file, we need to delete this clone file and rename
		 * the
		 * // old file back
		 * File delete = new File(filepath);
		 * File originalPath = new File(filepath);
		 * if (delete.exists()) {
		 * delete.delete();
		 * File old = new File(filepath + ".bak");
		 * ifRename = old.renameTo(originalPath);
		 * if (ifRename) {
		 * System.out.println("rename(back) sucess");
		 * }
		 * }
		 * 
		 */
		// our tests finished
		return true;
	}

	// addSerialization() returns a list of sentenses that are required for using
	// XStream
	private static ArrayList<String> addSerialization() {
		ArrayList<String> sentenses = new ArrayList<>();
		sentenses.add("XStream xstream = new XStream(new DomDriver());");
		return sentenses;
	}

	// used for manual testing
	public static void main(String[] args) {
		testLang6();
		// String[] patches = args[3].split("##");
		// CloneInstrument.instru(args[0], args[1], Integer.parseInt(args[2]), patches, args[4], args[5], args[6],
		// 		args[7]);
	}

	private static void testLang6() {
		String[] p = { "pos += Character.charCount( Character.codePointAt( input , pos - pos ) ) ;", "pos += Character.charCount(Character.codePointAt(input, pt));"};
		CloneInstrument.instru("/Users/ruixinwang/Documents/Projects/ipr/repo/Lang_6",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Lang_6/src/main/java/org/apache/commons/lang3/text/translate/CharSequenceTranslator.java",
				95, p,
				"StringUtilsTest",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Lang_6", "", "testEscapeSurrogatePairs");
	}

	private static void testMath27() {
		String[] p = { "return 100.0*doubleValue( ) ;"};
		CloneInstrument.instru("/Users/ruixinwang/Documents/Projects/ipr/repo/Math_27",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_27/src/main/java/org/apache/commons/math3/fraction/Fraction.java",
				597, p,
				"FractionTest",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_27", "", "testMath835");
	}

	private static void testMath57() {
		String[] p = { "double sum = 0;"};
		CloneInstrument.instru("/Users/ruixinwang/Documents/Projects/ipr/repo/Math_57",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_57/src/main/java/org/apache/commons/math/stat/clustering/KMeansPlusPlusClusterer.java",
				175, p,
				"KMeansPlusPlusClustererTest",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_57", "", "testSmallDistances");
	}

	private static void testMath59() {
		String[] p = { "return (a <= b) ? b : (Float.isNaN(a + b) ? Float.NaN : a);"};
		CloneInstrument.instru("/Users/ruixinwang/Documents/Projects/ipr/repo/Math_59",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_59/src/main/java/org/apache/commons/math/util/FastMath.java",
				3482, p,
				"FastMathTest",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_59", "", "testMinMaxFloat");
	}

	private static void testMath94() {
		String[] p = { "if ((u == 0) || (v == 0)) {", "if ((u == 0) || (v == 0)) {" };
		CloneInstrument.instru(
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_94",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_94/src/java/org/apache/commons/math/util/MathUtils.java",
				412, p, "MathUtilsTest",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_94", "", "testGcd");
	}

	private static void testMath82() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math82/src/main/java/org/apache/commons/math/optimization/linear/SimplexSolver.java",
				82, "if (MathUtils.compareTo(entry, 0, epsilon) > 0) {", "SimplexSolverTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math82", "", "");
	}

	private static void testChart1() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/jfreechart/src/main/java/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.java",
				1488, "if (dataset == null) {", "AbstractCategoryItemRendererTest",
				"/Users/eddiii/Desktop/courses/ipr/jfreechart", "", "");
	}

	// testChart9 tests an open-source maven project jfreechart (using the correct
	// line)
	private static void testChart9() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/jfreechart/src/main/java/org/jfree/data/time/TimeSeries.java",
				1068, "if ((endIndex < 0) || (endIndex < startIndex)) {", "TimeSeriesTest",
				"/Users/eddiii/Desktop/courses/ipr/jfreechart", "", "");
	}

	// testMath75 tests a bug (Math 75) from defect4J (using the buggy
	// version--return statement)
	private static void testMath75() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math75/src/main/java/org/apache/commons/math/stat/Frequency.java",
				303, "return getPct((Comparable<?>) v);", "FrequencyTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math75", "", "");
	}

	private static void testMath30() {
		String[] p = { "final int n1n2prod = n1 * n2;", "final double n1n2prod = n1*n2;",
				"final double n1n2prod = n1*( n1+2+1) /2.0;" };
		CloneInstrument.instru("/Users/ruixinwang/Documents/Projects/ipr/repo/Math_30",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_30/src/main/java/org/apache/commons/math3/stat/inference/MannWhitneyUTest.java",
				173, p,
				"MannWhitneyUTestTest",
				"/Users/ruixinwang/Documents/Projects/ipr/repo/Math_30", "", "testBigDataSet");
	}

	private static void testObject() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/iprObject/src/main/java/org/apache/commons/math3/stat/inference/MannWhitneyUTest.java",
				56, "iprObject c = a.add(b);", "MannWhitneyUTestTest",
				"/Users/eddiii/Desktop/courses/ipr/iprObject", "", "");
	}
}