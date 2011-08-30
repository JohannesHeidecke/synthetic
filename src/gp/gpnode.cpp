/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpnode.h"
#include <iostream>


using std::cout;
using std::endl;
using std::flush;


GPNode::GPNode()
{
}


GPNode::~GPNode()
{
}


void GPNode::init(gpnode_type nodetype,
                    gpnode_fun fun,
                    gpval val,
                    unsigned int var,
                    GPNode* parent)
{
    type = nodetype;
    this->parent = parent;

    if (nodetype == FUN) {
        this->fun = fun;
        this->arity = fun_arity(fun);
        condpos = fun_condpos(fun);
    }
    else if (nodetype == VAR) {
        this->var = var;
        this->arity = 0;
        condpos = -1;
    }
    else {
        this->val = val;
        this->arity = 0;
        condpos = -1;
    }

    stoppos = arity;
}


int GPNode::fun_condpos(gpnode_fun fun)
{
    switch(fun) {
        case ZER:
            return 1;
        case EQ:
        case GRT:
        case LRT:
        case GET:
        case LET:
            return 2;
        default:
            return -1;
    }
}


unsigned int GPNode::fun_arity(gpnode_fun fun)
{
    switch(fun) {
        case SUM:
        case SUB:
        case MUL:
        case DIV:
            return 2;
        case ZER:
            return 3;
        case EQ:
        case GRT:
        case LRT:
        case GET:
        case LET:
            return 4;
        default:
            return 0;
    }
}

void GPNode::print()
{
    if (type == VAL) {
        cout << val;
        return;
    }

    if (type == VAR) {
        cout << "$" << val;
        return;
    }

    if (type != FUN) {
        cout << "???";
        return;
    }

    switch(fun) {
        case SUM:
            cout << "+";
            return;
        case SUB:
            cout << "-";
            return;
        case MUL:
            cout << "*";
            return;
        case DIV:
            cout << "/";
            return;
        case ZER:
            cout << "ZER";
            return;
        case EQ:
            cout << "==";
            return;
        case GRT:
            cout << ">";
            return;
        case LRT:
            cout << "<";
            return;
        case GET:
            cout << ">=";
            return;
        case LET:
            cout << "<=";
            return;
        default:
            cout << "F??";
            return;
    }
}
