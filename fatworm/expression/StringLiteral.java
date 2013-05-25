package fatworm.expression;
import fatworm.log.Log;
import fatworm.type.*;
public class StringLiteral extends Expression{
	private String s;
	public String getString() {
		return s;
	}
	public String getS() {
		return s;
	}
	public StringLiteral copy() {
		StringLiteral exp = new StringLiteral("fyc"); //copy is different from new
		exp.s = this.s;
		exp.setConnection(connection);
		return exp;
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
