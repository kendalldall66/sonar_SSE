package com.btg.core.util.bbm25;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random; 
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;//
import java.util.concurrent.CountDownLatch;//
import java.util.concurrent.ExecutorService;//
import java.util.concurrent.Executors;//
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ejml.simple.SimpleMatrix;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
/**
 * @author 20680
 *
 */


//定义节点类
public class LeafNode2 {
	//  调节因子
	private static final double k1 = 1.5;
	private static final double b = 0.75;
	
	private int id = 0;// 节点id
	private Vector<Double> D = new Vector<>(); // 构建数据向量  
	private static Hashtable DataVector = new Hashtable(); // 构建向量（字典类型保存数据向量）
//	private int Lf = 0;  // 该节点文档字符总数
//	private static int Lave = 0;  // 所有节点文档的平均字符数
	private LeafNode2 Lchild = null;
	private LeafNode2 Rchild = null;
	private String Fid = null;

	// 构造函数,创建节点时自动生成id
	public LeafNode2() {
		Random rand1 = new Random();
		id = Math.abs(rand1.nextInt(1000)); // 0-1000之间的随机数
	}

	// 给节点编号
	public int setID() {
		// TODO Auto-generated method stub

		Random rand1 = new Random();
		id = Math.abs(rand1.nextInt(1000)); // 0-1000之间的随机数
		return id;
	}
	
	//为向量构建大小
	
	// 给数据向量赋值
	public Vector<Double> setD(double data[]) {
		// Vector<String> D = new Vector<>();
		for (int i = 0; i < data.length; i++) {
//			D.add(Double.toString(data[i]));
//			D.add(2, "0.5362");
		}
		return D;
	}

	// 设置节点的指针指向 左孩子
	public LeafNode2 setLchild() {
		// 若节点为叶子节点
		Lchild = null;
		return Lchild;
	}

	// 设置节点的指针指向 右孩子
	public LeafNode2 setRchild() {
		Rchild = null;
		return Rchild;
	}
	
