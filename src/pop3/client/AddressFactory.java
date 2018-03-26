package pop3.client;

public class AddressFactory {
    public static String getAddress(String domain) {
        if (domain.equals("wurst.de")) {
            return "134.214.117.134";
        } else if (domain.equals("cheese.com")) {
            return "134.214.117.158";
        } else if (domain.equals("polyp.com")) {
            return "134.214.117.134";
        }
        return "";
    }
}
