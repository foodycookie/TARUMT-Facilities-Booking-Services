package entity;

import java.time.LocalTime;

/**
 * @author Ong Hao Howard
 */

// Utility class
public class BookingWindow implements Comparable<BookingWindow> {
    private final int startIndex;
    private final int blockCount;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public BookingWindow(int startIndex, int blockCount, LocalTime startTime, LocalTime endTime) {
        this.startIndex = startIndex;
        this.blockCount = blockCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
    
    @Override
    public int compareTo(BookingWindow other) {
        int timeComparison = this.startTime.compareTo(other.startTime);
        
        if (timeComparison != 0) {
            return timeComparison;
        }
        
        return Integer.compare(this.blockCount, other.blockCount);
    }
}