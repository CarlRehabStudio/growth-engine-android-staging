package com.google.android.apps.miyagi;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SectorRangesTest {

    @Test
    public void sector1_test1() throws Exception {
        assertEquals(0, calcNearestSectorIndex(-30, 4));
        assertEquals(3, calcNearestSectorIndex(30, 4));
    }

    @Test
    public void sector1_test2() throws Exception {
        assertEquals(0, calcNearestSectorIndex(-60, 4));
        assertEquals(3, calcNearestSectorIndex(60, 4));
    }

    @Test
    public void sector2_test1() throws Exception {
        assertEquals(1, calcNearestSectorIndex(-90, 4));
        assertEquals(3, calcNearestSectorIndex(90, 4));
    }

    @Test
    public void sector2_test2() throws Exception {
        assertEquals(1, calcNearestSectorIndex(-91, 4));
        assertEquals(2, calcNearestSectorIndex(91, 4));
    }

    @Test
    public void sector3_test1() throws Exception {
        assertEquals(2, calcNearestSectorIndex(-180, 4));
        assertEquals(2, calcNearestSectorIndex(180, 4));
    }

    @Test
    public void sector3_test2() throws Exception {
        assertEquals(2, calcNearestSectorIndex(-267, 4));
        assertEquals(1, calcNearestSectorIndex(267, 4));
    }

    @Test
    public void sector4_test1() throws Exception {
        assertEquals(3, calcNearestSectorIndex(-270, 4));
        assertEquals(1, calcNearestSectorIndex(270, 4));
    }

    @Test
    public void sector4_test2() throws Exception {
        assertEquals(3, calcNearestSectorIndex(-359, 4));
    }

    @Test
    public void sector1_overflow_test1() throws Exception {
        assertEquals(0, calcNearestSectorIndex(-360, 4));
        assertEquals(0, calcNearestSectorIndex(-(360 + 30), 4));
    }

    @Test
    public void sector2_overflow_test1() throws Exception {
        assertEquals(1, calcNearestSectorIndex(-(360 + 90), 4));
    }

    private int calcNearestSectorIndex(double currentAngle, int sectorsNum) {
        double angle = -currentAngle % 360;
        if (angle < 0) {
            angle = 360 + angle;
        }
        //...
        //create sectors ranges
        double currAngle;
        double prevAngle = 0;
        List<SectorRange> sectorRanges = new ArrayList<>();
        for (int i = 0; i < sectorsNum; i++) {
            currAngle = 360 / sectorsNum * (i + 1);
            sectorRanges.add(new SectorRange(i, prevAngle, currAngle));
            prevAngle = currAngle;
        }
        //...
        SectorRange activeRange = null;
        for (SectorRange range : sectorRanges) {
            if (range.contains(angle)) {
                activeRange = range;
                break;
            }
        }
        //...
        return activeRange.getSectorIndex();

    }

    private static class SectorRange {

        private int mSectorIndex;
        private final double mFrom;
        private final double mTo;

        public SectorRange(int sectorIndex, double from, double to) {
            mSectorIndex = sectorIndex;

            mFrom = from;
            mTo = to;
        }

        public boolean contains(double value) {
            return (value >= mFrom && value < mTo);

        }

        public int getSectorIndex() {
            return mSectorIndex;
        }

        public double getFrom() {
            return mFrom;
        }

        public double getTo() {
            return mTo;
        }
    }
}