	static int nodecount;
	
	
	// 主函数
	public static void main(String[] args) throws Exception {	
		
	
		//运行开始时间
		long stime1 = System.currentTimeMillis();
		
		String path = "D:\\PostGraduate\\MyPapers\\SSE草稿\\experiment\\RFC\\RFC\\RFC9056";// 我要遍历指定地址下的文件
		int fileAmount = 1000;// 构建关键词使用的文件数量
		//第一次遍历所有文件，计算文档字符数Lcount，构建关键词向量格式
		double Lcount = 0.0;// 所有文档字符数
		Hashtable dataVector = new Hashtable(); // 构建向量（字典类型保存数据向量）
		Map<String, Integer> Word_Count_Dictionary = new TreeMap<String, Integer>();// 构建字典（包含word词的文件个数）
	/////************************************////	
		Map<String, Integer> wordCountDictionary = new TreeMap<String, Integer>();//&&&&&&&&&
		
		Long start2 = System.currentTimeMillis();
		CountDownLatch countDownLatch = new CountDownLatch(fileAmount);//******
		 //2涓嚎绋 
        ExecutorService executorService = Executors.newFixedThreadPool(20);//******
        final Map<List<String>,Integer> wordsLf = new ConcurrentHashMap();//******
	
        for (int i = 1; i <= fileAmount; i++) {
			String fileName = new StringBuilder(path ).append("rfc" ).append(i).append(".txt").toString();// 鑾峰彇鏂囦欢鍚 
//			Map<List<String>,Integer> words_lf = Words_Lf(path + fileName);// 查询文档中的非停用词，和文档字符数
			executorService.submit(() -> {
			      try {
			    	  Words_Lf(wordsLf + fileName);
			    	  Map<List<String>,Integer> words_lf = Words_Lf(path + fileName);// 查询文档中的非停用词，和文档字符数
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
				  	System.out.println(Thread.currentThread().getName());
	                countDownLatch.countDown();
				}
            });
		
    	countDownLatch.await();
    	Set<List<String>> wordsList = wordsLf.keySet();
		List<String> wordslist = (List<String>) wordsList.toArray()[0];
//		System.out.println("wordsList锛 " + wordslist);
		for (String word:wordslist) {
			if (dataVector.get(word) == null) {
				dataVector.put(word, 0.0);
				wordCountDictionary.put(word, 1);
			} else {
				wordCountDictionary.put(word, wordCountDictionary.get(word) + 1);
			}
		}
		// 遍历计算所有文档字符数
					Collection<Integer> Lfs = wordsLf.values();
					Iterator<Integer> lfs = Lfs.iterator();
					while(lfs.hasNext()){ //判断是否有迭代元素
			            Integer s = lfs.next();//获取迭代出的元素
			            Lcount += s;
//			            System.out.println("第" + i + "个文件的字符数Lf=" + s);
			        }
			}
 
		
        
        
/* *******
		for (int i = 1; i <= fileAmount; i++) {
			String fileName = "rfc" + i + ".txt";// 获取文件名
			Map<List<String>,Integer> words_lf = Words_Lf(path + fileName);// 查询文档中的非停用词，和文档字符数
			//遍历所有文件构造节点向量格式,获取关键字集及映射的数据向量（向量大小）
			Set<List<String>> wordsList = words_lf.keySet();
			List<String> wordslist = (List<String>) wordsList.toArray()[0];
//			System.out.println("wordsList：" + wordslist);
			for(String word:wordslist) {
				if (dataVector.get(word) == null) {
					dataVector.put(word, 0.0);
					Word_Count_Dictionary.put(word, 1);
				} else {
					Word_Count_Dictionary.put(word, Word_Count_Dictionary.get(word) + 1);
				}
			}
			
			
			// 遍历计算所有文档字符数
			Collection<Integer> Lfs = words_lf.values();
			Iterator<Integer> lfs = Lfs.iterator();
			while(lfs.hasNext()){ //判断是否有迭代元素
	            Integer s = lfs.next();//获取迭代出的元素
	            Lcount += s;
//	            System.out.println("第" + i + "个文件的字符数Lf=" + s);
	        }
		}
********/
        
        
        
        
//		System.out.println("Word_Count_Dictionary：" + Word_Count_Dictionary);
		

		double Lave = Lcount / fileAmount;
//		System.out.println("数据向量格式：" + dataVector);
//		System.out.println("数据向量大小：" + dataVector.size());
//		System.out.println("所有文档字符总数Lcount:" + Lcount);
//		System.out.println("所有文档平均字符数Lave:" + Lave);
		
		System.out.println("原数据向量大小：" + dataVector.size());
		// 数据向量去低频词
		int Low_frequency_word_counter = 0;
		for(String word : Word_Count_Dictionary.keySet() ) {
//			double lf = fileAmount*0.1;
			double lf = 42;
			// 记录低频词的数量，并将其从数据向量中移除
			if (Word_Count_Dictionary.get(word) <= lf) {
				Low_frequency_word_counter += 1;
				dataVector.remove(word);
			}
		}
		System.out.println("低频单词个数：" + Low_frequency_word_counter);
		System.out.println("去低频词后的数据向量大小：" +  dataVector.size());
					
		// 创建足够多的叶子节点用于构造二叉树，构建count个叶子节点
		int count = 1024;// 构建树使用的节点数量
		CountDownLatch countDownLatch2 = new CountDownLatch(count);//&&&&&&&&&&&
		LeafNode2[] LeafNodes = new LeafNode2[count];// 创建用于存放叶子节点对象的数组
		
		Long start3 = System.currentTimeMillis();
////////&&&&&&&&&&&&&&&&&
		for (int i = 0; i < count; i++){
			LeafNode2 leafnode = new LeafNode2();// 
//			System.out.println("褰撳墠鍙跺瓙鑺傜偣id:" + leafnode.id);
			String fileName = new StringBuilder(path).append("rfc" ).append(i+1).append(".txt").toString();// 鑾峰彇鏂囦欢鍚 
			// 鍒ゆ柇璇ユ枃浠舵槸鍚﹀瓨鍦紝瀛樺湪鍒欒绠楀彾瀛愯妭鐐瑰悜閲忕殑鏁板   S(w,f) 骞跺皢鏁版嵁璧嬩簣鍙跺瓙鑺傜偣
			//File file = new File(fileName);
			
//			if(file.exists()) {
			final int tempI = i;
//				System.out.printf("瀛樺湪%s鏂囦欢\n" , fileName);
				  executorService.submit(() -> {
				     try {
				    	 if(tempI<6000) {   //鏋勫缓鏍戜娇鐢ㄧ殑鏂囦欢鏁伴噺***********************************************************
				    	 Map<Map<String, Double>, Integer> tfws_L = new ConcurrentHashMap<Map<String,Double>, Integer>();
				    	 Cal_tfw(tfws_L,fileName);// 鑾峰彇璇ユ枃浠剁殑璇嶉瀛楀吀 tfws 鍜屽瓧绗︽暟 Lf
				    	// 鑾峰彇褰撳墠鏂囨。瀛楃鏁癓f
							double  Lf = 0.0;
							Collection<Integer> ColLf = tfws_L.values();
							Iterator<Integer> Itlf = ColLf.iterator();
							while(Itlf.hasNext()){ //鍒ゆ柇鏄惁鏈夎凯浠ｅ厓绱 
								Lf = Itlf.next();//鑾峰彇杩唬鍑虹殑鍏冪礌
					        }	
//							System.out.println("褰撳墠鏂囨。瀛楃鏁癓f=" + Lf);
							
							// 鑾峰彇璇ユ枃浠剁殑璇嶉瀛楀吀 tfw
							Set<Map<String, Double>> Settfw = tfws_L.keySet();
							Map<String, Double> tfws = Settfw.iterator().next();// 鑾峰彇璇嶉瀛楀吀 tfws
//							System.out.println("璇嶉瀛楀吀tfws:" + tfws);
//							System.out.println("璇嶉瀛楀吀澶у皬:" + tfws.size());
//							System.out.println();
							
							// 閬嶅巻璇嶉瀛楀吀璁＄畻鍚戦噺鐨勬暟鍊  S(w,f)
							DataVector.putAll(dataVector);// 涓鸿妭鐐规暟鎹悜閲忓垱寤烘牸寮 
//							System.out.println("璇嶉瀛楀吀澶у皬:" + tfws.size());
							for(String word:tfws.keySet()) {
								// 鍙负鏁版嵁鍚戦噺涓瓨鍦ㄧ殑璇嶈绠梥wf
								if (DataVector.get(word) != null) {
									double tfw = tfws.get(word);
									double swf = ((k1+1)*tfw)/(k1*(1-b+b*(Lf/Lave)));
//									System.out.println("鏁版嵁Di swf:" + swf);
									DataVector.put(word, swf);
								}
							}
							leafnode.D.addAll(DataVector.values());//灏哾ataVector涓殑swf鍊  浼犻 掔粰D鍚戦噺
							leafnode.Fid = fileName;
							LeafNodes[tempI] = leafnode;//灏嗗彾瀛愯妭鐐瑰瓨鍏ユ暟缁勪腑锛岀敤浜庝箣鍚庝簩鍙夋爲鐨勬瀯寤 
//							System.out.println("leafnode.D鐨勫ぇ灏忥細"+leafnode.D.size());
//							System.out.println("leafnode.dataVector鐨勫ぇ灏忥細"+leafnode.dataVector.size());
//							System.out.println("杈撳嚭鍙跺瓙鑺傜偣鍐呭锛  id:" + LeafNodes[i].id +" Fid:"+ LeafNodes[i].Fid + " D:"+ LeafNodes[i].D + " ");
							
							
						}else {
//							System.out.printf("涓嶅瓨鍦 %s鏂囦欢\n" , fileName);
							DataVector.putAll(dataVector);// 涓鸿妭鐐规暟鎹悜閲忓垱寤烘牸寮 
							leafnode.D.addAll(DataVector.values());//灏哾ataVector涓殑swf鍊  浼犻 掔粰D鍚戦噺
							LeafNodes[tempI] = leafnode;//灏嗗彾瀛愯妭鐐瑰瓨鍏ユ暟缁勪腑锛岀敤浜庝箣鍚庝簩鍙夋爲鐨勬瀯寤 
							
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
					  	System.out.println(Thread.currentThread().getName());
					  	countDownLatch2.countDown();
					}
	            });
		}
		countDownLatch2.await();
//		System.out.println("DataVector鐨勫ぇ灏忥細"+DataVector.size());
//		System.out.println("宸插垱寤虹殑鍙跺瓙鑺傜偣涓暟锛 " + LeafNodes.length);
		System.out.println("start3: " + (System.currentTimeMillis() - start3));
		
		
/********************		
		// 循环构建count个叶子节点
		for (int i = 0; i < count; i++){
			LeafNode2 leafnode = new LeafNode2();// 新建叶子节点
//			System.out.println("当前叶子节点id:" + leafnode.id);
			String fileName = "rfc" + (i+1) + ".txt";// 获取文件名
			// 判断该文件是否存在，存在则计算叶子节点向量的数值 S(w,f) 并将数据赋予叶子节点
			File file = new File(path + fileName);
//			if(file.exists()) {
			if(i<1) {   //构建树使用的文件数量
//				System.out.printf("存在%s文件\n" , fileName);
				Map<Map<String, Double>, Integer> tfws_L = Cal_tfw(path + fileName);// 获取该文件的词频字典 tfws 和字符数 Lf
				// 获取当前文档字符数Lf
				double  Lf = 0.0;
				Collection<Integer> ColLf = tfws_L.values();
				Iterator<Integer> Itlf = ColLf.iterator();
				while(Itlf.hasNext()){ //判断是否有迭代元素
					Lf = Itlf.next();//获取迭代出的元素
		        }	
//				System.out.println("当前文档字符数Lf=" + Lf);
				
				// 获取该文件的词频字典 tfw
				Set<Map<String, Double>> Settfw = tfws_L.keySet();
				Map<String, Double> tfws = Settfw.iterator().next();// 获取词频字典 tfws
//				System.out.println("词频字典tfws:" + tfws);
//				System.out.println("词频字典大小:" + tfws.size());
//				System.out.println();
				
				// 遍历词频字典计算向量的数值 S(w,f)
				DataVector.putAll(dataVector);// 为节点数据向量创建格式
//				System.out.println("词频字典大小:" + tfws.size());
				for(String word:tfws.keySet()) {
					// 只为数据向量中存在的词计算swf
					if (DataVector.get(word) != null) {
						double tfw = tfws.get(word);
						double swf = ((k1+1)*tfw)/(k1*(1-b+b*(Lf/Lave)));
//						System.out.println("数据Di swf:" + swf);
						DataVector.put(word, swf);
					}
				}
				leafnode.D.addAll(DataVector.values());//将dataVector中的swf值 传递给D向量
				leafnode.Fid = fileName;
				LeafNodes[i] = leafnode;//将叶子节点存入数组中，用于之后二叉树的构建
//				System.out.println("leafnode.D的大小："+leafnode.D.size());
//				System.out.println("leafnode.dataVector的大小："+leafnode.dataVector.size());
//				System.out.println("输出叶子节点内容： id:" + LeafNodes[i].id +" Fid:"+ LeafNodes[i].Fid + " D:"+ LeafNodes[i].D + " ");
			
				
			}else {
//				System.out.printf("不存在%s文件\n" , fileName);
				DataVector.putAll(dataVector);// 为节点数据向量创建格式
				leafnode.D.addAll(DataVector.values());//将dataVector中的swf值 传递给D向量
				LeafNodes[i] = leafnode;//将叶子节点存入数组中，用于之后二叉树的构建
				
			}
		}
*****************/
//		System.out.println("DataVector的大小："+DataVector.size());
//		System.out.println("已创建的叶子节点个数：" + LeafNodes.length);
		
		//构建二叉树
		LeafNode2 HeadNode = new LeafNode2();
		CreatBTree(HeadNode,LeafNodes);
//		System.out.println("HeadNode:"+HeadNode.D);
		
		
		//加密索引二叉树，即加密数据向量D
		Vector<Integer> S = new Vector<>();
		S = setS();
//		System.out.println("密钥向量S：" + S);
		
		
		long etime1 = System.currentTimeMillis();
//		System.out.printf("执行时长：%d毫秒", (etime1-stime1));
		//自定义可逆矩阵M1，M2
		BaseMatrix M1 = setMatrix(dataVector.size(),dataVector.size());
		BaseMatrix M2 = setMatrix(dataVector.size(),dataVector.size());
		long stime2 = System.currentTimeMillis();
					
		//
		if(M1 != null && M2 != null) {//&&&&&&&&&&加了一个判断
			EncryptNode Encry_headNode =new EncryptNode();
			EncrytBTree(S,M1,M2, HeadNode,Encry_headNode);
		}
		
		
		//运行结束时间，输出运行总时长
		System.out.println();
		long etime2 = System.currentTimeMillis();
		System.out.printf("执行时长：%d毫秒", (etime1-stime1)+(etime2-stime2));
		executorService.shutdown();	//&&&&&&&关闭	
	}
	
	
	/**
	 * 根据密钥S,M1,M2分裂树中所有D向量，构建加密树，返回加密树的头节点
	 * @return 
	 * 
	 */
	private static void EncrytBTree(Vector<Integer> s, BaseMatrix m1, BaseMatrix m2, LeafNode2 Node,
			EncryptNode encry_Node) {
		nodecount++;
		System.out.println("nodecount:"+ nodecount);
		Collection<Vector<Double>> Iu = new Vector<>() ;// 计算Iu={m1Td1，m2Td2}
		Vector<Double> d1 = new Vector<>();
		Vector<Double> d2 = new Vector<>();
		Random rand = new Random();
		// S分裂D 
		for(int i=0;i<s.size();i++) {
			int ss = s.get(i);
			if(ss != 1 | Node.D.get(i)==0) {  //S[i]=0 
				d1.add(Node.D.get(i));
				d2.add(Node.D.get(i));
			}
			else {//S[i]=1  d1[i]+d2[i]=d[i]
//				System.out.println("Node.D.get(i):"+Node.D.get(i));
				double a = Math.abs(rand.nextDouble(Node.D.get(i)));
				double b = Node.D.get(i)-a;
				d1.add(a);
				d2.add(b);
			}
		}
		// 矩阵加密D1和D2
		BaseMatrix m1T = m1.reverse();// 转置m1矩阵
		BaseMatrix m2T = m2.reverse();// 转置m2矩阵
		
//		BaseMatrix m1_1 =  m1.invert();//求逆矩阵
		// 计算Iu={m1Td1，m2Td2}
		Iu.add(m1T.mult(d1));
		Iu.add(m2T.mult(d2));
		
		// 创建加密树节点
		EncryptNode e_lnode = new EncryptNode();
		EncryptNode e_rnode = new EncryptNode();
		
		// 为加密树节点添加数据
		encry_Node.setIu(Iu);
		if(Node.Lchild==null&Node.Rchild==null) {// 左右子节点为空，则为叶子节点
			encry_Node.setFid(Node.Fid);// 为叶子节点加入Fid
		}
		encry_Node.setLchild(e_lnode);
		encry_Node.setRchild(e_rnode);
//		System.out.println("encry_Node.Iu:"+encry_Node.getIu());
		
		//递归构建加密树
		if(Node.Lchild!=null){
			EncrytBTree(s,m1,m2, Node.Lchild,e_lnode);
		}
		if(Node.Lchild!=null){
			EncrytBTree(s,m1,m2, Node.Rchild,e_rnode);
		}
						
	}

	/**
	 * 根据密钥S,M1,M2分裂D向量，返回[M1D1,M2D2]向量  Note: unused.
	 * @return 
	 * 
	 */
	private static Collection<Vector<Double>> SplitD1(Vector<Integer> s, BaseMatrix m1, BaseMatrix m2, Vector<Double> d) {
		Collection<Vector<Double>> ColD = new Vector<>() ;  //存放{d1,d2}
		Vector<Double> d1 = new Vector<>();
		Vector<Double> d2 = new Vector<>();
		Random rand = new Random();
		// S分裂D 
		for(int i=0;i<s.size();i++) {
			int ss = s.get(i);
			if(ss != 1 ) {  //S[i]=0 
				d1.add(d.get(i));
				d2.add(d.get(i));
			}
			else {//S[i]=1  d1[i]+d2[i]=d[i]
				double a = Math.abs(rand.nextDouble(d.get(i)));
				double b = d.get(i)-a;
				d1.add(a);
				d2.add(b);
			}
		}
		// 矩阵加密D1和D2
		BaseMatrix m1T = m1.reverse();// 转置m1矩阵
		BaseMatrix m2T = m2.reverse();// 转置m2矩阵
		
//		BaseMatrix m1_1 =  m1.invert();//求逆矩阵
		// 计算Iu={m1Td1，m2Td2}
		ColD.add(m1T.mult(d1));
		ColD.add(m2T.mult(d2));
		return ColD;
	}

	
	

	/**
	 * 自定义可逆矩阵M1,M2（密钥向量） 
	 * 输入行数和列数，Random随机生成所有值
	 * @return 
	 */
	public static BaseMatrix setMatrix(int column, int row) {
		double [][] m = new double[column][row];
		if(m.length == 0){  //&&&&加了判断
			return null;
		}
		int col,rw; //分别声明矩阵的行数和列数
		BaseMatrix matrix = null;
//		Scanner s=new Scanner(System.in); //终端输入矩阵中的值
		Random rand3 = new Random();
		//构建可逆矩阵
		boolean isinvertible = false;
		long stime = System.currentTimeMillis();
		int count = 0;
		while(!isinvertible) {//判断是否可逆，不可逆重新构建矩阵
			for(int i=0;i<column;i++) {
				for(int j=0;j<row;j++) {
					double r = Math.abs(rand3.nextDouble(2000)-1000);
					m[i][j] = r;
				}
			}
			// 计算构建一次矩阵的耗时
			if(count ==1) {
				long etime = System.currentTimeMillis();
				System.out.printf("构建可逆矩阵执行时长：%d毫秒\n", (etime-stime));
			}
			
			//判断矩阵是否可逆
			matrix = new BaseMatrix(m);
			isinvertible = matrix.IsInvertible(column);
			count++;
		}
//		matrix.show();
		
		return matrix;
	}

	/**
	 * 生成随机二进制串S（密钥向量） 
	 * 
	 * @return 
	 */
	public static Vector<Integer> setS() {
		Vector<Integer> SV = new Vector<>();
		Random rand2 = new Random();	
		int s;
		for(int i=0;i< DataVector.size();i++)  //如何设置 使得密钥向量S的大小与数据向量D的大小一致
			{
				s=(int)Math.abs(rand2.nextInt(2)); // 0-1
				SV.add(s);
			}
		return SV;
	}

	/**
	 * 输入叶子节点数组leafNodes和头节点headNode，构建二叉树
	 * 
	 * @return 
	 */
	private static void CreatBTree(LeafNode2 headNode, LeafNode2[] leafNodes) {
		//如果列表中的中间节点不止1个节点，则两两节点合并，构建上一层中间节点
		if(leafNodes.length > 1) {
			LeafNode2[] midNodes = new LeafNode2[(leafNodes.length)/2];
			for(int i=0;i<leafNodes.length;i=i+2) {
				LeafNode2 lNode = leafNodes[i];//获取左节点
				LeafNode2 rNode = leafNodes[i+1];//获取右节点
				
				int count = lNode.D.size();
				// 剩下最后两个节点，直接指向头节点；否则创建中间节点连接左右字节点
				if(leafNodes.length == 2) {
					//通过左右节点的数据D计算中间节点的数据D
					for(int j = 0;j<count;j++) {
						double Di ; 
						if(lNode.D.get(j) > rNode.D.get(j)) {
							Di = lNode.D.get(j);
						}else {
							Di = rNode.D.get(j);
						}
						headNode.D.add(Di);
					}
					headNode.Lchild=lNode;// 连接左节点
					headNode.Rchild=rNode;// 连接右节点
				}else {
					//通过左右节点的数据D计算中间节点的数据D
					LeafNode2 midNode = new LeafNode2();
					for(int j = 0;j<count;j++) {
						double Di ; 
//						System.out.println(j);
//						System.out.println(count);
						if(lNode.D.get(j) > rNode.D.get(j)) {
							Di = lNode.D.get(j);
						}else {
							Di = rNode.D.get(j);
						}
						midNode.D.add(Di);
					}
					midNode.Lchild=lNode;// 连接左节点
					midNode.Rchild=rNode;// 连接右节点
//					System.out.println("midNode.D:"+i+midNode.D);
					midNodes[i/2]=midNode;// 将中间节点放入数组中用于递归构建上一次中间节点
				}
				
			}
			System.out.println(" ");
//			System.out.println(midNodes);
			CreatBTree(headNode,midNodes);
		}
	}

	
	/**
	 * 输入文档位置 fileName，输出该文档中的非停用词词频字典 tfw 和该文档字符数 Lf
	 * 新添加方法**********
	 * @return
	 * @throws IOException 
	 */
	private static void Cal_tfw(Map<Map<String, Double>, Integer> tfws_L,String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> lists_all1 = new ArrayList<String>(); // 瀛樺偍鏂囦欢涓殑鎵�鏈夎嫳鏂囧崟璇嶇殑鍒楄〃
		int Lf = 0; // 瀛樺偍鎵�鏈夊瓧绗︾殑涓暟
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
//			String regex1 = "[a-zA-Z]+(\\-)?[a-zA-Z]*";// 鐢ㄤ簬鍖归厤鑻辨枃鍗曡瘝鐨勬鍒欒〃杈惧紡
			String regex1 = "[a-zA-Z]+(\\-[a-zA-Z]+)?";// 鐢ㄤ簬鍖归厤鑻辨枃鍗曡瘝鐨勬鍒欒〃杈惧紡
			List<String> wordsArr1 = getMatchers(regex1, readLine);//鍖归厤璇ヨ鐨勮嫳鏂囧崟璇嶏紝淇濆瓨鍦ㄥ垪琛�
			if(readLine.length() != 0) {
				Lf += readLine.replaceAll(" ","").length();
			}
			for (String word : wordsArr1) {
				if (word.length() != 0) { // 鍘婚櫎闀垮害涓�0鐨勮
					lists_all1.add(word);
				}
			}
		}
		br.close();
		
		int N = lists_all1.size();// 缁熻璇xt鏂囦欢涓殑鎬诲崟璇嶆暟
		
		// 鍙彇鏈夋晥鍗曡瘝瀛樺叆鍒楄〃 lists 涓�
		String sentences = String.join(" ", lists_all1);// 鎷兼帴瀛楃涓插垪琛ㄤ负涓�涓瓧绗︿覆
		List<String> lists = ikanalyzer(sentences);
		
		Map<String, Integer> wordsCount = new TreeMap<String, Integer>(); // 瀛樺偍鍗曡瘝璁℃暟淇℃伅锛宬ey鍊间负鍗曡瘝锛寁alue涓哄崟璇嶆暟
		// 鍗曡瘝鐨勮瘝棰戯紙鍗曡瘝鍑虹幇娆℃暟锛夌粺璁★紝瀛楀吀褰㈠紡淇濆瓨鍦╳ordsCount涓�
		for (String li : lists) {
			if (wordsCount.get(li) != null) {
				wordsCount.put(li, wordsCount.get(li) + 1);
			} else {
				wordsCount.put(li, 1);
			}

		}
		
		//璇嶉锛堝崟璇嶅湪鎵�鍦ㄦ枃妗ｄ腑鐨勫崰姣旓級鐨勮绠楋紝瀛楀吀褰㈠紡淇濆瓨鍦╳ordsFre涓�
		Map<String, Double> wordsFre = new HashMap<String, Double>(); // 瀛樺偍鍗曡瘝璇嶉淇℃伅锛宬ey鍊间负鍗曡瘝锛寁alue涓哄崟璇嶅湪璇ユ枃妗ｄ腑鐨勯鐜�
		Set<Entry<String, Integer>> set = wordsCount.entrySet();
		for (Entry<String, Integer> entry : set) {
	         String key = entry.getKey();
	         Double value = (double)entry.getValue();
//	         System.out.println(v);
//	         System.out.println(N);
	         Double tfw = value/N;// 璁＄畻璇嶉
	         wordsFre.put(key, tfw);
	      }
		
		//璇嶉 + 璇ユ枃妗ｅ瓧绗︽�绘暟
		//Map<Map<String, Double>,Integer> tfw_L = new HashMap<>();
		tfws_L.put(wordsFre, Lf);
		//return tfw_L;
	}
	
	
	/**
	 * 输入文档位置 fileName，输出该文档中的非停用词词频字典 tfw 和该文档字符数 Lf
	 * 
	 * @return
	 * @throws IOException 
	 */
	private static Map<Map<String, Double>, Integer> Cal_tfw(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> lists_all1 = new ArrayList<String>(); // 存储文件中的所有英文单词的列表
		int Lf = 0; // 存储所有字符的个数
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
//			String regex1 = "[a-zA-Z]+(\\-)?[a-zA-Z]*";// 用于匹配英文单词的正则表达式
			String regex1 = "[a-zA-Z]+(\\-[a-zA-Z]+)?";// 用于匹配英文单词的正则表达式
			List<String> wordsArr1 = getMatchers(regex1, readLine);//匹配该行的英文单词，保存在列表
			if(readLine.length() != 0) {
				Lf += readLine.replaceAll(" ","").length();
			}
			for (String word : wordsArr1) {
				if (word.length() != 0) { // 去除长度为0的行
					lists_all1.add(word);
				}
			}
		}
		br.close();
		
