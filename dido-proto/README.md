dido-proto
==========

Protobuf in and out.

Just starting. Not deployed yet.

### Building

You will need the protobuf compiler for your OS from https://github.com/google/protobuf/releases

A Maven `toolchain.xml` file in your `~/.m2` directory is required with something like

    <toolchains>
      <toolchain>
        <type>protobuf</type>
        <provides>
          <version>4.33.0</version>
        </provides>
        <configuration>
          <protocExecutable>~/bin/protoc.exe</protocExecutable>
        </configuration>
      </toolchain>
    </toolchains>

The Maven `protoc` also needs to be enabled. 