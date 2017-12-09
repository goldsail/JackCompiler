# JackCompiler

This project is my implementation of a compiler that compiles the `Jack` language into `Jack VM` bytecodes. See [Nand2Tetris](http://nand2tetris.org/) for details.

## `JackTokenizer` Module

Run the unit test without IntelliJ IDEA:

```bash
cd <this git repo>
javac -d ./out/shell/ ./src/JackCompiler/*.java
cd ./out/shell/
java JackCompiler.JackTokenizer ../../test/Test.jack
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
java JackCompiler.JackAnalyzer ../../test/Test.jack ../../test/Test.xml
cd ../../
```

This module implements the `JackAnalyzer` in textbook on pp.208-209. It reads a `.jack` file, parses it, and output an `.xml` file.

This module is finished, but is still under test.

## `JackCodeGenerator` Module

Developed using recursive-descent. It should be able to generate a `.vm` file from an input `.xml` file.

Run the unit test without IntelliJ IDEA:

```bash
cd <this git repo>
javac -d ./out/shell/ ./src/JackCompiler/*.java
cd ./out/shell/
java JackCompiler.JackCodeGenerator ../../test/Test.jack ../../test/Test.xml ../../test/Test.vm
cd ../../
```

This module is finished, but is still under test.

## `HttpWebApi` Module

You can send your `Jack` code by sending POST to endpoint `/compile` at port `80`. The POST data is the code in text format.

Run the Http Web API server (it listens to port `80`):

```bash
cd <this git repo>
javac -d ./out/shell/ ./src/JackCompiler/*.java
cd ./out/shell/
java JackCompiler.HttpWebApi
cd ../../
```

## Reference

[Nand2Tetris Project 10](http://nand2tetris.org/10.php)
[Nand2Tetris Project 11](http://nand2tetris.org/11.php)