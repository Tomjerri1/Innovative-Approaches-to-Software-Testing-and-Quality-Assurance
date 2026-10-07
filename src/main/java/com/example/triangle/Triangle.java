package com.example.triangle;

import java.util.Arrays;

/**
 * An immutable triangle defined by the lengths of its three sides in the same units of measurement.
 * <p>Invariants: all sides are finite and positive, and the strict triangle inequality holds.
 * Violation of these conditions results in an {@link IllegalArgumentException} in the constructor.
 */
public final class Triangle {

    /** Tolerance for comparing real numbers. */
    private static final double EPSILON = 1e-9;

    private final double sideA;
    private final double sideB;
    private final double sideC;

    /**
     * Creates a triangle.
     * @param sideA length of the first side
     * @param sideB length of the second side
     * @param sideC length of the third side
     * @throws IllegalArgumentException if a side is not finite or not positive,
     *     or if a triangle with such sides does not exist
     */
    public Triangle(double sideA, double sideB, double sideC) {
        requirePositiveFinite(sideA, "sideA");
        requirePositiveFinite(sideB, "sideB");
        requirePositiveFinite(sideC, "sideC");
        if (!(sideA + sideB > sideC && sideA + sideC > sideB && sideB + sideC > sideA)) {
            throw new IllegalArgumentException(
                    "Порушено нерівність трикутника: " + sideA + ", " + sideB + ", " + sideC);
        }
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    private static void requirePositiveFinite(double side, String name) {
        if (!Double.isFinite(side) || side <= 0) {
            throw new IllegalArgumentException(
                    "Сторона " + name + " має бути скінченним додатним числом, а не " + side);
        }
    }

    /** Returns the length of the first side. */
    public double getSideA() {
        return sideA;
    }

    /** Returns the length of the second side. */
    public double getSideB() {
        return sideB;
    }

    /** Returns the length of the third side. */
    public double getSideC() {
        return sideC;
    }

    /** Returns the perimeter of the triangle. */
    public double getPerimeter() {
        return sideA + sideB + sideC;
    }

    /**
     * Calculates the area using Heron's formula, which is stable against errors.
     * @return  area of the triangle
     */
    public double getArea() {
        double[] s = sortedSidesDescending();
        double longest = s[0];
        double middle = s[1];
        double shortest = s[2];
        double product = (longest + (middle + shortest))
                * (shortest - (longest - middle))
                * (shortest + (longest - middle))
                * (longest + (middle - shortest));
        return Math.sqrt(product) / 4;
    }

    /** Checks whether the triangle is equilateral (comparison with tolerance {@value #EPSILON}). */
    public boolean isEquilateral() {
        return approxEquals(sideA, sideB) && approxEquals(sideB, sideC);
    }

    /** Checks whether the triangle is isosceles, i.e., has two equal sides (with a tolerance). */
    public boolean isIsosceles() {
        return approxEquals(sideA, sideB)
                || approxEquals(sideB, sideC)
                || approxEquals(sideA, sideC);
    }

    /** Checks if a triangle is right-angled (Pythagorean theorem with relative tolerance). */
    public boolean isRight() {
        double[] s = sortedSidesDescending();
        double hypotenuseSquared = s[0] * s[0];
        double legsSquared = s[1] * s[1] + s[2] * s[2];
        return Math.abs(hypotenuseSquared - legsSquared) <= EPSILON * hypotenuseSquared;
    }

    private static boolean approxEquals(double x, double y) {
        return Math.abs(x - y) <= EPSILON;
    }

    private double[] sortedSidesDescending() {
        double[] sides = {sideA, sideB, sideC};
        Arrays.sort(sides);
        return new double[] {sides[2], sides[1], sides[0]};
    }

    @Override
    public String toString() {
        return "Triangle{sideA=" + sideA + ", sideB=" + sideB + ", sideC=" + sideC + '}';
    }

    /**
     * Two triangles are equal if they have the same sets of sides, regardless of their order.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Triangle other)) {
            return false;
        }
        return Arrays.equals(sortedSidesDescending(), other.sortedSidesDescending());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(sortedSidesDescending());
    }
}
