package edu.purdue.cs.ipr.instrument;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import edu.purdue.cs.ipr.file.FileUtils;
import edu.purdue.cs.ipr.instrument.CloneInstrument;

public class CloneInstrumentTest {

	@Test
	@Ignore
	public void testMath82() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math82/src/main/java/org/apache/commons/math/optimization/linear/SimplexSolver.java",
				82, "if (MathUtils.compareTo(entry, 0, epsilon) > 0) {", "SimplexSolverTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math82", "", "");
	}

	@Test
	@Ignore
	public void testChart1() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/jfreechart/src/main/java/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.java",
				1488, "if (dataset == null) {", "AbstractCategoryItemRendererTest",
				"/Users/eddiii/Desktop/courses/ipr/jfreechart", "", "");
	}

	@Test
	@Ignore
	public void testChart9() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/jfreechart/src/main/java/org/jfree/data/time/TimeSeries.java",
				1068, "if ((endIndex < 0) || (endIndex < startIndex)) {", "TimeSeriesTest",
				"/Users/eddiii/Desktop/courses/ipr/jfreechart", "", "");
	}

	@Test
	@Ignore
	public void testMath94() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math94/src/java/org/apache/commons/math/util/MathUtils.java",
				412, "if ((u == 0) || (v == 0)) {", "MathUtilsTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math94", "", "");
	}

	@Test
	@Ignore
	public void testMath75() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math75/src/main/java/org/apache/commons/math/stat/Frequency.java",
				303, "return getPct((Comparable<?>) v);", "FrequencyTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math75", "", "");
	}

	@Test
	@Ignore
	public void testMath30() {
		CloneInstrument.showDiff(
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math30/src/main/java/org/apache/commons/math3/stat/inference/MannWhitneyUTest.java",
				173, "final double n1n2prod = n1 * n2;", "MannWhitneyUTestTest",
				"/Users/eddiii/Desktop/courses/ipr/defects4j-repair-Math30", "", "");
	}

	private void restore(String file) {
		File new_file = new File(file);
		File old_file = new File(file + ".bak");
		new_file.delete();
		old_file.renameTo(new File(file));
	}
}
