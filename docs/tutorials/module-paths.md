---
layout: page
title: Module paths
image:
  url: /images/tutorials/module-paths.png
  width: 550px
  height: 210px
  alt: Module paths
---

1.  [Specifying the source root](#specifying-the-source-root)
1.  [Adding content roots](#adding-content-roots)

-----

To configure the module paths, you can:

1.  Select the *File* menu.
1.  Select the *Project Structure* menu.
1.  In the left navigation pane of the project structure dialog, select the
    *Modules* item.

Once you are finished, press the *Apply* button to make the changes and keep
the dialog open, or *OK* to make the changes and close the dialog.

### Specifying the source root
The source roots define where importable modules are searched for when resolving
the import paths in the `at` part of importable modules.

__NOTE:__ This does not currently work for relative import namespace paths, like
those used by BaseX.

__NOTE:__ If there are multiple `at` paths, only the first path that locates a
file is used. The plugin does not currently support adding all the `at` paths to
the specified namespace, like is used in some MarkLogic modules.

To specify the source root, you can:

1.  Click on the module source path on the right, below the *Add content root*
    button.
1.  Navigate to the directory you want to make the source root in the directory
    tree pane.
1.  Click on the *Sources* button above the directory tree pane.

### Adding content roots
MarkLogic provides many helper modules that are importable, for example:

<pre class="highlight">
<span class="keyword">import</span> <span class="keyword">module</span> <span class="keyword">namespace</span> sec = <span class="string">"http://marklogic.com/xdmp/security"</span>
                     <span class="keyword">at</span> <span class="string">"/MarkLogic/security.xqy"</span>;
</pre>

These can be supported in IntelliJ by adding a content root to the project. To
do this, you can:

1.  Click on the *Add content root* button above the pane on the right.
1.  Navigate to the path of the modules you want to support, e.g.
    <div><code class="highlight">C:\Program Files\MarkLogic\Modules</code>.</div>
1.  Press *OK*.
1.  Click on the top-level path that was added in the directory tree pane.
1.  Click on the *Sources* button above the directory tree pane.
