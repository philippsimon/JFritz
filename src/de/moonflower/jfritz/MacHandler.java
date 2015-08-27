package de.moonflower.jfritz;

import javax.swing.SwingUtilities;

import java.lang.reflect.*;

import de.moonflower.jfritz.JFritz;
import de.moonflower.jfritz.utils.Debug;



/**
 * @author rob
 * 
 * Handles events thrown by MacOSX's Application Menubar (Quit, About, Prefs)
 */
public class MacHandler {

	private JFritz jfritz;

	/**
	 * Register quit-, about-, prefsHandler
	 * 
	 *            An instance of JFritz
	 */
	public MacHandler(JFritz jfritz) {
		this.jfritz = jfritz;
		try {

			System.setProperty("apple.laf.useScreenMenuBar", "true");  //$NON-NLS-1$,  //$NON-NLS-2$
			System
					.setProperty(
							"com.apple.mrj.application.apple.menu.about.name", //$NON-NLS-1$
							"JFritz"); //$NON-NLS-1$
			System.setProperty("com.apple.mrj.application.growbox.intrudes", //$NON-NLS-1$
					"false"); //$NON-NLS-1$

			Class quitHandler = Class.forName("com.apple.mrj.MRJQuitHandler"); //$NON-NLS-1$
			Class aboutHandler = Class.forName("com.apple.mrj.MRJAboutHandler"); //$NON-NLS-1$
			Class prefsHandler = Class.forName("com.apple.mrj.MRJPrefsHandler"); //$NON-NLS-1$
			Class MRJApplicationUtils = Class
					.forName("com.apple.mrj.MRJApplicationUtils"); //$NON-NLS-1$

			MyInvocationHandler invocationHandler = new MyInvocationHandler();

			// Array of arguments for calling method
			Object argslist[] = new Object[1];
			// Array of argument types
			Class[] types = new Class[1];

			Debug.msg("MAC: Register quitHandler"); //$NON-NLS-1$
			Proxy proxy = (Proxy) Proxy.newProxyInstance(quitHandler
					.getClassLoader(), new Class[] { quitHandler },
					invocationHandler);

			argslist[0] = proxy;
			types[0] = quitHandler;
			Method registerQuitHandler = MRJApplicationUtils.getMethod(
					"registerQuitHandler", types); //$NON-NLS-1$
			registerQuitHandler.invoke(proxy, argslist);

			Debug.msg("MAC: Register aboutHandler"); //$NON-NLS-1$
			proxy = (Proxy) Proxy.newProxyInstance(aboutHandler
					.getClassLoader(), new Class[] { aboutHandler },
					invocationHandler);

			argslist[0] = proxy;
			types[0] = aboutHandler;
			Method registerAboutHandler = MRJApplicationUtils.getMethod(
					"registerAboutHandler", types); //$NON-NLS-1$
			registerAboutHandler.invoke(proxy, argslist);

			Debug.msg("MAC: Register prefsHandler"); //$NON-NLS-1$
			proxy = (Proxy) Proxy.newProxyInstance(prefsHandler
					.getClassLoader(), new Class[] { prefsHandler },
					invocationHandler);

			argslist[0] = proxy;
			types[0] = prefsHandler;
			Method registerPrefsHandler = MRJApplicationUtils.getMethod(
					"registerPrefsHandler", types); //$NON-NLS-1$
			registerPrefsHandler.invoke(proxy, argslist);

		} catch (Throwable e) {
			System.err.println(e);
		}
	}

	/**
	 * Handles thrown Events (handleQuit, handleAbout, handlePrefs)
	 * 
	 * @author rob
	 */
	public class MyInvocationHandler implements InvocationHandler {

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			if (method.getName().equalsIgnoreCase("handleQuit")) { //$NON-NLS-1$
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Debug.msg("MAC Application Menu: Show Exit Dialog"); //$NON-NLS-1$
						jfritz.maybeExit(0);
					}
				});

				
				//throw new IllegalStateException("Let the quit handler do it"); //$NON-NLS-1$

				// This is a workaround so that we can convince OSX to let
				// jfritz shutdown
				// before stopping the log off / shut down process
				// This would be unnecessary if we didnt exit JFritz with
				// System.exit
				// If we ever do that, you can remove this code here
				// NOTE: Code throws a harmless exception as a side effect

				// ((ApplicationEvent) args[0]).setHandled(true);

				// throw new IllegalStateException("Let the quit handler do
				// it"); //$NON-NLS-1$
			}

			else if (method.getName().equalsIgnoreCase("handleAbout")) { //$NON-NLS-1$
				Debug.msg("MAC Application Menu: Show About Dialog"); //$NON-NLS-1$
                JFritz.getJframe().showAboutDialog();
			} else if (method.getName().equalsIgnoreCase("handlePrefs")) { //$NON-NLS-1$
				Debug.msg("MAC Application Menu: Show Prefs Dialog"); //$NON-NLS-1$
                JFritz.getJframe().showConfigDialog();
			}
			return null;
		}
	}

}
