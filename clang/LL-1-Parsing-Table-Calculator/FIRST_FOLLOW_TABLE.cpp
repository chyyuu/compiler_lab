#include <bits/stdc++.h>
using namespace std;

void print(set<string> s)
{
    for (auto c : s)
    {
        cout << c << "  ";
    }
}

//the set containing non-terminals.
set<string> non_terminals;
//the set containg terminals
set<string> terminals;

map<string, set<string>> prod_rules;

map<string, set<string>> FIRST;
map<string, set<string>> FOLLOW;

map<string, map<string, string>> parse_table;
map<string, set<string>> PS; //predictive_set, PS, ps(product)=set(first_tokens)

set<string> calc_first(string s, map<string, bool> &done)
{

    set<string> rules;
    if (prod_rules.find(s) == prod_rules.end())
    {
        cerr << "wrong non terminal\n";
        exit(0);
    }
    else
    {
        //finding the rules for non terminal s
        rules = prod_rules.at(s);
        int num_nullable_rules = 0; //var to count the nullable non terminals ,will be used to decide whether to enter e in the first
        set<string> first_set_prods;
        for (auto r : rules)
        {

            //segment the rules using ' '(space)
            stringstream test(r);
            string segment;
            vector<string> seglist;

            while (getline(test, segment, ' '))
            {
                seglist.push_back(segment);
            }

            for (auto seg : seglist)
            {
                int leave_loop = 0; //var to decide when to break from the loop

                //if the non terminal s is the same as the current token
                if (seg == s)
                    continue;
                //inserting terminals
                if (terminals.find(seg) != terminals.end())
                {
                    FIRST[s].insert(seg);
                    cout<<"\n entering into "<<s<<" the terminal "<<seg<<endl;
                    break;
                }
                else
                {
                    if (done[seg] == true)
                    { //checking if the first of the token seg is calculated already or else it is calculated
                        first_set_prods = FIRST[seg];
                    }
                    else
                    {
                        cout<<"\n calling the first funct for "<<seg<<" from "<<s<<endl;
                        first_set_prods = calc_first(seg, done);
                        cout<<"\n return  the first funct for "<<seg<<" from "<<s<<endl;
                    }
                    // "e" is epsilon
                    if (first_set_prods.find("e") != first_set_prods.end())
                    {
                        num_nullable_rules++; //if first(seg) contains e then nulable tokens increases
                    }
                    else
                    {
                        leave_loop = 1; //if there is no e then we can leave the loop
                    }

                    for (auto c : FIRST[seg])
                    {
                        if (c != "e")
                        { //inserting all elements of first(seg) except e
                            FIRST[s].insert(c);
                        }
                    }

                    cout<<"\n entering into "<<s<<" the first of "<<seg<<endl;

                    if (leave_loop == 1)
                        break;
                }
            }

            if (num_nullable_rules == seglist.size())
            {
                FIRST[s].insert("e"); //inserting e into first(s) if all tokens are nullable
                //cout<<"\n entering into "<<s<<" the terminal e"<<endl;
            }
        }

        done[s] = true; //the first(s) is calculated
    }

    return FIRST[s];
}

