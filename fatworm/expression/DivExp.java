package fatworm.expression;

import fatworm.type.FatType;

public class DivExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public DivExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public DivExp copy() {
		DivExp exp = new DivExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		return left.evaluate().computeDiv(right.evaluate());
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "DivExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")Div\n";
	}
}
