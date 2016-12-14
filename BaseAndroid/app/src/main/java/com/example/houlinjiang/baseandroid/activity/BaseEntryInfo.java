package com.example.houlinjiang.baseandroid.activity;


import com.example.houlinjiang.baseandroid.model.BaseModel;

/**
 * Created by lipan on 16/4/18.
 */

public class BaseEntryInfo extends BaseModel {
    private static final long serialVersionUID = 1L;
    public String fromVCName = "";      // 从哪一页来
    public String backVCName = "";      // 回到哪一页
    public String extra = "";           // 该参数进行套传,进入的时候带,返回的时候不带
    public String innerSource = "";     //用户路径
}
