package org.joo.scorpius.support.queue;

import org.joo.scorpius.trigger.TriggerExecutionContext;

public interface HandlingQueue {

    public boolean enqueue(TriggerExecutionContext context);

    public TriggerExecutionContext dequeue();

    public boolean isEmpty();
}
