package edsh.oneblock.util;

import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.scheduler.TaskHandler;
import edsh.oneblock.OneBlockPlugin;

public class Scheduler {
    public static ServerScheduler instance;

    public static TaskHandler delay(Runnable task, int delay) {
        return instance.scheduleDelayedTask(OneBlockPlugin.INSTANCE, task, delay);
    }

    public static TaskHandler repeat(Runnable task, int period) {
        return instance.scheduleRepeatingTask(OneBlockPlugin.INSTANCE, task, period);
    }

    public static TaskHandler delayRepeat(Runnable task, int delay, int period) {
        return instance.scheduleDelayedRepeatingTask(OneBlockPlugin.INSTANCE, task, delay, period);
    }

}
