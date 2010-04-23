package com.tabulaw.config;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ConfigTest
 * @author jpk
 */
@Test(groups = "config")
public class ConfigTest {

	/**
	 * Temp file that is created for unit testing.
	 */
	private static final String TEST_PROPS_FILE = "configTest.properties";

	/**
	 * ConfigKeys - Property keys in the test config.properties file.
	 * @author jpk
	 */
	private static final List<String> keys = new ArrayList<String>();

	static {
		// # simple keys
		keys.add("props.simple.propA");
		keys.add("props.simple.propB");

		// # case keys
		keys.add("props.case.lowercase");
		keys.add("props.case.UPPERCASE");

		// # complex property value keys
		keys.add("props.complex.ConversionPattern");

		// # interpolated props
		keys.add("props.interpolated.propA");
		keys.add("props.interpolated.propB");
		keys.add("props.interpolated.propC");

		// # props with ',' in their values (should remain)
		keys.add("props.commas.propA");
		keys.add("props.commas.propB");
	}

	/**
	 * Constructor
	 */
	public ConfigTest() {
		super();
	}

	/**
	 * Verify Config default loading.
	 * @throws Exception
	 */
	public void testDefaultLoading() throws Exception {
		try {
			Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));
			assert !config.isEmpty() : "Config instance is empty";
		}
		catch(Throwable t) {
			Assert.fail(t.getMessage(), t);
		}
	}

	/**
	 * Verifies the property values are properly interpolated
	 * @throws Exception
	 */
	public void testInterpolation() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));

		Iterator<?> itr = config.getKeys();
		while(itr.hasNext()) {
			Object obj = itr.next();
			assert obj != null;
			String key = obj.toString();
			assert key.length() > 1;
			String val = config.getString(key);
			assert val != null && val.indexOf("${") < 0 : "Encountered non-interpolated property value: " + val;
		}
	}

	/**
	 * Verifies {@link Config}s asMap method(s) work for all loaded properties
	 * @throws Exception
	 */
	public void testAllAsMap() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));

		Map<String, String> map = config.asMap(null, null);
		assert map != null;

		// ensure all properties are in the map
		for(String key : keys) {
			assert map.keySet().contains(key) : "Encountered property in map that was NOT in the loaded config properties file: "
					+ key;
		}
	}

	/**
	 * Verifies {@link Config}s asMap method(s) work given a pefix
	 * @throws Exception
	 */
	public void testNestedAsMap() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));

		Map<String, String> map = config.asMap("simple", "simple.");
		assert map != null;

		// ensure only log4j props are in the map
		for(String key : map.keySet()) {
			assert key.startsWith("simple.") : "Encountered non simple property in map";
		}
	}

	private File stubTestConfigOutputPropsFile() throws Exception {
		File f = new File(TEST_PROPS_FILE);
		f.createNewFile();
		f.deleteOnExit();
		return f;
	}

	/**
	 * Verifies config saving to disk.
	 * @throws Exception
	 */
	public void testSaveAllToFile() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));

		File f = stubTestConfigOutputPropsFile();
		config.saveAsPropFile(f, null, null);

		Properties props = new Properties();
		props.load(new FileReader(TEST_PROPS_FILE));

		Enumeration<?> enm = props.propertyNames();

		while(enm.hasMoreElements()) {
			Object obj = enm.nextElement();
			String key = obj == null ? null : obj.toString();
			String val = props.getProperty(key);

			// verify comma having prop values
			if(key.equals("props.commas.propA")) {
				assert val.equals("a,b,c") : "props.commas.propA - mismatch!";
			}
			else if(key.equals("props.commas.propB")) {
				assert val.equals("d,e,f") : "props.commas.propB - mismatch!";
			}
		}
	}

	/**
	 * Verifies config saving to disk.
	 * @throws Exception
	 */
	public void testSaveSubsetToFile() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));

		File f = stubTestConfigOutputPropsFile();
		config.saveAsPropFile(f, "props.commas", "props.commas.");

		Properties props = new Properties();
		props.load(new FileReader(TEST_PROPS_FILE));

		Enumeration<?> enm = props.propertyNames();
		while(enm.hasMoreElements()) {
			Object obj = enm.nextElement();
			String key = obj == null ? null : obj.toString();
			assert key.startsWith("props.commas.") : "Key doesn't start with commas.";
			assert keys.contains(key) : "The props keys list does not contain key: " + key;
		}
	}

	/**
	 * Tests variable interpolation across a config file boundary. We want to be
	 * able to put a variable encountered in a previously loaded config file into
	 * a config file loaded subsequently!
	 * @throws Exception
	 */
	public void testIntraConfigFileVariableInterpolation() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"), new ConfigRef("com/tabulaw/config/config2.properties"));
		String pv = config.getString("props.simple.propA");
		String iipv = config.getString("props.intrainterpolated.propA");
		assert iipv != null && iipv.equals(pv);
	}

	/**
	 * Tests filtering of config props via a {@link IConfigFilter}.
	 * @throws Exception
	 */
	public void testConfigFiltering() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"));

		// create a filter to extract only those keys beginning with: 'props.simple'
		IConfigFilter filter = new IConfigFilter() {

			@Override
			public boolean accept(String keyName) {
				return keyName.startsWith("props.simple");
			}
		};
		Config filtered = config.filter(filter);
		assert filtered != null && !filtered.isEmpty() : "Unable to obtain non-empty filtered config instance";
		assert filtered.getProperty("props.simple.propA") != null;
		assert filtered.getProperty("props.simple.propB") != null;
		int num = 0;
		for(Iterator<String> itr = filtered.getKeys(); itr.hasNext();) {
			String key = itr.next();
			assert key != null && key.startsWith("props.simple") : "Encountered invalid filtered key";
			num++;
		}
		assert num == 2 : "Invalid number of filtered keys.";
	}

	/**
	 * Verifies that properties are overridden when multiple property files are
	 * loaded.
	 * @throws Exception
	 */
	public void testConfigFileOverriding() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties"), new ConfigRef("com/tabulaw/config/config2.properties"));
		String pv = config.getString("props.simple.propB");
		assert "val2-overridden".equals(pv);
	}

	public void testLoadAll() throws Exception {
		Config config = Config.load(new ConfigRef("com/tabulaw/config/config.properties", false, true));

		for(String key : keys) {
			assert config.getProperty(key) != null;
		}
	}
}
