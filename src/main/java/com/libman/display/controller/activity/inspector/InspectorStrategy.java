package com.libman.display.controller.activity.inspector;

import com.libman.display.controller.activity.Activity;

public abstract class InspectorStrategy extends Activity
{
    public void initalise() throws Exception{};

    public abstract void setTargetObject(Object target) throws Exception;
}
