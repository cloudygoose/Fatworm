package fatworm.scan;


import fatworm.expression.IdExpression;
import fatworm.index.*;
import fatworm.type.*;
import fatworm.table.*;
import fatworm.log.*;

public class PatternThreeScan extends Scan {
	FatIndex index;
	IdExpression id;
	FatType low, high;
	BPlusCursor cursor;
	fatworm.driver.Connection connection;
	Tuple nextT;
	boolean begin;
	Scan scan;
	public PatternThreeScan(FatIndex index, Scan scan, IdExpression id, FatType low, FatType high, fatworm.driver.Connection connection) {
		this.index = index;
		this.id = id;
		this.low = low;
		this.high = high;
		this.connection = connection;
		this.scan = scan;
		begin = true;
	}
	@Override
	public Tuple generateExTuple() {
		try {
			return scan.generateExTuple();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void open() {
		cursor = new BPlusCursor(index);
		cursor.setFirstBiggerThan(low);
	}
	@Override
	public boolean next() {
		if (!begin)
			return false;
		nextT = null;
		
		if (cursor.next()) {
			nextT = cursor.getT();
			if (high != null && nextT.getValueFromIdSW(id).compareTo(high) > 0) {
				begin = false;
				return false;
			}
			return true;
		} else {
			begin = false;
			return false;
		}
	}
	@Override
	public Tuple getTuple() {
		if (nextT == null)
			throw new DevelopException();
		return nextT;
	}
	@Override
	public void beforeFirst() {
		cursor.setFirstBiggerThan(low);
		begin = true;
	}
	@Override
	public void close() {

	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "PatternThreeScan(\n" + id.getPrint(old + 1) + 
				low.getPrint(old + 1) + high.getPrint(old + 1) + padding(old) + ")PatternThreeScan\n";
	}
}
