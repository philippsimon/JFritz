package de.moonflower.jfritz.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * A Button with 3 states for the FilterButtons of the CallerListpanel
 * 
 * @author marc
 * 
 */
public class ThreeStateButton extends JButton implements ActionListener {
	private int state;

	public static final int NOTHING = 0;
	
	public static final int SELECTED = 2;

	public static final int INVERTED = 1;


	private Icon[] icons = new Icon[3];

	/**
	 * Creates a Button with 3 images for the 3 states
	 * 
	 * @param image
	 *            this image is used for the selected state a grey image is
	 *            created from this image for the NOTHING state and a crossed
	 *            for the SELECTED_NOT state
	 */
	public ThreeStateButton(ImageIcon image) {
		super(image);
		state = NOTHING;
		icons[NOTHING] = image;
		addActionListener(this);
	}
	/**
	 *@depreceated use setState(int state) with SELECTED  
	 */
	
	public void setSelected(boolean b){
		super.setSelected(b);
	}
	/**
	 * 
	 * @param image
	 * @param cross
	 * @param grey
	 */
	public ThreeStateButton(ImageIcon image, ImageIcon cross, ImageIcon grey) {
		this(image);
		icons[INVERTED] = cross;
		icons[SELECTED] = grey;
	}

	/**
	 * Draws a cross through the image on the icon
	 * 
	 * @param imageIcon
	 *            this should be a ImageIcon
	 * @param width
	 *            width of the icon
	 * @param height
	 *            height of the icon
	 * @return the icon with the crossed image
	 */
	private static ImageIcon crossIcon(Icon imageIcon, int width, int height) {
		Image i1 = ((ImageIcon) imageIcon).getImage();
		Image image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();

		g.drawImage(i1, 0, 0, null);
		g.setColor(new Color(255, 0, 0));
		g.drawLine(0, 0, width - 1, height - 1);
		g.drawLine(1, 0, width - 1, height - 2);
		g.drawLine(0, 1, width - 2, height - 1);
		g.drawLine(0, height - 1, width - 1, 0);
		g.drawLine(0, height - 2, width - 2, 0);
		g.drawLine(1, height - 1, width - 1, 1);
		ImageIcon result = new ImageIcon(image);
		return result;
	}

	/**
	 * Makes the image on the item "disabled" normally grey depeding on the look
	 * and feel
	 * 
	 * @param icon
	 * @return the disabled icon
	 */
	private Icon greyIcon(Icon icon) {
		Icon result = UIManager.getLookAndFeel().getDisabledIcon(this, icon);
		return result;
	}

	/*
	 * private ImageIcon composeIcons(ImageIcon src1, ImageIcon src2){
	 * //BufferedImage src2 = new
	 * BufferedImage(120,120,BufferedImage.TYPE_INT_RGB); Image i1 =
	 * src1.getImage(); Image i2 = src2.getImage(); Image image = new
	 * BufferedImage(16, 16, ColorSpace.TYPE_3CLR); Graphics g =
	 * image.getGraphics(); //
	 * g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
	 * 0.3f)); g.drawImage(i1,0,0,null); g.drawImage(i2, 0, 0,null); ImageIcon
	 * result = new ImageIcon(image); return result; }
	 */

	private int getNextState() {
		return (state + 1) % 3;
		/*
		 * if (state == SELECTED) state = SELECTED_NOT; else if (state ==
		 * SELECTED_NOT) state = NOTHING; else if (state == NOTHING) state =
		 * SELECTED;
		 */
	}

	private static final long serialVersionUID = 1L;

	public Icon getNothingIcon() {
		return icons[NOTHING];
	}

	public void setNothingIcon(ImageIcon nothingIcon) {
		icons[NOTHING] = nothingIcon;
	}

	public Icon getSelectedIcon() {
		return icons[SELECTED];
	}

	public void setSelectedIcon(ImageIcon selectedIcon) {
		icons[SELECTED] = selectedIcon;
	}

	public Icon getSelectedNotIcon() {
		return icons[INVERTED];
	}

	public void setSelectedNotIcon(ImageIcon selectedNotIcon) {
		icons[INVERTED] = selectedNotIcon;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	// we need to be faster than all other Listeners
	protected void fireActionPerformed(ActionEvent event){
		state = getNextState();
		Debug.msg("state: " + stateToString(state));
		setIcon(getCurrentIcon()); // dont use icons[state] we need to load some icons first
		super.fireActionPerformed(event);
	}

	public void actionPerformed(ActionEvent e) {
		//nothing to do we did all work in protected void fireActionPerformed(ActionEvent event){
		// but we have to be sure the ActionListenerList is not empty
	}
	
	private String stateToString(int state){
		if(state==SELECTED)
			return "SELECTED";		
		if(state==INVERTED)
			return "SELECTED_NOT";		
		if(state==NOTHING)
			return "NOTHING";
		return "No known state chosen this must be an error";
	}
	private Icon getCurrentIcon() {
		// lazy loading here, because you cant get the images in the
		// constructor, i think the look and deel is not set yet, so we cant get
		// the
		// greyIcon
		if (icons[SELECTED] == null) {
			icons[SELECTED] = greyIcon(icons[NOTHING]);
		}
		if (icons[INVERTED] == null) {
			icons[INVERTED] = crossIcon(icons[NOTHING], icons[NOTHING]
					.getIconWidth(), icons[NOTHING].getIconHeight());
		}
		return icons[state];
	}

}
