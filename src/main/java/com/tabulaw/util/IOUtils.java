package com.tabulaw.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 
 * @author ymakhno
 *
 */
public class IOUtils {
	
	public static void closeQuitely(Closeable... closeables) {
		for (Closeable closeable : closeables) {
			try {
				if (closeable != null) {
					closeable.close();
				}
			} catch (IOException ignore) {
				// ignore
			}
		}
	}
}
