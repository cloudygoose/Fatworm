package fatworm.expression;
import fatworm.type.*;
public class MulExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public MulExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public MulExp copy() {
		MulExp exp = new MulExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		return left.evaluate().computeMul(right.evaluate());
	} 
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "MulExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")Mul\n";
	}
}
