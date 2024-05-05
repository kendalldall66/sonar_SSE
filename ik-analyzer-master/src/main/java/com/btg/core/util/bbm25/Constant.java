package com.btg.core.util.bbm25;

public class Constant {
	static double [][]matrix = {{1.0,2.0},{3.0,4.0}};
	
	private static BaseMatrix M1_1000 = new BaseMatrix(matrix);
	public static void main(String[] args) {
		Constant c = new Constant();
		c.M1_1000.show();
//		System.out.println("c.M1_1000：" + c.M1_1000);

	}

}