		int N = lists_all1.size();// 统计该txt文件中的总单词数
		
		// 只取有效单词存入列表 lists 中
		String sentences = String.join(" ", lists_all1);// 拼接字符串列表为一个字符串
		List<String> lists = ikanalyzer(sentences);
		
		Map<String, Integer> wordsCount = new TreeMap<String, Integer>(); // 存储单词计数信息，key值为单词，value为单词数
		// 单词的词频（单词出现次数）统计，字典形式保存在wordsCount中
		for (String li : lists) {
			if (wordsCount.get(li) != null) {
				wordsCount.put(li, wordsCount.get(li) + 1);
			} else {
				wordsCount.put(li, 1);
			}

		}
		
		//词频（单词在所在文档中的占比）的计算，字典形式保存在wordsFre中
		Map<String, Double> wordsFre = new HashMap<String, Double>(); // 存储单词词频信息，key值为单词，value为单词在该文档中的频率
		Set<Entry<String, Integer>> set = wordsCount.entrySet();
		for (Entry<String, Integer> entry : set) {
	         String key = entry.getKey();
	         Double value = (double)entry.getValue();
//	         System.out.println(v);
//	         System.out.println(N);
	         Double tfw = value/N;// 计算词频
	         wordsFre.put(key, tfw);
	      }
		
