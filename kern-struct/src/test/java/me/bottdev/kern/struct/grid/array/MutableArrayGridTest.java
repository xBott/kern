package me.bottdev.kern.struct.grid.array;

import me.bottdev.kern.struct.grid.Grid;
import me.bottdev.kern.struct.grid.GridFitResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutableArrayGridTest {

    private MutableArrayGrid<Character> grid;

    @BeforeEach
    void setUp() {
        grid = new ArrayGridBuilder<Character>(3, 3)
                .row(0, new Character[]{null, 'N', null})
                .row(1, new Character[]{'W',  '+', 'E'})
                .row(2, new Character[]{null, 'S', null})
                .mutable();
    }

    // ── angle ────────────────────────────────────────────────────────────────

    @Test
    void angle_isZeroInitially() {
        assertEquals(0.0, grid.angle());
    }

    @Test
    void angle_updatesAfterRotate() {
        grid.rotate(90);
        assertEquals(90.0, grid.angle());
    }

    @Test
    void angle_wrapsAt360() {
        grid.rotate(270);
        grid.rotate(180);
        assertEquals(90.0, grid.angle());
    }

    // ── set ──────────────────────────────────────────────────────────────────

    @Test
    void set_updatesValue() {
        grid.set(0, 0, 'X');
        assertEquals('X', grid.get(0, 0));
    }

    @Test
    void set_overwritesExisting() {
        grid.set(1, 1, 'Z');
        assertEquals('Z', grid.get(1, 1));
    }

    @Test
    void set_toNull_clearsCell() {
        grid.set(1, 0, null);
        assertNull(grid.get(1, 0));
    }

    // ── clear ────────────────────────────────────────────────────────────────

    @Test
    void clear_setsAllToNull() {
        grid.clear();
        for (int row = 0; row < grid.height(); row++) {
            for (int col = 0; col < grid.width(); col++) {
                assertNull(grid.get(row, col));
            }
        }
    }

    @Test
    void clear_preservesSize() {
        grid.clear();
        assertEquals(3, grid.width());
        assertEquals(3, grid.height());
    }

    // ── canFit ───────────────────────────────────────────────────────────────

    @Test
    void canFit_returnsOutOfBounds_whenOtherExceedsGrid() {
        var other = new ArrayGridBuilder<Character>(3, 3)
                .cell(0, 0, 'X')
                .immutable();

        assertEquals(GridFitResult.OUT_OF_BOUNDS, grid.canFit(1, 1, other, false));
    }

    @Test
    void canFit_returnsIntersects_whenOverlapAndNoOverride() {
        var other = new ArrayGridBuilder<Character>(1, 1)
                .cell(0, 0, 'X')
                .immutable();

        assertEquals(GridFitResult.INTERSECTS, grid.canFit(1, 1, other, false));
    }

    @Test
    void canFit_returnsFit_whenOverlapAndOverride() {
        var other = new ArrayGridBuilder<Character>(1, 1)
                .cell(0, 0, 'X')
                .immutable();

        assertEquals(GridFitResult.FIT, grid.canFit(1, 1, other, true));
    }

    @Test
    void canFit_returnsFit_whenEmptyCell() {
        var other = new ArrayGridBuilder<Character>(1, 1)
                .cell(0, 0, 'X')
                .immutable();

        assertEquals(GridFitResult.FIT, grid.canFit(0, 0, other, false));
    }

    // ── fit ──────────────────────────────────────────────────────────────────

    @Test
    void fit_placesValuesCorrectly() {
        var other = new ArrayGridBuilder<Character>(2, 2)
                .row(0, new Character[]{'A', 'B'})
                .row(1, new Character[]{'C', 'D'})
                .immutable();

        grid.clear();
        GridFitResult result = grid.fit(0, 0, other, false);

        assertEquals(GridFitResult.FIT, result);
        assertEquals('A', grid.get(0, 0));
        assertEquals('B', grid.get(0, 1));
        assertEquals('C', grid.get(1, 0));
        assertEquals('D', grid.get(1, 1));
    }

    @Test
    void fit_placesValuesCorrectly_Not_Square() {
        var other = new ArrayGridBuilder<Character>(1, 3)
                .row(0, new Character[]{'A'})
                .row(1, new Character[]{'A'})
                .row(2, new Character[]{'A'})
                .immutable();

        grid.clear();
        GridFitResult result = grid.fit(0, 0, other, false);

        assertEquals(GridFitResult.FIT, result);
        assertEquals('A', grid.get(0, 0));
        assertEquals('A', grid.get(1, 0));
        assertEquals('A', grid.get(2, 0));
    }

    @Test
    void fit_doesNotModifyGrid_whenOutOfBounds() {
        var other = new ArrayGridBuilder<Character>(3, 3)
                .fill('X')
                .immutable();

        grid.fit(1, 1, other, false);

        assertEquals('N', grid.get(0, 1));
        assertEquals('+', grid.get(1, 1));
    }

    @Test
    void fit_returnsIntersects_whenConflict() {
        var other = new ArrayGridBuilder<Character>(1, 1)
                .cell(0, 0, 'X')
                .immutable();

        GridFitResult result = grid.fit(1, 1, other, false);
        assertEquals(GridFitResult.INTERSECTS, result);

        assertEquals('+', grid.get(1, 1));
    }

    // ── rotate ───────────────────────────────────────────────────────────────

    @Test
    void rotate90_movesValuesCorrectly() {
        Grid<Character> rotated = grid.rotate(90).immutable();

        //. N . 00 01 02
        //W + E 10 11 12
        //. S . 20 21 22
        //
        //. W . 00 01 02
        //S + N 10 11 12
        //. E . 20 21 22

        assertEquals('W', rotated.get(0, 1));
        assertEquals('S', rotated.get(1, 0));
        assertEquals('N', rotated.get(1, 2));
        assertEquals('E', rotated.get(2, 1));
        assertEquals('+', rotated.get(1, 1));
    }

    @Test
    void rotate90_changesBoundsCorrectly() {

        Grid<Character> original = new ArrayGridBuilder<Character>(10, 5)
                .fill('A')
                .immutable();

        Grid<Character> rotated = original.rotate(90).immutable();

        assertEquals(5, rotated.width());
        assertEquals(10, rotated.height());

    }

    @Test
    void rotate180_isSymmetric() {
        Grid<Character> rotated = grid.rotate(180).immutable();

        assertEquals('S', rotated.get(0, 1));
        assertEquals('N', rotated.get(2, 1));
        assertEquals('E', rotated.get(1, 0));
        assertEquals('W', rotated.get(1, 2));
        assertEquals('+', rotated.get(1, 1));
    }

    @Test
    void rotate360_restoresOriginal() {
        Grid<Character> rotated = grid.rotate(360).immutable();

        assertEquals('N', rotated.get(0, 1));
        assertEquals('W', rotated.get(1, 0));
        assertEquals('+', rotated.get(1, 1));
        assertEquals('E', rotated.get(1, 2));
        assertEquals('S', rotated.get(2, 1));
    }

    @Test
    void rotateTwice90_equalsSingle180() {
        MutableArrayGrid<Character> gridA = new ArrayGridBuilder<Character>(3, 3)
                .row(0, new Character[]{null, 'N', null})
                .row(1, new Character[]{'W',  '+', 'E'})
                .row(2, new Character[]{null, 'S', null})
                .mutable();

        MutableArrayGrid<Character> gridB = new ArrayGridBuilder<Character>(3, 3)
                .row(0, new Character[]{null, 'N', null})
                .row(1, new Character[]{'W',  '+', 'E'})
                .row(2, new Character[]{null, 'S', null})
                .mutable();

        gridA.rotate(90);
        gridA.rotate(90);
        gridB.rotate(180);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertEquals(gridB.get(row, col), gridA.get(row, col),
                        "Mismatch at (%d, %d)".formatted(row, col));
            }
        }
    }
}