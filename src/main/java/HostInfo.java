public class HostInfo {
    public String timestamp;
    public CPU cpu;
    public MEM mem;
    public DSK dsk;

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

    public void saveToDb() {
        System.out.println("\nTime: " + timestamp + "\n");
        System.out.println("Cores: " + cpu.coresNum + " LA: " + cpu.la1 + " " + cpu.la5 + " " + cpu.la15 + " CPU utilized: " + cpu.usedPrc + "%" + "\n");
        System.out.println("Memory total, free and used in %: " + mem.total + " " + mem.free + " " + mem.usedPrc + "%" + "\n");
        System.out.println("Disk space total, free and used in %: " + dsk.total + " " + dsk.free + " " + dsk.usedPrc + "%" + "\n");
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
            this.usedPrc = (float) la5 / coresNum * 100;
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
}
