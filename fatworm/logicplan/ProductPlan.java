package fatworm.logicplan;


import org.antlr.runtime.tree.CommonTree;

import fatworm.log.Log;
import fatworm.scan.*;
public class ProductPlan extends Plan{
	fatworm.driver.Connection connection;
	Plan s1;
	Plan s2;
	public Plan getSa() {
		return s1;
	}
	public Plan getSb() {
		return s2;
	}
	/*
	 * construct from a FromClause
	 */
	public ProductPlan(Plan p1, Plan p2, fatworm.driver.Connection c) {
		connection = c;
		s1 = p1; s2 = p2;
		p1.setFather(this);
		p2.setFather(this);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "ProductPlan(\n" + 
				s1.getPrint(old + 1) + 
				s2.getPrint(old + 1) + padding(old) + ")Product\n";
	}
	public Scan getScan() throws Exception {
		return new ProductScan(s1.getScan(), s2.getScan(), connection);
	}
}
