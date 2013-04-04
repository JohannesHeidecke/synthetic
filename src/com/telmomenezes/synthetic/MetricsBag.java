package com.telmomenezes.synthetic;

import com.telmomenezes.synthetic.motifs.TriadicProfile;


public class MetricsBag {
	private Net net;
	private int bins;
	
	private DiscreteDistrib degrees;
	private DiscreteDistrib inDegrees;
    private DiscreteDistrib outDegrees;
    private Distrib dPageRanks;
    private Distrib uPageRanks;
    private TriadicProfile triadicProfile;
    private DiscreteDistrib dDists;
    private DiscreteDistrib uDists;
    
    private double degreesDist;
    private double inDegreesDist;
    private double outDegreesDist;
    private double dPageRanksDist;
    private double uPageRanksDist;
    private double triadicProfileDist;
    private double dDistsDist;
    private double uDistsDist;
    
    private MetricsBag randomBag;
    
    
    public MetricsBag(Net net, int bins) {
    	this.net = net;
    	this.bins = bins;
		
		uPageRanks = new Distrib(net.prUSeq(), this.bins);
		triadicProfile = TriadicProfile.create(net);
		
		// distances
		if (net.isDirected()) {
			inDegrees = new DiscreteDistrib(net.inDegSeq());
			outDegrees = new DiscreteDistrib(net.outDegSeq());
			dPageRanks = new Distrib(net.prDSeq(), this.bins);
			
			DistMatrix dDM = new DistMatrix(net.getNodeCount(), true);
			dDM.calc(net);
			dDists = dDM.getDistrib();
			
			degrees = null;
		}
		else {
			degrees = new DiscreteDistrib(net.degSeq());
			
			inDegrees = null;
			outDegrees = null;
			dPageRanks = null;
			dDists = null;
		}
		DistMatrix uDM = new DistMatrix(net.getNodeCount(), false);
		uDM.calc(net);
		uDists = uDM.getDistrib();
		
		degreesDist = 0;
	    inDegreesDist = 0;
	    outDegreesDist = 0;
	    dPageRanksDist = 0;
	    uPageRanksDist = 0;
	    triadicProfileDist = 0;
	    dDistsDist = 0;
	    uDistsDist = 0;
	    
	    randomBag = null;
    }
    
    
    public MetricsBag(Net net, DistMatrix dDM, DistMatrix uDM, int bins, MetricsBag bag) {
    	this.net = net;
    	this.bins = bins;
    	
    	uPageRanks = new Distrib(net.prUSeq(), this.bins, bag.uPageRanks);
		triadicProfile = TriadicProfile.create(net);
    	
    	if (net.isDirected()) {
    		inDegrees = new DiscreteDistrib(net.inDegSeq(), bag.inDegrees);
    		outDegrees = new DiscreteDistrib(net.outDegSeq(), bag.outDegrees);
    		dPageRanks = new Distrib(net.prDSeq(), this.bins, bag.dPageRanks);
    		
    		if (dDM != null) {
    			dDists = dDM.getDistrib();
    		}
    		else {
    			DistMatrix dm = new DistMatrix(net.getNodeCount(), true);
    			dm.calc(net);
    			dDists = dm.getDistrib();
    		}
    	}
    	else {
    		degrees = new DiscreteDistrib(net.degSeq());
			
			inDegrees = null;
			outDegrees = null;
			dPageRanks = null;
			dDists = null;
    	}
		
		
		if (uDM != null) {
			uDists = uDM.getDistrib();
		}
		else {
			DistMatrix dm = new DistMatrix(net.getNodeCount(), false);
			dm.calc(net);
			uDists = dm.getDistrib();
		}
		
		calcDistances(bag);
    }
    
    
    private void calcDistances(MetricsBag bag) {
    	double verySmall = 0.999;
    	
    	TriadicProfile rp;
    	if (randomBag == null) {
    		rp = this.triadicProfile;
    	}
    	else {
    		rp = randomBag.triadicProfile;
    	}
    	
    	uPageRanksDist = uPageRanks.emdDistance(bag.uPageRanks);
        triadicProfileDist = triadicProfile.proportionalDistance(bag.triadicProfile, rp);
        uDistsDist = uDists.emdDistance(bag.uDists);
        
        
        if (uPageRanksDist == 0) uPageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        if (uDistsDist == 0) uDistsDist = verySmall;
        
    	if (net.isDirected()) {
    		inDegreesDist = inDegrees.emdDistance(bag.inDegrees);
            outDegreesDist = outDegrees.emdDistance(bag.outDegrees);
            dPageRanksDist = dPageRanks.emdDistance(bag.dPageRanks);
            dDistsDist = dDists.emdDistance(bag.dDists);
            
            if (inDegreesDist == 0) inDegreesDist = verySmall;
            if (outDegreesDist == 0) outDegreesDist = verySmall;
            if (dPageRanksDist == 0) dPageRanksDist = verySmall;
            if (dDistsDist == 0) dDistsDist = verySmall;
    	}
    	else {
    		degreesDist = degrees.emdDistance(bag.degrees);
    		
    		if (inDegreesDist == 0) inDegreesDist = verySmall;
    	}
    }
    

	public double getInDegreesDist() {
		return inDegreesDist;
	}

	
	public double getOutDegreesDist() {
		return outDegreesDist;
	}

	
	public double getDPageRanksDist() {
		return dPageRanksDist;
	}
	
	
	public double getUPageRanksDist() {
		return uPageRanksDist;
	}

	
	public double getTriadicProfileDist() {
		return triadicProfileDist;
	}

	
	public DiscreteDistrib getInDegrees() {
		return inDegrees;
	}

	
	public Distrib getDPageRanks() {
		return dPageRanks;
	}
	
	
	public Distrib getUPageRanks() {
		return uPageRanks;
	}

	
	public DiscreteDistrib getOutDegrees() {
		return outDegrees;
	}
	
	
	public DiscreteDistrib getDegrees() {
		return degrees;
	}
	
	
	public double getdDistsDist() {
		return dDistsDist;
	}

	
	public double getuDistsDist() {
		return uDistsDist;
	}

	
	@Override
	public String toString() {
		String str = "";
		
		str += "inDegreesDist: " + inDegreesDist;
		str += "; outDegreesDist: " + outDegreesDist;
		str += "; dPageRanksDist: " + dPageRanksDist;
		str += "; uPageRanksDist: " + uPageRanksDist;
		str += "; dDistsDist: " + dDistsDist;
		str += "; uDistsDist: " + uDistsDist;
		str += "; triadicProfileDist: " + triadicProfileDist;
		
		return str;
	}

	
	public TriadicProfile getTriadicProfile() {
		return triadicProfile;
	}

	
	public DiscreteDistrib getdDists() {
		return dDists;
	}

	
	public DiscreteDistrib getuDists() {
		return uDists;
	}


	public double getDegreesDist() {
		return degreesDist;
	}
	
	
	public MetricsBag getRandomBag() {
		if (randomBag == null) {
			Net randomNet = RandomNet.generate(net);
			randomBag = new MetricsBag(randomNet, null, null, bins, this);
		}
		
		return randomBag;
	}
}