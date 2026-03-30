package adt;

import java.util.Iterator;

/**
 * This is a doubly linked implementation
 * @author Lai Yu Hui, Ong Hao Howard, Tay Tian You, Tiw Hong Xuan
 * @param <T>
 */

public class SortedLinkedList<T extends Comparable<T>> implements SortedListInterface<T> {
    private Node firstNode;
    private int numberOfEntries;
    
    public SortedLinkedList() {
        firstNode = null;
        numberOfEntries = 0;
    }

    @Override
    public boolean add(T newEntry) {
        Node newNode = new Node(newEntry);
        
        // Case 1. List is empty
        if (isEmpty()) {
            firstNode = newNode;
        }
        
        // Case 2. newEntry is smaller than or equal to the first node
        else if (newEntry.compareTo(firstNode.data) <= 0) {
            newNode.next = firstNode;
            firstNode.previous = newNode;
            firstNode = newNode;
        }
        
        // Case 3. newEntry is larger than the first node
        else {
            Node currentNode = firstNode;
            
            // Transverse, stop at lastNode or right before the node which has data larger than or equal to newEntry
            while (currentNode.next != null && newEntry.compareTo(currentNode.next.data) > 0) {
                currentNode = currentNode.next;
            }
            
            newNode.previous = currentNode;
            newNode.next = currentNode.next;
            currentNode.next = newNode;

            if (newNode.next != null) {
                newNode.next.previous = newNode;
            }
        }
        
        numberOfEntries++;
        
        return true;
    }

    @Override
    public boolean remove(T anEntry) {
        // Case 1. List is empty
        if (isEmpty()) {
            return false;
        }
        
        Node currentNode = firstNode;
        
        // Transverse, stop early right before the node which has data larger than anEntry
        while (currentNode != null && anEntry.compareTo(currentNode.data) >= 0) {
            if (anEntry.compareTo(currentNode.data) == 0) {
                // Case 2. Remove the first node
                if (currentNode == firstNode) {
                    if (currentNode.next != null) {
                        currentNode.next.previous = null;
                    }

                    firstNode = currentNode.next;
                }
                
                // Case 3. Remove the node after the first node
                else {
                    currentNode.previous.next = currentNode.next;

                    if (currentNode.next != null) {
                        currentNode.next.previous = currentNode.previous;
                    }
                }
                
                numberOfEntries--;
                
                return true;
            }
            
            currentNode = currentNode.next;
        }
        
        //Case 4. Not found
        return false;
    }

    @Override
    public void clear() {
        firstNode = null;
        numberOfEntries = 0;
    }

    @Override
    public boolean contains(T anEntry) {
        Node currentNode = firstNode;
        
        // Transverse, stop early right before the node which has data larger than anEntry
        while (currentNode != null && anEntry.compareTo(currentNode.data) >= 0) {
            if (anEntry.compareTo(currentNode.data) == 0) {
                return true;
            }
            
            currentNode = currentNode.next;
        }
        
        return false;
    }

    @Override
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return (numberOfEntries == 0);
    }
    
    @Override
    public Iterator<T> getIterator() {
        return new SortedLinkedListIterator();
    }

    private class SortedLinkedListIterator implements Iterator<T> {
        private Node currentNode;
                
        public SortedLinkedListIterator() {
            currentNode = firstNode;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public T next() {
            T data = currentNode.data;
            currentNode = currentNode.next;
            
            return data;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Node currentNode = firstNode;
        
        while (currentNode != null) {
            stringBuilder.append(currentNode.data);
            
            if (currentNode.next != null) {
                stringBuilder.append(" -> ");
            }
            
            currentNode = currentNode.next;
        }
        
        return stringBuilder.toString();
    }

    private class Node {
        private T data;
        private Node next;
        private Node previous;
        
        private Node(T data) {
            this.data = data;
            next = null;
            previous = null;
        }
        
        private Node(T data, Node next, Node previous) {
            this.data = data;
            this.next = next;
            this.previous = previous;
        }
    }
}