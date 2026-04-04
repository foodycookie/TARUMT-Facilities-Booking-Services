package adt;

import java.io.Serializable;
import java.util.Iterator;

/**
 * We assumed the entries in the list have positions that begin with 1.
 * But in the array logic, it begins with 0.
 * @author
 * @param <T>
 */

public class SortedArrayList<T extends Comparable<T>> implements SortedListInterface<T>, Serializable {
    private T[] array;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 20;
    
    public SortedArrayList() {
        this(DEFAULT_CAPACITY);
    }
    
    public SortedArrayList(int initialCapacity) {
        numberOfEntries = 0;
        array = (T[]) new Comparable[initialCapacity];
    }
    
    @Override
    public boolean add(T newEntry) {
        if (isFull()) {
            doubleArray();
        }

        int result = binarySearch(newEntry);
        int indexToInsert;

        if (result >= 0) {
            indexToInsert = result;
        } 
        
        else {
            indexToInsert = -(result + 1);
        }

        makeRoom(indexToInsert);
        array[indexToInsert] = newEntry;
        numberOfEntries++;

        return true;
    }

    @Override
    public boolean remove(T anEntry) {
        int indexToRemove = binarySearch(anEntry);

        if (indexToRemove < 0) {
            return false;
        }

        removeGap(indexToRemove);
        numberOfEntries--;
        
        return true;
    }

    @Override
    public void clear() {
        numberOfEntries = 0;
    }

    @Override
    public boolean contains(T anEntry) {
        return binarySearch(anEntry) >= 0;
    }

    @Override
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }
    
    @Override
    public int getPosition(T anEntry) {
        int result = binarySearch(anEntry);

        if (result >= 0) {
            return result + 1;
        } 
        
        else {
            // Return negative if not found
            return result; 
        }
    }

    @Override
    public T getEntry(int givenPosition) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            return array[givenPosition - 1];
        } 
        
        else {
            return null; 
        }
    }

    @Override
    public Iterator<T> getIterator() {
        return new SortedArrayListIterator();
    }
    
    private class SortedArrayListIterator implements Iterator<T> {
        private int nextIndex;

        private SortedArrayListIterator() {
            nextIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return nextIndex < numberOfEntries;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T nextEntry = array[nextIndex];
                nextIndex++;
                
                return nextEntry;
            } 
            
            else {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        String output = "List Content (Size: " + numberOfEntries + "):\n";

        for (int i = 0; i < numberOfEntries; i++) {
            output += (i + 1) + ". " + array[i].toString() + "\n";
        }

        return output;
    }
    
    private boolean isFull() {
        return numberOfEntries >= array.length;
    }
    
    private void doubleArray() {
        T[] tempArray = (T[]) new Comparable[array.length * 2];
        
        for (int i = 0; i < numberOfEntries; i++) {
            tempArray[i] = array[i];
        }
        
        array = tempArray;
    }
    
    private void makeRoom(int newPosition) {
        int lastIndex = numberOfEntries - 1;
        int indexToStop = newPosition;
        
        for (int i = lastIndex; i >= indexToStop; i--) {
            array[i + 1] = array[i];
        }
    }
    
    private void removeGap(int givenPosition) {
        int startIndex = givenPosition;
        int lastIndex = numberOfEntries - 1;
        
        for (int i = startIndex; i < lastIndex; i++) {
            array[i] = array[i + 1];
        }
        
        array[lastIndex] = null;
    }
    
    private int binarySearch(T anEntry) {
        int lower = 0;
        int upper = numberOfEntries - 1;

        while (lower <= upper) {
            int mid = (lower + upper) / 2;

            int comparison = anEntry.compareTo(array[mid]);

            if (comparison == 0) {
                return mid;
            } 
            
            else if (comparison < 0) {
                upper = mid - 1;
            } 
            
            else {
                lower = mid + 1;
            }
        }

        // If not found, return a negative number
        // If the return number is -3, result = -3
        // Why: To get the insertion point of the entry
        // Use: -(result + 1), the insertion point is index 2 in this case
        // +1 is to prevent returning 0, which is a valid index
        return -(lower + 1);
    }
}