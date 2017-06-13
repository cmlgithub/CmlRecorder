package com.cml.cmlrecorder.bean;

/**
 * author：cml on 2017/6/13
 * github：https://github.com/cmlgithub
 */

public class AudioBean {

    public String id;
    public String name;
    public String path;
    public String length;

    public AudioBean(String id, String name, String path, String length) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.length = length;
    }
}
