package com.btg.core.util.bbm25;

import java.util.Collection;
import java.util.Random;
import java.util.Vector;

public class EncryptNode {

	private int id = 0;// 节点id
	private Collection<Vector<Double>> Iu = new Vector<>() ;// 计算Iu={m1Td1，m2Td2}
	private String Fid = null;
	private EncryptNode Lchild = null;
	private EncryptNode Rchild = null;
	
	// 构造函数,创建节点时自动生成id
	public EncryptNode() {
		Random rand1 = new Random();
		id = Math.abs(rand1.nextInt(1000)); // 0-1000之间的随机数
	}

	public Collection<Vector<Double>> getIu() {
		return Iu;
	}

	public void setIu(Collection<Vector<Double>> iu) {
		Iu = iu;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFid() {
		return Fid;
	}

	public void setFid(String fid) {
		Fid = fid;
	}

	public EncryptNode getLchild() {
		return Lchild;
	}

	public void setLchild(EncryptNode lchild) {
		Lchild = lchild;
	}

	public EncryptNode getRchild() {
		return Rchild;
	}

	public void setRchild(EncryptNode rchild) {
		Rchild = rchild;
	}
	
}
