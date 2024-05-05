package com.btg.core.util.bbm25;





import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
* https://www.jianshu.com/p/1e498888f505
* https://www.cnblogs.com/jiangxinyang/p/10516302.html
* https://www.zybuluo.com/evilking/note/902621
* https://github.com/hankcs/HanLP/blob/master/src/main/java/com/hankcs/hanlp/summary/BM25.java
* https://github.com/haifengl/smile/blob/master/nlp/src/main/java/smile/nlp/relevance/BM25.java
* https://github.com/jllan/jannlp
*
* BM25 调参调研 https://www.cnblogs.com/NaughtyBaby/p/9774836.html
*
* 搜索相关性评分算法
* 计算句子与文本的相似度
*/
public class BM25Test {

  //  调节因子
  private static final double k1 = 1.5;
  private static final double b = 0.75;

  /**
   * 文本集合
   * @return
   */
  private static List<String> sentences() {
      List<String> sentences = new ArrayList<>();
      sentences.add("Elasticsearch is a highly scalable open-source full-text search and analytics engine");
      sentences.add("It allows you to search, store, search, and analyze big volumes of data quickly and in near real time");
      sentences.add("is generally used as the underlying engine/technology that powers applications that have complex search features and requirements");
      sentences.add("You want to run an online web store where you allow your customers to search for products that you sell");
      sentences.add("You want to collect log or transaction data and you want to analyze and mine this data to look for trends, statistics, summarizations, or anomalies");
      return sentences;
  }

  /**
   * 查询语句
   * @return
   */
  private static String query() {  //搜索关键词“want”，最后一个文本（句子）中含有该单词
      return "want,search";
  }

  public static void main(String[] args) {
      //建立一个数组存放相关性得分集合，并实现降序输出
	//  double[] score = {0,0,0,0,0};
	  List<String> sentences = sentences();
      // 获取N， 即总文本（总句子）数
      final int N = sentences.size();
      // avgdl文本的平均长度
      final double avgdl = avgdl(sentences);
      System.out.println("avgdl = " + avgdl);   // 获取N， 即总文本（总句子）数

      //获取查询关键词
      String query = query();
      System.out.println("查询语句 = " + query);
      //  利用ikanalyzer函数对查询内容进行分词  
      List<String> queryWords = ikanalyzer(query);
      System.out.println("查询语句分词结果 = " + queryWords);

      System.out.println("--------------------------------------------------------");

      System.out.println("文档集合 = " + sentences);
      List<List<String>> allWords = new ArrayList<>();
      //文档分词
      for(int i = 0, len = sentences.size(); i < len; i++) {
          allWords.add(i, ikanalyzer(sentences.get(i)));
      }
      System.out.println("文档集合分词结果 =" + allWords);
      System.out.println("--------------------------------------------------------");

      List<Map<String, Integer>> fs = f(allWords);
      System.out.println("文档中每个句子中的每个词与词频 = " + fs);
      System.out.println("--------------------------------------------------------");

      Map<String, Integer> nqis = nqi(allWords, queryWords);
      System.out.println("包含查询词的文本数量 = " + nqis);
      System.out.println("--------------------------------------------------------");

      Map<String, Double> idfs = idf(N, queryWords, nqis);
      System.out.println("统计每个查询词在文本集合中的权重 =  " + idfs);
      System.out.println("--------------------------------------------------------");

      for(int i = 0, len = sentences.size(); i < len; i++) {
          String sentence = sentences.get(i);
          double res = 0;
          for(String qw : queryWords) {
//              if(!sentence.contains(qw)) {
//                  continue;
//              }
              // fi为qi在文本d中出现的频率
              Double wi = idfs.get(qw);
              int fi = fs.get(i).getOrDefault(qw, 0);
//              System.out.println(sentence + " qw = " + qw + " fi = " + fi);
              double R = fi * (k1 + 1) / (fi + K(sentence, avgdl));
              res += wi * R;
          }
         // score[i]=res;  //存放相关性得分
          System.out.println("句子 = " + sentence + " 相似度 = " + res);
      }
  }


  

  /**
   * 分词
   * @return
   */
  private static List<String> ikanalyzer(String line) {
      StringReader re = new StringReader(line);
      IKSegmenter ik = new IKSegmenter(re,true);
      Lexeme lex = null;
      List<String> words = new ArrayList<>();
      try {
          while((lex = ik.next()) != null){
              String text = lex.getLexemeText();
              words.add(text);
          }
          return words;
      }catch (Exception e) {
          e.printStackTrace();
      }
      return words;
  }

  /**
   * 包含qi这个词的文本的数量
   * @return
   */
  private static Map<String, Integer> nqi(List<List<String>> allWords, List<String> queryWords) {
      Map<String, Integer> nqis = new TreeMap<>();
      for(String qw : queryWords) {
          for(List<String> aws : allWords) {
              if(aws.contains(qw)) {
                  nqis.put(qw, nqis.getOrDefault(qw, 0) + 1);
              }
          }
      }
      return nqis;
  }

  /**
   * 计算w(i)，q(i)权重
   * 词与文档的相关度
   * @return
   */
  private static Map<String, Double> idf(int N, List<String> queryWords, Map<String, Integer> nqis) {
      Map<String, Double> idfs = new HashMap<>();
      for(String qw : queryWords) {
//          System.out.println("包含 " + qw + " 的文本数量 = " + nqis.getOrDefault(qw, 0));
          double temp = (N - nqis.getOrDefault(qw, 0) + 0.5) / (nqis.getOrDefault(qw, 0) + 0.5);
          double idf = Math.log10(1 + temp);
//          System.out.println("查询词" + qw + ", idf = " + idf);
          idfs.put(qw, idf);
      }
      return idfs;
  }

  /**
   * 文档中每个句子中的每个词与词频
   * @param allWords
   * @return
   */
  private static List<Map<String, Integer>> f(List<List<String>> allWords) {
      List<Map<String, Integer>> fs = new ArrayList<>();
      for(List<String> aw : allWords) {
          Map<String, Integer> map = new TreeMap<>();
          for(String w : aw) {
              map.put(w, map.getOrDefault(w, 0) + 1);
          }
          fs.add(map);
      }
      return fs;
  }

  /**
   * 文本集D中所有文本的平均长度
   * @param sentences
   * @return
   */
  private static double avgdl(List<String> sentences) {
      double totalLen = 0.0;
      for(String sentence : sentences) {
          totalLen += sentence.length();
      }
      return totalLen / sentences.size();
  }

  private static double K(String sentence, double avgdl) {
      int dl = sentence.length();
      return k1 * (1 - b + b * dl / avgdl);
  }

}