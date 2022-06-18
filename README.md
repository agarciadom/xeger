[![Java CI](https://github.com/bluezio/xeger/actions/workflows/ci.yml/badge.svg)](https://github.com/bluezio/xeger/actions/workflows/ci.yml)

This is a maintenance fork of the Xeger library. It is a Java library
for generating strings that match a specific regular expression. The
original version of Xeger is available from this address, but
development seems to have stopped:

  http://code.google.com/p/xeger/

The objective of this fork is to simply maintain Xeger, fixing the
reported bugs as they come. Pull requests are welcome, though :-).

For reference, I have copied several useful sections of the original
website below.

Introduction
------------

Think of it as the opposite of regular expression matchers. This
library allows you to generate text that is guaranteed to match a
regular expression passed in.

Let's take the regular expression: [ab]{4,6}c Using Xeger, you can now
generate Strings matching this pattern like this:

    String regex = "[ab]{4,6}c";
    Xeger generator = new Xeger(regex);
    String result = generator.generate();
    assert result.matches(regex);

Limitations
-----------

Xeger does not support all valid Java regular expressions. The full
set of what is defined here and is summarized below. Future versions
might support a more complete set, in case of popular demand.

```
regexp  ::=     unionexp                
|                       
unionexp        ::=     interexp | unionexp     (union) 
|       interexp                
interexp        ::=     concatexp & interexp    (intersection)  [OPTIONAL]
|       concatexp               
concatexp       ::=     repeatexp concatexp     (concatenation) 
|       repeatexp               
repeatexp       ::=     repeatexp ?     (zero or one occurrence)        
|       repeatexp *     (zero or more occurrences)      
|       repeatexp +     (one or more occurrences)       
|       repeatexp {n}   (n occurrences) 
|       repeatexp {n,}  (n or more occurrences) 
|       repeatexp {n,m} (n to m occurrences, including both)    
|       complexp                
complexp        ::=     ~ complexp      (complement)    [OPTIONAL]
|       charclassexp            
charclassexp    ::=     [ charclasses ] (character class)       
|       [^ charclasses ]        (negated character class)       
|       simpleexp               
charclasses     ::=     charclass charclasses           
|       charclass               
charclass       ::=     charexp - charexp       (character range, including end-points) 
|       charexp         
simpleexp       ::=     charexp         
|       .       (any single character)  
|       #       (the empty language)    [OPTIONAL]
|       @       (any string)    [OPTIONAL]
|       " <Unicode string without double-quotes> "      (a string)      
|       ( )     (the empty string)      
|       ( unionexp )    (precedence override)   
|       < <identifier> >        (named automaton)       [OPTIONAL]
|       <n-m>   (numerical interval)    [OPTIONAL]
charexp ::=     <Unicode character>     (a single non-reserved character)       
|       \ <Unicode character>   (a single character)
```

Maven repository
---

A binary build of this library is available from the GitHub Package Registry:

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub agarciadom Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/agarciadom/xeger</url>
  </repository>
</repositories>
```
