package com.github.mengxianun.core;

import java.io.InputStream;

/**
 * 翻译器, 将 JSON 翻译为 DATA
 * 
 * @author mengxiangyun
 *
 */
public interface Translator {

	/**
	 * 将 JSON 翻译为对象返回
	 * 
	 * @param json
	 *            JSON 请求字符串
	 * @return JSON格式的结果对象
	 */
	public DataResultSet translate(String json);

	/**
	 * 将 JSON 翻译为字符串返回
	 * 
	 * @param json
	 *            JSON 请求字符串
	 * @return JSON格式的结果字符串
	 */
	public String translateToString(String json);

	/**
	 * 将 JSON 翻译为流返回
	 * 
	 * @param json
	 *            JSON 请求字符串
	 * @return JSON格式的结果流
	 */
	public InputStream translateToStream(String json);

	/**
	 * 
	 * @param name
	 * @param dataContext
	 */
	public void registerDataContext(String name, DataContext dataContext);

}
