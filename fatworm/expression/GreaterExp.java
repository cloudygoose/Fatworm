package fatworm.expression;
import fatworm.log.Log;
import fatworm.type.*;

public class GreaterExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public GreaterExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public GreaterExp copy() {
		GreaterExp exp = new GreaterExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		//Log.v("GreaterLeft!!" + left.getPrint(0));
		//Log.v("!!" + left.evaluate().getPrint(0));
		int co = left.evaluate().compareTo(right.evaluate());
		if (co > 0)
			return new FatBoolean(true);
		else
			return new FatBoolean(false);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "GreaterExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")Greater\n";
	}
}
