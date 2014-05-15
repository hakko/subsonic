package net.sourceforge.subsonic.androidapp.service.parser;

/**
 * @author Sindre Mehus
 * @version $Id: SubsonicRESTException.java 1787 2010-08-27 09:59:06Z sindre_mehus $
 */
public class SubsonicRESTException extends Exception {

    private final int code;

    public SubsonicRESTException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
