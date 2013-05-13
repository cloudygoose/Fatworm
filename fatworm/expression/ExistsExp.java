package fatworm.expression;
import fatworm.logicplan.*;
import fatworm.scan.*;
import fatworm.type.*;

public class ExistsExp extends Expression {
	Plan source;
	public Plan getSource() {
		return source;
	}
	public ExistsExp(Plan s) {
		source = s;
	}
	public FatType evaluate() throws Exception {
		Scan scan = source.getScan();
		scan.open();
		boolean b = scan.next();
		scan.close();
		return new FatBoolean(b);
	}
	@Override
 	public String getPrint(int old) throws Exception {
		return padding(old) + "ExistsPlan(\n" + 
				source.getPrint(old + 1) +
				padding(old) + ")Exists\n";
	}
}
