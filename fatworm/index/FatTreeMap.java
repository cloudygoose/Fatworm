package fatworm.index;
import java.util.*;
public class FatTreeMap<K, V> implements FatTreeMapInterface<K, V>{
	TreeMap<K, V> map;
	public FatTreeMap() {
		map = new TreeMap<K, V>();
	}
	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public void put(K key, V value) {
		map.put(key, value);
	}
	public Set<K> getKeySet() {
		return map.keySet();
	}
}
