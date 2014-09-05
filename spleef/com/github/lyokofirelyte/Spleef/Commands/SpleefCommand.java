package com.github.lyokofirelyte.Spleef.Commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface SpleefCommand {
	public String[] aliases();
	public String name() default "none";
	public String desc() default "A Spleef Command";
	public String help() default "/spleef help";
	public String perm() default "spleef.use";
	public boolean player() default false;
	public int max() default 9999;
	public int min() default 0;
}