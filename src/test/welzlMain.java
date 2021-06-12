package test;

import java.util.ArrayList;
import java.util.List;

public class welzlMain {
    public static void main(String[] args) {
        List<Point> p1 = new ArrayList<>();
        List<Point> p2 = new ArrayList<>();
        List<Point> p3 = new ArrayList<>();
        p1.add(new Point(0, 0));
        p1.add(new Point(0, 1));
        p1.add(new Point(1, 0));

        p2.add(new Point(5, -2));
        p2.add(new Point(-3, -2));
        p2.add(new Point(-2, 5));
        p2.add(new Point(1, 6));
        p2.add(new Point(0, 2));
        p2.add(new Point(4, 4));
        p2.add(new Point(1, 2));
        p2.add(new Point(1, 3));

        Circle c1 = StatLib.makeCircle(p1);
        Circle c2 = StatLib.makeCircle(p2);

        System.out.println("c1 center: (" + c1.center.x + "," + c1.center.y + ")   c1 radius: " + c1.radius);
        System.out.println("c2 center: (" + c2.center.x + "," + c2.center.y + ")   c2 radius: " + c2.radius);

        TimeSeries timeSeries = new TimeSeries("reg_flight.csv");
        float[][] data = timeSeries.getData();
        Point[] p = StatLib.arrToPoints(data[24], data[25]);
        for (Point a : p)
            p3.add(a);
        Circle c3 = StatLib.makeCircle(p3);
        System.out.println("c3 center: (" + c3.center.x + "," + c3.center.y + ")   c3 radius: " + c3.radius);
        TimeSeries ts = new TimeSeries("reg_flight.csv");
        TimeSeries ts2 = new TimeSeries("anomaly_flight.csv");
        //Linear Regression
        //Learning phase
        SimpleAnomalyDetector ad = new SimpleAnomalyDetector();
        ad.learnNormal(ts);
        List<CorrelatedFeatures> cf = ad.getNormalModel();
        //Detecting phase
        List<AnomalyReport> LRReports = ad.detect(ts2);
        //Printing results
        int i = 0;
        for (AnomalyReport r : LRReports) {
            System.out.println(r.description + " at: " + r.timeStep);
            ++i;
        }
        if (i < 190)
            System.out.println("Linear Reg did not detect all the anomalies.");
        else if (i > 210)
            System.out.println("Linear Reg detector detected too many anomalies.");
        else
            System.out.println("Linear Reg detected all the anomalies. Well Done!");
        System.out.println(i + " Anomalies were detected. (Out of ~200)");

        //Z-Score
        //Learning phase
        ZscoreAnomalyDetector zad = new ZscoreAnomalyDetector();
        zad.learnNormal(ts);
        //Detecting phase
        List<AnomalyReport> ZSReports = zad.detect(ts2);

        //Printing results
        int j = 0;
        for (AnomalyReport r : ZSReports) {
            System.out.println(r.description + " at: " + r.timeStep);
            ++j;
        }
        if (j < 190)
            System.out.println("Z-score detector did not detect all the anomalies.");
        else if (j > 210)
            System.out.println("Z-score detector detected too many anomalies.");
        else
            System.out.println("Z-score detected all the anomalies. Well Done!");
        System.out.println(j + " Anomalies were detected. (Out of ~200)");

        //Hybrid
        //Learning phase
        HybridAnomalyDetector hyb = new HybridAnomalyDetector();
        hyb.learnNormal(ts);
        //Detecting phase
        List<AnomalyReport> hybReports = hyb.detect(ts2);

        //Printing results
        int k = 0;
        for (AnomalyReport r : hybReports) {
            System.out.println(r.description + " at: " + r.timeStep);
            ++k;
        }
        if (k < 190)
            System.out.println("Hybrid detector did not detect all the anomalies.");
        else if (k > 210)
            System.out.println("Hybrid detector detected too many anomalies.");
        else
            System.out.println("hybrid detected all the anomalies. Well Done!");
        System.out.println(k + " Anomalies were detected. (Out of ~200)");

//        TimeSeries ts4 = new TimeSeries("trainFile1.csv");
//        TimeSeries ts5 = new TimeSeries("testFile1.csv");
//        SimpleAnomalyDetector simp = new SimpleAnomalyDetector();
//        HybridAnomalyDetector hyb2 = new HybridAnomalyDetector();
//        hyb2.learnNormal(ts4);
//        simp.learnNormal(ts4);
//        List<AnomalyReport> simp2 = simp.detect(ts5);
//        List<AnomalyReport> hybid2 = hyb2.detect(ts5);
//        int w = 0;
//        for (AnomalyReport r : hybid2) {
//            System.out.println(r.description + " at: " + r.timeStep);
//            ++w;
//        }
//        System.out.println(w + " Anomalies were detected.");
//
//        int u = 0;
//        for (AnomalyReport r : simp2) {
//            System.out.println(r.description + " at: " + r.timeStep);
//            ++u;
//        }
//        System.out.println(u + " Anomalies were detected.");
//        ZscoreAnomalyDetector zi = new ZscoreAnomalyDetector();
//        zi.learnNormal(ts4);
//        List<AnomalyReport> zii = zi.detect(ts5);
//        int h = 0;
//        for (AnomalyReport r : zii) {
//            System.out.println(r.description + " at: " + r.timeStep);
//            ++h;
//        }
//        System.out.println(h + " Anomalies were detected.");
    }
}
