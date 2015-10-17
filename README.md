# JMetaGraphX
## Modeling Java and Scala code in Spark GraphX

**Rationale**

Apache Spark GraphX has algorithms to process graphs.
Java and Scala code have large call graphs and dependency graphs.
These programs are to aide getting the data needed for GraphX, Vertex and Edge objects from byte code using ASM and other tools.

We can now explore the structure of large implementations with the GraphX algorithms.
We can also join unique datasets with graphs; information on programmers, platform releases, etc ...

**Mapping Java and Scala to GraphX**

The core mapping from the Java/Scala/JVM to GraphX will be Class = Vertex, Method Call = Edge, and Method Return = Edge.

    class A {
      B b = new B();
      C c = b.getC();
    }

Let's write the graph.
There are three Vertex objects: A, B, C.
These are the edges.

- A "has field" B
- A "has field" C
- A "method call" B, named "getC"
- B "method return" C, named "getC"

**Assigning VertexId**

GraphX uses 64-bit longs for VertexId.
We will construct a map from every class and inner class to a unique VertexId 64-bit long.
We will want to scan class files and jars in parallel; therefore, we will also have a utility merge the fully qualified class names into a single VertexId mapping.

**Vertex Data**

Each vertex from a class will have vertex data, VD, items containing (if available):

* extraction UTC timestamp in milliseconds
* source file name, jar or class
* source line number
* MANIFEST.MF/META-INF items

**Edge Data**

There are many different kinds of Edge relationships.
Each will have different edge data, ED, classes depending on the specific relationship.
Refer to each edge data class for the specifics of the relationship it models.