set<string> calc_follow(string s, map<string, bool> &done)
{

    string non_term_var;
    //reading through all the productions and searching for non terminal s to calculate follow
    for (auto p : prod_rules)
    {

        non_term_var = p.first;

        for (auto rules : p.second)
        {

            //segment the rules using ' '(space)

            stringstream test(rules);
            string segment;
            vector<string> seglist;
            vector<string>::iterator it;

            while (getline(test, segment, ' '))
            {
                seglist.push_back(segment);
            }

            it = find(seglist.begin(), seglist.end(), s); //finding the non terminal s

            if (it == seglist.end())
            { //if not found continue to next rule
                continue;
            }
            cout<<" calc follow of "<<s<<" --> "<<non_term_var<<" = "<<rules<<endl;

            it++; //or else incrementing it to point to next token
            for (it; it != seglist.end(); it++)
            {
                int move_forward = 0;
                if (terminals.find(*it) != terminals.end())
                {
                    FOLLOW[s].insert(*it);
                    cout<<"\n entering into "<<s<<" the terminal "<<*it<<endl;
                    break;
                }
                else
                {
                    set<string> temp_first = FIRST[*it];
                    if (temp_first.find("e") != temp_first.end())
                        move_forward = 1;
                    for (auto c : temp_first)
                    {
                        if (c != "e")
                            FOLLOW[s].insert(c);
                    }

                    if (!move_forward)
                        break;
                }
            }

            if (it == seglist.end())
            {
                if (s == non_term_var)
                    continue;
                
                set<string> temp;
                if (done[non_term_var] == true)
                {
                    cout<<"\n The follow of "<<non_term_var<<" is entered in "<<s<<endl;;
                    temp = FOLLOW[non_term_var];
                }
                else
                {
                    cout<<"\n calling the follow funct for "<<non_term_var<<" from "<<s<<endl;
                    temp = calc_follow(non_term_var, done);
                    cout<<"\n return  the follow funct for "<<non_term_var<<" from "<<s<<endl;

                }

                FOLLOW[s].insert(temp.begin(), temp.end());
            }
        }
    }

    done[s] = true;
    return FOLLOW[s];
}

void print_map(map<string, set<string>> mp)
{

    string l_side;
    set<string> sym;
    for (auto r : mp)
    {
        l_side = r.first;
        printf("|%12s|", l_side.c_str());
  

        sym = r.second;
        for (auto c : sym)
        {
            printf("%s,", c.c_str());
        }
        cout << '\n';
    }
}

void calc_parse_table()
{
    //initialising the parse table with error
    for (auto nt : non_terminals)
    {
        map<string, string> temp_mp;
        for (auto t : terminals)
        {
            if (t != "e")
                temp_mp[t] = "error";
        }
        temp_mp["$"] = "error";
        parse_table[nt] = temp_mp;
        temp_mp.clear();
    }



    for (auto p : prod_rules)
    {
        string non_term_var = p.first;
        
        for (auto rule : p.second)
        {
            //segment the rule using ' '(space)
            stringstream test(rule);
            string segment;
            vector<string> seglist;
            vector<string>::iterator it;

            while (getline(test, segment, ' '))
            {
                seglist.push_back(segment);
            }

            map<string, string> temp_mp;
            set<string> temp_first;
            string first_token = seglist[0];
            //if terminal ,then first(terminal)=terminal
            if (terminals.find(first_token) != terminals.end())
            {
                if (first_token != "e") {
                    parse_table[non_term_var][first_token] = rule;
                    PS[non_term_var+"->"+test.str()].insert(first_token);
                }
                else
                {
                    set<string> temp_follow = FOLLOW[non_term_var];
                    for (auto b : temp_follow)
                    {
                        parse_table[non_term_var][b] = rule;
                        PS[non_term_var+"->"+test.str()].insert(b);
                    }
                }
            }
            else
            {
                temp_first = FIRST[first_token]; //calculating first

                if (temp_first.find("e") != temp_first.end())
                {
                    set<string> temp_follow = FOLLOW[non_term_var];
                    for (auto b : temp_follow)
                    {
                        parse_table[non_term_var][b] = rule; //calculating follow
                        PS[non_term_var+"->"+test.str()].insert(b);
                    }
                }
                for (auto a : temp_first)
                {
                    if (a != "e")
                    { //inserting all elements except e
                        parse_table[non_term_var][a] = rule;
                        PS[non_term_var+"->"+test.str()].insert(a);
                    }
                }
            }
        }
    }
}

void print_parse_table()
{

    for (auto p : parse_table)
    {

        string non_term = p.first;
        cout << non_term << ":\n";
        for (auto r : p.second)
        {
            cout << "\t" << r.first << ":" << r.second << endl;
        }
        cout << "\n\n";
    }
}

