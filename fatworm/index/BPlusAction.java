package fatworm.index;

public class BPlusAction {
	BPlusAction next;
	public void setNextAction(BPlusAction action) {
		next = action;
	}
	public BPlusAction getNextAction() {
		return next;
	}
}
