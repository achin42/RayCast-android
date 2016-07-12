package com.algo.raycast;

class LineUtil {

  /**
   * Tests if the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2) intersects
   * the line segment from (X3,&nbsp;Y3) to (X4,&nbsp;Y4).
   * 
   * @param X1
   *            ,&nbsp;Y1 the coordinates of the beginning of the first
   *            specified line segment
   * @param X2
   *            ,&nbsp;Y2 the coordinates of the end of the first specified
   *            line segment
   * @param X3
   *            ,&nbsp;Y3 the coordinates of the beginning of the second
   *            specified line segment
   * @param X4
   *            ,&nbsp;Y4 the coordinates of the end of the second specified
   *            line segment
   * @return <code>true</code> if the first specified line segment and the
   *         second specified line segment intersect each other;
   *         <code>false</code> otherwise.
   */
  public static boolean linesIntersect(final double X1, final double Y1, final double X2, final double Y2,
      final double X3, final double Y3, final double X4, final double Y4) {
    return ((relativeCCW(X1, Y1, X2, Y2, X3, Y3)
        * relativeCCW(X1, Y1, X2, Y2, X4, Y4) <= 0) && (relativeCCW(X3,
            Y3, X4, Y4, X1, Y1)
            * relativeCCW(X3, Y3, X4, Y4, X2, Y2) <= 0));
  }

  /**
   * Returns an indicator of where the specified point (PX,&nbsp;PY) lies with
   * respect to the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2). The
   * return value can be either 1, -1, or 0 and indicates in which direction
   * the specified line must pivot around its first endpoint, (X1,&nbsp;Y1),
   * in order to point at the specified point (PX,&nbsp;PY).
   * <p>
   * A return value of 1 indicates that the line segment must turn in the
   * direction that takes the positive X axis towards the negative Y axis. In
   * the default coordinate system used by Java 2D, this direction is
   * counterclockwise.
   * <p>
   * A return value of -1 indicates that the line segment must turn in the
   * direction that takes the positive X axis towards the positive Y axis. In
   * the default coordinate system, this direction is clockwise.
   * <p>
   * A return value of 0 indicates that the point lies exactly on the line
   * segment. Note that an indicator value of 0 is rare and not useful for
   * determining colinearity because of floating point rounding issues.
   * <p>
   * If the point is colinear with the line segment, but not between the
   * endpoints, then the value will be -1 if the point lies
   * "beyond (X1,&nbsp;Y1)" or 1 if the point lies "beyond (X2,&nbsp;Y2)".
   * 
   * @param X1
   *            ,&nbsp;Y1 the coordinates of the beginning of the specified
   *            line segment
   * @param X2
   *            ,&nbsp;Y2 the coordinates of the end of the specified line
   *            segment
   * @param PX
   *            ,&nbsp;PY the coordinates of the specified point to be
   *            compared with the specified line segment
   * @return an integer that indicates the position of the third specified
   *         coordinates with respect to the line segment formed by the first
   *         two specified coordinates.
   */
  private static int relativeCCW(final double X1, final double Y1, double X2, double Y2, double PX,
      double PY) {
    X2 -= X1;
    Y2 -= Y1;
    PX -= X1;
    PY -= Y1;
    double ccw = PX * Y2 - PY * X2;
    if (ccw == 0) {
      // The point is colinear, classify based on which side of
      // the segment the point falls on. We can calculate a
      // relative value using the projection of PX,PY onto the
      // segment - a negative value indicates the point projects
      // outside of the segment in the direction of the particular
      // endpoint used as the origin for the projection.
      ccw = PX * X2 + PY * Y2;
      if (ccw > 0) {
        // Reverse the projection to be relative to the original X2,Y2
        // X2 and Y2 are simply negated.
        // PX and PY need to have (X2 - X1) or (Y2 - Y1) subtracted
        // from them (based on the original values)
        // Since we really want to get a positive answer when the
        // point is "beyond (X2,Y2)", then we want to calculate
        // the inverse anyway - thus we leave X2 & Y2 negated.
        PX -= X2;
        PY -= Y2;
        ccw = PX * X2 + PY * Y2;
        if (ccw < 0) {
          ccw = 0;
        }
      }
    }
    return (ccw < 0) ? -1 : ((ccw > 0) ? 1 : 0);
  }

}