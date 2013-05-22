package fatworm.opt;

import fatworm.logicplan.SlotPlan;
import fatworm.expression.*;
public class SlotPair {
	SlotPlan slot;
	int level;
	Expression cond;
	public SlotPair(SlotPlan plan, int l, Expression exp) {
		slot = plan;
		level = l;
		cond = exp;
	}
	public SlotPlan getSlotPlan() {
		return slot;
	}
	public int getLevel() {
		return level;
	}
	public Expression getExp() {
		return cond;
	}
}
