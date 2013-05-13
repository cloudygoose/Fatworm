package fatworm.scan;
import fatworm.log.DevelopException;
import fatworm.table.*;
public class DistinctScan extends Scan {
	Scan source;
	fatworm.driver.Connection connection;
	Scan sortedSource;
	Tuple last, next;
	public DistinctScan(Scan s, fatworm.driver.Connection c) {
		connection = c;
		source = s;
	}
	@Override
	public String getPrint(int old) throws Exception{
		return padding(old) + "DistinctScan(\n" + 
				source.getPrint(old + 1) +
				padding(old) + ")DistinctScan\n";	
	}
	public Scan getSource() {
		return source;
	}
	@Override
	public void open() throws Exception {
		sortedSource = new RealSortScan(source, null, null, connection);
		sortedSource.open();
		last = null;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		return source.generateExTuple();
	}
	@Override
	public void beforeFirst() {
		last = null;
		sortedSource.beforeFirst();
	}
	@Override
	public void close() {
		last = null;
		sortedSource.close();
	}
	@Override
	public boolean next() throws Exception {
		while (sortedSource.next()) {
			next = sortedSource.getTuple();
			if (last == null || next.testValueEqual(last) == false) {
				last = next;
				return true;
			}
		}
		return false;
	}
	@Override
	public Tuple getTuple() {
		if (next == null)
			throw new DevelopException();
		return next;
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
