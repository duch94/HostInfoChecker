import javax.xml.crypto.Data;

public class HostInfo {
    public String timestamp;
    public CPU cpu;
    public MEM mem;
    public DSK dsk;
    private static byte tickerCpu = 0;
    private static float lastCpuPrc = 0;
    private static byte tickerMem = 0;
    private static float lastMemPrc = 0;
    private static byte tickerDsk = 0;
    private static float lastDskPrc = 0;

    public HostInfo(String newTimestamp, String newLa, String newCpuCores, String newMem, String newDisk) {
        timestamp = newTimestamp;
        cpu = new CPU(
                Integer.parseInt(newCpuCores),
                Float.parseFloat(newLa.split(" ")[0]),
                Float.parseFloat(newLa.split(" ")[1]),
                Float.parseFloat(newLa.split(" ")[2])
        );
        mem = new MEM(
                Integer.parseInt(newMem.split(" ")[0]),
                Integer.parseInt(newMem.split(" ")[1])
        );
        dsk = new DSK(
                Integer.parseInt(newDisk.split(" ")[0]),
                Integer.parseInt(newDisk.split(" ")[1])
        );
    }

    public class CPU {
        int coresNum;
        float la1;
        float la5;
        float la15;
        float usedPrc;

        public CPU(int coresNum, float la1, float la5, float la15) {
            this.coresNum = coresNum;
            this.la1 = la1;
            this.la5 = la5;
            this.la15 = la15;
            this.usedPrc = (float) la1 / coresNum * 100;
        }
    }

    public class MEM {
        // in kilobytes
        int total;
        int used;
        int free;
        float usedPrc;

        public MEM(int total, int free) {
            this.total = total;
            this.free = free;
            this.used = total - free;
            this.usedPrc = (float) this.used / this.total * 100;
        }
    }

    public class DSK {
        // in 1K-blocks
        int total;
        int used;
        int free;
        float usedPrc;

        public DSK(int total, int free) {
            this.total = total;
            this.free = free;
            this.used = total - free;
            this.usedPrc = (float) this.used / this.total * 100;
        }
    }

    public void checkHostResources() {
        // send email if any of metrics reach 90% - critical
        // send email if any of metrics reach 70% and was increasing during last 15 minutes - warning
        float maxTicks = 15;
        String text = "";
        if (isCritical(this.mem.usedPrc)) {
            text += "CPU usage is " + this.cpu.usedPrc + "%!\n";
        }
        if (tickerCpu >= maxTicks) {
            text += "CPU usage was increasing suspiciously for last " + tickerCpu + " minutes!\n";
        }
        if (isCritical(this.mem.usedPrc)) {
            text += "Memory usage is " + this.mem.usedPrc + "%!\n";
        }
        if (tickerMem >= maxTicks) {
            text += "Memory usage was increasing suspiciously for last " + tickerMem + " minutes!\n";
        }
        if (isCritical(this.dsk.usedPrc)) {
            text += "Disk usage is " + this.dsk.usedPrc + "%!\n";
        }
        if (tickerDsk >= maxTicks) {
            text += "Disk usage was increasing suspiciously for last " + tickerDsk + " minutes!\n";
        }

        if (!text.equals("")) {
            sendEmail(text);
        }
        checkForSuspiciousConstantIncreasing();
    }

    private void checkForSuspiciousConstantIncreasing() {
        float warningPercent = 70;
        if (this.cpu.usedPrc > lastCpuPrc) {
            if (this.cpu.usedPrc > warningPercent) {
                tickerCpu++;
            }
        } else {
            tickerCpu = 0;
        }
        lastCpuPrc = this.cpu.usedPrc;

        if (this.mem.usedPrc > lastMemPrc) {
            if (this.mem.usedPrc > warningPercent) {
                tickerMem++;
            }
        } else {
            tickerMem = 0;
        }
        lastMemPrc = this.mem.usedPrc;

        if (this.dsk.usedPrc > lastDskPrc) {
            if (this.dsk.usedPrc > warningPercent) {
                tickerDsk++;
            }
        } else {
            tickerDsk = 0;
        }
        lastDskPrc = this.dsk.usedPrc;
    }

    private boolean isCritical(float percent) {
        if (percent > 90) {
            return true;
        }
        return false;
    }

    private void sendEmail(String text) {
        debugPrint(text);
        GmailClient email = new GmailClient();
        email.send(text);
    }

    public void printStats() {
        System.out.println("\nTime: " + timestamp + "\n");
        System.out.println("Cores: " + cpu.coresNum + " LA: " + cpu.la1 + " " + cpu.la5 + " " + cpu.la15 + " CPU utilized: " + cpu.usedPrc + "%" + "\n");
        System.out.println("Memory total, free and used in %: " + mem.total + " " + mem.free + " " + mem.usedPrc + "%" + "\n");
        System.out.println("Disk space total, free and used in %: " + dsk.total + " " + dsk.free + " " + dsk.usedPrc + "%" + "\n");
    }

    public void saveToDb() {
        DataBaseClient dbClient = new DataBaseClient("127.0.0.1");
        dbClient.getHosts();
    }

    private void debugPrint(String text) {
        final String CYAN = "\033[0;36m";
        final String RESET = "\033[0m";
        System.out.println(CYAN + text + RESET);
    }
}
