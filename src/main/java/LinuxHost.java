import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;

public class LinuxHost {
    private String fqdn;
    private int port;
    private String user;
    private String password;
    private HostInfo metrics;

    public LinuxHost(String newFqdn, int newPort, String newUser, String newPassword) {
        fqdn = newFqdn;
        port = newPort;
        user = newUser;
        password = newPassword;
    }

    public void receiveMetricsFromHost() {
        String laInfoCommand = "/proc/loadavg";
        String cpuInfoCommand = "/proc/cpuinfo";
        String memInfoCommand = "/proc/meminfo";
        String dskInfoCommand = "/proc/diskstats";

        try {
            JSch jsch = new JSch();
            jsch.addIdentity("~/.ssh/id_rsa");
            Session session = jsch.getSession(user, fqdn, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.print("Connecting by SSH to host " + fqdn + "... ");
            session.connect();
            System.out.println("connected!");

            System.out.print("Opening SFTP channel... ");
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            System.out.println("opened!");

            Timestamp timestampObject = new Timestamp(System.currentTimeMillis());
            String timestamp = timestampObject.toString();
            InputStream laInfoStream = sftpChannel.get(laInfoCommand);
            InputStream cpuCoresStream = sftpChannel.get(cpuInfoCommand);
            InputStream memInfoStream = sftpChannel.get(memInfoCommand);
            //InputStream dskInfoStream = sftpChannel.get(dskInfoCommand);

            ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setCommand("df");
            execChannel.connect();
            InputStream dskInfoStream = execChannel.getInputStream();

            String ncores = streamToString(cpuCoresStream, "NCORES");
            String la = streamToString(laInfoStream,"LA");
            String mem = streamToString(memInfoStream, "MEM");
            String disk = streamToString(dskInfoStream, "DISK");
            metrics = new HostInfo(
                    timestamp,
                    la,
                    ncores,
                    mem,
                    disk);

            session.disconnect();
        } catch (com.jcraft.jsch.JSchException e) {
            System.err.print(e);
        } catch (com.jcraft.jsch.SftpException e) {
            System.err.print(e);
        } catch (java.io.IOException e) {
            System.err.print(e);
        }
    }

    private String streamToString(InputStream stream, String streamType) {
        BufferedReader bufer = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        String temp;
        switch (streamType) {
            case "LA":
                try {
                    while ((temp = bufer.readLine()) != null) {
                        String la1 = temp.split(" ")[0];
                        String la5 = temp.split(" ")[1];
                        String la15 = temp.split(" ")[2];
                        line = la1 + " " + la5 + " " + la15;
                        stream.close();
                        return line;
                    }
                } catch (java.io.IOException e) {
                    System.out.println(e.getMessage() + ": " + e.getCause().toString());
                }
            case "NCORES":
                try {
                    while ((temp = bufer.readLine()) != null) {
                        if (temp.contains("cpu cores")) {
                            line = temp.split(": ")[1];
                            stream.close();
                            return line;
                        }
                    }
                } catch (java.io.IOException e) {
                    System.out.println(e.getMessage() + ": " + e.getCause().toString());
                }
            case "MEM":
                try {
                    while ((temp = bufer.readLine()) != null) {
                        if (temp.contains("MemTotal")) {
                            line = temp.split("[ ]+")[1];
                        } else if (temp.contains("MemAvailable")) {
                            line = line + " " + temp.split("[ ]+")[1];
                            stream.close();
                            return line;
                        }
                    }

                } catch (java.io.IOException e) {
                    System.out.println(e.getMessage() + ": " + e.getCause().toString());
                }
                break;
            case "DISK":
                try {
                    while ((temp = bufer.readLine()) != null) {
                        if (temp.contains("/dev/sda")) {
                            String total = temp.split("[ ]+")[1];
                            String used = temp.split("[ ]+")[3];
                            line = total + " " + used;
                            stream.close();
                            return line;
                        }
                    }
                } catch (java.io.IOException e) {
                    System.out.println(e.getMessage() + ": " + e.getCause().toString());
                }
                break;
            default:
                System.err.println("Unexpected InputStream type: \"" + streamType + "\"");
                try {
                    while ((temp = bufer.readLine()) != null)
                        line += temp + "\n";
                    bufer.close();
                } catch (java.io.IOException e) {
                    System.out.println(e.getMessage() + ": " + e.getCause().toString());
                }
                break;
        }
        return line;
    }

    private String cpuCoresFromStreamToString(InputStream stream) {
        BufferedReader bufer = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        String temp;
        try {
            while ((temp = bufer.readLine()) != null)
                if (temp.contains("cpu cores")) {
                    line = temp.split(": ")[1];
                    return line;
                }
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage() + ": " + e.getCause().toString());
        }
        return line;
    }

    public HostInfo getMetricsObject() {
        return metrics;
    }
}
