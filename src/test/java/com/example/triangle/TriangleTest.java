package com.example.triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TriangleTest {

    private static final double DELTA = 1e-9;

    @Test
    void perimeterOfRightTriangle() {
        assertEquals(12.0, new Triangle(3, 4, 5).getPerimeter(), DELTA);
    }

    @Test
    void areaOfRightTriangle() {
        assertEquals(6.0, new Triangle(3, 4, 5).getArea(), DELTA);
    }

    @Test
    void areaDoesNotDependOnSideOrder() {
        assertEquals(new Triangle(3, 4, 5).getArea(), new Triangle(5, 3, 4).getArea(), DELTA);
    }

    @Test
    void equilateralTriangle() {
        Triangle t = new Triangle(2, 2, 2);
        assertTrue(t.isEquilateral());
        assertTrue(t.isIsosceles());
        assertFalse(t.isRight());
    }

    @Test
    void isoscelesTriangle() {
        Triangle t = new Triangle(5, 5, 8);
        assertTrue(t.isIsosceles());
        assertFalse(t.isEquilateral());
    }

    @Test
    void scaleneTriangleIsNotIsosceles() {
        assertFalse(new Triangle(4, 5, 6).isIsosceles());
    }

    @Test
    void rightTriangleDetected() {
        assertTrue(new Triangle(5, 3, 4).isRight());
        assertFalse(new Triangle(4, 5, 6).isRight());
    }

    @Test
    void rejectsZeroAndNegativeSides() {
        assertThrows(IllegalArgumentException.class, () -> new Triangle(0, 4, 5));
        assertThrows(IllegalArgumentException.class, () -> new Triangle(3, -4, 5));
    }

    @Test
    void rejectsNanAndInfinity() {
        assertThrows(IllegalArgumentException.class, () -> new Triangle(Double.NaN, 4, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new Triangle(3, Double.POSITIVE_INFINITY, 5));
    }

    @Test
    void rejectsViolationOfTriangleInequality() {
        assertThrows(IllegalArgumentException.class, () -> new Triangle(1, 1, 10));
        assertThrows(IllegalArgumentException.class, () -> new Triangle(1, 2, 3));
    }

    @Test
    void equalsAndHashCodeIgnoreSideOrder() {
        Triangle first = new Triangle(3, 4, 5);
        Triangle second = new Triangle(5, 4, 3);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentTrianglesAreNotEqual() {
        assertFalse(new Triangle(3, 4, 5).equals(new Triangle(4, 5, 6)));
        assertFalse(new Triangle(3, 4, 5).equals(null));
    }
}
