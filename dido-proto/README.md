
To build a Maven toolchain.xml file is required with something like

<toolchains>
  <toolchain>
    <type>protobuf</type>
    <provides>
      <version>3.21.2</version>
    </provides>
    <configuration>
      <protocExecutable>C:/Users/Rob/bin/protoc.exe</protocExecutable>
    </configuration>
  </toolchain>
</toolchains>
