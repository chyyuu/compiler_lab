//Write CPP code here

#include <iostream>
using namespace std;

class Stock
{
public:
	virtual void accept(class Visitor *) = 0;
	
};

class Apple : public Stock
{
public:
	/*virtual*/ void accept(Visitor *);
	void buy()
	{
		cout << "Apple::buy\n";
	}
	void sell()
	{
		cout << "Apple::sell\n";
	}
	
};
class Google : public Stock
{
public:
	/*virtual*/ void accept(Visitor *);
	void buy()
	{
		cout << "Google::buy\n";
	}

	void sell()
	{
		cout << "Google::sell\n";
	}
};

class Visitor
{
public:
	virtual void visit(Apple *) = 0;
	virtual void visit(Google *) = 0;
	//private:
	static int m_num_apple, m_num_google;
	void total_stocks()
	{
		cout << "m_num_apple " << m_num_apple
			<< ", m_num_google " << m_num_google << '\n';
	}
};
int Visitor::m_num_apple = 0;
int Visitor::m_num_google = 0;
class BuyVisitor : public Visitor
{
public:
	BuyVisitor()
	{
		m_num_apple = m_num_google = 0;
	}
	/*virtual*/ void visit(Apple *r)
	{
		++m_num_apple;
		r->buy();
		cout << "m_num_apple " << m_num_apple << endl;
	}
	/*virtual*/ void visit(Google *b)
	{
		++m_num_google;
		b->buy();
		cout << " m_num_google " << m_num_google << '\n';
	}
};

class SellVisitor : public Visitor
{
public:
	/*virtual*/ void visit(Apple *a)
	{
		
		--m_num_apple;
		a->sell();
		cout << "m_num_apple " << m_num_apple << endl;
	}
	/*virtual*/ void visit(Google *g)
	{
		--m_num_google;
		g->sell();
		cout << "m_num_google " << m_num_google << endl;
	}
};

void Apple::accept(Visitor *v)
{
	v->visit(this);
}

void Google::accept(Visitor *v)
{
	v->visit(this);
}

int main()
{
	Stock *set[] = { new Apple, new Google, new Google,
					new Apple, new Apple, 0 };

	BuyVisitor buy_operation;
	SellVisitor sell_operation;
	for (int i = 0; set[i]; i++)
	{
		set[i]->accept(&buy_operation);
	}
	buy_operation.total_stocks();

	for (int i = 0; set[i]; i++)
	{

		set[i]->accept(&sell_operation);
	}
	sell_operation.total_stocks();
}
