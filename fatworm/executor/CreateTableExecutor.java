package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;


import fatworm.driver.Connection;
import fatworm.driver.Driver;
import fatworm.driver.TableMgr;
import fatworm.expression.*;
import fatworm.log.*;
import fatworm.parser.ParserManager;
import fatworm.planner.*;
import fatworm.table.*;
import fatworm.type.*;
public class CreateTableExecutor extends Executor {
	CommonTree tree;
	LogicPlanner trans;
	fatworm.driver.Statement statement;
	public CreateTableExecutor(CommonTree t, fatworm.driver.Statement s) {
		tree = t;
		statement = s;
		trans = statement.getConnection().logicPlanner;
	}
	public void execute() throws Exception{
		TableMgr tableMgr = statement.getConnection().
				getDatabaseMgr().currentTableMgr;
		String tableName = tree.getChild(0).getText();
		Table newTable = tableMgr.createTable(tableName);
		for (int i = 1;i < tree.getChildCount();i++) {
			if (isCreateDefinition(tree.getChild(i))) {
				newTable.addColumn(createColumn((CommonTree)(tree.getChild(i))));
			} 
		}
	}
	Column createColumn(CommonTree t) throws Exception {
		Column re = new Column();
		Log.assertTrue(isId(t.getChild(0)));
		//ParserManager.LogAST(t, "111");
		re.setName(t.getChild(0).getText());
		re.setType(getType(t.getChild(1)));
		if (haveNotNull(t))
			re.setNotNull(true);
		if (haveAutoIncrement(t))
			re.setAutoIncrement(true);
		if (findDefault(t) != null) {
			re.setDefault(re.getType().newInstance(
					findDefault(t)));
			//Log.v(re.getDefault().toString());
		}
		return re;
	}
	public FatType getType(Tree t) throws DevelopException {
		if (isInt(t)) {
			return new FatInteger();
		} else
		if (isFloat(t)) {
			return new FatFloat();
		} else
		if (isChar(t)) {
			return new FatChar(Integer.parseInt(t.getChild(0).getText()));
		} else
		if (isDateTime(t)) {
			return new FatDateTime();
		} else
		if (isBoolean(t)) {
			return new FatBoolean();
		} else
		if (isDecimal(t)) {
			int a1 = Integer.parseInt(t.getChild(0).getText());
			int a2 = - 1;
			if (t.getChildCount() > 1)
				a2 = Integer.parseInt(t.getChild(1).getText());
			return new FatDecimal(a1, a2);
		} else
		if (isTimeStamp(t)) {
			return new FatTimeStamp();
		} else
		if (isVarChar(t)) {
			return new FatVarChar(Integer.parseInt(t.getChild(0).getText()));
		} else
		throw new DevelopException();
	}
	public boolean haveNotNull(Tree t) throws DevelopException {
		for (int i = 0;i < t.getChildCount();i++)
			if (isNull(t.getChild(i))) {
				if (isNot(t.getChild(i).getChild(0)))
					return true;
				else
					throw new DevelopException();
			}
		return false;
	}
	public Expression findDefault(Tree t) throws Exception {
		for (int i = 0;i < t.getChildCount();i++)
			if (isDefault(t.getChild(i))) {
				Tree c = t.getChild(i).getChild(0);
				return trans.translateExpression((CommonTree)c);
			}
		return null;
	}
	public boolean haveAutoIncrement(Tree t) throws DevelopException {
		for (int i = 0;i < t.getChildCount();i++)
			if (isAutoIncrement(t.getChild(i))) {
				return true;
			}
		return false;
	}
	
}
