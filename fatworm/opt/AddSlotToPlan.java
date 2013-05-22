package fatworm.opt;

import java.lang.reflect.Field;
import java.util.*;

import fatworm.log.Log;
import fatworm.logicplan.*;
import fatworm.expression.*;

public class AddSlotToPlan {
	public static void addSlotToPlan(Plan plan) {
		if (plan.getAddedSlot())
			return;
		plan.setAddedSlot();
		if (plan instanceof OneTuplePlan || plan instanceof FetchTablePlan) 
			return;
		Field[] fields = plan.getClass().getDeclaredFields();
		for (int i = 0;i < fields.length;i++) {
			Object son = null;
			try {
				son = Log.getter(plan, Log.toFirstUpper(fields[i].getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (son instanceof Plan) {
				Plan sonP = (Plan)son;
				//ProjectPlan, a FetchTable, ProductPlan
				if (sonP instanceof ProjectPlan || sonP instanceof FetchTablePlan || sonP instanceof ProductPlan) {
					SlotPlan newSlot = new SlotPlan(sonP, new BNFList(), plan.getConnection());
					Log.setterPlan(plan, Log.toFirstUpper(fields[i].getName()), newSlot);
				}
				addSlotToPlan(sonP);
			}
		}
	}
	public static ArrayList<SlotPair> getAllReachSlots(Plan plan, ArrayList<SlotPair> res, int level, Expression exp) {
		if (res == null)
			res = new ArrayList<SlotPair>();
		if (plan instanceof ProjectPlan || plan instanceof GroupPlan)
			return res;
		if (plan instanceof AliasPlan && !FatOptUtil.getDomainFromExpression(exp, null).tableNameAllNull())
			return res;
		try {
			if (!FatOptUtil.getDomainFromExpression(exp, null).allInTuple(plan.getScan().generateExTuple()))
				return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (plan instanceof SlotPlan) {
			res.add(new SlotPair((SlotPlan)plan, level, exp.copy()));
		}
		Iterator<Plan> iter = FatOptUtil.getImeSonPlans(plan).iterator();
		while (iter.hasNext())
			res = getAllReachSlots(iter.next(), res, level + 1, exp);
		return res;
	}
	public static void pushDownSelect(SelectPlan plan) {
		BNFList list = FatOptUtil.getAndComToList(plan.getCondition(), null);
		for (int kk = 0;kk < list.size();kk++) {
			Expression exp = list.get(kk);
			if (FatOptUtil.hasSubqueryExpOrFunc(exp))
				continue;
			ArrayList<SlotPair> optList = getAllReachSlots(plan, null, 1, exp);
			if (optList.size() == 0)
				continue;
			int maxLevel = 0; 
			SlotPair maxSlot = null;
			for (int i = 0;i < optList.size();i++) {
				SlotPair sp = optList.get(i);
				if (sp.getLevel() > maxLevel) {
					maxLevel = sp.getLevel();
					maxSlot = sp;
				}
			}
			maxSlot.getSlotPlan().addExp(maxSlot.getExp());
		}
	}
}

