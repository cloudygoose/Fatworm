package fatworm.scan;

import fatworm.expression.IdExpression;

public class BPlusScan extends Scan {
	fatworm.driver.Connection connection;
	Scan source;
	IdExpression idExp;
	public Scan getSource() {
		return source;
	}
	public IdExpression getIdExp() {
		return idExp;
	}
	public void open() throws Exception {
		source.open();
	}
	public void close() {
		source.close();
	}
	public BPlusScan(Scan s, IdExpression i, fatworm.driver.Connection c) {
		connection = c;
		source = s;
		idExp = i;
	}
	public String getPrint(int old) throws Exception {
		return padding(old) + "BPlusScan(\n" +
				padding(old + 1) + "KEY:\n" + 
				idExp.getPrint(old + 1) +
				padding(old + 1) + "SOURCE:\n" + 
				source.getPrint(old + 1) +
				padding(old) + ")BPlusScan\n";
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
