package syp.htlfragebogenapplication.utils;

public class FractionUtils {

    /**
     * Parses a fraction string in the format "numerator/denominator"
     * 
     * @param fractionStr The fraction string to parse
     * @return An array of two integers [numerator, denominator]
     * @throws NumberFormatException if the string is not a valid fraction format
     * @throws ArithmeticException   if the denominator is zero
     */
    public static int[] parseFraction(String fractionStr) {
        if (fractionStr == null || fractionStr.isEmpty()) {
            throw new IllegalArgumentException("Fraction string cannot be null or empty");
        }

        String[] parts = fractionStr.trim().split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid fraction format. Expected 'numerator/denominator'");
        }

        try {
            int numerator = Integer.parseInt(parts[0].trim());
            int denominator = Integer.parseInt(parts[1].trim());

            if (denominator == 0) {
                throw new ArithmeticException("Denominator cannot be zero");
            }

            return new int[] { numerator, denominator };
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Failed to parse fraction: " + e.getMessage());
        }
    }

    /**
     * Calculates the greatest common divisor of two integers using the Euclidean
     * algorithm
     * 
     * @param a First integer
     * @param b Second integer
     * @return The greatest common divisor
     */
    public static int gcd(int a, int b) {
        return b == 0 ? Math.abs(a) : gcd(b, a % b);
    }

    /**
     * Simplifies a fraction to its lowest terms
     * 
     * @param numerator   The numerator
     * @param denominator The denominator
     * @return An array of two integers [simplified_numerator,
     *         simplified_denominator]
     */
    public static int[] simplify(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }

        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        int gcd = gcd(numerator, denominator);
        return new int[] { numerator / gcd, denominator / gcd };
    }

    /**
     * Checks if two fraction strings are mathematically equivalent
     * 
     * @param fraction1 First fraction string
     * @param fraction2 Second fraction string
     * @return True if the fractions are equivalent, false otherwise
     */
    public static boolean areEquivalent(String fraction1, String fraction2) {
        try {
            int[] frac1 = parseFraction(fraction1);
            int[] frac2 = parseFraction(fraction2);

            // Cross-multiply and compare
            return frac1[0] * frac2[1] == frac1[1] * frac2[0];
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formats a fraction as a simplified string
     * 
     * @param numerator   The numerator
     * @param denominator The denominator
     * @return The formatted fraction string in simplified form
     */
    public static String formatSimplified(int numerator, int denominator) {
        int[] simplified = simplify(numerator, denominator);
        return simplified[0] + "/" + simplified[1];
    }
}
