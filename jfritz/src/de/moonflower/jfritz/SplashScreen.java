package de.moonflower.jfritz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.moonflower.jfritz.utils.JFritzUtils;

public class SplashScreen extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1231912446567250102L;

	private JLabel statusBar;
	private JLabel versionPanel;
	
	public SplashScreen()
	{
		super();
		this.setUndecorated(true);
		String splashPath = JFritzUtils.getFullPath(JFritzUtils.rootID);
		ImageIcon imageIcon = new ImageIcon(splashPath +"/splash.png");
		JLabel imageLabel = new JLabel(imageIcon);
		getContentPane().add(imageLabel, BorderLayout.CENTER);
	
		BackgroundPanel statusPanel = new BackgroundPanel(splashPath +"/status.png");
		statusBar = new JLabel("");		
		versionPanel = new JLabel("");
		versionPanel.setForeground(Color.white);
		Dimension statusSize = new Dimension(100, 20);
		statusBar.setForeground(Color.white);
		statusBar.setPreferredSize(statusSize);
		statusBar.setMaximumSize(statusSize);
		statusBar.setMaximumSize(statusSize);
		statusPanel.add(statusBar, BorderLayout.CENTER);
		statusPanel.add(versionPanel, BorderLayout.EAST);
		statusPanel.setOpaque(true);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
		
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setTitle(Main.PROGRAM_NAME + " v"+Main.PROGRAM_VERSION);
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(getClass()
				.getResource("/de/moonflower/jfritz/resources/images/trayicon.png"))); //$NON-NLS-1$		
		this.pack();
		this.setLocation((screenDim.width / 2) - (this.getWidth() / 2), 
				(screenDim.height / 2) - (this.getHeight() / 2));
		this.setVisible(true);
	}
	
	public void setVersion(String version)
	{
		versionPanel.setText(" " + version + "   ");
	}
	
	public void setStatus(String status)
	{
		statusBar.setText("   " +status);
	}
	
	public class BackgroundPanel extends JPanel {
		private Image img ;
		public BackgroundPanel(String background) {
			setLayout( new BorderLayout() ) ;
			img = new ImageIcon( background ).getImage() ;
			if( img == null ) {
				System.out.println( "Image is null" );
			}
			if( img.getHeight(this) <= 0 || img.getWidth( this ) <= 0 ) {
				System.out.println( "Image width or height must be positive" );
			}
		}
		public void drawBackground( Graphics g ) {
			int w = getWidth() ;
			int h = getHeight() ;
			int iw = img.getWidth( this ) ;
			int ih = img.getHeight( this ) ;
			for( int i = 0 ; i < w ; i+=iw ) {
				for( int j = 0 ; j < h ; j+= ih ) {
					g.drawImage( img , i , j , this ) ;
				}
			}
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawBackground( g ) ;
		}
	}
}
