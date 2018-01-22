package com.nativewrapper.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.ByteOrder;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Uint32 {
}
