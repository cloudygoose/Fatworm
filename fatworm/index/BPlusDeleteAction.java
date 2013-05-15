package fatworm.index;

public class BPlusDeleteAction extends BPlusAction {
	IndexPair deletePair;
	public BPlusDeleteAction(IndexPair ip) {
		deletePair = ip;
	}
	public IndexPair getDeletePair() {
		return deletePair;
	}
}
