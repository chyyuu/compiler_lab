int a = 0;
if (true)
{
    double b = 1.0;
    int c = 2;
    string d = "hello";
    {
        double e = 4.5;
        int f = 5;

        println("b = " + toString(b));
        println("c = " + toString(c));
        println("d = " + d);
        println("e = " + toString(e));
        println("f = " + toString(f));
    }
    c = 42;
    int e = a + 6;

    println("e = " + toString(e));
    println("c = " + toString(c));
}
int b = 7;
double c = 8;
{
    int d = 9;
    println("d = " + toString(d));
}
bool e = true;

println("a = " + toString(a));
println("b = " + toString(b));
println("c = " + toString(c));
println("e = " + toString(e));
