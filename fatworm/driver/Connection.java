package fatworm.driver;

import java.sql.Array;
import java.io.*;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import fatworm.log.Log;
import fatworm.parser.ParserManager;
import fatworm.planner.LogicPlanner;
import java.util.*;
import java.util.concurrent.Executor;

import fatworm.storage.*;
import fatworm.table.*;
import fatworm.func.*;
public class Connection implements java.sql.Connection{
	//public static Connection currentConnection = null;
	static public String folder = "files";
	static public String metaDataFileName = "metaAll";
	String url;
	DatabaseMgr dbMgr;
	public BufferManager bufferManager;
	public ParserManager parserManager;
	public LogicPlanner logicPlanner;
	public Stack<Tuple> tupleStack;
	public Stack<FuncEnv> funcStack;
	public fatworm.driver.Statement createStatement() {
		fatworm.driver.Statement s = new fatworm.driver.Statement(this);
		return s;
	}
	public DatabaseMgr getDatabaseMgr() {
		return dbMgr;
	}
	public Connection(String u, Properties info) {
		parserManager = new ParserManager();
		logicPlanner = new LogicPlanner(this);
		bufferManager = new BufferManager(this);
		url = u;
		Log.v(url);
		folder = url.substring(14, u.length());
		Log.openFile(Driver.logFile);
//		dbMgr = new DatabaseMgr();
		try {
			getMetaDataFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbMgr.setConnection(this);
		tupleStack = new Stack<Tuple>();
		funcStack = new Stack<FuncEnv>();
	}
	public void close() {
		bufferManager.close();
		dbMgr.close();
		try {
			storeMetaDataToFile();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Log.v("connection closed!!!");
		Log.closeFile();
	}
	boolean getMetaDataFromFile() throws IOException, ClassNotFoundException {
		try {
			FileInputStream ins = new FileInputStream(folder + File.separator + metaDataFileName);
			ObjectInputStream ooi = new ObjectInputStream(ins);
		
			DatabaseMgr mm = (DatabaseMgr)ooi.readObject();
			if (mm != null)
				dbMgr = mm;
			else {
				dbMgr = new DatabaseMgr(this);
				Log.v("!!noCurrentMetaDataFile found, now newing one!!");
			}
	        ooi.close();
		} catch (Exception e) {
			//e.printStackTrace();
			Log.v("MetaDataFileNotGood, creating the MetaDatafile, now newing one");
			dbMgr = new DatabaseMgr(this);
			RandomAccessFile file = new RandomAccessFile(folder + File.separator + metaDataFileName, "rw");
			file.setLength(0);
			return false;
		}
        return true;
	}
	boolean storeMetaDataToFile() throws IOException, ClassNotFoundException {
		try {
			FileOutputStream ios = new FileOutputStream(folder + File.separator + metaDataFileName);
			ObjectOutputStream ooi = new ObjectOutputStream(ios);
		
			ooi.writeObject(dbMgr);
	        ooi.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("MetaDataWritingFailed");
			return false;
		}
        return true;
	}
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void commit() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public java.sql.Statement createStatement(int arg0, int arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public java.sql.Statement createStatement(int arg0, int arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getClientInfo(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isValid(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String nativeSQL(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CallableStatement prepareCall(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void rollback() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void rollback(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setAutoCommit(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCatalog(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setHoldability(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setReadOnly(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Savepoint setSavepoint(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setTransactionIsolation(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
}
