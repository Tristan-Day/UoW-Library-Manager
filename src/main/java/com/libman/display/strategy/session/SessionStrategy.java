package com.libman.display.strategy.session;

import com.libman.display.controller.scene.SessionScene;
import java.io.IOException;

public interface SessionStrategy {
    public abstract void buildSession(SessionScene session) throws IOException, Exception;
}
