package com.btg.core.util.bbm25;

import java.util.Hashtable;
import java.util.List;

public class Trapdoor {
	
	//查询语句映射为Q向量
	 static String query = query();
	 static List<String> queryWords = LeafNode.ikanalyzer(query);//获得查询关键词
    
	
	public static void main(String[] args) {
		//统计每个关键词的文档数，计算每个查询关键词的IDF值
//	     Hashtable Query = new Hashtable();
//	     Query.putAll(LeafNode.DataVector);  //获得数据向量的格式
//	     System.out.println("查询向量的格式 = " + Query);
//	     for(int i=0;i<Query.size();i++) {
//	    	 if(Query.get(LeafNode.wordslist)== queryWords) {
//	    		 Query.put(queryWords, words_lf);
//	    	 }
//	     }
		 try {
			System.out.println("查询语句分词结果 = " + queryWords);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	/**
	 * 查询语句
	 * @return 
	 * 
	 */
	private static String query() {  //搜索关键词“want”，最后一个文本（句子）中含有该单词
	      return "want,search,lucky";
	  }
	 
}
