package com.pulsar.inkexpansion.accessors;

import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;

import java.util.UUID;

public interface EclipseAccessor {
    void inkexpansion$setEclipseData(ExtendedBlackRainComponent.Data data);
    void inkexpansion$setEclipseOwner(UUID owner);
}
