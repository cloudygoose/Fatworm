package fatworm.scan;
import fatworm.driver.Driver;
import fatworm.expression.*;
import fatworm.index.*;
import fatworm.log.Log;
import fatworm.func.*;
import java.util.*;
import fatworm.type.*;
import org.omg.CORBA.Environment;
import fatworm.log.*;
import fatworm.table.*;

public class GroupScan extends Scan {
	fatworm.driver.Connection connection;
	Scan source;
	ExpList expList;
	IdExpression idExp;
	Expression having;
	FuncEnv env;
	Tuple nextT;
	Tuple exT;
	boolean findExTuple;
	public GroupScan(Scan s, ExpList list, IdExpression id, Expression h, fatworm.driver.Connection c) throws Exception {
		connection = c;
		source = s;
		expList = list;
		idExp = id;
		having = h;
		exT = null;
		findExTuple = false;
		//Ex
		Scan tmp = source;
		source = new OneTupleScan(connection, tmp.generateExTuple());
		findExTuple = true;
		open();
		next();
		exT = getTuple();
		findExTuple = false;
		close();
		source = tmp;
		nextT = null;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		if (exT == null)
			throw new DevelopException();
		return exT;
	}
	@Override
	public void open() throws Exception {
		source.open();
		env = new FuncEnv(idExp);
		LinkedList<FuncExp> funcs = new LinkedList<FuncExp>();
		if (expList != null) {
			Log.v("searching expList");
			Log.getAllFunc(expList, funcs);
		}
		if (having != null) {
			Log.v("searching having");
			Log.getAllFunc(having, funcs);
		}
//		Log.v("!!");
//		for (int i = 0;i < funcs.size();i++)
//			Log.v(funcs.get(i).getPrint(0));
		
		env.prepareMaps(funcs);
		
		while (source.next()) {
			//Log.v("!!" + source.getTuple().getPrint());
			env.accept(source.getTuple());
		}
		env.open();
		nextT = null;
		connection.bufferManager.setBufferSize(Driver.BUFFERSIZE / 2);
	}
	@Override
	public boolean next() throws Exception {
		if (env.next()) {
			nextT = null;
			
			while (nextT == null) {
				FatType group = env.getCurrentGroup();
				Tuple t = new Tuple();
				t.addColumn(new TupleColumn(idExp.getTableName(), idExp.getColumnName(), group));

				connection.tupleStack.push(t.copy());
				connection.funcStack.push(env);
				nextT = expList.evaluate();
				FatType hav;
				if (having != null)
					hav = having.evaluate();
				else
					hav = new FatBoolean(true);
				connection.funcStack.pop();
				connection.tupleStack.pop();
				if (findExTuple || Log.checkFatBoolean(hav)) {
					return true;
				}
				nextT = null;
				if (!env.next())
					return false;
			}
			return false;
		} else return false;
	}
	@Override
	public void beforeFirst() {
		env.beforeFirst();
	}
	@Override
	public Tuple getTuple() {
		if (nextT != null)
			return nextT;
		throw new DevelopException("GroupScan has no tuple");
	}
	@Override 
	public void close() {
		env.close();
		source.close();
		connection.bufferManager.setBufferSize(Driver.BUFFERSIZE);
	}
	@Override
	public String getPrint(int old) throws Exception {
		String result = padding(old) + "GroupScan(\n" + 
				expList.getPrint(old + 1) + 
				padding(old + 1) + "GROUP BY\n" + 
				idExp.getPrint(old + 1) +
				padding(old + 1) + "SOURCE FROM\n" + source.getPrint(old + 1); 
		if (having != null)
			result += padding(old + 1) + "HAVING:\n" +
				having.getPrint(old + 1);
		result += padding(old) + ")GroupScan\n";
		return result;
	}
	public Scan getSource() {
		return source;
	}
	public ExpList getExpList() {
		return expList;
	}
	public IdExpression getIdExp() {
		return idExp;
	}
	public Expression getHaving() {
		return having;
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
