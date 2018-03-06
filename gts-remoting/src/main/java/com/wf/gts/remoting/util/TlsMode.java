package com.wf.gts.remoting.util;



/**
 * For server, three SSL modes are supported: disabled, permissive and enforcing.
 * <ol>
 *     <li><strong>disabled:</strong> SSL is not supported; any incoming SSL handshake will be rejected, causing connection closed.</li>
 *     <li><strong>permissive:</strong> SSL is optional, aka, server in this mode can serve client connections with or without SSL;</li>
 *     <li><strong>enforcing:</strong> SSL is required, aka, non SSL connection will be rejected.</li>
 * </ol>
 */
public enum TlsMode {

    DISABLED("disabled"),
    PERMISSIVE("permissive"),
    ENFORCING("enforcing");

    private String name;

    TlsMode(String name) {
        this.name = name;
    }

    public static TlsMode parse(String mode) {
        for (TlsMode tlsMode : TlsMode.values()) {
            if (tlsMode.name.equals(mode)) {
                return tlsMode;
            }
        }

        return PERMISSIVE;
    }

    public String getName() {
        return name;
    }
}
