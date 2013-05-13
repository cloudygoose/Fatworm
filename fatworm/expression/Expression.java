package fatworm.expression;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

import fatworm.log.*;
import fatworm.logicplan.Plan;
import fatworm.scan.Scan;
import fatworm.type.*;

public class Expression {
	fatworm.driver.Connection connection;
	public void setConnection(fatworm.driver.Connection c) {
		connection = c;
	}
	public String getPrint(int old) throws Exception{
		throw new DevelopException();
//		return  padding(old) + "a expression\n";
	}
	public FatType evaluate() throws Exception{
		throw new DevelopException();
	}
	public String getAsColName() {
		if (this instanceof AsExp) {
			return ((AsExp)this).getName();
		}
		return "";
	}
	public String padding(int kk) {
		String result = "";
		for (int i = 1;i <= kk;i++)
			result += "    ";
		return result;
	}
	public FatType evaulate() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean weakEquals(Object o) {
		throw new DevelopException();
	}
	public boolean strongEquals(Object o) {
		throw new DevelopException();
	}
}
