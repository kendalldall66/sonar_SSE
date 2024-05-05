package com.btg.core.util.bbm25;

import java.util.Hashtable;
import java.util.Vector;

public class MidEndNode {
	private int id = 0;// 节点id
	private Vector<Double> D = new Vector<>(); // 构建数据向量  
	private Hashtable dataVector = new Hashtable(); // 构建向量（字典类型保存数据向量）
//	private int Lf = 0;  // 该节点文档字符总数
//	private static int Lave = 0;  // 所有节点文档的平均字符数
	private LeafNode Lchild = null;
	private LeafNode Rchild = null;
	private String Fid = null;
}
