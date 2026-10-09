

/* 
 * Copyright (C) 2016 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.weather.display.windrose;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

/**
 * A specialized polar plot that displays wind speed and direction summary graphically.
 * 
 * @author Bruce
 *
 */
public class WindRosePlot extends PolarPlot {
	
    private final DefaultPolarItemRenderer renderer;
    
    private WindRoseData data;
    
    Logger logger = Logger.getLogger(WindRosePlot.class);
    
    private final Color binColor[] = { Color.blue,
    								   Color.green.darker() ,
    								   Color.green,
    								   Color.cyan,
    								   Color.yellow,
    								   Color.orange,
    								   Color.pink,
    								   Color.magenta.brighter(),
    								   Color.red,
    								   Color.red.darker(),
    								   
    								   
    };

    /**
     * Constructor.
     */
    public WindRosePlot() {
        super();

        NumberAxis axis = new NumberAxis("%");
        axis.setTickUnit(new NumberTickUnit(10));
//        axis.setAutoTickUnitSelection(true);
//        axis.setAutoRangeIncludesZero(true);
        setAxis(axis);
        
        renderer = new CompassPolarItemRenderer();
        renderer.setBasePaint(Color.black);
        
        renderer.setShapesVisible(false);
        renderer.setDrawOutlineWhenFilled(false);
        renderer.setFillComposite(AlphaComposite.Src);
        
        setRenderer(renderer);
        setAngleGridlinesVisible(true);
        setAngleGridlinePaint(Color.black);
        setRadiusGridlinesVisible(true);
        setRadiusGridlinePaint(Color.black);
        
    }

    /**
     * Load the wind rose data into the dataset.
     * 
     * @param d The wind rose data
     */
    public void setDataset(WindRoseData d) {
        data = d;
        if (d == null) {
            setDataset((XYSeriesCollection) null);
            return;
        }

        int numSlices = d.getNumSlices();
        double arcLength = (360.0 / numSlices) / 2.0 ;
        double halfArcLength = arcLength / 2.0 ;
        
        double delta =3;  //rupa izm latica 2* delta deg. 
        arcLength = arcLength -delta;
        
        XYSeriesCollection dataset = new XYSeriesCollection();

        logger.info("Creating Wind Rose dataset with "+numSlices+" slices. arcLength = "+arcLength);


        //
        // First go through the wind direction slices. Each weather station can have a different number of
        // direction slices. The minimum is probably 4 and the maximum is probably 360.
        //
        int seriesNumber = 0;
        //double maxPercent=0;
        
        for (int i = 0; i < numSlices; i++) {
            WindSlice slice = d.getSlice(i);

            //
            // The length of the pie slice is determined by the percentage of the wind that was blowing within
            // the slice. Calm winds entries are ignored.
            //
            double percent = slice.getPercentageOfWind();
            
            //if (percent> maxPercent) maxPercent=percent;

            double lastLength = 0.0;

            logger.info( "Slice "+ i +" has "+ slice.getNumSpeedBins()+" speed bins" );

            //
            // Each slice is then divided by speed bins. Each bin is represented by a color and the length
            // of each slice segment is determined by the percentage of time the wind was blowing within the
            // speed bin.
            //
            for (int j = 0; j < slice.getNumSpeedBins(); j++) {
                Heading heading = new Heading(i, numSlices);
                XYSeries series = new XYSeries(heading.toString() + j, false);

                double headingValue = heading.getHeadingValue();
                double binPercentage = slice.getSpeedBinPercentage(j);

                double length = lastLength;

                if (binPercentage != 0.0)
                    length += percent * (binPercentage / 100.0);


                logger.info("Adding series "+j+" : "+ slice.getSpeedBinPercentage(j) +"%" );

                //
                // Each slices speed segment is drawn as a separate series with its own color
                //
                series.add(headingValue - arcLength, lastLength);
                series.add(headingValue - halfArcLength, lastLength);
                series.add(headingValue, lastLength);
                series.add(headingValue + halfArcLength, lastLength);
                series.add(headingValue + arcLength, lastLength);

                series.add(headingValue + arcLength, length);
                series.add(headingValue + halfArcLength, length);
                series.add(headingValue, length);
                series.add(headingValue - halfArcLength, length);
                series.add(headingValue - arcLength, length);

                dataset.addSeries(series);

                renderer.setSeriesPaint(seriesNumber, binColor[j]);
                renderer.setSeriesFilled(seriesNumber++, true);

                lastLength = length;
            }

        }
        
        setDataset(dataset);
        
    }

    /*
     * (non-Javadoc)
     * @see org.jfree.chart.plot.PolarPlot#getLegendItems()
     */
    @Override
    public LegendItemCollection getLegendItems() {
        //
        // Create the legend item to differentiate the speed bins
        //
        LegendItemCollection items = new LegendItemCollection();

        LegendItem defaultItem = new LegendItem("Dummy");

        if (data != null) {
            List<SpeedBin> bins = data.getSpeedBins();

            for (int j = 0; j < bins.size(); j++) {
                LegendItem item = new LegendItem(bins.get(j).getSpeedRange(), "Speed Bin", "", "",
                        defaultItem.getShape(), binColor[j], defaultItem.getOutlineStroke(),
                        defaultItem.getOutlinePaint());

                items.add(item);
            }
        }
        return items;
    }
    
    
    @Override
    protected List refreshAngleTicks() {
        List ticks = new ArrayList();
        // produce some ticks, e.g. NumberTick instances
        ticks.add(new NumberTick(0, "N", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(45, "NE", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(90, "E", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(135, "SE", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(180, "S", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(225, "SW", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(270, "W", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        ticks.add(new NumberTick(315, "NW", TextAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, 0));
        return ticks;
    }
    
}