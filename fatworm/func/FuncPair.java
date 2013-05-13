package fatworm.func;

import fatworm.expression.IdExpression;
import fatworm.log.*;

public class FuncPair {
	private String token;
	private IdExpression id;
	public FuncPair(String t, IdExpression i) {
		token = t;
		id = i;
	}
	public boolean weakEquals(Object o) {
		if (o instanceof FuncPair) {
			FuncPair f = (FuncPair)o;
			return f.token.equals(token) && f.id.weakEquals(id);
		} else
		throw new DevelopException();
	}
	public String getToken() {
		return token;
	}
	public IdExpression getId() {
		return id;
	}
	public boolean strongEquals(Object o) {
		if (o instanceof FuncPair) {
			FuncPair f = (FuncPair)o;
			return f.token.equals(token) && f.id.strongEquals(id);
		} else
		throw new DevelopException();
	}

}
