# JackCompiler

## `JackTokenizer` Module

Run the unit test without IntelliJ IDEA:

```bash
cd <this git repo>
javac -d ./out/shell/ ./src/JackCompiler/*.java
cd ./out/shell/
java JackCompiler.JackTokenizer ../../test/test.jack
cd ../../
```

This module implements the `JackTokenizer` in textbook on p.214. It reads a `.jack` file and tokenizes it.

## `JackAnalyzer` Module

Developed using top-down `LL(0)` parser...

Run the unit test without IntelliJ IDEA:

```bash
cd <this git repo>
javac -d ./out/shell/ ./src/JackCompiler/*.java
cd ./out/shell/
java JackCompiler.JackAnalyzer ../../test/test.jack ../../test/test.xml
cd ../../
```

This module implements the `JackAnalyzer` in textbook on pp.208-209. It reads a `.jack` file, parses it, and output an `.xml` file.

## `JackCodeGenerator` Module

Developing using recursive-descent. It should be able to generate a single `.vm` file from several input `.xml` files, each file defining a class. 
