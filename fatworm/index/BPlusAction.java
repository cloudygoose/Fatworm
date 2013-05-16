package fatworm.index;

public class BPlusAction {
	BPlusAction last;
	BPlusAction next;
	public BPlusAction() {
		last = this;
	}
	public void setLastAction(BPlusAction action) {
		last.next = action;
		last = action;
	}
	public BPlusAction getNextAction() {
		return next;
	}
}
