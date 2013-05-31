package fatworm.scan;

import java.util.ArrayList;

import fatworm.expression.ExpList;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.table.*;
public class ProductScan extends Scan{
	Scan sa, sb;
	fatworm.driver.Connection connection;
	Tuple nextT;
	boolean begins;
	static public int kk = 0;
	public Scan getSa() {
		return sa;
	}
	public Scan getSb() {
		return sb;
	}
	public ProductScan(Scan s1, Scan s2, fatworm.driver.Connection c) {
		connection = c;
		sa = s1;
		sb = s2;
		begins = false;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		Tuple t = sa.generateExTuple();
		Tuple t2 = sb.generateExTuple();
		for (int i = 0;i < t2.size();i++)
			t.addColumn(t2.get(i));
		return t;
	}
	@Override
	public void open() throws Exception {
		sa.open();
		begins = sa.next();
		sb.open();
	}
	@Override
	public void close() {
		sa.close();
		sb.close();
	}
	@Override
	public boolean next() throws Exception {
		if (!begins)
			return false;
		nextT = null;
		if (!sb.next()) {
			if (!sa.next())
				return false;
			sb.beforeFirst();
			if (!sb.next())
				return false;
		}
		nextT = sa.getTuple().copy();
		Tuple bb = sb.getTuple();
		for (int i = 0;i < bb.size();i++) {
			nextT.addColumn(bb.get(i));
		}
		//kk++;
		//if (kk % 100 == 0)
		//	Log.v(kk);
		return true;
	}
	@Override
	public Tuple getTuple() {
		if (nextT != null)
			return nextT;
		throw new DevelopException();
	}
	@Override
	public void beforeFirst() {
		sa.beforeFirst();
		try {
			begins = sa.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.beforeFirst();
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "ProductScan(\n" + 
				sa.getPrint(old + 1) + 
				sb.getPrint(old + 1) + padding(old) + ")ProductScan\n";
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
