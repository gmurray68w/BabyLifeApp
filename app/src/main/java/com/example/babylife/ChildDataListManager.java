package com.example.babylife;

import java.util.ArrayList;
import java.util.List;

public class ChildDataListManager {
    private static final ChildDataListManager ourInstance = new ChildDataListManager();
    private final List<ChildInfo> childInfoList;

    public static ChildDataListManager getInstance() {
        return ourInstance;
    }

    private ChildDataListManager() {
        childInfoList = new ArrayList<>();
    }

    public void addChildInfo(ChildInfo childInfo) {
        childInfoList.add(childInfo);
    }

    public List<ChildInfo> getChildInfoList() {
        return childInfoList;
    }
}
