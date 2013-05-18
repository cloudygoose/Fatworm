package fatworm.driver;
import org.antlr.runtime.tree.CommonTree;

import org.antlr.runtime.tree.Tree;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.*;

import fatworm.executor.*;
import fatworm.expression.*;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.logicplan.*;
import fatworm.parser.FatwormParser;
import fatworm.parser.ParserManager;
import fatworm.planner.LogicPlanner;
import fatworm.scan.*;
import fatworm.table.*;
import fatworm.index.*;
import fatworm.type.*;
import java.io.*;
import java.util.*;
import fatworm.index.*;
public class Driver implements java.sql.Driver{
	/*
	 * TODO:
	 * createIndex should scan all the tuples in the table
	 */
	
	/*
	 * do not support transaction and concurrency
	 * I use RATFile to do the File-qsort instead of the standard file-merge sort
	 * the tuples in table is fixed-length so the updates and insert are very easy, and so I don't need pointers for table-file
	 * cont- so the insert's performance is maximized, but update and scan depends on whether exists massive delete
	 * for me, unique index is just index, because the data has no error
	 * many types are both instance and factory, like tuple, FatType, IndexPair, beacause they carry type information
	 */
	/*
	 * implementation notes:
	 * The tupleNum in the table is added by the insertExecutors, not by table itself
	 * When I delete a tuple, the only thing I do is to set the null byte to 1
	 * The getTupleFromByteArray(byte[]) method in Tuple and in Schema are very similiar
	 * Different from PagIds, RATFileCursor leaves all the pointer movements to the user, you should explicitly forward or backward
	 * Drier.BLOCKLENGTH the length of a page
	 * In OrderScan and DistinctScan, the RealSortScan is used for sorting, instead of the fake SortScan
	 * FatDecimal.byteLength the serial length of FatDecimal
	 * BufferManager.BUFFERSIZE the number of buffers in the page
	 * Table.getTableCursor() return new RealTableCursor(name, records, schema, this); Whether table returns TableCursor or RealTableCursor
	 * Driver.logFile boolean indicates whether Log to file
	 * -1 in index means null marker, -2 in index means false delete
	 */
	static {
		try {
			Driver d = new Driver();
			java.sql.DriverManager.registerDriver(d);
			
			try {
				d.test();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Can't register driver!");
		}
	}
	public static final boolean logFile = false;
	public static final int BLOCKLENGTH = 40;
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public fatworm.driver.Connection connect(String url, Properties info) throws SQLException {
		return new fatworm.driver.Connection(url, info);
	}

	public static void main(String[] args) throws Exception {
		Log.v("max memory : " + Runtime.getRuntime().maxMemory());
	}
	public void test() throws SQLException, IOException, ClassNotFoundException {
		Log.open = true;
		
		String str1 = "select hello from java";
		
		String str2 = "select branch_name, avg_balance from (select branch_name, avg(balance) as avg_balance " + 
					 "from account " + 
					 "group by branch_name) " + 
					 "as branch_avg " + 
					 "where avg_balance > 1200";	
		String str3 = "drop table aadas, basdad";
		String str4 = "select now as busy, good from kk as count, fuck, messi where messi.jk > 100 and exists (select * from jj)";
		String str5 = "create table model (model int, logp int)";
		String str6 = "select aa as aa, bb from (select * from A, B) as AB, CC as C where ((1.5 + 2) > cc and AB.c < mesi)";
		String str7 = "select max(aa), 1+2 from InfraTest, another where (a = b) and (b = c) or (a = c) or (b = d)";
		String str8 = "select distinct customer_name from borrower,loan where borrower.loan_number = loan.loan_number and "
				+ "branch_name = 'Perryridge' and (branch_name, customer_name) in (select branch_name, customer_name "  
				+ "from depositor, account where depositor.account_number = account.account_number)";
		String str9 = "select distinct customer_name from borrower, loan where (branch_name, customer_name) in (select branch_name, customer_name "  
				+ "from depositor, account where depositor.account_number = account.account_number)";
		
		fatworm.driver.Connection connection = connect("", null);
		Log.v("!!test begin!!");
		Statement stmt = connection.createStatement();
		stmt.execute("create database test");
		stmt.execute("use test");
		stmt.execute("create table test(a int not null auto_increment, " +
				"b varchar(3) default 'aaa', primary key(a))");
		stmt.execute("create table test2(aa int not null auto_increment, " + 
				"b varchar(3) default 'aaa')");
		FatIndex testIndex = connection.dbMgr.dbs.get("test").getTable("test").createIndex("index1", "a");
		for (int i = 1;i <= 4;i++)
			testIndex.insertPair(new FatInteger(i), i);
		testIndex.logBPlus();
		stmt.execute("insert into test values (1, 'aa')");
		stmt.execute("insert into test values (3, 'aa')");
		stmt.execute("insert into test (a) values(2)");
		stmt.execute("insert into test values (1, 'aa')");
		stmt.execute("insert into test values (3, 'aa')");
		stmt.execute("insert into test (a) values(2)");
		stmt.execute("select * from test order by a");
		//		stmt.execute("insert into test2 (select * from test)");
//		stmt.execute("select * from test2");
//		stmt.execute("insert into test (select * from test)");
		connection.close();
	}
	public static TableMgr getCurrentTableMgr() {
		return currentTableMgr;
	}
	public static TableMgr currentTableMgr;
	public static boolean isQuery(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "SELECT" || s == "SELECT_DISTINCT")
			return true;
		return false;	
	}
	public static boolean isCreateTable(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "CREATE_TABLE")
			return true;
		return false;		
	}
	
	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		// TODO Auto-generated method stub
		return false;
	}
}
