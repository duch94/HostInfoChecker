/**
 * - connect by ssh to host
 * - get hardware info from host (cpu, memory, disk usage)
 * - save in DB timestamp and the rest info
 * - add scheduler to autostart every minute
 */

public class HostInfoChecker {
    public static void main(String[] args) {
        LinuxHost host = new LinuxHost(
                "localhost",
                "22",
                "vtitov",
                "asdQWE123");
        host.connectoBySsh();
        host.getHostMetrics();
        HostInfo metrics = host.getMetrics();
        metrics.saveToDb();
    }
}