		//词频 + 该文档字符总数
		Map<Map<String, Double>,Integer> tfw_L = new HashMap<>();
		tfw_L.put(wordsFre, Lf);
		return tfw_L;
	}
	

	/**
	 * 输入文档位置 fileName，输出该文档中的非停用词列表wordsList 和文档字符数Lf
	 * 
	 * @return
	 * @throws IOException 
	 */
	private static Map<List<String>, Integer> Words_Lf(String fileName) throws IOException {
		Map<List<String>, Integer> wordsList = new HashMap<List<String>, Integer>(); // 存储单词计数信息，key值为单词，value为单词数
		//找到所有英文单词存入列表 lists_all 中
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> lists_all1 = new ArrayList<String>(); // 存储过滤后单词的列表
		int Lf = 0; // 存储所有字符的个数
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
			String regex1 = "[a-zA-Z]+(\\-[a-zA-Z]+)?";// 用于匹配英文单词的正则表达式
//			String regex1 = "[a-zA-Z]+(\\-)?[a-zA-Z]*";// 用于匹配英文单词的正则表达式
			List<String> wordsArr1 = getMatchers(regex1, readLine);//匹配该行的英文单词，保存在列表
			if(readLine.length() != 0) {
				Lf += readLine.replaceAll(" ","").length();
			}
			for (String word : wordsArr1) {
				if (word.length() != 0) { // 去除长度为0的行
					lists_all1.add(word);
				}
			}
		}
