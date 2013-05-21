package fatworm.logicplan;

import fatworm.expression.Expression;
import fatworm.scan.Scan;
import fatworm.scan.SelectScan;
import fatworm.opt.*;
public class SlotPlan extends Plan {
	Plan source;
	BNFList bnf;
	public Plan getSource() {
		return source;
	}
	public void setSource(Plan p) {
		source = p;
	}
	public BNFList getBnf() {
		return bnf;
	}
	public SlotPlan(Plan s, BNFList b, fatworm.driver.Connection c) {
		connection = c;
		source = s; bnf = b;
	}
	@Override
	public String getPrint(int old) throws Exception {
		String res = padding(old) + "SlotPlan(\n" +
				padding(old + 1) + "SOURCE FROM\n" + 
				source.getPrint(old + 1) + 
				padding(old + 1) + "CONDITION ON\n" + 
				(bnf.size() != 0 ? bnf.getPrint(old + 1):padding(old + 1) + "BNFNULL\n") + padding(old) + ")SlotPlan\n";
		return res;
	}
	@Override
	public Scan getScan() throws Exception {
		if (bnf.size() == 0)
			return source.getScan();
		return new SelectScan(source.getScan(), bnf, connection);
	}
}
