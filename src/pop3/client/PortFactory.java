package pop3.client;

public class PortFactory {
    public static int getPort(String domain) {
        if (domain.equals("wurst.de")) {
            return 1339;
        } else if (domain.equals("cheese.com")) {
            return 1338;
        } else if (domain.equals("polyp.com")) {
            return 1337;
        }
        return -1;
    }
}
