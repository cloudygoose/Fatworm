package fatworm.planner;

import java.math.BigDecimal;
import java.sql.SQLException;

import java.util.ArrayList;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import fatworm.expression.*;
import fatworm.log.*;
import fatworm.logicplan.*;
import fatworm.driver.*;
public class LogicPlanner extends PlanTranslater{
	fatworm.driver.Connection connection;
	static public boolean productLeftMode = true;
	public LogicPlanner(fatworm.driver.Connection c) {
		connection = c;
	}
	public Plan translate(Tree t) throws Exception {
		CommonTree tr = (CommonTree)t;
		if (isSelect(tr))
			return translateSelect(tr);
		if (isSelectDistinct(tr))
			return translateSelectDistinct(tr);
		if (isAS(tr)) 
			return translateAS(tr);
		if (isID(tr)) 
			return translateID(tr);
		if (isFrom(tr))
			return translateFrom(tr);
		throw new SQLException();
	}
	Plan translateSelectDistinct(CommonTree tr) throws Exception {
		DistinctPlan plan = new DistinctPlan(translateSelect(tr), connection);
		if (plan.getSource() instanceof OrderPlan) {
			OrderPlan order = ((OrderPlan)plan.getSource());
			Plan son = order.getSource();
			plan.setSource(son);
			order.setSource(plan);
			return order;
		}
		return plan;
		//NOTE:if we have a order by primary key then the distinct is done easily
	}
	/*
	 * select ... from a as b
	 */
	Plan translateAS(CommonTree tr) throws Exception {
		Plan source = translate(tr.getChild(0));
		Log.assertTrue(isID(tr.getChild(1)) || isFunc(tr.getChild(1)));
		String change = ((CommonTree)tr.getChild(1)).getText();
		return new AliasPlan(source, change, connection);
	}
	Plan translateSelect(CommonTree tr) throws Exception {
		Plan p = null;
		if (findFrom(tr) != null)
			p = translateFrom(findFrom(tr));
		else
			p = new OneTuplePlan(connection);
		
		if (findWhere(tr) != null)
			p = new SelectPlan(p, translateExpression(
					(CommonTree)findWhere(tr).getChild(0)), connection);
		ArrayList<Expression> aList = new ArrayList<Expression>();
		while (!isFrom(tr.getChild(0))) {
			aList.add(translateExpression((CommonTree)tr.getChild(0)));			
			tr.deleteChild(0);
			if (tr.getChildCount() == 0)
				break;
		}
		ExpList expList = new ExpList(aList);
		if (findGroup(tr) != null) {
			Expression gId = translateExpression(
					(CommonTree)(findGroup(tr).getChild(0)));
			Log.assertTrue(gId instanceof IdExpression);
			Expression h = null;
			if (findHaving(tr) != null)
				h = translateExpression(
						(CommonTree)(findHaving(tr).getChild(0)));
			p = new GroupPlan(expList, p, (IdExpression)gId, h, connection);
		} else
		p = new ProjectPlan(expList, p, connection);
		
		if (findOrderBy(tr) != null) {
			CommonTree orderTree = findOrderBy(tr);
			ArrayList<IdExpression> ids = new ArrayList<IdExpression>();
			ArrayList<Boolean> ascs = new ArrayList<Boolean>();
			for (int i = 0;i < orderTree.getChildCount();i++) {
				CommonTree orderClause = (CommonTree)orderTree.getChild(i);
				Boolean asc = true; Expression id;
				if (isAsc(orderClause)) {
					asc = true;
					id = translateExpression((CommonTree)(orderClause.getChild(0)));
				} else
				if (isDesc(orderClause)) {
					asc = false;
					id = translateExpression((CommonTree)(orderClause.getChild(0)));
				} else
				{
					asc = true;
					id = translateExpression(orderClause);
				}
				Log.assertTrue(id instanceof IdExpression);
				ids.add((IdExpression)id);
				ascs.add(asc);
			}
			p = new OrderPlan(ids, ascs, p, connection);
		}
		return p;
	} 
	public Expression translateExpression(CommonTree tr) throws Exception {
		//Connection for the expression is set at the last position
		Expression e;
		if (isQuery(tr)) {
			e = new SubqueryExp(translate(tr));
		} else
		if (isGreater(tr)) {
			e = new GreaterExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1)));
		} else
		if (isGreaterEqual(tr)) {
			e = new GreaterEqualExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1)));
		} else
		if (isLessEqual(tr)) {
			e = new LessEqualExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1)));
		} else
		if (isLess(tr)) 
			e = new LessExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 			
		else
		if (isNotEqual(tr))
			e = new NotEqualExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 	
		else
		if (isTuple(tr))
			e = new IdExpression(tr.getChild(0).getText(), tr.getChild(1).getText());
		else
		if (isID(tr))
			e = new IdExpression("", tr.getText());
		else
		if (isAnd(tr))
			e = new AndExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 		
		else
		if (isOr(tr))
			e = new OrExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 		
		else
		if (isPlus(tr))
			e = new PlusExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 		
		else
		if (isMinus(tr)) {
			if (tr.getChildCount() > 1)
				e = new MinusExp(translateExpression((CommonTree)tr.getChild(0)),
						translateExpression((CommonTree)tr.getChild(1)));
			else
				e = new MinusExp(new IntegerLiteral(0),
						translateExpression((CommonTree)tr.getChild(0)));
		}
		else
		if (isMul(tr))
		{
			if (tr.getChildCount() > 1)
				e = new MulExp(translateExpression((CommonTree)tr.getChild(0)),
						translateExpression((CommonTree)tr.getChild(1)));
			else
				e = new StarExp();
		}
		else
		if (isDiv(tr))
			e = new DivExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 		
		else
		if (isMod(tr))
			e = new ModExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1))); 		
		else
		if (isFloatLiteral(tr))
			e = new FloatLiteral(new BigDecimal(tr.getText()));
		else
		if (isIntegerLiteral(tr)) {
			if (tr.getText().length() > 16)
				e = new StringLiteral("'" + tr.getText() + "'");
			else
				e = new IntegerLiteral(Integer.parseInt(tr.getText()));
		}
		else
		if (isStringLiteral(tr))
			e = new StringLiteral(tr.getText());
		else
		if (isTrue(tr))
			e = new BooleanLiteral(Boolean.parseBoolean(tr.getText()));
		else
		if (isFalse(tr))
			e = new BooleanLiteral(Boolean.parseBoolean(tr.getText()));
		else
		if (isEqual(tr))
			e = new EqualExp(translateExpression((CommonTree)tr.getChild(0)),
					translateExpression((CommonTree)tr.getChild(1)));
		else
		if (isAS(tr))
			e = new AsExp(translateExpression((CommonTree)tr.getChild(0)),
					tr.getChild(1).getText());		
		else
		if (isMax(tr) || isMin(tr) || isSum(tr) || isAvg(tr) || isCount(tr)) {
			String token = tr.getText();
			Expression exp = translateExpression((CommonTree)tr.getChild(0));
			Log.assertTrue(exp instanceof IdExpression);
			e = new FuncExp((IdExpression)exp, token);
		}
		else
		if (isExists(tr)) {
			e = new ExistsExp(translate(tr.getChild(0)));
		} 
		else
		if (isNotExists(tr)) {
			e = new NotExistsExp(translate(tr.getChild(0)));
		} 
		else
		if (isIn(tr)) {
			ArrayList<Expression> expList = new ArrayList<Expression>();
			while (!isQuery(tr.getChild(0))) {
				expList.add(translateExpression((CommonTree)tr.getChild(0)));
				tr.deleteChild(0);
			}
			e = new InExp(new ExpList(expList), 
					translate(tr.getChild(0)));
		}
		else
		if (isAny(tr)) {
			e = new AnyExp(translateExpression((CommonTree)(tr.getChild(0))),
					tr.getChild(1).getText(), translate(tr.getChild(2)));
		}
		else
		if (isAll(tr)) {
			e = new AllExp(translateExpression((CommonTree)(tr.getChild(0))),
					tr.getChild(1).getText(), translate(tr.getChild(2)));
		}
		else
		throw new DevelopException();
		e.setConnection(connection);
		return e;
	}
	/*
	 * select ... from a
	 */
	Plan translateID(CommonTree tr) throws Exception{
		return new FetchTablePlan(tr.getText(), connection);
	}
	Plan translateFrom(CommonTree tr) throws Exception {
		if (tr.getChildCount() == 1)
			return translate(tr.getChild(0));
		if (this.productLeftMode) {
			Plan p = translate(tr.getChild(tr.getChildCount() - 1));
			tr.deleteChild(tr.getChildCount() - 1);
			return new ProductPlan(translateFrom(tr), p, connection);
		} else {
			Plan p = translate(tr.getChild(0));
			tr.deleteChild(0);
			return new ProductPlan(p, translateFrom(tr), connection);			
		}
	}
	
}