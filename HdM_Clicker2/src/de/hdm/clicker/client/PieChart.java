package de.hdm.clicker.client;

import com.googlecode.gchart.client.GChart;

import de.hdm.clicker.shared.bo.ChartInfo;

public class PieChart extends GChart {

	public PieChart(ChartInfo ci) {
		
		double entireResults = ci.getWrongs() + ci.getCorrects();
		
		double corrects = (double)Math.round((ci.getCorrects() / entireResults) *100) /100; 
		double wrongs = (double)Math.round((ci.getWrongs() / entireResults) *100) /100;
		
		if (corrects + wrongs < 1) {
			corrects = corrects + (1 - (corrects + wrongs));
		}
		
		StringBuffer sb = new StringBuffer();
		
		int intervall = 30;
		int count = 0;
		int lastIndex = 0;
		for(int i = 0; i < ci.getQuestionBody().length(); i++) {
			count++;
			if (count == intervall) {
				sb.append(ci.getQuestionBody().substring(lastIndex, i+1)+"<br>");
				lastIndex = i+1;
				count = 0;
			}
		}
		if (ci.getQuestionBody().length() % 30 != 0) {
			sb.append(ci.getQuestionBody().substring(lastIndex));
		}
			
		
		double[] pieMarketShare = {corrects,wrongs,};
	     String[] pieTypes = {"Richtig", "Falsch"};
	     String[] pieColors = {"green", "red"};
	       
	     this.setChartSize(300, 200);
	     setChartTitle("<h3>"+sb.toString()+"</h3>");
	     this.setLegendVisible(false);
	     getXAxis().setAxisVisible(false);
	     getYAxis().setAxisVisible(false);
	     getXAxis().setAxisMin(0);
	     getXAxis().setAxisMax(10);
	     getXAxis().setTickCount(0);
	     getYAxis().setAxisMin(0);
	     getYAxis().setAxisMax(10);
	     getYAxis().setTickCount(0);
	// this line orients the center of the first slice (apple) due east
	     setInitialPieSliceOrientation(0.75 - pieMarketShare[0]/2);
	     for (int i=0; i < pieMarketShare.length; i++) {
	       addCurve();
	       getCurve().addPoint(5,5);
	       getCurve().getSymbol().setSymbolType(
	         SymbolType.PIE_SLICE_OPTIMAL_SHADING);
	       getCurve().getSymbol().setBorderColor("white");
	       getCurve().getSymbol().setBackgroundColor(pieColors[i]);
	       // next two lines define pie diameter in x-axis model units
	       getCurve().getSymbol().setModelWidth(6);
	       getCurve().getSymbol().setHeight(0);
	       getCurve().getSymbol().setFillSpacing(3);
	       getCurve().getSymbol().setFillThickness(3);
	       getCurve().getSymbol().setHovertextTemplate(
	         GChart.formatAsHovertext(pieTypes[i] + ", " + 
	            Math.round(100*pieMarketShare[i])+"%"));
	       getCurve().getSymbol().setPieSliceSize(pieMarketShare[i]);
	       getCurve().getPoint().setAnnotationText(pieTypes[i]);
	       getCurve().getPoint().setAnnotationLocation(
	         AnnotationLocation.OUTSIDE_PIE_ARC);
	     }
	}
}
