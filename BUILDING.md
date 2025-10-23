Building Dido
=============

Dido builds with Maven from the project root use `maven install`. 
Dido requires JDK 21 or later.

Bespoke Maven targets:
- Build the reference pages: `exec:java@reference -N -X`. Run from the
  root project.
- Build the Documentation: `exec:java@docs` run from `oj-examples`
- Build all the javadocs: `javadoc:aggregate@aggregate` run from the 
  root project

Other useful standard targets:
- Check dependencies: `versions:display-dependency-updates`
- Check plugins: `versions:display-plugin-updates`
