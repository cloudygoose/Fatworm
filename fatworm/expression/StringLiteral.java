package fatworm.expression;
import fatworm.type.*;
public class StringLiteral extends Expression{
	private String s;
	public String getString() {
		return s;
	}
	public String getS() {
		return s;
	}
	public StringLiteral(String num) {
		//get away with the ' '
		num = num.substring(1, num.length() - 1);
		s = num;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + s + "\n";
	}
	@Override
	public FatChar evaluate() {
		return new FatChar(s);
	}
}
