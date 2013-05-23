package fatworm.logicplan;

import fatworm.opt.BNFList;
import fatworm.scan.*;
//PatternOnePlan is contained by PatternTwo !!!!
//So this file is only a memory
public class PatternOnePlan extends Plan {
	String a1, t1, c1, a2, t2, c2;
	public PatternOnePlan(String a1, String t1, String c1, String a2, String t2, String c2, fatworm.driver.Connection c) {
		connection = c;
		this.a1 = a1;
		this.t1 = t1;
		this.c1 = c1;
		this.a2 = a2;
		this.t2 = t2;
		this.c2 = c2;
	}
	@Override
	public Scan getScan() {
		return new PatternOneScan(a1, t1, c1, a2, t2, c2, connection);
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "PatternOnePlan(" + a1 + " " + t1 + " " + c1 + ", " + 
				a2 + " " + t2 + " " + c2 + ")\n";
	}
}
