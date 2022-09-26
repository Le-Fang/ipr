package edu.ucla.cs.grafter.instrument;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucla.cs.grafter.file.FileUtils;

public class CloneInstrumentTest {

	@Test
	public void testMath82() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math82/src/main/java/org/apache/commons/math/optimization/linear/SimplexSolver.java",
				82, "if (MathUtils.compareTo(entry, 0, epsilon) > 0) {", "SimplexSolverTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math82", "", "");
	}

	@Test
	// @Ignore
	public void testChart1() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/jfreechart/src/main/java/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.java",
				1488, "if (dataset == null) {", "AbstractCategoryItemRendererTest",
				"/Users/eddiii/Desktop/courses/ipr/jfreechart", "", "");
	}

	@Test
	// @Ignore
	public void testChart9() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/jfreechart/src/main/java/org/jfree/data/time/TimeSeries.java",
				1068, "if ((endIndex < 0) || (endIndex < startIndex)) {", "TimeSeriesTest",
				"/Users/eddiii/Desktop/courses/ipr/jfreechart", "", "");
	}

	@Test
	// @Ignore
	public void testMath94() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math94/src/java/org/apache/commons/math/util/MathUtils.java",
				412, "if ((u == 0) || (v == 0)) {", "MathUtilsTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math94", "", "");
	}

	@Test
	// @Ignore
	public void testMath75() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math75/src/main/java/org/apache/commons/math/stat/Frequency.java",
				303, "return getPct((Comparable<?>) v);", "FrequencyTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math75", "", "");
	}

	@Test
	// @Ignore
	public void testMath30() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math30/src/main/java/org/apache/commons/math3/stat/inference/MannWhitneyUTest.java",
				173, "final double n1n2prod = n1 * n2;", "MannWhitneyUTestTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math30", "", "");
	}

	@Test
	@Ignore
	public void testGetPackageName() {
		String path = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/SimpleApnsNotification.java";
		CloneInstrument ci = new CloneInstrument(0, path, 108, 112);

		assertEquals(ci.getPackageName(), "com.notnoop.apns");
	}

	@Test
	@Ignore
	public void testTrackerExists() {
		String path = "/home/troy/SysAssure/code/Grafter/Grafter/src/main/java/edu/ucla/cs/grafter/instrument/JavaParser.java";
		CloneInstrument ci = new CloneInstrument(0, path, 136, 148);
		try {
			ci.addTestTracker();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void testTrackerNotExists() {
		String path = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/SimpleApnsNotification.java";
		CloneInstrument ci = new CloneInstrument(0, path, 108, 112);
		try {
			ci.addTestTracker();
			// check if new TestTracker.java exists
			File file = new File(
					"/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/TestTracker.java");
			assertTrue(file.exists());
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void testInsertPrintStatement() {
		String path = "/home/troy/SysAssure/dataset/java-apns-1.0.0.Beta3/src/main/java/com/notnoop/apns/SimpleApnsNotification.java";
		CloneInstrument ci = new CloneInstrument(0, path, 108, 112);
		try {
			ci.insertPrintStatement();
			// check if the print statement is inserted in the right position
			String code = FileUtils.readFileToString(path);
			String lineSeparator = System.getProperty("line.separator");
			String[] ss = code.split(lineSeparator);
			assertEquals(ss[108],
					"System.out.println(\"[Grafter][Clone Group 0][Class SimpleApnsNotification][Range(108,112)]\"+ TestTracker.getTestName());");
			restore(path);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (InstrumentException e) {
			fail();
		}
	}

	private void restore(String file) {
		File new_file = new File(file);
		File old_file = new File(file + ".bak");
		new_file.delete();
		old_file.renameTo(new File(file));
	}
}