//		System.out.println("Lf:" + Lf);
		
		// 只取有效单词存入列表 lists 中
		String sentences = String.join(" ", lists_all1);// 拼接字符串列表为一个字符串
		List<String> lists = ikanalyzer(sentences);
//				System.out.println(sentences);
//		System.out.println("有效单词列表:" + lists);
		
		wordsList.put(lists, Lf);
		return wordsList;
	}
	private static Map<List<String>, Integer> Words_Lf2(String fileName) throws IOException {
		Map<List<String>, Integer> wordsList = new HashMap<List<String>, Integer>(); // 存储单词计数信息，key值为单词，value为单词数
		//找到所有英文单词存入列表 lists_all 中
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> lists_all1 = new ArrayList<String>(); // 存储过滤后单词的列表
		int Lf = 0; // 存储所有字符的个数
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
			String regex1 = "[a-zA-Z]+(\\-[a-zA-Z]+)?";// 用于匹配英文单词的正则表达式
			List<String> wordsArr1 = getMatchers(regex1, readLine);//匹配该行的英文单词，保存在列表
			if(readLine.length() != 0) {
				Lf += readLine.replaceAll(" ","").length();
			}
			for (String word : wordsArr1) {
				if (word.length() != 0) { // 去除长度为0的行
					lists_all1.add(word);
				}
			}
		}
