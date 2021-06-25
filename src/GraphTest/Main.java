package GraphTest;
	
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import test.Point;
import test.StatLib;
import test.TimeSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class Main extends Application {
	@Override
	public void start(Stage stage) {
		test.Circle circle;
	    float minX;
	    float maxX;
	    float minY;
	    float maxY;
	    float maxM;
	    Point[] p;
		TimeSeries regTS = new TimeSeries("reg_flight.csv");
		TimeSeries anomalyTS = new TimeSeries("anomaly_flight.csv");
		List<Point> points = new ArrayList<>();
		float[][] data = regTS.getData();
        p = StatLib.arrToPoints(data[24], data[25]);
        for (Point a : p)
            points.add(a);
        circle = StatLib.makeCircle(points);
        minX = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        data = anomalyTS.getData();
        p = StatLib.arrToPoints(data[24], data[25]);
		for(Point s : p) {
			if(s.x<minX)
				minX=s.x;
			if(s.x>maxX)
				maxX=s.x;
			if(s.y<minY)
				minY=s.y;
			if(s.y>maxY)
				minX=s.y;
		}
		
		if(circle.center.x + circle.radius > maxX)
			maxX = circle.center.x + circle.radius;
		
		if(circle.center.x - circle.radius < minX)
			minX= circle.center.x - circle.radius;
		
		if(circle.center.y + circle.radius > maxY)
			maxY = circle.center.y + circle.radius;
		
		if(circle.center.y - circle.radius < minY)
			minY= circle.center.y - circle.radius;
		
		float maxyX,maxyY;
		if(Math.abs(maxX)>Math.abs(minX))
			maxyX = Math.abs(maxX);
		else 
			maxyX = Math.abs(minX);
		if(Math.abs(maxY)>Math.abs(minY))
			maxyY = Math.abs(maxY);
		else 
			maxyY = Math.abs(minY);
		
		if(maxyY>maxyX)
			maxM=maxyY;
		else
			maxM=maxyX;
		
		int xS,yS;
		xS=(int)(Math.abs(2*maxM)/10);
		yS=(int)(Math.abs(2*maxM)/10);
        Axes axes = new Axes(
                400, 400,
                -(maxM*1.5), (maxM*1.5), xS,
                -(maxM*1.5), (maxM*1.5), yS
        );
       
        
        
        
        Plot plot = new Plot(
                x -> .25 * (x + 4) * (x + 1) * (x - 2),
                circle.center.x - circle.radius, circle.center.x + circle.radius, 0.01,
                axes,circle.radius,circle.center.x,circle.center.y,p,circle
        );

        StackPane layout = new StackPane(
                plot
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgb(35, 39, 50);");

        stage.setTitle("y = \u00BC(x+4)(x+1)(x-2)");
        stage.setScene(new Scene(layout, Color.rgb(35, 39, 50)));
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
    class Axes extends Pane {
        private NumberAxis xAxis;
        private NumberAxis yAxis;

        public Axes(
                int width, int height,
                double xLow, double xHi, double xTickUnit,
                double yLow, double yHi, double yTickUnit
        ) {
            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            setPrefSize(width, height);
            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

            xAxis = new NumberAxis(xLow, xHi, xTickUnit);
            xAxis.setSide(Side.BOTTOM);
            xAxis.setMinorTickVisible(false);
            xAxis.setPrefWidth(width);
            xAxis.setLayoutY(height / 2);

            yAxis = new NumberAxis(yLow, yHi, yTickUnit);
            yAxis.setSide(Side.LEFT);
            yAxis.setMinorTickVisible(false);
            yAxis.setPrefHeight(height);
            yAxis.layoutXProperty().bind(
                Bindings.subtract(
                    (width / 2) + 1,
                    yAxis.widthProperty()
                )
            );

            getChildren().setAll(xAxis, yAxis);
        }

        public NumberAxis getXAxis() {
            return xAxis;
        }

        public NumberAxis getYAxis() {
            return yAxis;
        }
    }

	
    class Plot extends Pane {
        public Plot(
                Function<Double, Double> f,
                double xMin, double xMax, double xInc,
                Axes axes,double r,double xCenter,double yCenter,Point[] p,test.Circle circle
        ) {
            Path path = new Path();
            path.setStroke(Color.ORANGE.deriveColor(0, 1, 1, 0.6));
            path.setStrokeWidth(2);
            
            path.setClip(
                    new Rectangle(
                            0, 0, 
                            axes.getPrefWidth(), 
                            axes.getPrefHeight()
                    )
            );

            double x = xMin;
           // double y = f.apply(x);
            double y = plusC(x, r, xCenter, yCenter);
            double y2;
            path.getElements().add(
                    new MoveTo(
                            mapX(x, axes), mapY(y, axes)
                    )
            );

            x += xInc;
            while (x < xMax) {
               // y = f.apply(x);
            	
            	y = plusC(x, r, xCenter, yCenter);
            	y2 = minusC(x, r, xCenter, yCenter);
                path.getElements().add(
                        new LineTo(
                                mapX(x, axes), mapY(y, axes)
                        )
                );
                path.getElements().add(
                        new LineTo(
                                mapX(x, axes), mapY(y2, axes)
                        )
                );
                

                x += xInc;
            }
            
        	y = plusC(xMax, r, xCenter, yCenter);
            path.getElements().add(
                    new LineTo(
                            mapX(x, axes), mapY(y, axes)
                    )
            );
            
            
            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

            getChildren().setAll(axes, path);
            
            for(Point s : p) {
            	if(StatLib.is_inside(circle, s)) {
            		Circle c = new Circle();
            		c.setRadius(2);
            		c.setCenterX(mapX(s.x, axes));
            		c.setCenterY(mapY(s.y, axes));
            		c.setFill(Color.GREEN);
            		getChildren().add(c);
            	}
            	else
            	{
            		Circle c = new Circle();
            		c.setRadius(2);
            		c.setCenterX(mapX(s.x, axes));
            		c.setCenterY(mapY(s.y, axes));
            		c.setFill(Color.RED);
            		getChildren().add(c);
            	}
            }
            
            
        }

        private double mapX(double x, Axes axes) {
            double tx = axes.getPrefWidth() / 2;
            double sx = axes.getPrefWidth() / 
               (axes.getXAxis().getUpperBound() - 
                axes.getXAxis().getLowerBound());

            return x * sx + tx;
        }

        private double mapY(double y, Axes axes) {
            double ty = axes.getPrefHeight() / 2;
            double sy = axes.getPrefHeight() / 
                (axes.getYAxis().getUpperBound() - 
                 axes.getYAxis().getLowerBound());

            return -y * sy + ty;
        }
    }
	
	
    public double plusC(double x,double r,double xCenter,double yCenter) {
    	
    	double y = Math.sqrt(Math.pow(r, 2) - Math.pow((x - xCenter),2)) + yCenter;
    	
    	return y;
    }
    
    public double minusC(double x,double r,double xCenter,double yCenter) {
    	
    	double y = -1*(Math.sqrt(Math.pow(r, 2) - Math.pow((x - xCenter),2))) + yCenter;
    	
    	return y;
    }
    
    
}
