package com.bazooka.bluetoothbox;

import com.bazooka.bluetoothbox.cache.db.entity.FmChannelCache;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {

    }
    @Test
    public void sort(){
        int  [] args  = new int[]{2,6,12,1,0};
        //第一层循环从数组的最后往前遍历
        for (int i = args.length - 1; i > 0 ; --i) {
            //这里循环的上界是 i - 1，在这里体现出 “将每一趟排序选出来的最大的数从sorted中移除”
            for (int j = 0; j < i; j++) {
                //保证在相邻的两个数中比较选出最大的并且进行交换(冒泡过程)
                if (args[j] > args[j+1]) {
                    int temp = args[j];
                    args[j] = args[j+1];
                    args[j+1] = temp;
                }
            }
        }
        for (int i = 0 ;i<args.length;i++){
            System.out.print(args[i]+" ");
        }
    }
    @Test
    public void test (){

    }

}