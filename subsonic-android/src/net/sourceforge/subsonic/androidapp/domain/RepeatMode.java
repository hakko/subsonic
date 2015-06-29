package net.sourceforge.subsonic.androidapp.domain;

/**
 * @author Sindre Mehus
 * @version $Id: RepeatMode.java 2187 2011-02-25 11:47:14Z sindre_mehus $
 */
public enum RepeatMode {
    OFF {
        @Override
        public RepeatMode next() {
            return ALL;
        }
    },
    ALL {
        @Override
        public RepeatMode next() {
            return SINGLE;
        }
    },
    SINGLE {
        @Override
        public RepeatMode next() {
            return OFF;
        }
    };

    public abstract RepeatMode next();
}
