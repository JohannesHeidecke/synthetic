/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpgenerator.h"
#include "utils.h"
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <stdio.h>


syn_gpgen *syn_create_gpgenerator()
{
    syn_gpgen *gen = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen->edges = 0;
    gen->cycle = 0;
    gen->prog = create_random_gptree(2, 0.2, 2, 5);

    return gen;
}


void syn_destroy_gpgenerator(syn_gpgen *gen)
{
    destroy_gptree(gen->prog);
    free(gen);
}


syn_gpgen *syn_clone_gpgenerator(syn_gpgen *gen)
{
    syn_gpgen *gen_clone = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen_clone->edges = 0;
    gen_clone->cycle = 0;
    gen_clone->prog = clone_gptree(gen->prog);

    return gen_clone;
}


syn_net* syn_gpgen_run(syn_gpgen *gen, unsigned int nodes, unsigned int edges, unsigned int max_cycles)
{
    unsigned int i;

    syn_net *net = syn_create_net();

    gen->edges = 0;
    gen->cycle = 0;

    for (i = 0; i < nodes; i++) {
        syn_add_node(net, 0);
    }

    syn_node* orig_node;
    syn_node* targ_node;
    double prob;

    while ((gen->edges < edges) && (gen->cycle < max_cycles)) {
        orig_node = syn_get_random_node(net);
        targ_node = orig_node;
        while (targ_node == orig_node) {
            targ_node = syn_get_random_node(net);
        }
        prob = 1;
        if (RANDOM_TESTPROB(prob)) {
            syn_add_edge(orig_node, targ_node, gen->cycle);
            gen->edges++;
        }
            
        gen->cycle++;
        //printf("%d\n", gen->cycle);
    }

    return net;
}
