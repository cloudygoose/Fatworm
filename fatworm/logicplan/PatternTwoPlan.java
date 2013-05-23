package fatworm.logicplan;

import fatworm.scan.*;
import fatworm.scan.Scan;
import fatworm.expression.*;

public class PatternTwoPlan extends Plan {
	Plan p1;
	IdExpression e1;
	String a2, t2, c2;
	public PatternTwoPlan(Plan p1, IdExpression e1, String a2, String t2, String c2, fatworm.driver.Connection c) {
		connection = c;
		this.p1 = p1;
		this.e1 = e1;
		this.a2 = a2;
		this.t2 = t2;
		this.c2 = c2;
	}
	@Override
	public Scan getScan() {
		return new PatternTwoScan(p1, e1, a2, t2, c2, connection);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "PatternTwoPlan(\n" + p1.getPrint(old + 1) + e1.getPrint(old + 1) + padding(old + 1) + "PatternTwo " +     
				a2 + " " + t2 + " " + c2 + ")PatternTwoPlan\n";
	}
}
