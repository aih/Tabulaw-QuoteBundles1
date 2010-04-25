/**
 * The Logic Lab
 * @author jpk
 * Jun 4, 2008
 */
package com.tabulaw.util;

import org.testng.annotations.Test;



/**
 * PropertyPathTest - Test the public methods for {@link PropertyPath}.
 * @author jpk
 */
@Test(groups = "common")
public class PropertyPathTest {

	public void test() throws Exception {
		final PropertyPath pp = new PropertyPath();

		String path;

		// test empty property path
		pp.parse(null);
		assert pp.toString() == null;
		assert pp.nameAt(0) == null;
		assert pp.indexAt(0) == -1;

		// test path at
		path = "pathA.pathB.pathC.pathD[3]";
		pp.parse(path);
		assert "pathA".equals(pp.pathAt(0));
		assert "pathB".equals(pp.pathAt(1));
		assert "pathC".equals(pp.pathAt(2));
		assert "pathD[3]".equals(pp.pathAt(3));

		// test single node non-indexed property path
		path = "path";
		pp.parse(path);
		assert path.equals(pp.toString());
		assert pp.depth() == 1;
		assert pp.indexAt(0) == -1;
		try {
			pp.nameAt(1);
			throw new Exception("fail");
		}
		catch(final ArrayIndexOutOfBoundsException e) {
			// expected
		}

		// test single node indexed property path
		path = "path[2]";
		pp.parse(path);
		assert path.equals(pp.toString());
		assert pp.depth() == 1;
		assert "path".equals(pp.nameAt(0));
		assert pp.indexAt(0) == 2;

		// test invalid single node indexed property path w/ non-numeric index
		path = "path[a]";
		pp.parse(path);
		assert path.equals(pp.toString());
		assert pp.depth() == 1;
		assert "path".equals(pp.nameAt(0));
		try {
			pp.indexAt(0);
		}
		catch(final IllegalArgumentException e) {
			// expected
		}

		// test property path depth
		path = "pathA.pathB[3].pathC.pathD";
		pp.parse(path);
		assert path.equals(pp.toString());
		assert pp.depth() == 4;
		assert "pathA".equals(pp.nameAt(0));
		assert "pathB".equals(pp.nameAt(1));
		assert "pathC".equals(pp.nameAt(2));
		assert "pathD".equals(pp.nameAt(3));
		assert pp.indexAt(0) == -1;
		assert pp.indexAt(1) == 3;
		assert pp.indexAt(2) == -1;
		assert pp.indexAt(3) == -1;

		// test replace at
		pp.replaceAt(0, "pathAU");
		assert "pathAU.pathB[3].pathC.pathD".equals(pp.toString());
		pp.replaceAt(3, "pathDU");
		assert "pathAU.pathB[3].pathC.pathDU".equals(pp.toString());
		pp.replaceAt(2, null);
		assert "pathAU.pathB[3].pathDU".equals(pp.toString());

		// test replace
		pp.replace("pathAU", "pathA");
		assert "pathA.pathB[3].pathDU".equals(pp.toString());
		pp.replace("pathB[3]", "pathB[3]");
		assert "pathA.pathB[3].pathDU".equals(pp.toString());
		pp.replace("pathDU", "pathD");
		assert "pathA.pathB[3].pathD".equals(pp.toString());

		// set get parent property path
		pp.parse(null);
		assert pp.getParentPropertyPath() == null;
		pp.parse("path");
		assert pp.getParentPropertyPath() == null;
		pp.parse("pathA.pathB");
		assert "pathA".equals(pp.getParentPropertyPath());
		pp.parse("pathA.pathB.pathC");
		assert "pathA.pathB".equals(pp.getParentPropertyPath());

		// test set parent property path
		pp.parse(null);
		assert pp.setParentPropertyPath("path") == false;
		pp.parse("path");
		assert pp.setParentPropertyPath("parent") == true;
		assert "parent.path".equals(pp.toString());
		pp.parse("pathA.pathB");
		assert pp.setParentPropertyPath("parent1.parent2") == true;
		assert "parent1.parent2.pathB".equals(pp.toString());

		// test trim();
		pp.parse("pathA.pathB.pathC.pathD");
		assert "pathA.pathB.pathC".equals(pp.trim(1));
		assert "pathA.pathB".equals(pp.trim(2));
		assert "pathA".equals(pp.trim(3));
		assert null == pp.trim(4);
		assert null == pp.trim(5);
		assert null == pp.trim(0);
		assert null == pp.trim(-1);

		// test clip();
		pp.parse("pathA.pathB.pathC.pathD");
		assert "pathB.pathC.pathD".equals(pp.clip(1));
		assert "pathC.pathD".equals(pp.clip(2));
		assert "pathD".equals(pp.clip(3));
		assert null == pp.clip(4);
		assert null == pp.clip(5);
		assert null == pp.clip(0);
		assert null == pp.clip(-1);
	}
}
