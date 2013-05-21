package fatworm.opt;
import fatworm.expression.*;

import fatworm.log.*;
import fatworm.logicplan.*;
import java.lang.reflect.Field;
import java.util.*;
//The most naive BNF geneator!!
public class FatOptUtil {
	public static BNFList getAndComToList(Expression e, BNFList res) {
		if (res == null)
			res = new BNFList();
		if (!(e instanceof AndExp)) {
			res.add(e);
			return res;
		}
		AndExp andE = (AndExp)e;
		res = getAndComToList(andE.getLeft(), res);
		res = getAndComToList(andE.getRight(), res);
		return res;
	}
	public static DomainList getDomainFromExpression(Expression e, DomainList res) {
		if (res == null)
			res = new DomainList();
		if (e instanceof IdExpression) {
			res.add((IdExpression)e);
			return res;
		}
		Field[] fields = e.getClass().getDeclaredFields();
		for (int i = 0;i < fields.length;i++) {
			Object son = null;
			try {
				son = Log.getter(e, Log.toFirstUpper(fields[i].getName()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (son instanceof Expression)
				getDomainFromExpression((Expression)son, res);
		}
		return res;
	}
	public static boolean hasSubqueryExpression(Expression e) {
		Field[] fields = e.getClass().getDeclaredFields();
		for (int i = 0;i < fields.length;i++) {
			Object son = null;
			try {
				son = Log.getter(e, Log.toFirstUpper(fields[i].getName()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (son instanceof Plan)
				return true;
			else
			if (son instanceof Expression)
				if (hasSubqueryExpression((Expression)son))
					return true;
		}
		return false;
	}
	public static ArrayList<Plan> getImeSonPlans(Plan p) {
		ArrayList<Plan> res = new ArrayList<Plan>();
		Field[] fields = p.getClass().getDeclaredFields();
		for (int i = 0;i < fields.length;i++) {
			Object son = null;
			try {
				son = Log.getter(p, Log.toFirstUpper(fields[i].getName()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (son instanceof Plan)
				res.add((Plan)son);
		}
		return res;
	}
	public static ArrayList<SelectPlan> getRecSelectPlans(Plan p, ArrayList<SelectPlan> res) {
		if (res == null)
			res = new ArrayList<SelectPlan>();
		Iterator<Plan> iter = getImeSonPlans(p).iterator();
		while (iter.hasNext())
			getRecSelectPlans(iter.next(), res);
		return res;
	}
}
