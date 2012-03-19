package com.telmomenezes.synthetic.evo;

import com.telmomenezes.synthetic.DRMap;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.generators.GPGen2P;
import com.telmomenezes.synthetic.generators.Generator;

public class EvoDRMap2P implements EvoGenCallbacks {
    private Net targNet;
    private DRMap targDRMap;
    private Generator gen;
    private int nodeCount;
    private int edgeCount;
    private double enratio;
    private long effort;
    private long maxEffort;
    
    public EvoDRMap2P(Net targNet, long maxEffort) {
        this.targNet = targNet;
        this.targDRMap = genDRMap(targNet);
        this.maxEffort = maxEffort;
        
        nodeCount = targNet.getNodeCount();
        edgeCount = targNet.getEdgeCount();
        
        effort = 2 * nodeCount * edgeCount;
        
        // in case max effort is exceeded...
        if (effort > maxEffort) {
            enratio = ((double)edgeCount) / ((double)nodeCount);
            nodeCount = (int)Math.sqrt(((double)maxEffort) / enratio);
            edgeCount = (int)(nodeCount * enratio);
            effort = maxEffort;
        }
        
        gen = new GPGen2P(nodeCount, edgeCount);
    }
    
    public Generator baseGenerator() {
        return gen;
    }

    public double computeFitness(Generator gen) {
        Net net = gen.run();
        DRMap drmap = genDRMap(net);
        return targDRMap.emdDistance(drmap);
    }

    public void onGeneration(EvoGen evo) {
        System.out.println(">> gen: " + evo.getCurgen() + "; best fit: " + evo.getBestFitness() + "; best gen fit: " + evo.getBestGenFitness());
        System.out.println("     gen time: " + evo.getGenTime() + "; fit time: " + evo.getFitTime());
    }

    private DRMap genDRMap(Net net) {
        net.computePageranks();
        
        DRMap drmap = net.getDRMapWithLimit(10, -7, 7, -7, 7);
        drmap.logScale();
        drmap.normalizeMax();
        
        return drmap;
    }
    
    public String infoString() {
        String str = "target net node count: " + targNet.getNodeCount() + "\n";
        str += "target net edge count: " + targNet.getEdgeCount() + "\n";
        str += "generated nets node count: " + nodeCount + "\n";
        str += "generated nets edge count: " + edgeCount + "\n";
        str += "edge/node ratio: " + enratio + "\n";
        str += "max effort: " + maxEffort + "\n";
        str += "effort: " + effort + "\n";
        
        return str;
    }
}