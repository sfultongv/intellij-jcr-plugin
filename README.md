Intellij IDEA JCR content editor plugin
=======================================

The idea behind this plugin is to allow people developing for CQ5 to use intellij for all aspects of development, so
that they don't need to utilize CRXDE to edit jcr content (nodes/properties).

Intended development process using this plugin
----------------------------------------------

1. export jcr tree using vlt
2. use plugin to "unpack" any existing xml specifying jcr nodes that you wish to edit (right click on xml file and
select unpack xml)
3. use vlt to import content back into the JCR (you should be able to import unpacked content)
