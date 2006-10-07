package de.moonflower.jfritz.monitoring;

import org.jfree.data.xy.XYSeries;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import java.util.Timer;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.Main;
import de.moonflower.jfritz.monitoring.UpdateInternetTask;

/**
 * Class for displaying monitoring information like current internet
 * or phone usage
 * 
 * Class uses jfreechart to display internet usage as chart
 * 
 * @author brian jensen
 *
 */
public class MonitoringPanel extends JPanel implements ActionListener {

	public static final long serialVersionUID = 1;
	
	private XYSeries inSeries, outSeries;
	
	private XYSeriesCollection collection;
	
	private JFreeChart inetChart;
	
	private JToggleButton enableInetMonitoring;
	
	private Timer timer;
	
	private static int count = 0;
	
	/**
	 * Creates the two monitoring sub panels and initializes everything
	 *
	 */
	public MonitoringPanel(){
		setLayout(new BorderLayout());

		add(createPhonePanel(), BorderLayout.SOUTH);
		add(createInternetPanel(), BorderLayout.NORTH);
		//TODO: Phone usage panel here!
		
		enableInetMonitoring = new JToggleButton(Main.getMessage("enable_inet_monitoring"));
		enableInetMonitoring.setActionCommand("toggleInetMonitoring");
		enableInetMonitoring.addActionListener(this);
		if(Main.getProperty("inet.monitoring", "false").equals("true")){
			enableInetMonitoring.setSelected(true);
			setTimer();
		}else{
			enableInetMonitoring.setSelected(false);
		}

		add(enableInetMonitoring, BorderLayout.SOUTH);
		
	}
	/**
	 * This creates the internet panel, which is the top half of the monitoring tab
	 * 
	 * @return the internet panel
	 */
	public JPanel createInternetPanel(){
		JPanel inetPanel = new JPanel();
		inetPanel.setLayout(new BorderLayout());
		
		inSeries = new XYSeries("In");
		inSeries.add(System.currentTimeMillis(), 0.0);

		outSeries = new XYSeries("Out");
		outSeries.add(System.currentTimeMillis(), 0.0);
		
		collection = new XYSeriesCollection();
		collection.addSeries(inSeries);
		collection.addSeries(outSeries);
		
		inetChart  = ChartFactory.createXYLineChart("Internet Usage", "", "KB/s", collection, 
				PlotOrientation.VERTICAL, true, true, false);

		inetChart.getXYPlot().getDomainAxis().setTickLabelsVisible(false);

		inetPanel.setSize(this.getWidth(), (this.getHeight()/2));
		ChartPanel cp = new ChartPanel(inetChart);
		cp.setSize(this.getWidth(), (this.getHeight()/2));
		
		inetPanel.add(cp, BorderLayout.CENTER);
		
		//we don't want to draw the chart every single time
		inSeries.setNotify(false);
		outSeries.setNotify(false);
		
		return inetPanel;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private JPanel createPhonePanel(){
		JPanel phonePanel = new JPanel();
		phonePanel.setLayout(new BorderLayout());
		
		phonePanel.add(new JLabel("This is where the phone monitor should go"), BorderLayout.CENTER);
		return phonePanel;
	
	}
	
	/**
	 * This function updates the internet monitor with current values
	 * on every 6th update, the graphic is redrawn
	 * 
	 * @param in, the current amount of incoming bytes
	 * @param out, the current amout of outgoing bytes
	 */
	public void updateInternetUsage(String in, String out){
		
		inSeries.add(System.currentTimeMillis(), (Double.parseDouble(in)/1024) );
		outSeries.add(System.currentTimeMillis(), (Double.parseDouble(out)/1024));
		
		if(inSeries.getItemCount() >  145){
			inSeries.remove(0);
			outSeries.remove(0);
		}
		
		count++;
		
		if(count > 5){
			outSeries.setNotify(true);
			inSeries.fireSeriesChanged();
			outSeries.setNotify(false);
			count = 0;
		}
		
	}
	
	public void setStatus(){
		JFritz.getJframe().setStatus("Change Me!");
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("toggleInetMonitoring")){
			if(enableInetMonitoring.isSelected()){
				Main.setProperty("inet.monitoring", "true");
				setTimer();
			}else{
				Main.setProperty("inet.monitoring", "false");
				timer.cancel();
			}
		}

	}
	
	/**
	 * This function sets up the timer for automatic updates
	 * of the current internet status
	 *
	 */
	private void setTimer(){
		//Create timer for updating the values
		timer = new Timer();
		UpdateInternetTask task = new UpdateInternetTask(this);
		timer.schedule(task, 1000, 850);
	}
	
}
