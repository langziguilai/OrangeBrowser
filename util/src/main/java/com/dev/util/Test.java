package com.dev.util;

import android.view.View;

@KeepNameIfNecessary
public class Test {
    @KeepMemberIfNecessary
    View view;
    Integer test;
    Integer test2;
    @KeepMemberIfNecessary
    void say(){
        test=new Integer(5);
        testPrivate();
    }

    void say2(){
        System.out.println(test);
        testPrivate();
    }
    private void testPrivate(){
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        System.out.println("testPrivate");
        while (true){
            System.out.println("testPrivate");
        }
    }
}
