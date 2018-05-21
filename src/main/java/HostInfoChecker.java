import java.util.Timer;
import java.util.TimerTask;

/**
 * - connect by ssh to host (/)
 * - get hardware info from host (laString LA, memoryString usage, diskString usage) (/)
 * - save in DB timestamp and the rest info
 * - add scheduler to autostart every minute (/)
 * - send email if any of metrics reach 90% (/)
 * - send email if any of metrics reach 70% and was increasing during last 15 minutes (/)
 */

public class HostInfoChecker {
    public static void main(String[] args) {
        long second = 1000;
        long minute = 60 * second;

        Timer timer = new Timer();
        MyTask task = new MyTask();

        timer.scheduleAtFixedRate(task, 0, minute);
    }
}

class MyTask extends TimerTask {
    private LinuxHost host = new LinuxHost(
            "",
            22,
            "root",
            "");

    @Override
    public void run() {
        host.receiveMetricsFromHost();
        HostInfo metrics = host.getMetricsObject();
        metrics.printStats();
        metrics.saveToDb();
        metrics.checkHostResources();
    }
}
