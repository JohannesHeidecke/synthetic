#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jun 2011"


"""
Copyright (C) 2011 Telmo Menezes.

This program is free software; you can redistribute it and/or modify
it under the terms of the version 2 of the GNU General Public License 
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
"""


import sys
from syn.net import Net


def drmap_positions(netfile, outpath):
    bins = 50
    steps = 100
    cur_ts = 0

    net = Net(netfile)

    interval = (net.max_ts - net.min_ts) / steps
    cur_ts = net.min_ts + interval

    nodes_x = {}
    nodes_y = {}
    nodes_d = {}

    print steps, 'time steps'

    for step in range(steps):
        print 'step #%d' % step
        min_ts = net.min_ts
        max_ts = cur_ts
        syn_net = net.load_net(min_ts, max_ts)
        compute_evc(syn_net)

        node = net_first_node(syn_net)
        while node != 0:
            id = node_id(node)
            in_degree = node_in_degree(node)
            out_degree = node_out_degree(node)
            degree = in_degree + out_degree
            evc_in = node_evc_in(node)
            evc_out = node_evc_out(node)

            if evc_in < -7.0:
                evc_in = -7.0
            if evc_in > 7.0:
                evc_in = 7.0
            if evc_out < -7.0:
                evc_out = -7.0
            if evc_out > 7.0:
                evc_out = 7.0

            if id in nodes_x:
                nodes_x[id].append(evc_in)
                nodes_y[id].append(evc_out)
                nodes_d[id].append(degree);
            else:
                nodes_x[id] = [evc_in,]
                nodes_y[id] = [evc_out,]
                nodes_d[id] = [degree,]

            node = node_next_node(node)

        destroy_net(syn_net)

        cur_ts += interval

    print 'writing output file'

    f = open(outpath, 'w')
    for nodeid in nodes_x.keys():
        line = '%d'
        for i in len(nodes_x):
            if nodes_d[i] > 0:
                line = '%s; %f, %f' % (line, nodes_x[i], nodes_y[i])
            else:
                line = '%s; ' % line
        line = '%s\n' % line
        f.write(line)
    f.close()

    print 'done.'


if __name__ == '__main__':
    drmap_positions(sys.argv[1], sys.argv[2])