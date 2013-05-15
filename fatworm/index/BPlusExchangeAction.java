package fatworm.index;

public class BPlusExchangeAction extends BPlusAction {
	IndexPair iPFrom, iPTo;
	public BPlusExchangeAction(IndexPair from, IndexPair to) {
		iPFrom = from;
		iPTo = to;
	}
	public IndexPair getFrom() {
		return iPFrom;
	}
	public IndexPair getTo() {
		return iPTo;
	}
}
