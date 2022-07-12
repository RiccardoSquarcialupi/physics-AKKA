package model;

import lib.Body;
import lib.Boundary;
import lib.P2d;
import lib.V2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationModel {

    private List<Body> bodies;
    private Boundary bounds;
    private long totalIter;
    private long iter;
    private int nBodies;
    /* virtual time */
    private double vt = 0;
    /* virtual time step */
    private double dt = 0.001;

    public SimulationModel(final int nBodies, final long totalIter){
        this.bounds = new Boundary(-6.0, -6.0, 6.0, 6.0);
        this.nBodies = nBodies;
        this.totalIter = totalIter;
        this.iter = 0;
    }

    // generate bodies
    public void init(){
        this.bodies = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < nBodies; i++) {
            double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
            bodies.add(b);
        }
    }

    public void updateVirtualTime(){
        vt = vt + dt;
        iter++;
    }

    public long getTotalIter() {
        return totalIter;
    }

    public boolean isCompleted(){
        return totalIter == iter;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Boundary getBounds() {
        return bounds;
    }

    public long getIter() {
        return iter;
    }

    public double getVt() {
        return vt;
    }

    public int getnBodies() { return nBodies;}

    public double getDt() { return dt;}

    public void setBodies(ArrayList newBodies){
        this.bodies = new ArrayList<>(newBodies);
    }

}
