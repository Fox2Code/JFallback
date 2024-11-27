# JFallback

Run modern java code on java8

## Important note

I decided to switch to [JvmDowngrader](https://github.com/unimined/JvmDowngrader), and contributed over there to deduplicate efforts.  

I already contributed areas where JFallback was in advance over there, so it should offer **better** application compatibility in general.

You can expect me to no longer work on JFallback and instead bring all the efforts over on [JvmDowngrader](https://github.com/unimined/JvmDowngrader) instead.

## Why?

Because I cannot expect users to be able to install anything newer than java8, 
and I want to be able to run java11 code on their systems.

This was initially in https://github.com/Fox2Code/FoxLoader

You probably don't need this library if you can control which java runtime is used, which is most of the time.

## Compatibility

Note: JFallback currently only support up to java11 code by default, 
but can support up to java 17 with `-Djfallback.targetJvmVersion=17` jvm arg.

If something doesn't work, open an issue, I'll probs fix it.

##

Priority support programs:
- https://github.com/Vineflower/vineflower

##

Known working programs:

##

Known not-working programs:
- Anything having obfuscated method calls

##
