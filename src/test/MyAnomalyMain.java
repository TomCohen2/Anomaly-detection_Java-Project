package test;

import java.util.List;

public class MyAnomalyMain {

	public static void main(String[] args) {

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
		int i=0;
		for (AnomalyReport r : LRReports) {
				System.out.println(r);
				++i;
		}
		if(i<190) 
			System.out.println("Linear Reg did not detect all the anomalies.");
		else if (i>210) 
			System.out.println("Linear Reg detector detected too many anomalies.");
		else
			System.out.println("Linear Reg detected all the anomalies. Well Done!");
		System.out.println(i + " Anomalies were detected. (Out of ~200)");
		
		for (CorrelatedFeatures cr : ad.getCorList()) {
			System.out.println(cr.feature1 + " " + cr.corrlation + " " + cr.feature2);
		}
		//Z-Score
		//Learning phase
//		ZscoreAnomalyDetectorV2 zad = new ZscoreAnomalyDetectorV2();
//		zad.learnNormal(ts);
//		//Detecting phase
//		List<AnomalyReport> ZSReports = zad.detect(ts2);
//		
//		//Printing results
//		int j=0;
//		for (AnomalyReport r : ZSReports) {
//			System.out.println(r);
//			++j;
//		}
//		if(j<190) 
//			System.out.println("Z-score detector did not detect all the anomalies.");
//		else if (j>210) 
//			System.out.println("Z-score detector detected too many anomalies.");
//		else
//			System.out.println("Z-score detected all the anomalies. Well Done!");
//		System.out.println(j + " Anomalies were detected. (Out of ~200)");
//		
//		//Hybrid
//		//Learning phase
//				HybridAnomalyDetector had = new HybridAnomalyDetector();
//				had.learnNormal(ts);
//				
//				//Detecting phase
//				List<AnomalyReport> HybridReports = had.detect(ts2);
//				//Printing results
//				int k=0;
//				for (AnomalyReport r : HybridReports) {
//						System.out.println(r);
//						++k;
//				}
//				if(k<190) 
//					System.out.println("Hybrid did not detect all the anomalies.");
//				else if (k>210) 
//					System.out.println("Hybrid detector detected too many anomalies.");
//				else
//					System.out.println("Hybrid detected all the anomalies. Well Done!");
//				System.out.println(k + " Anomalies were detected. (Out of ~200)");
	}
}

// First conclusion -> LR works!
