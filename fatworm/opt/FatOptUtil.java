package fatworm.opt;
import fatworm.expression.*;

import fatworm.log.*;
import fatworm.logicplan.*;
import java.lang.reflect.Field;
import java.util.*;
//The most naive BNF geneator!!
public class FatOptUtil {
	public static int ss = 1;
	public static BNFList getAndComToList(Expression e, BNFList res) {
		if (res == null)
			res = new BNFList();
		if (!(e instanceof AndExp)) {
			if (e instanceof OrExp && ((OrExp)e).getRight() instanceof AndExp && ss < 10) {
				Expression left = ((OrExp)e).getLeft();
				Expression left1 = ((AndExp)((OrExp)e).getRight()).getLeft();
				Expression right1 = ((AndExp)((OrExp)e).getRight()).getRight();
				ss++;
				res = getAndComToList(new OrExp(left1, left), res);
				res = getAndComToList(new OrExp(right1, left), res);
				//Log.v("FatOptUtil : Or Opt !");
			} else
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
	public static boolean hasSubqueryExpOrFunc(Expression e) {
		if (e instanceof FuncExp)
			return true;
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
				if (hasSubqueryExpOrFunc((Expression)son))
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
		if (p instanceof SelectPlan)
			res.add((SelectPlan)p);
		Iterator<Plan> iter = getImeSonPlans(p).iterator();
		while (iter.hasNext())
			getRecSelectPlans(iter.next(), res);
		return res;
	}
	public static ArrayList<SlotPlan> getRecSlotPlans(Plan p, ArrayList<SlotPlan> res) {
		if (res == null)
			res = new ArrayList<SlotPlan>();
		if (p instanceof SlotPlan)
			res.add((SlotPlan)p);
		Iterator<Plan> iter = getImeSonPlans(p).iterator();
		while (iter.hasNext())
			getRecSlotPlans(iter.next(), res);
		return res;
	}
}
