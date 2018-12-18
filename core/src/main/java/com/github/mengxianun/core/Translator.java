package com.github.mengxianun.core;

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
	 * 将 JSON 翻译为对象返回, 同时添加过滤条件
	 * 
	 * @param json
	 * @param filterExpressions
	 * @return
	 */
	public DataResultSet translate(String json, String... filterExpressions);

	/**
	 * 注册 DataContext, 重新读取配置文件
	 * 
	 * @param name
	 * @param dataContext
	 */
	public void registerDataContext(String name, DataContext dataContext);

}
