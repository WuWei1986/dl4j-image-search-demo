package com.example.core.datasource;

/**
 * @author wuwei
 *
 */
public class DatasourceHolder {
	private static final ThreadLocal<String> datasourceKey = new ThreadLocal<String>();

	public static void setDatasourceKey(String key) {
		datasourceKey.set(key);
	}

	public static void setMaster(String datasource) {
		datasourceKey.set(datasource + "-master");
	}

	public static void setSlave(String datasource) {
		datasourceKey.set(datasource + "-slave1");
	}

	public static String getDatasourceKey() {
		return (String) datasourceKey.get();
	}
}
