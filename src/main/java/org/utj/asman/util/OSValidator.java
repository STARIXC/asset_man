package org.utj.asman.util;
import org.springframework.stereotype.Component;
@Component
public class OSValidator {
    
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public boolean isWindows() {
        return OS.contains("win");
    }

    public boolean isMac() {
        return OS.contains("mac");
    }

    public boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public boolean isSolaris() {
        return OS.contains("sunos");
    }
}