//		System.out.println("Lf:" + Lf);
		
		// 只取有效单词存入列表 lists 中
		String sentences = String.join(" ", lists_all1);// 拼接字符串列表为一个字符串
		List<String> lists = ikanalyzer(sentences);
//				System.out.println(sentences);
//		System.out.println("有效单词列表:" + lists);
		
		wordsList.put(lists, Lf);
		return wordsList;
	}

	/**
	 * 输入文档位置 fileName，输出该文档中的单词词频字典 WordFre
	 * 
	 * @return
	 */
	public static Map<Map<String, Double>, Integer> WordCount(String fileName) throws Exception {
//		BufferedReader br = new BufferedReader(new FileReader("D:\\PostGraduate\\要毕业\\SSE\\SSE草稿\\experiment\\RFC\\RFC\\RFCtxt(50)\\rfc53.txt"));  
		//找到所有英文单词存入列表 lists_all 中
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> lists_all1 = new ArrayList<String>(); // 存储过滤后单词的列表
		int Lf = 0; // 存储所有字符的个数
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
			String regex1 = "[a-zA-Z]+(\\-)?[a-zA-Z]*";// 用于匹配英文单词的正则表达式
			List<String> wordsArr1 = getMatchers(regex1, readLine);//匹配该行的英文单词，保存在列表
			if(readLine.length() != 0) {
				Lf += readLine.replaceAll(" ","").length();
			}
			for (String word : wordsArr1) {
				if (word.length() != 0) { // 去除长度为0的行
					lists_all1.add(word);
				}
			}
		}
