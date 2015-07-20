package org.noorganization.instalist.view.modelwrappers;

/**
 * Created by TS on 25.05.2015.
 */
public class BaseItemReturnType<T> {

    public T mObject;
    public BaseItemReturnType(T _Object){
        mObject = _Object;
    }

    public T getObject(){
        return this.mObject;
    }

    public void setObject(T _Object){
        mObject = _Object;
    }
}
