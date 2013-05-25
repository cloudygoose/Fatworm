package fatworm.driver;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;


import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;


import fatworm.executor.*;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.logicplan.*;
import fatworm.opt.AddSlotToPlan;
import fatworm.opt.*;
import fatworm.parser.FatwormParser;
import fatworm.parser.ParserManager;
import fatworm.planner.LogicPlanner;
import fatworm.scan.Scan;
import fatworm.table.Tuple;
import java.util.*;
public class Statement implements java.sql.Statement {
	private fatworm.driver.Connection connection;
	private Scan scan;
	public Statement(fatworm.driver.Connection c) {
		connection = c;
	}
	public void setConnection(fatworm.driver.Connection c) {
		connection = c;
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
	public boolean execute(String sql) {
		FatOptUtil.ss = 1;
//		if (sql.charAt(sql.length() - 1) == ';')
//			sql = sql.substring(0, sql.length() - 1);
//		Log.v(sql);
		//if (sql.contains("select a from test1 order by b"))
		//	sql = "select a from (select * from test1 order by b) as aa\n";
		ParserManager parserManager = connection.parserManager;
		LogicPlanner logicPlanner = connection.logicPlanner;
		CommonTree t = parserManager.getCommonTree(sql); // get tree from parser
		CommonTree t1 = parserManager.getCommonTree(sql); // get tree from parser
		CommonTree t2 = parserManager.getCommonTree(sql); //get another tree, because the parse will delete some nodes in the tree
		scan = null;
		try {
			//Log.v("now running sql : " + sql);
			//parserManager.LogAST(t, "");
			//Log.v("============AST complete.===============");
			if (isQuery(t)) {
				LogicPlanner.productLeftMode = true;
				Plan p2 = logicPlanner.translate(t2);
				//Log.v(p2.getPrint(0));
				p2 = Patterns.fuckOrderProjectPlan(p2);
				LogicPlanner.productLeftMode = false;
				Plan p1 = logicPlanner.translate(t1);
				p1 = Patterns.fuckOrderProjectPlan(p1);
				if (Driver.addSlotPlan) {
					AddSlotToPlan.addSlotToPlan(p2);
					AddSlotToPlan.addSlotToPlan(p1);
				}
				if (Driver.logPlanTree) {
					Log.v("logPlanTreeBeforePush");
					Log.v("\n" + p2.getPrint(0));
				}
				int sum1 = 0, sum2 = 0;
				if (Driver.pushDownSelect) {				
					ArrayList<SelectPlan> slee = FatOptUtil.getRecSelectPlans(p2, null);
					for (int i = 0;i < slee.size();i++) {
						SelectPlan sep = slee.get(i);
						sum2 += AddSlotToPlan.pushDownSelect(sep);
					}
					
					slee = FatOptUtil.getRecSelectPlans(p1, null);
					for (int i = 0;i < slee.size();i++) {
						SelectPlan sep = slee.get(i);
						sum1 += AddSlotToPlan.pushDownSelect(sep);
					}
				}
				
				Plan p;
				Log.v("sum2 : " + sum2);
				p = p2;
				if (Driver.pushDownSelect && sum1 > sum2 + 50)
					p = p1;
				if (Driver.logPlanAfterPush) {
					Log.v("logPlanAfterPush");
					Log.v(p.getPrint(0));
				}
				
				
				//Log.v("statement : " + p.getPrint(0));
				
				boolean bb = false, ff = false;
				if (Driver.tryPattern) {
					ArrayList<SlotPlan> slots = FatOptUtil.getRecSlotPlans(p, null);
					for (int i = 0;i < slots.size();i++) {
						if (Patterns.tryPatternTwo(slots.get(i))) {
							//Log.v("Pattern2 opted!!");
							bb = true;
						} else
						if (Patterns.tryPatternThree(slots.get(i))) {
							//Log.v("Pattern3 opted!!");
							bb = true;
						}
					}
				}
				//Log.v("Statement : " + p.getPrint(0));
				//Log.v(p.getPrint(0));
				scan = p.getScan();
				Log.v(scan.getPrint(0));
				if (Driver.logScanTree)
					Log.v("\n" + scan.getPrint(0));
				
				/*
				LinkedList<FuncExp> list = new LinkedList<FuncExp>();
				Log.getAllFunc(scan, list);
				for (int i = 0;i < list.size();i++)
					Log.v(list.get(i).getPrint(0));
				 */
				/*
				scan.open();
				while (scan.next()) {
					Tuple res = scan.getTuple();
					Log.v(res.getPrint());
				}
				scan.close();
				Log.v("...one query complete...");
				*/
				//Log.v(scan.generateExTuple().getPrint());
			} else
			if (isCreateTable(t)) {
				CreateTableExecutor executor = new CreateTableExecutor(t, this);
				executor.execute();
			} else 
			if (isCreateDatabase(t)) {
				CreateDatabaseExecutor executor = new CreateDatabaseExecutor(t, this);
				executor.execute();
			} else
			if (isUseDatabase(t)) {
				UseDatabaseExecutor executor = new UseDatabaseExecutor(t, this);
				executor.execute();
			} else
			if (isInsertValues(t)) {
				InsertValuesExecutor executor = new InsertValuesExecutor(t, this);
				executor.execute();
			} else
			if (isInsertColumns(t)) {
				InsertColumnsExecutor executor = new InsertColumnsExecutor(t, this);
				executor.execute();
			} else
			if (isInsertSubquery(t)) {
				InsertQueryExecutor executor = new InsertQueryExecutor(t, this);
				executor.execute();
			} else	
			if (isUpdate(t)) {
				UpdateExecutor executor = new UpdateExecutor(t, this);
				executor.execute();
			} else
			if (isDelete(t)) {
				DeleteExecutor executor = new DeleteExecutor(t, this);
				executor.execute();
			} else		
			if (isDropDatabase(t)) {
				DropDatabaseExecutor executor = new DropDatabaseExecutor(t, this);
				executor.execute();
			} else
			if (isDropTable(t)) {
				DropTableExecutor executor = new DropTableExecutor(t, this);
				executor.execute();
			} else 
			if (isCreateIndex(t)) {
				CreateIndexExecutor executor = new CreateIndexExecutor(t, this);
				executor.execute();
			} else
			if (isDropIndex(t)) {
				//This Executor doesn't do anything
				DropIndexExecutor executor = new DropIndexExecutor(t, this);
				executor.execute();
			}
			else
			throw new DevelopException("top level : Unknown query");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public ResultSet getResultSet() {
		ResultSet rs = new fatworm.driver.ResultSet();
		rs.setScan(scan);
		return rs;
	}
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
		if (s.equals("CREATE_TABLE"))
			return true;
		return false;		
	}
	public static boolean isDropIndex(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DROP_INDEX"))
			return true;
		return false;		
	}
	public static boolean isDropTable(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DROP_TABLE"))
			return true;
		return false;
	}
	public boolean isDropDatabase(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DROP_DATABASE"))
			return true;
		return false;		
	}
	public boolean isCreateIndex(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("CREATE_INDEX") || s.equals("CREATE_UNIQUE_INDEX"))
			return true;
		return false;		
	}
	public static boolean isCreateDatabase(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("CREATE_DATABASE"))
			return true;
		return false;		
	}
	public static boolean isUseDatabase(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("USE_DATABASE"))
			return true;
		return false;		
	}
	public static boolean isInsertValues(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("INSERT_VALUES"))
			return true;
		return false;			
	}
	public static boolean isInsertColumns(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("INSERT_COLUMNS"))
			return true;
		return false;			
	}
	public static boolean isInsertSubquery(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("INSERT_SUBQUERY"))
			return true;
		return false;			
	}
	public static boolean isDelete(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DELETE"))
			return true;
		return false;			
	}
	public static boolean isUpdate(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("UPDATE"))
			return true;
		return false;			
	}
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void addBatch(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean execute(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public java.sql.ResultSet executeQuery(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int executeUpdate(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public java.sql.ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean getMoreResults(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
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
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setCursorName(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setMaxFieldSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setMaxRows(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setQueryTimeout(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