//		System.out.println("Lf:" + Lf);
//		System.out.println(lists_all);
		br.close();
		
		int N = lists_all1.size();// 统计该txt文件中的总单词数，即文档长度
//      System.out.println("文档总单词数：" +N);
		
		// 只取有效单词存入列表 lists 中
		String sentences = String.join(" ", lists_all1);// 拼接字符串列表为一个字符串
		List<String> lists = ikanalyzer(sentences);
//		System.out.println(sentences);
//		System.out.println("有效单词列表:" + lists);
		
		Map<String, Integer> wordsCount = new TreeMap<String, Integer>(); // 存储单词计数信息，key值为单词，value为单词数
		// 单词的词频（单词出现次数）统计，字典形式保存在wordsCount中
		for (String li : lists) {
			if (wordsCount.get(li) != null) {
				wordsCount.put(li, wordsCount.get(li) + 1);
			} else {
				wordsCount.put(li, 1);
			}

		}
		
		//词频（单词在所在文档中的占比）的计算，字典形式保存在wordsFre中
		Map<String, Double> wordsFre = new HashMap<String, Double>(); // 存储单词词频信息，key值为单词，value为单词在该文档中的频率
		Set<Entry<String, Integer>> set = wordsCount.entrySet();
		for (Entry<String, Integer> entry : set) {
	         String key = entry.getKey();
	         Double value = (double)entry.getValue();
//	         System.out.println(v);
//	         System.out.println(N);
	         Double tfw = value/N;// 计算词频
//	         System.out.println(key + " : " + tfw);
	         wordsFre.put(key, tfw);
	      }
		
		//词频 + 该文档字符总数
		Map<Map<String, Double>,Integer> S_w_f = new HashMap<>();
		S_w_f.put(wordsFre, Lf);
//		System.out.println(wordsFre);
		
		//词频（单词在所在文档中的占比）的计算，字典形式保存在wordsFre中
		//另一种方法
		/*
		Set<String> set = wordsCount.keySet();
	      for (String s : set) {
	         String key = s;
	         Integer value = wordsCount.get(s);
	         System.out.println(key + " : " + value);
	      }
	      */
		
		
		return S_w_f;
	}

	// 匹配source（字符串）中的所有英文单词，并按字符串数组输出
	public static List<String> getMatchers(String regex, String source) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		List<String> list = new ArrayList<>();
		while (matcher.find()) {
			list.add(matcher.group());

		}
//	      System.out.println(list);
		return list;
	}

	/**
	 * 分词:输入字符串（一句话），输出一个单词列表（有效单词）
	 * 
	 * @return
	 */
	private static List<String> ikanalyzer(String line) {
		StringReader re = new StringReader(line);
		IKSegmenter ik = new IKSegmenter(re, true);
		Lexeme lex = null;
		List<String> words = new ArrayList<>();
		try {
			while ((lex = ik.next()) != null) {
				String text = lex.getLexemeText();
				words.add(text);
			}
			return words;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return words;
	}
}



