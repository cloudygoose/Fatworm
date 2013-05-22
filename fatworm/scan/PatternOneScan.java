package fatworm.scan;

import fatworm.log.*;
import fatworm.table.Tuple;
import fatworm.expression.*;
import fatworm.index.*;
import fatworm.type.*;

public class PatternOneScan extends Scan {
	String a1, t1, c1, a2, t2, c2;
	TableScan scan1;
	TableScan scan2;
	IdExpression e1;
	IdExpression e2;
	BPlusCursor cursor;
	fatworm.driver.Connection connection;
	Tuple nextT;
	Tuple exT;
	FatType cur;
	public PatternOneScan(String a1, String t1, String c1, String a2, String t2, String c2, fatworm.driver.Connection c) {
		connection = c;
		this.a1 = a1;
		this.t1 = t1;
		this.c1 = c1;
		this.a2 = a2;
		this.t2 = t2;
		this.c2 = c2;
		e1 = new IdExpression("", c1);
		e1.setConnection(connection);
		e2 = new IdExpression("", c2);
		e2.setConnection(connection);
		scan1 = new TableScan(t1, connection);
		scan2 = new TableScan(t2, connection);
		cursor = new BPlusCursor(connection.getDatabaseMgr().currentTableMgr.getTable(t2).getIndex(c2));
		Tuple tt1 = scan1.generateExTuple();
		Tuple tt2 = scan2.generateExTuple();
		tt1.aliasTo(a1);
		tt2.aliasTo(a2);
		for (int i = 0;i < tt2.size();i++)
			tt1.addColumn(tt2.get(i));
		exT = tt1;
		try {
			Log.v(exT.getPrint());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void open() throws Exception{
		cur = null;
		this.scan1.open();
		if (this.scan1.next()) {
			cur = this.scan1.getTuple().getValueFromIdSW(e1);
			this.cursor.setFirstBiggerThan(cur);
		}
	}
	public void close() {
		this.scan1.close();
	}
	public boolean next() throws Exception {
		//not accurate scan!!
		nextT = null;
		if (cur == null)
			return false;
		while (!this.cursor.next()) {
			if (!scan1.next()) {
				cur = null;
				return false;
			}
			cur = this.scan1.getTuple().getValueFromIdSW(e1);
			this.cursor.setFirstBiggerThan(cur);
		}
		Tuple tt1 = this.scan1.getTuple();
		Tuple tt2 = this.cursor.getT();
		
		FatType now2 = tt2.getValueFromIdSW(e2);
		if (cur.compareTo(now2) < 0) {
			if (!scan1.next()) {
				cur = null;
				return false;
			}
			cur = this.scan1.getTuple().getValueFromIdSW(e1);
			this.cursor.setFirstBiggerThan(cur);
		}
		
		tt1.aliasTo(a1);
		tt2.aliasTo(a2);
		for (int i = 0;i < tt2.size();i++)
			tt1.addColumn(tt2.get(i));
		nextT = tt1;
		return true;
	}
	public Tuple getTuple() throws Exception {
		if (nextT == null)
			throw new DevelopException();
		return nextT;
	}
	public void beforeFirst() {
		cur = null;
		this.scan1.beforeFirst();
		if (this.scan1.next()) {
			cur = this.scan1.getTuple().getValueFromIdSW(e1);
			this.cursor.setFirstBiggerThan(cur);
		}
	}
	public Tuple generateExTuple() throws Exception {
		return exT;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "PatternOneScan(" + a1 + " " + t1 + " " + c1 + ", " + 
				a2 + " " + t2 + " " + c2 + ")\n";
	}
}
