package fatworm.expression;
import fatworm.type.*;
public class PlusExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public PlusExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	@Override
	public FatType evaluate() throws Exception {
		return left.evaluate().computeAdd(right.evaluate());
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "PlusExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")Plus\n";
	}
}
