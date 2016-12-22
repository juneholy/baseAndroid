// IMyAidlInterface.aidl
package com.example.houlinjiang.baseandroid.service;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int plus(int a, int b);
    String toUpperCase(String str);
}
