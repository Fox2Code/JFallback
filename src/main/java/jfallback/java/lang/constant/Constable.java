package jfallback.java.lang.constant;

import java.util.Optional;

public interface Constable {
    Optional<? extends ConstantDesc> describeConstable();
}
