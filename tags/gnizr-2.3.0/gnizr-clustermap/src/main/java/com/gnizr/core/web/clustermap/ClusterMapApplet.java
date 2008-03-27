/*
 * gnizr is a trademark of Image Matters LLC in the United States.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either expressed or implied. See the License
 * for the specific language governing rights and limitations under the License.
 * 
 * The Initial Contributor of the Original Code is Image Matters LLC.
 * Portions created by the Initial Contributor are Copyright (C) 2007
 * Image Matters LLC. All Rights Reserved.
 */
package com.gnizr.core.web.clustermap;

import info.aduna.clustermap.ClusterMapMediator;
import info.aduna.clustermap.ClusterMapMediatorListener;
import info.aduna.clustermap.ClusterMapUI;
import info.aduna.clustermap.DefaultObject;
import info.aduna.clustermap.GraphPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * <p>An applet version of the Aduna Clustermap application (2006.3-beta2). This implementation
 * wraps the core functions provided by the Clustermap application. Some functions such 
 * as opening a URL link can't be directly ported into a browser environment. This class overrides
 * those functions.</p>
 * <p>To use this applet, the <code>APPLET</code> in the client HTML page must defined a 
 * <code>PARAM</code> value called <code>dataSourceUrl</code>.
 * </p> 
 * <p>
 * <pre>
 * &ltapplet code="com.gnizr.core.web.clustermap.ClusterMapApplet.class" 
 *      codebase="/gnizr/applets"
 *      archive="gnizr-clustermap-applet.jar"
 *       width="100" height="100"&gt
 *     &ltparam name=datasourceurl value="[url-points-to-a-clustermap-data-XML-document]"
 * &lt/applet&gt
 * </pre>
 * </P>
 * @author Harry Chen
 *
 */
public class ClusterMapApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 477476797909541563L;

	private static final Logger logger = Logger
			.getLogger(ClusterMapApplet.class);

	/**
	 * Applet paramater name <code>dataSourceUrl</code>, which defines the
	 * URL of an XML document that defines the data model of this
	 * <code>ClusterMapApplet</code>
	 */
	public static final String DATA_SOURCE_URL = "dataSourceUrl";

	private ClusterMapMediator mediator;

	private ClusterMapUI clusterMapGui;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
	}

	/**
	 * Initializes the applet UI
	 */
	@Override
	public void init() {
		// Execute a job on the event-dispatching thread:
		// creating this applet's GUI.
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
			
		} catch (Exception e) {
			logger.error("createGUI didn't successfully complete");
		}
	}

	private ProgressMonitor progressMonitor;

	private void createGUI() {
		clusterMapGui = new ClusterMapUI();
		mediator = clusterMapGui.getMediator();
		registerDoubleClickListener(mediator);
		add(clusterMapGui);
	
		progressMonitor = new ProgressMonitor(this.getRootPane(),"Building Clustermap","",0,3);
		progressMonitor.setMillisToPopup(0);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setNote("Fetching data... Please wait...");
		progressMonitor.setProgress(1);
		
		// disable user input. must watch the progress monitor.
		getContentPane().setEnabled(false);
		mediator.addClusterMapMediatorListener(new ClusterMapMediatorListener(){
			public void classificationTreeChanged(ClusterMapMediator cmm) {
				progressMonitor.setProgress(3);
				getContentPane().setEnabled(true);
			}

			public void propertyChanged(String arg0, Object arg1, ClusterMapMediator arg2) {
				// no code				
			}

			public void visualisedClassesChanged(ClusterMapMediator arg0) {
				// no code
			}			
		});			
	}

	private void registerDoubleClickListener(ClusterMapMediator mediator) {
		final GraphPanel panel = mediator.getGraphPanel();

		// a hack to disable the default double-click listener
		// that comes with the clustermap API, which is impl
		// for a Java Application not an Applet
		MouseListener[] listeners = panel.getMouseListeners();
		//for (int i = 0; i < listeners.length; i++) {
		if(listeners != null && listeners.length == 2)
			panel.removeMouseListener(listeners[1]);
		//}

		// adds our own MouseListener for opening
		// URL in applet context.
		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					// the coord where the user has double-clicked
					int x = e.getX();
					int y = e.getY();
					// resolve the object if the user double-clicked on
					// a cluster object.
					Object obj = panel.resolveObject(x, y);
					if (obj != null && (obj instanceof DefaultObject)) {
						DefaultObject defObj = (DefaultObject) obj;
						if (defObj.getLocation() != null) {
							URL url;
							try {
								url = new URL(defObj.getLocation());
								getAppletContext().showDocument(url, "_blank");
							} catch (MalformedURLException e1) {
								logger.error(e1);
							}
						}
					}
				}
			}
		});
	}

	/**
	 * Load clustermap data model XML from the defined URL
	 */
	@Override
	public void start() {
		final String xmlDataSource = getParameter(DATA_SOURCE_URL);
		logger.debug("PARAM: " + DATA_SOURCE_URL + "=" + xmlDataSource);
		if (xmlDataSource != null) {
			try {
				Thread t = new Thread(new Runnable() {
					public void run() {
						reloadData(xmlDataSource);
					}
				});
				t.start();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	private void reloadData(String dataSourceUrl){		
		try {
			URL url = new URL(dataSourceUrl);
			InputStream is = url.openStream();
			if(progressMonitor.isCanceled() == false){
				progressMonitor.setNote("Building classifications...");
				progressMonitor.setProgress(2);
				mediator.loadClassificationTree(is);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Disposes the UI of Clustermap
	 */
	@Override
	public void stop() {
		super.stop();
		clusterMapGui.dispose();
	}

}