int main()
{
    //reading the grammar production rules
    //the rules are of the form (NON-Terminal)=(Terminal,Non-terminal)|....
    //each terminal,non terminal on the right hand side of production is space separated.
    //i.e. E=E + T
    //     E=T

    string filename, start_symbol;
    cout << "Enter the filename wgich contains the grammar rules:";
    cin >> filename;

    cout << "Enter the start symbol:";
    cin >> start_symbol;

    fstream infile;
    infile.open(filename, ios::in);

    if (infile.is_open())
    {
        string line;
        string nt;         //var to get non-terminal
        string production; //the right side of the rule
        //string c;         //reaing each token
        while (getline(infile, line))
        {
            //cout<<line<<endl;
            //identifying the non terminal by splitting the production
            // into left and right of '=' sign
            nt.clear();
            string::iterator it;
            for (it = line.begin(); it != line.end(); it++)
            {
                if (*it == '=')
                    break;
                else
                {
                    nt.push_back(*it);
                }
            }

            //it now points to '=' ,thus incrementing so that it gets the string after '='
            it++;
            //inserting the right side of the rule
            production.assign(it, line.end());
            //cout<<nt<<'-'<<production<<endl;
            //cout<<'\n';//
            non_terminals.insert(nt);
            prod_rules[nt].insert(production);
        }
    }
    infile.close();

    //now finding the all the symbols and then the terminals for FIRST and FOLLOW
    //since all symbols are space separated then split the production rule using space and
    //then check its presence in non_terminals set .If not then add to terminals set

    string l_side, r_side;
    for (auto r : prod_rules)
    {
        l_side = r.first;
        //now for each non terminal we check each production rule and filter out the non terminals
        for (auto rules : r.second)
        {

            stringstream test(rules);
            string segment;
            vector<string> seglist;

            while (std::getline(test, segment, ' '))
            {
                seglist.push_back(segment);
            }

            /*
            cout <<"seglist"<<'\n';
            for(auto i:seglist)
                cout<<i<<' ';
            cout<<'\n';
            */
            for (auto c : seglist)
            {
                if (non_terminals.find(c) == non_terminals.end())
                {
                    terminals.insert(c);
                }
            }
        }
    }

    //printing the terminals and non terminals

    cout << "\n\nthe non terminals are:{ ";
    print(non_terminals);
    cout << "}.\n";

    cout << "\nthe terminals are:{ ";
    print(terminals);
    cout << "}.\n";

    cout << "\n\n N_TERMINAL  |  PRODUCTIONS  \n";
    cout << "-----------------------------------\n";
    print_map(prod_rules);
    cout << "\n\n\n";

    map<string, bool> calced;
    for (auto c : non_terminals)
    {
        calced[c] = false;
    }

    cout<<"\n-------------CALC FIRST SET----------------"<<endl;
    for (auto c : non_terminals)
    {
        if (calced[c] == false)
        {
            cout<<"\nEntering into FIRST funct the non term "<<c<<endl;
            calc_first(c, calced);
            cout<<"\nReturn   from FIRST funct the non term "<<c<<endl;
        }
    }

    cout << "\n\n N_TERMINAL | FIRST\n";
    cout << "-------------------------------\n";
    print_map(FIRST);


    cout<<"\n-------------CALC FOLLOW SET----------------"<<endl;
    for (auto c : non_terminals)
    {
        calced[c] = false;
    }
    // "$" is the end of stream of tokens
    FOLLOW[start_symbol].insert("$");
    for (auto c : non_terminals)
    {
        if (calced[c] == false)
        {
            cout<<"\n entering into funct the non term "<<c<<endl;
            calc_follow(c, calced);
            cout<<"\n return   from funct the non term "<<c<<endl;
        }
    }

    cout << "\n\n N_TERMINAL | FOLLOW\n";
    cout << "-------------------------------\n";
    print_map(FOLLOW);

    cout<<"\n-------------CALC PS table----------------"<<endl;
    calc_parse_table();

  
    cout << "\n-------------Predictive Set--------------\n";
    print_map(PS);
      cout << "\n\nPARSING TABLE\n";
    cout << "-------------------------------\n";
    print_parse_table();

    return 0;
}