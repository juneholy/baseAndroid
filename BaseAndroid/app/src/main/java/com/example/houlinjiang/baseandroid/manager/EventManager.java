package com.example.houlinjiang.baseandroid.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by lipan on 15/11/6.
 */
public class EventManager {
    public static final String DEFAULT_GROUP = "EVENT_DEFAULT_GROUP";
    private static EventManager sInstance;
    private static Map<String, Map<String, List<OnEventObserver>>> sGroups = new HashMap<>();
    private static Map<String, Map<String, Object>> sEventDatas = new HashMap<>();

    private EventManager() {}

    public static EventManager getInstance() {
        if (sInstance == null) {
            synchronized (EventManager.class) {
                if (sInstance == null) {
                    sInstance = new EventManager();
                }
            }
        }
        return sInstance;
    }

    public void register(String event, OnEventObserver observer) {
        register(DEFAULT_GROUP, event, observer);
    }

    public synchronized void register(String group, String event, OnEventObserver observer) {
        Map<String, List<OnEventObserver>> keyObserverMap = sGroups.get(group);
        if (keyObserverMap == null) {
            keyObserverMap = new HashMap<>();
            sGroups.put(group, keyObserverMap);
        }
        List<OnEventObserver> observers = keyObserverMap.get(event);
        if (observers == null) {
            observers = new ArrayList<>();
            keyObserverMap.put(event, observers);
        }
        if (!observers.contains(observer)){
            observers.add(observer);
        }
        Map<String, Object> keyDataMap = sEventDatas.get(group);
        if (keyDataMap != null && keyDataMap.size() > 0) {
            Object obj = keyDataMap.remove(event);
            if (obj != null) {
                observer.onEventChanged(group, event, obj);
            }
        }

    }

    public void unregist(String event, OnEventObserver observer) {
        unregist(DEFAULT_GROUP, event, observer);
    }

    public synchronized void unregist(String group, String event, OnEventObserver observer) {
        Map<String, List<OnEventObserver>> keyObserverMap = sGroups.get(group);
        if (keyObserverMap != null && keyObserverMap.size() > 0) {
            List<OnEventObserver> observers = keyObserverMap.get(event);
            if (observers != null && observers.size() > 0) {
                observers.remove(observer);
            }
            if (observers != null && observers.size() == 0) {
                keyObserverMap.remove(event);
            }
        }
        if (keyObserverMap != null && keyObserverMap.size() == 0) {
            sGroups.remove(group);
        }
    }

    public synchronized void unregist(String group, String event) {
        Map<String, List<OnEventObserver>> keyObserverMap = sGroups.get(group);
        if (keyObserverMap != null && keyObserverMap.size() > 0) {
            keyObserverMap.remove(event);
        }
        if (keyObserverMap != null && keyObserverMap.size() == 0) {
            sGroups.remove(group);
        }
    }

    public synchronized void unregistAllGroup(String group){
        sGroups.remove(group);
    }

    public synchronized void unregistAllEvent(String event){
        List<String> willRemoveGroup = new ArrayList<>();
        for (Entry<String, Map<String, List<OnEventObserver>>> entry : sGroups.entrySet()) {
            Map<String, List<OnEventObserver>> keyObserverMap = entry.getValue();
            keyObserverMap.remove(event);
            if (keyObserverMap.size() == 0) {
                willRemoveGroup.add(entry.getKey());
            }
        }
        if (willRemoveGroup.size() > 0) {
            for (String group : willRemoveGroup) {
                sGroups.remove(group);
            }
        }
    }

    public synchronized void unregistAll(OnEventObserver observer){
        List<String> willRemoveGroup = new ArrayList<>();

        for (Entry<String, Map<String, List<OnEventObserver>>> groupEntry : sGroups.entrySet()) {
            Map<String, List<OnEventObserver>> keyObserverMap = groupEntry.getValue();

            List<String> willRemoveEvent = new ArrayList<>();
            for (Entry<String, List<OnEventObserver>> eventEntry : keyObserverMap.entrySet()) {
                List<OnEventObserver> observers = eventEntry.getValue();
                if (observers.contains(observer)) {
                    observers.remove(observer);
                }
                if (observers.size() == 0) {
                    willRemoveEvent.add(eventEntry.getKey());
                }
            }
            if (willRemoveEvent.size() > 0) {
                for (String event : willRemoveEvent) {
                    keyObserverMap.remove(event);
                }
            }
            if (keyObserverMap.size() == 0) {
                willRemoveGroup.add(groupEntry.getKey());
            }
        }
        if (willRemoveGroup.size() > 0) {
            for (String group : willRemoveGroup) {
                sGroups.remove(group);
            }
        }
    }

    public synchronized void publish(String event, Object object){
        publish(DEFAULT_GROUP, event, object);
    }

    public synchronized void publish(String group, String event, Object object){
        publish(DEFAULT_GROUP, event, object, false);
    }


    public synchronized void publish(String group, String event, Object object, boolean sticky){
        Map<String, List<OnEventObserver>> keyObserverMap = sGroups.get(group);
        if (keyObserverMap != null && keyObserverMap.size() > 0) {
            List<OnEventObserver> observers = keyObserverMap.get(event);
            if (observers != null && observers.size() > 0) {
                for (int i = observers.size() - 1; i >= 0; i--) {
                    OnEventObserver observer = observers.get(i);
                    if (observer.onEventChanged(group, event, object)) {
                        return;
                    }
                }
            }
        }

        if (sticky) {
            Map<String, Object> keyDataMap = sEventDatas.get(group);
            if (keyDataMap == null) {
                keyDataMap = new HashMap<>();
                sEventDatas.put(group, keyDataMap);
            }
            keyDataMap.put(event, object);
        }
    }

    public synchronized void removeGroupData(String group) {
        sEventDatas.remove(group);
    }

    public synchronized void removeEventData(String event) {
        List<String> willRemoveGroup = new ArrayList<>();
        for (Entry<String, Map<String, Object>> entry : sEventDatas.entrySet()) {
            Map<String, Object> keyDataMap = entry.getValue();
            keyDataMap.remove(event);
            if (keyDataMap.size() == 0) {
                willRemoveGroup.add(entry.getKey());
            }
        }
        if (willRemoveGroup.size() > 0) {
            for (String group : willRemoveGroup) {
                sEventDatas.remove(group);
            }
        }
    }

    public synchronized void removeData(String group, String event) {
        Map<String, Object> keyDataMap = sEventDatas.get(group);
        if (keyDataMap != null && keyDataMap.size() > 0) {
            keyDataMap.remove(event);
        }
        if (keyDataMap != null && keyDataMap.size() == 0) {
            sEventDatas.remove(group);
        }
    }

    public interface OnEventObserver {
        boolean onEventChanged(String group, String event, Object object);
    }
}
