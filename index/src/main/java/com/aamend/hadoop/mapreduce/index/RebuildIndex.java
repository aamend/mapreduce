package com.aamend.hadoop.mapreduce.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.aamend.hadoop.mapreduce.service.IndexService;

public class RebuildIndex {

	private static final Logger LOGGER = Logger.getLogger(RebuildIndex.class);

	public static String INPUT_PATH;
	public static String INPUT_INDEX_PATH;
	public static String OUTPUT_PATH;
	public static String OUTPUT_INDEX_PATH;
	public static String PROPERTY_FILE;
	public static int INDEXED_COLUMN;
	public static int REDUCE_NUMBER;

	public static void main(String[] args) {

		LOGGER.info("***********************************");
		LOGGER.info("Welcome to Hadoop Index Rebuild");
		LOGGER.info("***********************************");

		IndexService index = new IndexService();

		// --------------------------------------
		// Read command line arguments
		// --------------------------------------

		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(args[0]));
			INPUT_PATH = prop.getProperty("INPUT_PATH");
			INPUT_INDEX_PATH = prop.getProperty("INPUT_INDEX_PATH");
			OUTPUT_PATH = prop.getProperty("OUTPUT_PATH");
			OUTPUT_INDEX_PATH = prop.getProperty("OUTPUT_INDEX_PATH");

			if (INPUT_PATH == null || INPUT_PATH.isEmpty()) {
				LOGGER.error("INPUT_PATH property is empty");
				System.exit(1);
			}
			if (INPUT_INDEX_PATH == null || INPUT_INDEX_PATH.isEmpty()) {
				LOGGER.error("INPUT_INDEX_PATH property is empty");
				System.exit(1);
			}
			if (OUTPUT_PATH == null || OUTPUT_PATH.isEmpty()) {
				LOGGER.error("OUTPUT_PATH property is empty");
				System.exit(1);
			}
			if (OUTPUT_INDEX_PATH == null || OUTPUT_INDEX_PATH.isEmpty()) {
				LOGGER.error("OUTPUT_INDEX_PATH property is empty");
				System.exit(1);
			}

			LOGGER.info("INPUT_PATH : \t" + INPUT_PATH);
			LOGGER.info("INPUT_INDEX_PATH : \t" + INPUT_INDEX_PATH);
			LOGGER.info("OUTPUT_PATH : \t" + OUTPUT_PATH);
			LOGGER.info("OUTPUT_INDEX_PATH : \t" + OUTPUT_INDEX_PATH);

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Property file must be supplied");
			LOGGER.error("usage : hadoop jar ${JAR_FILE} "
					+ RebuildIndex.class.getCanonicalName()
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		} catch (FileNotFoundException e) {
			LOGGER.error("Property file is not found ," + e.getMessage());
			LOGGER.error("usage : hadoop jar ${JAR_FILE} "
					+ RebuildIndex.class.getCanonicalName()
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		} catch (IOException e) {
			LOGGER.error("Cannot read property file ," + e.getMessage());
			LOGGER.error("usage : hadoop jar ${JAR_FILE} "
					+ RebuildIndex.class.getCanonicalName()
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		}

		try {
			INDEXED_COLUMN = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Column index must be supplied");
			LOGGER.error("usage : hadoop jar ${JAR_FILE} "
					+ RebuildIndex.class.getCanonicalName()
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		} catch (NumberFormatException e) {
			LOGGER.error("Column index is not a valid number");
			LOGGER.error("usage : hadoop jar ${JAR_FILE} "
					+ RebuildIndex.class.getCanonicalName()
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		}
		
		try {
			REDUCE_NUMBER = Integer.parseInt(args[2]);
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Number of reduce must be supplied");
			LOGGER.error("usage : hadoop jar ${JAR_FILE} " + RebuildIndex.class
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		} catch (NumberFormatException e) {
			LOGGER.error("Number of reduce is not a valid number");
			LOGGER.error("usage : hadoop jar ${JAR_FILE} " + RebuildIndex.class
					+ " <property_file> <index_column> <num_reduce>");
			System.exit(1);
		}

		// --------------------------------------
		// Recreate index
		// --------------------------------------

		try {
			HashMap<String, String> result = index.rebuildIndex(INDEXED_COLUMN,
					INPUT_PATH, INPUT_INDEX_PATH, REDUCE_NUMBER);
			for (Entry<String, String> entry : result.entrySet()) {
				LOGGER.info(entry.getKey() + " : " + entry.getValue());
			}
		} catch (Exception e) {
			LOGGER.error("Test rebuildIndex threw exception : "
					+ e.getMessage());
			System.exit(1);
		}

		LOGGER.info("***********************************");
		LOGGER.info("Index rebuild on " + INPUT_INDEX_PATH);
		LOGGER.info("***********************************");

	}

}