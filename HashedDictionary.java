import java.util.Iterator;
import java.util.NoSuchElementException;

//look at the seperate chaning

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {

	// the dictionary
	private int numberOfEntries;
	private static final int DEFAULT_CAPACITY = 5;
	private static final int MAX_CAPACITY = 300000;
	private static final int K = 31;
	private static final int MAX_SIZE = 2 * MAX_CAPACITY;

	// selections of algorithm
	private static String hashType = "PAF";
	private static String COLLISIONTYPE = "DH";
	private static double MAX_LOAD_FACTOR = 0.8; // 0.8
	private static int COLLISIONNUMBER = 0;

	// the hash table
	private TableEntry<K, V>[] hashTable;
	private static int tableSize; // must be prime
	private boolean initialized = false;

	public HashedDictionary() { // constructor
		this(DEFAULT_CAPACITY);// call next constructor
	}

	public HashedDictionary(int initialCapacity) {
		checkCapacity(initialCapacity);
		numberOfEntries = 0; // dict is empty
		// set up hash table: (size must be prime)
		tableSize = getNextPrime(initialCapacity);
		checkSize(tableSize); // check for max array size

		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[tableSize];
		hashTable = temp;
		initialized = true;
	}// end constructor

	private int prime = getPrevPrime(DEFAULT_CAPACITY);

	public V getValue(K key) {
		checkInitialization();
		V result = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1)
			result = hashTable[index].getValue(); // Key found; get value
		// Else key not found; return null
		return result;
	} // end getValue

	public V remove(K key) {
		checkInitialization();
		V removedValue = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1) { // Key found; flag entry as removed and return its value
			removedValue = hashTable[index].getValue();
			hashTable[index].setToRemoved();
			numberOfEntries--;
		} // end if
			// Else key not found; return null
		return removedValue;
	} // end remove

	public V put(K key, V value) {
		checkInitialization();
		if ((key == null) || (value == null))
			throw new IllegalArgumentException();
		else {
			V oldValue; // Value to return
			int index = getHashIndex(key);
			index = probe(index, key); // Check for and resolve collision
			// Assertion: index is within legal range for hashTable
			assert (index >= 0) && (index < hashTable.length);
			if ((hashTable[index] == null) || hashTable[index].isRemoved()) { // Key not found, so insert new entry
				hashTable[index] = new TableEntry<>(key, value);
				numberOfEntries++;
				oldValue = null;
			} else { // Key found; get old value for return and then replace it
				oldValue = hashTable[index].getValue();
				hashTable[index].setValue(value);
			} // end if
				// Ensure that hash table is large enough for another add
			if (isHashTableTooFull())
				resize();
			return oldValue;
		} // end if
	} // end add

	// Precondition: checkInitialization has been called.
	private int locate(int index, K key) {
		boolean found = false;
		int step = 0;
		int firstindex = index;
		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].isIn() && key.equals(hashTable[index].getKey()))
				found = true; // Key found
			else // Follow probe sequence
			{
				if (COLLISIONTYPE.equals("LP"))
					index = (index + 1) % hashTable.length; // linear probing
				else {
					index = (hash1(firstindex) + step * hash2(firstindex)) % hashTable.length; // double hashing
					step++;
				}
			}

		} // end while
			// Assertion: Either key or null is found at hashTable[index]
		int result = -1;
		if (found)
			result = index;
		return result;
	} // end locate

	// Precondition: checkInitialization has been called.
	private int probe(int index, K key) {
		int firstindex = index;
		boolean found = false;
		int step = 0;
		int removedStateIndex = -1; // Index of first location in removed state
		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].isIn()) {
				if (key.equals(hashTable[index].getKey()))
					found = true; // Key found
				else // Follow probe sequence
				{
					if (COLLISIONTYPE.equals("LP"))
						index = (index + 1) % hashTable.length; // linear probing
					else {
						index = (hash1(firstindex) + step * hash2(firstindex)) % hashTable.length; // double hashing
						
					}
					step++;
				}

			} else // Skip entries that were removed
			{
				// Save index of first location in removed state
				if (removedStateIndex == -1)
					removedStateIndex = index;
				if (COLLISIONTYPE.equals("LP"))
					index = (index + 1) % hashTable.length; // linear probing
				else {
					index = (hash1(firstindex) + step * hash2(firstindex)) % hashTable.length; // double hashing
				
				}
				step++;

			} // end if
		} // end while
		if(step>1) COLLISIONNUMBER++;
			// Assertion: Either key or null is found at hashTable[index]
		if (found || (removedStateIndex == -1))
			return index; // Index of either key or null
		else
			return removedStateIndex; // Index of an available location
	} // end probe

	private int hash1(int index) {
		return index % hashTable.length;
	}

	private int hash2(int index) {
		return (prime - (index % prime));
	}

	private boolean isHashTableTooFull() {
		if ((double) numberOfEntries / (double) hashTable.length >= MAX_LOAD_FACTOR)
			return true;
		else
			return false;
	}

	public int getHashIndex(K key) {
		int n = key.toString().length();
		int hashCode = 0;

		// PAF
		// int hash = 0;
		if (hashType.equals("PAF"))
			for (int i = 0; i < n; i++) {
				hashCode = K * hashCode + ((String) key).charAt(i);
				hashCode = hashCode % hashTable.length;
			}
		// SSF
		else if (hashType.equals("SSF"))
			for (int i = 0; i <= n - 1; i++) {
				hashCode += (int) (key.toString().charAt(i));
				hashCode = hashCode % hashTable.length;
			}

		return hashCode % tableSize;
	}

	// Precondition: checkInitialization has been called.
	private void resize() {
		TableEntry<K, V>[] oldTable = hashTable;
		int oldSize = hashTable.length;
		int newSize = getNextPrime(oldSize + oldSize);
		checkSize(newSize);

		// The cast is safe because the new array contains null entries
		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[newSize];
		hashTable = temp;
		numberOfEntries = 0; // Reset number of dictionary entries, since
		// it will be incremented by add during rehash
		// Rehash dictionary entries from old array to the new and bigger
		// array; skip both null locations and removed entries
		for (int index = 0; index < oldSize; index++) {
			if ((oldTable[index] != null) && oldTable[index].isIn())
				put(oldTable[index].getKey(), oldTable[index].getValue());
		} // end for
	} // end enlargeHashTable

	private static class TableEntry<S, T> {
		private S key;
		private T value;
		private States state; // Flags whether this entry is in the hash table

		private enum States {
			CURRENT, REMOVED
		} // Possible values of state

		private TableEntry(S searchKey, T dataValue) {
			key = searchKey;
			value = dataValue;
			state = States.CURRENT;
		} // end constructor

		public void setToRemoved() {
			state = States.REMOVED;
		}

		public boolean isIn() {
			if (state.CURRENT == States.CURRENT)
				return true;
			else
				return false;
		}

		public boolean isRemoved() {
			if (state.CURRENT == States.REMOVED)
				return true;
			else
				return false;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public S getKey() {
			return key;
		}

	}

	private class KeyIterator implements Iterator<K> {
		private int currentIndex; // Current position in hash table
		private int numberLeft; // Number of entries left in iteration

		private KeyIterator() {
			currentIndex = 0;
			numberLeft = numberOfEntries;
		} // end default constructor

		public boolean hasNext() {
			return numberLeft > 0;
		} // end hasNext

		public K next() {
			K result = null;
			if (hasNext()) {
				// Skip table locations that do not contain a current entry
				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {
					currentIndex++;
				} // end while
				result = hashTable[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			} else
				throw new NoSuchElementException();
			return result;
		} // end next

		public void remove() {
			throw new UnsupportedOperationException();
		} // end remove
	} // end KeyIterator

	public int getNextPrime(int currentNumber) {
		boolean flag = true;
		while (flag) {
			// if(currentNumber%2 == 0)
			// currentNumber++;
			if (isPrime(currentNumber))
				flag = false;
			else
				currentNumber++;
		}
		return currentNumber;
	}

	public static boolean isPrime(int n) {
		if (n <= 1) {
			return false;
		}
		for (int i = 2; i <= Math.sqrt(n); i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	// Throws an exception if this object is not initialized.
	private void checkInitialization()

	{
		if (!initialized)
			throw new SecurityException("ArrayBag object is not initialized " + "properly.");
	} // end checkInitialization

	private static int getPrevPrime(int index) {

		boolean found = false;
		int prime = index - 1;
		while (!found) {
			if (isPrime(prime))
				found = true;
			else
				prime--;
		}
		return prime;

	}

	public int getnumOfEntries() {
		return numberOfEntries;
	}

	private void checkSize(int size) {
		if (size > MAX_CAPACITY)// || size<=0
			throw new IllegalStateException(
					"Attempt to create a hashtable whose " + "capacity exeeds allowed " + "maximum of " + MAX_CAPACITY);
	} // end checkSize

	private void checkCapacity(int capacity) {
		if (capacity > MAX_CAPACITY)
			throw new IllegalStateException(
					"Attempt to create a bag whose " + "capacity exeeds allowed " + "maximum of " + MAX_CAPACITY);
	} // end checkCapacity

	public void setLoadFactor(double loadFactor) {
		MAX_LOAD_FACTOR = loadFactor;
	}

	public void setCollisionHandlingType(String collhandType) {
		COLLISIONTYPE = collhandType;
	}

	public void setHashType(String hash_type) {
		hashType = hash_type;
	}

	public int getCollisionNumber() {
		return COLLISIONNUMBER;
	}

}