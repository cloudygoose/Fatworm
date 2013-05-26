package fatworm.scan;
import fatworm.expression.*;
import fatworm.table.*;
import fatworm.log.*;
import java.util.*;
import fatworm.func.*;
public class ProjectScan extends Scan {
	Scan source;
	ExpList expList;
	fatworm.driver.Connection connection;
	Tuple nextT;
	Tuple firstT;
	LinkedList<FuncExp> funcs;
	Tuple exT;
	public Scan getSource() {
		return source;
	}
	public ExpList getExpList() {
		return expList;
	}
	public ProjectScan(Scan s, ExpList l, fatworm.driver.Connection c) throws Exception {
		connection = c;
		source = s;
		expList = l;
		exT = null;
		
		if (source == null)
			source = new OneTupleScan(connection);
		//get EX
		Scan tmp = source;
		if (tmp != null)
			source = new OneTupleScan(connection, tmp.generateExTuple());
		else
			source = new OneTupleScan(connection);
		open();
		next();
		exT = getTuple();
		close();
		//wash out
		funcs = new LinkedList<FuncExp>();
		Log.getAllFunc(expList, funcs);
		for (int i = 0;i < funcs.size();i++)
			funcs.get(i).setAssocOverallAggregator(null);
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
		funcs = new LinkedList<FuncExp>();
		firstT = null;
		Log.getAllFunc(expList, funcs);
		
		if (funcs.size() > 0) {
			Tuple tt = source.generateExTuple();
			for (int i = 0;i < funcs.size();i++) {
				FuncExp func = funcs.get(i);
				if (func.getAssocOverallAggregator() == null)
					func.setAssocOverallAggregator(new Aggregator(func.getToken(),
							tt.getValueFromIdSW(func.getId())));
				//Log.v(t.getValueFromId(func.getId()).getPrint(0));
			}
		
			while (source.next()) {
				Tuple t = source.getTuple();
				if (firstT == null)
					firstT = t;
				for (int i = 0;i < funcs.size();i++) {
					FuncExp func = funcs.get(i);
					if (func.getAssocOverallAggregator() == null)
						func.setAssocOverallAggregator(new Aggregator(func.getToken(),
								t.getValueFromIdSW(func.getId())));
					func.getAssocOverallAggregator().accept(t.getValueFromIdSW(func.getId()));
					//Log.v(t.getValueFromId(func.getId()).getPrint(0));
				}
			}
			source.beforeFirst();
		}
	}
	@Override
	public boolean next() throws Exception {
		if (funcs.size() > 0 && nextT != null) 
			return false;
		if (funcs.size() > 0) {
			connection.tupleStack.push(firstT);
			nextT = expList.evaluate();
			connection.tupleStack.pop();
			return true;
		}
		nextT = null;
		//Log.v("!!!");
		if (!source.next())
			return false;
		//Log.v("!!!");
		Tuple t = source.getTuple();
		connection.tupleStack.push(t);
		if (expList.firstStarExp())
			nextT = expList.evaluateFirstStar(t);
		else
			nextT = expList.evaluate();
		connection.tupleStack.pop();
		return true;
	}
	@Override
	public Tuple getTuple() {
		if (nextT != null) 
			return nextT;
		else
			throw new DevelopException();
	}
	@Override
	public void beforeFirst() {
		source.beforeFirst();
		nextT = null;
	}
	@Override
	public void close() {
		source.close();
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "ProjectScan(\n" + 
				expList.getPrint(old + 1) + 
				source.getPrint(old + 1) + padding(old) + ")ProjectScan\n";
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
