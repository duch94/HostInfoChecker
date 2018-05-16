public class LinuxHost {
    private String fqdn;
    private String port;
    private String user;
    private String password;
    private HostInfo metrics;

    public LinuxHost(String newFqdn, String newPort, String newUser, String newPassword) {
        fqdn = newFqdn;
        port = newPort;
        user = newUser;
        password = newPassword;
    }

    public void connectoBySsh() {

    }

    public void getHostMetrics() {

    }

    public HostInfo getMetrics() {
        return new HostInfo();
    }
}
