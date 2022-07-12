package view;

import model.SimulationModel;
import lib.Body;
import lib.Boundary;
import lib.P2d;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import akka.actor.ActorRef;


public class SimulationView{
        
	private VisualiserFrame frame;

	public SimulationView(int w, int h){
		frame = new VisualiserFrame(w,h);
	}

	public void modelUpdated(SimulationModel model) {
		frame.display((ArrayList<Body>) model.getBodies(), model.getVt(), model.getIter(), model.getBounds());
	}

	public JButton getStop(){
		return this.frame.getStopButton();
	}

	public JButton getStart(){
		return this.frame.getStartButton();
	}

	public static class VisualiserFrame extends JFrame {

        private VisualiserPanel panelBodies;
		private JButton startButton;
		private JButton stopButton;

		public JButton getStartButton(){
			return this.startButton;
		}
		public JButton getStopButton(){
			return this.stopButton;
		}

		public VisualiserFrame(int w, int h){
			setTitle("Bodies Simulation");
			setSize(w+200,h+200);
			setResizable(false);
			panelBodies = new VisualiserPanel(w,h);
			getContentPane().add(panelBodies);
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent ev){
					System.exit(-1);
				}
				public void windowClosed(WindowEvent ev){
					System.exit(-1);
				}
			});

			//Start button
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.setSize(100, 50);
			this.startButton = new JButton("Start");
			this.stopButton = new JButton("Stop");
			buttonsPanel.add(this.startButton);
			buttonsPanel.add(this.stopButton);

			startButton.setEnabled(false);

			JPanel mainPanel = new JPanel();
			LayoutManager layout = new BorderLayout();
			mainPanel.setLayout(layout);
			mainPanel.add(BorderLayout.CENTER,panelBodies);
			mainPanel.add(BorderLayout.SOUTH,buttonsPanel);
			setContentPane(mainPanel);

			setLocationRelativeTo(null);
			this.setVisible(true);
		}

        
        public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
        	try {
	        	SwingUtilities.invokeAndWait(() -> {
	        		panelBodies.display(bodies, vt, iter, bounds);
	            	repaint();
	        	});
        	} catch (Exception ex) {}
        };
        
        public void updateScale(double k) {
        	panelBodies.updateScale(k);
        }
	}

    public static class VisualiserPanel extends JPanel implements KeyListener {
        
    	private ArrayList<Body> bodies;
    	private Boundary bounds;
    	
    	private long nIter;
    	private double vt;
    	private double scale = 1;
    	
        private long dx;
        private long dy;
        
        public VisualiserPanel(int w, int h){
            setSize(w,h);
            dx = w/2 - 20;
            dy = h/2 - 20;
			this.addKeyListener(this);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			requestFocusInWindow(); 
        }

        public void paint(Graphics g){    		    		
    		if (bodies != null) {
        		Graphics2D g2 = (Graphics2D) g;
        		
        		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        		          RenderingHints.VALUE_ANTIALIAS_ON);
        		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        		          RenderingHints.VALUE_RENDER_QUALITY);
        		g2.clearRect(0,0,this.getWidth(),this.getHeight());

        		
        		int x0 = getXcoord(bounds.getX0());
        		int y0 = getYcoord(bounds.getY0());
        		
        		int wd = getXcoord(bounds.getX1()) - x0;
        		int ht = y0 - getYcoord(bounds.getY1());
        		
    			g2.drawRect(x0, y0 - ht, wd, ht);
    			
	    		bodies.forEach( b -> {
	    			P2d p = b.getPos();
			        int radius = (int) (10*scale);
			        if (radius < 1) {
			        	radius = 1;
			        }
			        g2.drawOval(getXcoord(p.getX()),getYcoord(p.getY()), radius, radius); 
			    });		    
	    		String time = String.format("%.2f", vt);
	    		g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
    		}
        }
        
        private int getXcoord(double x) {
        	return (int)(dx + x*dx*scale);
        }

        private int getYcoord(double y) {
        	return (int)(dy - y*dy*scale);
        }
        
        public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
            this.bodies = bodies;
            this.bounds = bounds;
            this.vt = vt;
            this.nIter = iter;
        }
        
        public void updateScale(double k) {
        	scale *= k;
        }

		@Override
		public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 38){  		/* KEY UP */
					scale *= 1.1;
				} else if (e.getKeyCode() == 40){  	/* KEY DOWN */
					scale *= 0.9;  
				} 
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
    }
}
