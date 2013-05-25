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
	TreeMap<FatType, Tuple> tupleFuckMap;
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

		//deal with query like
		//select id as a, name as b from student group by b
		//having name > any (select name from student where a < id) 
		
		IdExpression havFuck = null;
		if (having != null && having instanceof GreaterExp) {
			if (((GreaterExp)having).getLeft() instanceof IdExpression)
				havFuck = (IdExpression)((GreaterExp)having).getLeft();
		}
		if (having != null && having instanceof GreaterEqualExp) {
			if (((GreaterEqualExp)having).getLeft() instanceof IdExpression)
				havFuck = (IdExpression)((GreaterEqualExp)having).getLeft();
		}
		
		ArrayList<IdExpression> asFromList = new ArrayList<IdExpression>();
		ArrayList<String> asToList = new ArrayList<String>();
		for (int i = 0;i < expList.getExpList().size();i++)
			if (expList.getExpList().get(i) instanceof AsExp) {
				AsExp asExp = (AsExp)(expList.getExpList().get(i));
				if (!(asExp.getSource() instanceof IdExpression))
						continue;
				asFromList.add((IdExpression)asExp.getSource());
				asToList.add(asExp.getName());
				if (idExp.strongEquals(new IdExpression("", asExp.getName()))) {
					idExp = (IdExpression)asExp.getSource();
				}
				if (havFuck != null && havFuck.strongEquals((IdExpression)asExp.getSource()))
					havFuck.setColumnName(asExp.getName());
			}
		
		
		
		
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
			Log.getAllFunc(expList, funcs);
		}
		if (having != null) {
			Log.getAllFunc(having, funcs);
		}
//		Log.v("!!");
//		for (int i = 0;i < funcs.size();i++)
//			Log.v(funcs.get(i).getPrint(0));
		if (funcs.size() == 0) {
			TupleColumn col = source.generateExTuple().get(0);
			FuncExp newF = new FuncExp(new IdExpression(col.getTableName(), col.getColumnName()), "min");
			funcs.add(newF);
		}
		
		env.prepareMaps(funcs);
		tupleFuckMap = new TreeMap<FatType, Tuple>();
		
		while (source.next()) {
			//Log.v("GroupScan!!" + source.getTuple().getPrint());
			env.accept(source.getTuple());
			
			//fuck
			FatType g = source.getTuple().getValueFromIdSW(this.idExp);
			if (tupleFuckMap.get(g) == null)
				tupleFuckMap.put(g, source.getTuple());
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

				//fuck
				Tuple fuckTuple = tupleFuckMap.get(group);
				connection.tupleStack.push(fuckTuple);
				
				connection.tupleStack.push(t.copy());
				connection.funcStack.push(env);
				nextT = expList.evaluate();
				connection.tupleStack.push(nextT.copy());
				FatType hav;
				if (having != null)
					hav = having.evaluate();
				else
					hav = new FatBoolean(true);
				connection.tupleStack.pop();
				connection.funcStack.pop();
				connection.tupleStack.pop();
				//fuck
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
