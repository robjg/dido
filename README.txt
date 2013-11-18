Dido-0.0.1
=============

Dido stands for Data-In/Data-Out and is a  framework for reading and 
writing data. It is designed to be used within Oddjob.

This code must be considered experimental at this stage.

In this release:

- Jobs for reading and writing simple flat files (delimited and fixed width). 
- Jobs for Reading and writing Excel files, using Aapche Poi.


To do
-----
- Implement header/trailer functionality by introducing a layout something
  like:
<section>
 <header>
  <text ... />
 </header>
 <body>
  <lines leaveLast="3"/>
   <text ... />
  </lines>
 </body>
 <trailer>
  <lines>
   <text ... />
  </lines>
 </trailer>
</section>

Deferred until a later release
------------------------------

- Supporting persisting layouts. This is so a layout can remember how many 
  lines it has read already and cary on where it left off.
