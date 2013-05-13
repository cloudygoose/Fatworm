package fatworm.expression;

import fatworm.logicplan.Plan;
import fatworm.type.FatType;

public class AsExp extends Expression {
	Expression source;
	String name;
	public Expression getSource() {
		return source;
	}
	public String getName() {
		return name;
	}
	public FatType evaluate() throws Exception {
		FatType f = source.evaluate();
		f.assocColumnName = name;
		return f;
	}
	public AsExp(Expression s, String n) {
			source = s; name = n;
		}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AsExp(\n" +
				source.getPrint(old + 1) + 
				padding(old + 1) + name + "\n" + 
				padding(old) + ")AsExp\n";
	}
}


