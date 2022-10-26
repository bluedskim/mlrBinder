package net.shed.mlrbinder;

import java.util.Collection;

/**
 * argument interface
 */
public interface Arg {
    public String toString();
    public Collection<? extends String> toStringList();
}
