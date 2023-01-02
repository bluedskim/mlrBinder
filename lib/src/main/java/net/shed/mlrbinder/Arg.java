package net.shed.mlrbinder;

import java.util.Collection;

/**
 * argument interface like verb, flag, file name
 */
public interface Arg {
    public String toString();
    public Collection<String> toStringList();
}
