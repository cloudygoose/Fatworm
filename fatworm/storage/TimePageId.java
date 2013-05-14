package fatworm.storage;

import java.io.RandomAccessFile;

import fatworm.log.DevelopException;
import fatworm.table.Table;

public class TimePageId extends PageId implements Comparable {
	Long lastUseTime;
	public TimePageId(String fs, int i, Table t) {
		super(fs, i, t);
		lastUseTime = System.currentTimeMillis();
	}
	public Long getLastUseTime() {
		return lastUseTime;
	}
	public void setLastUseTime(Long time) {
		lastUseTime = time;
	}
	//about the priority of being kicked out of the tree
	@Override
	public int compareTo(Object o) {
		if (o instanceof TimePageId) {
			TimePageId p = (TimePageId)o;
			if (lastUseTime.compareTo(p.lastUseTime) != 0)
				return lastUseTime.compareTo(p.lastUseTime);
			if (fileName.compareTo(p.fileName) != 0)
				return fileName.compareTo(p.fileName);
			return id.compareTo(p.id);
		}
		throw new DevelopException();
	}
	@Override
	public String getPrint() {
		return "TimePageId : " + fileName + " " + id;
	}
	public PageId getPageId() {
		PageId p = new PageId(fileName, id, table); 
		p.file = file;
		return p;
	}
}
