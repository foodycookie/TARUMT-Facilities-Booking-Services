package adt;

import java.util.Iterator;

/**
 * Entries in the list have positions that begin with 1.
 * @author
 */

public interface SortedListInterface<T extends Comparable<T>> {
    /** 
     * Adds a new entry to the list in its correct sorted position.
     * Uses Binary Search to find the index, then calls makeRoom().
     * @param newEntry The object to be added.
     */
    public boolean add(T newEntry);
    
    /** 
     * Removes a specific entry from the list.
     * Uses Binary Search to find the item, then calls removeGap().
     * @param anEntry The object to be removed.
     * @return True if removal was successful, false otherwise.
     */
    public boolean remove(T anEntry);

    /** 
     * Checks if the list contains a specific entry.
     * Uses Binary Search for efficiency.
     * @param anEntry The object to check for.
     * @return True if found, false otherwise.
     */
    public boolean contains(T anEntry);

    /** 
     * Removes all entries from the list.
     */
    public void clear();

    /** 
     * @return The current number of entries in the list.
     */
    public int getNumberOfEntries();

    /** 
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty();
    
        /** 
     * Finds the position of an entry using Binary Search.
     * HIGHLIGHT: This is the $O(log n)$ operation for Login and Start-Time lookups.
     * @param anEntry The object to locate.
     * @return The 1-based position of the entry, or a negative value if not found.
     */
    public int getPosition(T anEntry);

    /** 
     * Retrieves the entry at a given position.
     * Used for Range Checking (e.g., checking if the next 30-min blocks are free).
     * @param givenPosition The 1-based index of the entry to retrieve.
     * @return The object at that position.
     */
    public T getEntry(int givenPosition);
    
    public Iterator<T> getIterator();
}