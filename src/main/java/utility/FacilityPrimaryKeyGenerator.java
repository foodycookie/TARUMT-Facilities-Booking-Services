/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

/**
 *
 * @author Ong Hao Howard
 *
 * Utility class for generating and parsing facility primary key strings.
 *
 * ID format:  &lt;prefix&gt;&lt;3-digit number&gt;
 *   L001, L002, … — Library Discussion Room / Individual Study Room
 *   C001, C002, … — Cyber Centre Discussion Room
 *   S001, S002, … — Sports Facilities
 *
 * This class is stateless — the logic that knows the current highest ID
 * for each prefix group lives in FacilityMaintenance.generateFacilityId().
 * This class provides the formatting/parsing/validation primitives that any
 * module can reuse without depending on the control layer.
 *
 */

public class FacilityPrimaryKeyGenerator {

    // ------------------------------------------------------------------ //
    //  Prefix constants                                                     //
    // ------------------------------------------------------------------ //

    /** Prefix for Library Discussion Room and Individual Study Room. */
    public static final String PREFIX_LIBRARY = "L";

    /** Prefix for Cyber Centre Discussion Room. */
    public static final String PREFIX_CYBER   = "C";

    /** Prefix for Sports Facilities. */
    public static final String PREFIX_SPORTS  = "S";

    /** Number of digits in the numeric portion of a facility ID. */
    public static final int PAD_WIDTH = 3;

    // Private constructor — utility class; do not instantiate.
    private FacilityPrimaryKeyGenerator() {}

    // ------------------------------------------------------------------ //
    //  Public API                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Formats a prefix letter and sequence number into a full facility ID.
     *
     * Examples:
     *   format("L", 1)  → "L001"
     *   format("C", 12) → "C012"
     *   format("S", 3)  → "S003"
     *
     * @param prefix         one of PREFIX_LIBRARY / PREFIX_CYBER / PREFIX_SPORTS
     * @param sequenceNumber the numeric part (must be &gt; 0)
     * @return the formatted facility ID string
     * @throws IllegalArgumentException if sequenceNumber is not positive or
     *                                  prefix is unrecognised
     */
    public static String format(String prefix, int sequenceNumber) {
        if (!isKnownPrefix(prefix)) {
            throw new IllegalArgumentException("Unknown prefix: " + prefix
                    + ". Expected L, C, or S.");
        }
        if (sequenceNumber <= 0) {
            throw new IllegalArgumentException(
                    "Sequence number must be positive, got: " + sequenceNumber);
        }
        return prefix + String.format("%0" + PAD_WIDTH + "d", sequenceNumber);
    }

    /**
     * Parses a facility ID string and returns its integer sequence number.
     *
     * Examples:
     *   parse("L001") → 1
     *   parse("C012") → 12
     *   parse("S003") → 3
     *
     * @param facilityId the facility ID string to parse
     * @return the integer sequence number, or -1 if the format is invalid
     */
    public static int parse(String facilityId) {
        if (facilityId == null || facilityId.length() < 2) return -1;
        String prefix = facilityId.substring(0, 1);
        if (!isKnownPrefix(prefix)) return -1;
        try {
            return Integer.parseInt(facilityId.substring(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Extracts the prefix letter from a facility ID string.
     *
     * Example: extractPrefix("C007") → "C"
     *
     * @param facilityId the facility ID string
     * @return the one-letter prefix, or null if the format is invalid
     */
    public static String extractPrefix(String facilityId) {
        if (facilityId == null || facilityId.isEmpty()) return null;
        String prefix = facilityId.substring(0, 1);
        return isKnownPrefix(prefix) ? prefix : null;
    }

    /**
     * Validates that a string conforms to the facility ID format
     * (known prefix + at least one digit).
     *
     * @param facilityId the string to validate
     * @return true if the string is a valid facility ID
     */
    public static boolean isValid(String facilityId) {
        return parse(facilityId) > 0;
    }

    /**
     * Determines the correct prefix for a facility name using the same
     * rules as FacilityMaintenance.resolvePrefixFor().
     *
     * @param facilityName the facility category name
     * @return PREFIX_CYBER, PREFIX_SPORTS, or PREFIX_LIBRARY (default)
     */
    public static String resolvePrefixFor(String facilityName) {
        if (facilityName == null) return PREFIX_LIBRARY;
        String lower = facilityName.toLowerCase();
        if (lower.contains("cyber"))  return PREFIX_CYBER;
        if (lower.contains("sport"))  return PREFIX_SPORTS;
        return PREFIX_LIBRARY;
    }

    // ------------------------------------------------------------------ //
    //  Private helpers                                                      //
    // ------------------------------------------------------------------ //

    private static boolean isKnownPrefix(String prefix) {
        return PREFIX_LIBRARY.equals(prefix)
            || PREFIX_CYBER.equals(prefix)
            || PREFIX_SPORTS.equals(prefix);
    }
}