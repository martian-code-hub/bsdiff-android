package com.yz.myapplication.listener;

/**
 * Create by：Martian
 * on 2021/1/19
 */
public interface RxPermissionsResultListener {
    void isGranted();
    void isDenied();
    void showToast();
    void nothing();
}
