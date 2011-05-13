from django.http import HttpResponse, HttpResponseRedirect
from django.template import Context, RequestContext, loader
from django.shortcuts import render_to_response
from synapp.forms import AddNetForm
from synapp.models import Network
from synweb.settings import DB_DIR, TMP_DIR
from syn.io import snap2syn


def networks(request):
    addnetform = AddNetForm()
    variables = RequestContext(request, {
        'net_list': Network.objects.all(),
        'addnetform': addnetform,
    })
    return render_to_response('networks.html', variables)


def addnet(request):
    if request.method == 'POST':
        form = AddNetForm(request.POST, request.FILES)
        if form.is_valid():
            name = form.cleaned_data['name']
            net = Network(name=name)
            net.save()

            netfile = request.FILES['netfile']
            tmp_path = TMP_DIR + 'tmpnet'
            tmp_file = open(tmp_path, 'w')
            for chunk in netfile.chunks():
                tmp_file.write(chunk)
            tmp_file.close()

            dest_path = '%s/net_%d' % (DB_DIR, net.id)
            node_count, edge_count = snap2syn(tmp_path, dest_path)

            net.nodes = node_count
            net.edges = edge_count
            net.save()

    return HttpResponseRedirect('/')


def network(request, net_id):
    variables = Context({
        'net': Network.objects.get(id=net_id),
    })
    return render_to_response('network.html', variables)
