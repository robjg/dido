The Dido Oddball
================

An Oddball is an Oddjob module that plugs into Oddjob in a way that keeps its dependencies separate from
other Oddjob Oddballs.

If you use the Downloaded Oddjob Application then Dido can be resolved
as an 'Oddball'. Here is the configuration to enable the example to be run in this
way:
{@oddjob.xml.file src/test/resources/examples/DidoResolve.xml}
Obviously this complicates the configuration somewhat. It would be
quite simple to create an Oddjob that copied the resolved files into Oddjob's Oddball
directory so that Dido was permanently available within Oddjob. 
