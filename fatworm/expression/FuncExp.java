package fatworm.expression;
import fatworm.type.*;
import fatworm.index.*;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.func.*;

public class FuncExp extends Expression {
	private String token;
	private IdExpression id;
	private Aggregator assocOverallAggregator;
	public String getToken() {
		return token;
	}
	public IdExpression getId() {
		return id;
	}
	public void setAssocOverallAggregator(Aggregator agg) {
		assocOverallAggregator = agg;
	}
	public Aggregator getAssocOverallAggregator() {
		return assocOverallAggregator;
	}
	public FuncExp(IdExpression col, String to) {
		id = col; token = to;
		token = token.toUpperCase();
		assocOverallAggregator = null;
	}
	public FatType evaluate() throws Exception {
		if (assocOverallAggregator != null)
			return assocOverallAggregator.getValue();
		FuncPair pair = new FuncPair(token, id);
		FuncEnv funcEnv = connection.funcStack.peek();
		FatType group = funcEnv.getCurrentGroup();
		FatTreeMap<FatType, Aggregator> map = funcEnv.getMap(pair);
		return map.get(group).getValue();
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + token + "(\n" +
				id.getPrint(old + 1) + padding(old) +  
				")Func\n";
	}
}
