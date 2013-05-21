package fatworm.opt;
import java.lang.reflect.Field;

import fatworm.log.Log;
import fatworm.logicplan.*;
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
}
