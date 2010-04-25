package com.tabulaw.util;

import org.testng.annotations.Test;

/**
 * Simple plug to get encoded user passwords
 * @author jpk
 */
@Test(groups = "util")
public class CryptoUtilTest {

	@Test
	public void testEncrypt() {
		final String toEncrypt = "tester4life";
		final String e = CryptoUtil.encrypt(toEncrypt);
		final String d = CryptoUtil.decrypt(e);
		assert toEncrypt.equals(d);
	}

}
