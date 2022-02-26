package com.example.two_classes;

import com.example.one_interface.Box;

import javax.inject.Inject;

public class AppleBox implements Box {
    @Inject
    public AppleBox(Apple apple) {
    }
}
