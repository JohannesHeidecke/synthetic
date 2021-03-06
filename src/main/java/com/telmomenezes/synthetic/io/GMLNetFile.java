package com.telmomenezes.synthetic.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.NetBuilder;


public class GMLNetFile extends NetFile {

    public enum State {
        START, GRAPH, NODE, EDGE 
    }
    
    @Override
    public Net load(String filePath, boolean directed, boolean parallels) {
        NetBuilder nb = new NetBuilder(directed, false, parallels);
        Map<String, Integer> nodes = new HashMap<String, Integer>();
        
        try {
            State state = State.START;
            int nesting = 0;
            
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line;
            String source = "";
            String target = "";
            String id = "";
            int value = 1;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                
                switch (state) {
                case START:
                    if (line.startsWith("graph")) {
                        state = State.GRAPH;
                    }
                    break;
                case GRAPH:
                    if (line.startsWith("node")) {
                        state = State.NODE;
                        if (line.contains("[")) {
                        	nesting = 0;
                        }
                        else {
                        	nesting = -1;
                        }
                    }
                    else if (line.startsWith("edge")) {
                        state = State.EDGE;
                    }
                    else if (line.startsWith("]")) {
                        return nb.buildNet();
                    }
                    break;
                case NODE:
                    if (line.startsWith("id")) {
                    	if (nesting == 0) {
                    		String[] tokens = line.split(" ");
                    		if (tokens.length >= 2) {
                    			id = tokens[1];
                    			//System.out.println(id);
                    		}
                    	}
                    }
                    else if (line.startsWith("value")) {
                    	if (nesting == 0) {
                    		String[] tokens = line.split(" ");
                    		if (tokens.length >= 2) {
                    			value = new Integer(tokens[1]);
                    		}
                    	}
                    }
                    else if (line.startsWith("]")) {
                    	if (nesting == 0) {
                    		nodes.put(id, nb.addNode(value));
                    		state = State.GRAPH;
                    	}
                    	else {
                    		nesting--;
                    	}
                    }
                    else if (line.startsWith("[")) {
                        nesting++;
                    }
                    break;
                case EDGE:
                    if (line.startsWith("source")) {
                        String[] tokens = line.split(" ");
                        if (tokens.length >= 2) {
                            source = tokens[1];
                        }
                    }
                    else if (line.startsWith("target")) {
                        String[] tokens = line.split(" ");
                        if (tokens.length >= 2) {
                            target = tokens[1];
                        }
                    }
                    /*
                    else if (line.startsWith("value")) {
                        String[] tokens = line.split(" ");
                        if (tokens.length >= 2) {
                            value = new Integer(tokens[1]);
                        }
                    }
                    */
                    else if (line.startsWith("]")) {
                    	//System.out.println(source + " -> " + target);
                        nb.addEdge(nodes.get(source), nodes.get(target), value);
                        state = State.GRAPH;
                    }
                    break;
                }
            }
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return nb.buildNet();
    }

    @Override
    public void save(Net net, String filePath) {
        // TODO Auto-generated method stub

    }

    static public void main(String[] args) {
        GMLNetFile nf = new GMLNetFile();
        Net net = nf.load("celegansneural.gml", true, true);
        System.out.println(net);
    }
}
