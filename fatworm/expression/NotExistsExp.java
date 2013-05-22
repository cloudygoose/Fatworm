package fatworm.expression;
import fatworm.logicplan.*;
import fatworm.type.*;
import fatworm.scan.*;
public class NotExistsExp extends Expression {
	Plan source;
	public Plan getSource() {
		return source;
	}
	public NotExistsExp(Plan s) {
		source = s;
	}
	public NotExistsExp copy() {
		NotExistsExp exp = new NotExistsExp(source);
		exp.setConnection(connection);
		return exp;
	}
	@Override 
	public FatType evaluate() throws Exception {
		Scan scan = source.getScan();
		scan.open();
		boolean b = scan.next();
		scan.close();
		if (b == true) b = false; else b = true;
		return new FatBoolean(b);
	}
	@Override
 	public String getPrint(int old) throws Exception {
		return padding(old) + "ExistsPlan(\n" + 
				source.getPrint(old + 1) +
				padding(old) + ")NotExists\n";
	}
}
