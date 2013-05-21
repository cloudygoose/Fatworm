package fatworm.logicplan;


import org.antlr.runtime.tree.CommonTree;

import fatworm.log.Log;
import fatworm.scan.*;
public class ProductPlan extends Plan{
	Plan sa;
	Plan sb;
	public Plan getSa() {
		return sa;
	}
	public Plan getSb() {
		return sb;
	}
	public void setSa(Plan p) {
		sa = p;
	}
	public void setSb(Plan p) {
		sb = p;
	}
	/*
	 * construct from a FromClause
	 */
	public ProductPlan(Plan p1, Plan p2, fatworm.driver.Connection c) {
		connection = c;
		sa = p1; sb = p2;
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "ProductPlan(\n" + 
				sa.getPrint(old + 1) + 
				sb.getPrint(old + 1) + padding(old) + ")Product\n";
	}
	public Scan getScan() throws Exception {
		return new ProductScan(sa.getScan(), sb.getScan(), connection);
	}
}
