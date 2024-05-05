package com.btg.core.util.bbm25;

import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

/*我所设计的矩阵类包含以下功能:（不只是加法功能和转置功能）
 * 1.三种普通构造方法以及一种复制构造方法
 * 2.矩阵的加法、减法、数乘、求负以及矩阵乘法的运算
 * 3.矩阵的转置
 * 4.矩阵的三种判断：矩阵是否同型、矩阵是否相等以及矩阵是否为方阵
 * 5.计算矩阵的迹、矩阵中所有元素的和以及矩阵的乘方
 * 6.获取矩阵的行数和列数
 * 7.输出矩阵相关的所有信息
 * 8.获取行列数确定的单位矩阵的静态方法                                                      
 */
public class BaseMatrix
{
	private double[][] matrix;//用二维数组存储矩阵，数据成员声明为私有保证封装性和安全性
	private int column,row;//分别声明矩阵的行数和列数
	
	//声明了三种普通构造方法和一种复制构造方法
	public BaseMatrix() {}//默认构造方法，只是为了方便创建一个矩阵对象
	public BaseMatrix(int column,int row)//以行数和列数为参数的构造方法，确定矩阵的类型但是还不填充元素
	{
		this.column=column;
		this.row=row;
		this.matrix=new double[column][row];//此处容易出错，尽管只给出行数和列数，但是也应该对作为引用类型的数据成员matrix进行创建
	}
	public BaseMatrix(double [][]matrix)//以一个二维数组作为参数的构造方法，由二维数组的相关性质便可以确定矩阵的行数和列数
	{
		this.column=matrix.length;
		this.row=matrix[0].length;
		this.matrix=new double[column][row];
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				this.matrix[i][j]=matrix[i][j];
		}
	}
	public BaseMatrix(BaseMatrix Matrix)//复制构造函数
	{
		this.column=Matrix.column;
		this.row=Matrix.row;
		this.matrix=new double[column][row];//不直接使用this.matrix=Matrix.matrix是为了实现深复制
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				this.matrix[i][j]=matrix[i][j];
		}
	}
	
	//声明了矩阵的加法、减法、数乘和乘法运算
	public BaseMatrix add(BaseMatrix Matrix)//矩阵的加法运算
	{
		BaseMatrix temp=new BaseMatrix(column,row);//两个矩阵是同型矩阵时，对应位置的元素相加
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
			{
				temp.matrix[i][j]=this.matrix[i][j]+Matrix.matrix[i][j];
			}
		}
		return temp;
	}
	public BaseMatrix substract(BaseMatrix Matrix)//矩阵的减法运算，与加法运算类似
	{
		if(!this.SameKind(Matrix))
		{
			System.out.println("You can't substract two kinds of matrix");
			return (new BaseMatrix(1,1));
		}
		BaseMatrix temp=new BaseMatrix(column,row);
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				temp.matrix[i][j]=this.matrix[i][j]-Matrix.matrix[i][j];
		}
		return temp;
	}
	public BaseMatrix multiple(double n)//矩阵的数乘运算，每个元素分别数乘即可
	{
		BaseMatrix temp=new BaseMatrix(this.matrix);
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				temp.matrix[i][j]*=n;
		}
		return temp;
	}
	public BaseMatrix multiple(BaseMatrix Matrix)//矩阵的乘法运算，是矩阵运算中较为复杂的运算，使用了方法重载
	{
		if(this.row!=Matrix.column)//判断两个矩阵是否能进行乘法运算
		{
			System.out.println("You can't mutiple these two kinds of matrixs");
			return (new BaseMatrix(1,1));
		}
		BaseMatrix temp=new BaseMatrix(this.column,Matrix.row);//能进行乘法运算时，执行下列语句
		for(int i=0;i<temp.column;i++)
		{
			for(int j=0;j<temp.row;j++)
			{
				double sum=0;
				for(int k=0;k<this.row;k++)
					sum+=(this.matrix[i][k]*Matrix.matrix[k][j]);
				temp.matrix[i][j]=sum;//通过上述算法计算每一个元素的值
			}
		}
		return temp;
	}
	//声明了求负矩阵的方法
	public BaseMatrix negative()
	{
		BaseMatrix temp=new BaseMatrix(this.matrix);
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				temp.matrix[i][j]=-this.matrix[i][j];
		}
		return temp;
	}
	
	//声明了矩阵的转置方法
	public BaseMatrix reverse()
	{
		BaseMatrix temp=new BaseMatrix(row,column);//新建一个行数与列数与原矩阵相反的矩阵
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				temp.matrix[j][i]=this.matrix[i][j];//填充新矩阵中的元素
		}
		return temp;
	}
	
	//声明了判断矩阵同型、矩阵相等和是否为方阵的方法，返回值均为boolean类型
	public boolean SameKind(BaseMatrix Matrix)//判断两个矩阵是否同型
	{
		if(this.column==Matrix.column&&this.row==Matrix.row)
			return true;
		else
			return false;
	}
	public boolean equal(BaseMatrix Matrix)//判断两个矩阵是否相等
	{
		if(!this.SameKind(Matrix))//矩阵相等的前提条件是两个矩阵同型
			return false;
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				if(this.matrix[i][j]!=Matrix.matrix[i][j])
					return false;
		}
		return true;
	}
	public boolean phalanx()//判断矩阵是否为方阵
	{
		if(column==row)
			return true;
		else
			return false;
	}
	
	//声明了计算矩阵的迹和所有元素之和的方法
	public double trail()//计算矩阵的迹
	{
		if(!phalanx())//矩阵有迹的前提是矩阵是一个方阵
		{
			System.out.println("Not a phalanx!");
			return 0;
		}
		else//对角线上的元素相加
		{
			double Trail=0;
			for(int i=0;i<column;i++)
				Trail+=this.matrix[i][i];
			return Trail;
		}
	}
	public double sum()//计算矩阵中所有元素的和
	{
		double Sum=0;
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				Sum+=this.matrix[i][j];
		}
		return Sum;
	}
	public BaseMatrix pow(int n)//计算矩阵的乘方
	{
		if(!this.phalanx())//首先判断矩阵是否是方阵
		{
			System.out.println("You can't calculate the pow of a non-phalanx!");
			return (new BaseMatrix(1,1));
		}
		BaseMatrix temp=BaseMatrix.unit(this.column);
		for(int i=1;i<=n;i++)//通过循环方式多次矩阵相乘的方法来计算乘方
			temp=temp.multiple(this);
		return temp;
	}
	
	//获取行列数确定的单位矩阵的方法，由于该方法不属于类中的某一个对象，因此设置为静态方法
	public static BaseMatrix unit(int n)
	{
		BaseMatrix unit=new BaseMatrix(n,n);
		for(int i=0;i<unit.column;i++)
			unit.matrix[i][i]=1;
		return unit;
	}
	
	//声明了矩阵的两个对外公有接口，分别获取行数和列数
	public int getColumn() {return column;}
	public int getRow() {return row;}
	
	//声明了获取矩阵所有信息并输出的方法
	public void show()
	{
		//首先输出行数和列数
		System.out.println("This is a matrix with "+column+" columns and "+row+" rows.");
		System.out.println("The elements of the matrix:");
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				System.out.printf("%5.2f ", matrix[i][j]);//以格式化方法输出矩阵中每一个元素的值
			System.out.println("");
		}
	}
	
	// 计算矩阵的秩
	public static int Rank(double[][] Matrix,int error_,int List)
	   {
		   int n=List;
		   int m=Matrix.length ;
		   int i=0;
		   int i1;
		   int j=0;
		   int j1;
		   double temp1;
		   if(m>n)
		   {
			   i=m;
			   m=n;
			   n=i;
			   i=1;
		   }
		   m-=1;
		   n-=1;
		   double[][]temp=new double[m+1][n+1];
		   if(i==0)
		   {
			 for(i=0;i<=m;i++)
			 {
				 for(j=0;j<=n;j++)
				 {
					 temp[i][j]=Matrix[i][j];
				 }
					 
			 }
		   }
		   else
		   {
			   for(i=0;i<=m;i++)
			   {
				   for(j=0;j<=n;j++)
				   {
					   temp[i][j]=Matrix[j][i];
				   }
			   }
		   }
		   if(m==0)
		   {
			   i=0;
			   while(i<=n)
			   {
				   if(Matrix[0][i]!=0)
				   {
					   return 1;
				   }
				   i+=1;
			   }
			   return 0;
		   }
		   double error0;
		   if(error_==-1)
		   {
			   error0=Math.pow(0.1, 10);
		   }
		   else
		   {
			   error0=Math.pow(0.1, error_);
		   }
		   i=0;
		   while(i<=m)
		   {
			   j=0;
			   while(j<=n)
			   {
				   if(temp[i][j]!=0)
				   {
					   error0*=temp[i][j];
					   i=m;
					   break;
				   }
				   j+=1;
			   }
			   i+=1;
		   }
		   double error1;
		   for(i=0;i<=m;i++)
		   {
			   j=0;
			   while(j<=n)
			   {
				   if(temp[i][j]!=0)
				   {
					   break;
				   }
				   j+=1;
			   }
			   if(j<=n)
			   {
				   i1=0;
				   while(i1<=m)
				   {
					   if(temp[i1][j]!=0&&i1!=i)
					   {
						   temp1=temp[i][j]/temp[i1][j];
						   error1=Math.abs((temp[i][j]-temp[i1][j]*temp1))*100;
						   error1+=error0;
						   for(j1=0;j1<=n;j1++)
						   {
							   temp[i1][j1]=temp[i][j1]-temp[i1][j1]*temp1;
							   if(Math.abs(temp[i1][j1])<error1)
							   {
								   temp[i1][j1]=0;
							   }
						   }
						   
					   }
					   i1+=1;
				   }
			   }
		   }
		   
		   i1=0;
		   for(i=0;i<=m;i++)
		   {
			   for(j=0;j<=n;j++)
			   {
				   if(temp[i][j]!=0)
				   {
					   i1+=1;
					   break;
				   }
			   }
		   }
		   return i1;
	   }	
	
	// 判断矩阵是否可逆
	public boolean IsInvertible(int col) {
		int rank = Rank(matrix,-1,col);
		System.out.println("秩："+rank);
		if (rank==col){
			return true;
		}else {
			return false;
		}		
	}
	
	// 求矩阵行列式
	 public static double Det(double[][] m)  //求行列式
		{
			if(m.length != m[0].length)
			{System.out.println("错误，非方阵");  System.exit(0);}
			if(m.length == 1) //只有一个元素时直接返回
				return m[0][0];
			double l;
			double det=0;
			if(m.length > 2) //参数为矩阵时进行递归
			{
				for(int i=0;i<m.length;i++)
				{
					l = Math.pow(-1,i); //余子式的系数
					double[][] cof = new double[m.length -1][m.length -1];
					for(int j=0;j<cof.length ;j++) //构建余子式
					{
						for(int k=0;k<cof.length;k++)
						{
							if(j<i)
								cof[j][k] = m[k+1][j];
							if(j>i)
								cof[j][k] = m[k+1][j+1];
						}
					}
					det = det + l*m[0][i]*Det(cof);
				}
			}
			else
				return (m[0][0]*m[1][1] - m[0][1]*m[1][0]);
			
			return det;
		}

	//求逆矩阵
	 public BaseMatrix invert() {
		SimpleMatrix matrixD = new SimpleMatrix(column, row);
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++) {
				matrixD.set(i, j, matrix[i][j]);
			}
		}
		SimpleMatrix D_invert = matrixD.invert();//  计算逆矩阵
		// 将矩阵转化成的矩阵对象
		BaseMatrix D_matrix = new BaseMatrix(column, row);
		for(int i=0;i<column;i++)
		{
			for(int j=0;j<row;j++)
				D_matrix.matrix[i][j]=matrixD.get(i, j);
		}
		return D_matrix;
		 
	 }

	//
	 /**
	  *矩阵的右乘向量运算
	  * 就是相当于只有一行n列的矩阵与矩阵相乘，比二维矩阵相乘少一层循环。
	  * Vector<Double> D1
	  */
	 public Vector<Double> mult(Vector<Double> y )
	 {
	     int N = y.size();
//	     double[] c = new double[N];
	     Vector<Double> V = new Vector<Double>();
//	     BaseMatrix c = new BaseMatrix(this.column, this.row);
//	     int M = y.size();
	     if( N != this.column)
	     {
	         //抛出异常
	     }
	     for(int i = 0; i < N; i++)
	     {
	    	 double c = 0;
	         for(int j = 0; j < N; j++)
	         {
	             c += this.matrix[i][j] * y.get(j);
	         }
	         V.add(c);
	     }
	     return V;
	 }
	 
}
