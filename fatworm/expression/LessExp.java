package fatworm.expression;
import fatworm.type.*;
public class LessExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public LessExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public LessExp copy() {
		LessExp exp = new LessExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		if (left.evaluate().compareTo(right.evaluate()) < 0)
			return new FatBoolean(true);
		else
			return new FatBoolean(false);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "LessExp(\n" +
				left.getPrint(old + 1) + padding(old + 1)  + ",\n" +
				right.getPrint(old + 1) + padding(old) + ")\n";
	}
}
