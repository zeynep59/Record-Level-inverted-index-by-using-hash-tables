

public interface DictionaryInterface<K, V> {
	
	public V put(K key, V value);
	
	public V  remove(K key);
	
	public V getValue(K key);
	
	public int getHashIndex(K key);
	
	public void setHashType(String hashType);
	
	public void setLoadFactor(double loadFactor);
	
	public void setCollisionHandlingType(String collHandType);
	
	public int getCollisionNumber();
	
	public int getnumOfEntries();
}
