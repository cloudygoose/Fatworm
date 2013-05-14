package fatworm.scan;

import java.util.ArrayList;

import fatworm.expression.IdExpression;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.logicplan.Plan;
import fatworm.table.*;
public class OrderScan extends Scan {
	fatworm.driver.Connection connection;
	ArrayList<IdExpression> idList;
	ArrayList<Boolean> ascList;
	Scan source;
	Scan sortScan;
	Tuple nextT;
	public Scan getSource() {
		return source;
	}
	public ArrayList<Boolean> getAscList() {
		return ascList;
	}
	public ArrayList<IdExpression> getIdList() {
		return idList;
	}
	public OrderScan(ArrayList<IdExpression> al, 
			ArrayList<Boolean> bl, Scan s, fatworm.driver.Connection c) {
		connection = c;
		idList = al;
		ascList = bl;
		source = s;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		return source.generateExTuple();	
	}
	@Override
	public void open() throws Exception {
		sortScan = new SortScan(source, ascList, idList, connection);
		sortScan.open();
		nextT = null;
	}
	@Override
	public boolean next() throws Exception {
		nextT = null;
		if (sortScan.next()) {
			nextT = sortScan.getTuple();
			return true;
		} else
		return false;
	}
	@Override
	public Tuple getTuple() throws Exception {
		if (nextT != null)
			return nextT;
		throw new DevelopException();
	}
	@Override
	public void beforeFirst() {
		sortScan.beforeFirst();
	}
	@Override
	public void close() {
		sortScan.close();
	}
	@Override
	public String getPrint(int old) throws Exception {
		String result = padding(old) + "OrderScan(\n" +
				padding(old + 1) + "ORDERLIST:\n";
		for (int i = 0;i < idList.size();i++) {
			result += idList.get(i).getPrint(old + 1);
			if (ascList.get(i).equals(true))
				result += padding(old + 1) + "ASC\n";
			else
				result += padding(old + 1) + "DESC\n";
		}
		result += padding(old + 1) + "SOURCE:\n" +
				source.getPrint(old + 1);
		result += padding(old) + ")OrderScan\n";
		return result;	 
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
