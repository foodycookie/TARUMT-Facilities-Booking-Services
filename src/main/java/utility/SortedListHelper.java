package utility;

/**
 * @author Ong Hao Howard
 */

public class SortedListHelper {
    // Let the sorted list be: 1, 1, 1, 2, 2, 2, 3, 3, 3
    // If my target is 2, since this is a sorted list, there will be no reason to check pass 2
    public static boolean compareStringIfExceedTarget(String firstData, String secondData) {
        return firstData.compareToIgnoreCase(secondData) > 0;
    }
}