package fatworm.storage;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Date;

import fatworm.log.*;
import fatworm.table.*;
public class PageId implements Comparable, Serializable {
	private static final long serialVersionUID = 23L;
	protected String fileName;
	protected Integer id;
	protected Table table;
	public PageId(String fs, int i, Table t) {
		fileName = fs;
		id = i;
		table = t;
	}
	public String getFileName() {
		return fileName;
	}
	public int getId() {
		return id;
	}
	public RandomAccessFile getFile() {
		return table.getFile();
	}
	@Override
	public int compareTo(Object o) {
		if (o instanceof PageId) {
			PageId p = (PageId)o;
			if (fileName.compareTo(p.fileName) != 0)
				return fileName.compareTo(p.fileName);
			return id.compareTo(p.id);
		}
		throw new DevelopException();
	}
	public PageId clone() {
		return new PageId(fileName, id, table);
	}
	public Table getTable() {
		return table;
	}
	public String getPrint() {
		return "PageId : " + fileName + " " + id;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof PageId) {
			PageId p = (PageId)o;
			return (p.fileName.equals(fileName) && p.id.equals(id));
		}
		throw new DevelopException();
	}
}
