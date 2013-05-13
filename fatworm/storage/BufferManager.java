package fatworm.storage;
import fatworm.driver.*;
import fatworm.log.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
public class BufferManager {
	public static final int BUFFERSIZE = 100;
	int pageNumInBuffer;
	fatworm.driver.Connection connection;
	TreeMap<PageId, FatBlock> bufferMap;
	TreeSet<TimePageId> queue;
	public BufferManager(fatworm.driver.Connection c) {
		connection = c;
		bufferMap = new TreeMap<PageId, FatBlock>();
		pageNumInBuffer = 0;
		queue = new TreeSet<TimePageId>();
	}
	public void close() {
		Iterator<FatBlock> iter = bufferMap.values().iterator();
		while (iter.hasNext())
			iter.next().writeBack();
	}
	public void dumpAll(String fileName) {
		ArrayList<TimePageId> Dpages = new ArrayList<TimePageId>();
		Iterator<TimePageId> iter = queue.iterator();
		while (iter.hasNext()) {
			TimePageId p = iter.next();
			if (p.getFileName().equals(fileName))
				Dpages.add(p);
		}
		for (int i = 0;i < Dpages.size();i++) {
			Log.assertTrue(bufferMap.remove(Dpages.get(i).getPageId()) != null);
			Log.assertTrue(queue.remove(Dpages.get(i)));
			pageNumInBuffer--;
		}
	}
	public FatBlock getPage(PageId id) {
		FatBlock fb = bufferMap.get(id);
		//Log.v(pageNumInBuffer);
		if (fb != null) {
			TimePageId ppi = fb.getTimePageId();
			Log.assertTrue(queue.remove(ppi));
			ppi.setLastUseTime(System.currentTimeMillis());
			queue.add(ppi);
			return fb;
		}
		if (pageNumInBuffer + 1 > BUFFERSIZE) {
			//Log.v("!!!!!!!!!!!!!!!buffer write!!!!!!!!!!!!!!!!!!!!!!");
			TimePageId dump = null;
			try {
				dump = queue.first();
			} catch (Exception e) {
				Log.v("queue.first fail");
				//e.printStackTrace();
			}
			Log.assertTrue(queue.remove(dump));
			FatBlock dumpB = bufferMap.remove(dump.getPageId());
			Log.assertTrue(dumpB != null);
			if (dumpB.isDirty()) {
				Log.assertTrue(dumpB.writeBack());
			}
			pageNumInBuffer--;
			dumpB.setInBuffer(false);
		}
		
		RandomAccessFile raf = id.getFile();
		byte[] b = new byte[Driver.BLOCKLENGTH];
		try {
			raf.seek(id.getId() * Driver.BLOCKLENGTH);
			raf.read(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		TimePageId tpi = new TimePageId(id.getFileName(), id.getId(), id.getTable());
		fb = new FatBlock(b, tpi);
		queue.add(tpi);
		fb.setInBuffer(true);
		bufferMap.put(tpi.getPageId(), fb);
		pageNumInBuffer++;
		return fb;
	}
}
