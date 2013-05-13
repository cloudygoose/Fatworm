package fatworm.index;

public interface FatTreeMapInterface<K, V> {
	public V get(K key);
	public void put(K key, V value);
}
