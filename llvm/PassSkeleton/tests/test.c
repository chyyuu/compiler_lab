void do_math(int *x) {
    *x += 5;
}

int main(void) {
    int result = -1, val = 4;
    int n1, n2;
    //do_math(&val);
    n1=0; n2=3;
    printf("%x\n",!n2);
    printf("%x\n",!n1);
    printf("%x\n",!n1 & 1);
    printf("%x\n",!n1 & n2);
    return result;
}
