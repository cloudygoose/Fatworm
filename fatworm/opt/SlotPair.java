package fatworm.opt;

import fatworm.logicplan.SlotPlan;

public class SlotPair {
	SlotPlan slot;
	int level;
	public SlotPair(SlotPlan plan, int l) {
		slot = plan;
		level = l;
	}
	public SlotPlan getSlotPlan() {
		return slot;
	}
	public int getLevel() {
		return level;
	}
}
