package fatworm.scan;
import fatworm.table.*;

import fatworm.expression.*;
import fatworm.index.*;
import fatworm.logicplan.*;
import fatworm.type.*;
import fatworm.log.*;
public class PatternTwoScan extends Scan {

	String a2, t2, c2;
	Scan scan1;
	TableScan scan2;
	IdExpression e1;
	IdExpression e2;
	Plan p1;
	BPlusCursor cursor;
	fatworm.driver.Connection connection;
	Tuple nextT;
	Tuple exT;
	FatType cur;
	static int kk;
	public PatternTwoScan(Plan p1, IdExpression e1, String a2, String t2, String c2, fatworm.driver.Connection c) {
		connection = c;
		this.p1 = p1;
		this.e1 = e1;
		this.a2 = a2;
		this.t2 = t2;
		this.c2 = c2;
		e2 = new IdExpression("", c2);
		e2.setConnection(connection);
		try {
			scan1 = p1.getScan();
		} catch (Exception e) {
			e.printStackTrace();
		}
		scan2 = new TableScan(t2, connection);
		//Log.v(t2 + " " + c2);
		//Log.v(connection.getDatabaseMgr().currentTableMgr.getTable(t2).getIndex(c2).toString());
		cursor = new BPlusCursor(connection.getDatabaseMgr().currentTableMgr.getTable(t2).getIndex(c2));
		Tuple tt1 = null;
		try {
			tt1 = scan1.generateExTuple();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Tuple tt2 = scan2.generateExTuple();
		tt2.aliasTo(a2);
		for (int i = 0;i < tt2.size();i++)
			tt1.addColumn(tt2.get(i));
		exT = tt1;
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
		Tuple tt1 = this.scan1.getTuple().copy();
		Tuple tt2 = this.cursor.getT().copy();
		FatType now2 = tt2.getValueFromIdSW(e2);
		if (cur.compareTo(now2) < 0) {
			if (!scan1.next()) {
				cur = null;
				return false;
			}
			cur = this.scan1.getTuple().getValueFromIdSW(e1);
			this.cursor.setFirstBiggerThan(cur);
		}
		
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
		try {
			if (this.scan1.next()) {
				cur = this.scan1.getTuple().getValueFromIdSW(e1);
				this.cursor.setFirstBiggerThan(cur);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Tuple generateExTuple() throws Exception {
		return exT;
	}
	@Override
	public String getPrint(int old) {
		try {
			return padding(old) + "PatternTwoScan(\n" + scan1.getPrint(old + 1) + e1.getPrint(old + 1) + padding(old) +    
					a2 + " " + t2 + " " + c2 + ")\n";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
