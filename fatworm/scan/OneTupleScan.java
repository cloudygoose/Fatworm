package fatworm.scan;
import fatworm.driver.*;
import fatworm.table.*;
public class OneTupleScan extends Scan {
	fatworm.driver.Connection connection;
	boolean goo;
	Tuple tuple;
	public OneTupleScan(fatworm.driver.Connection c, Tuple t) {
		connection = c;
		goo = false;
		tuple = t;
	}
	public OneTupleScan(fatworm.driver.Connection c) {
		connection = c;
		goo = false;
		tuple = new Tuple();
	}
	@Override
	public Tuple generateExTuple() {
		return tuple;  
	}
	@Override
	public void open() {
		goo = true;
	}
	@Override
	public void close() {
		goo = false;
	}
	@Override
	public void beforeFirst() {
		goo = true;
	}
	@Override
	public boolean next() {
		boolean res = goo;
		goo = false;
		return res;
	}
	@Override 
	public Tuple getTuple() {
		return tuple;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "OneTupleScan()\n";
	}
}
