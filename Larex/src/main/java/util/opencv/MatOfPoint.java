package util.opencv;

import static org.bytedeco.javacpp.opencv_core.CV_32S;
import static org.bytedeco.javacpp.opencv_core.CV_CN_MAX;
import static org.bytedeco.javacpp.opencv_core.CV_CN_SHIFT;
import static org.bytedeco.javacpp.opencv_core.CV_DEPTH_MAX;

import java.util.Arrays;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point2d;
import org.bytedeco.javacpp.opencv_core.Range;
import org.bytedeco.javacpp.indexer.IntIndexer;

public class MatOfPoint extends Mat {
    // 32SC2
    private static final int _depth = CV_32S;
    private static final int _channels = 2;

    public MatOfPoint() {
        super();
    }

    protected MatOfPoint(long addr) {
        super(addr);
        if( !empty() && checkVector(_channels, _depth, true) < 0 )
            throw new IllegalArgumentException("Incomatible Mat");
        //FIXME: do we need release() here?
    }

    public static MatOfPoint fromNativeAddr(long addr) {
        return new MatOfPoint(addr);
    }

    public MatOfPoint(Mat m) {
        super(m, Range.all());
        if( !empty() && checkVector(_channels, _depth, true) < 0 )
            throw new IllegalArgumentException("Incomatible Mat");
        //FIXME: do we need release() here?
    }

    public MatOfPoint(Point2d...a) {
        super();
        fromArray(a);
    }

    public void alloc(int elemNumber) {
        if(elemNumber>0)
            super.create(elemNumber, 1, makeType(_depth, _channels));
    }

    public static final int makeType(int depth, int channels) {
        if (channels <= 0 || channels >= CV_CN_MAX) {
            throw new java.lang.UnsupportedOperationException(
                    "Channels count should be 1.." + (CV_CN_MAX - 1));
        }
        if (depth < 0 || depth >= CV_DEPTH_MAX) {
            throw new java.lang.UnsupportedOperationException(
                    "Data type depth should be 0.." + (CV_DEPTH_MAX - 1));
        }
        return (depth & (CV_DEPTH_MAX - 1)) + ((channels - 1) << CV_CN_SHIFT);
    }
    
    public void fromArray(Point2d...a) {
        if(a==null || a.length==0)
            return;
        int num = a.length;
        alloc(num);
        int buff[] = new int[num * _channels];
        for(int i=0; i<num; i++) {
            Point2d p = a[i];
            buff[_channels*i+0] = (int) p.x();
            buff[_channels*i+1] = (int) p.y();
        }
        IntIndexer idx = this.createIndexer();
        idx.put(0, 0, buff); //TODO: check ret val!
    }

    public Point2d[] toArray() {
        int num = (int) total();
        Point2d[] ap = new Point2d[num];
        if(num == 0)
            return ap;
        int buff[] = new int[num * _channels];
        IntIndexer idx = this.createIndexer();
        idx.get(0, 0, buff); //TODO: check ret val!
        for(int i=0; i<num; i++)
            ap[i] = new Point2d(buff[i*_channels], buff[i*_channels+1]);
        return ap;
    }

    public void fromList(List<Point2d> lp) {
        Point2d ap[] = lp.toArray(new Point2d[0]);
        fromArray(ap);
    }

    public List<Point2d> toList() {
        Point2d[] ap = toArray();
        return Arrays.asList(ap);
    }
}
