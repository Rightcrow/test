#include "protocal.h"

/*这两个函数，是为了把结构体里的4字节地址转换为connect能够识别的地址*/

/*这里不是很明白，回头看一下*/
int Int2String(int num, char* buf, int count)
{
    int a = num / 100;

    int b = (num / 10) % 10;

    int c = num % 10;

    if (a == 0)
    {
        if (b != 0)
        {
            buf[count++] = b + '0';

            buf[count++] = c + '0';
        }
        else
        {
            buf[count++] = c + '0';
        }
    }
    else
    {
        buf[count++] = a + '0';

        buf[count++] = b + '0';

        buf[count++] = c + '0';
    }

    return count;
}

char* int2char(unsigned int number)
{
    static char buf[20] = {0};

    int count = 0 ;

    int idx = 24;

    while(idx >= 0)
    {
        int num = (number >> idx) & 0xff;

        count = Int2String(num, buf, count);

        if (idx != 0)
        {
            buf[count++] = '.';
        }

        idx -= 8;
    }

    return buf;
}

