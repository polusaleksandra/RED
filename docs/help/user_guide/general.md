<html>
<head>
<link href="PLUGINS_ROOT/org.robotframework.ide.eclipse.main.plugin.doc.user/help/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<a href="RED/../../../help/index.html">RED - Robot Editor User Guide</a> &gt; <a href="RED/../../../help/user_guide/user_guide.html">User guide</a> &gt; 
<h2>General usage hints</h2>
<h3>Tab key behavior </h3>
Tab key press behavior can be changed at <code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.robotframework.ide.eclipse.main.plugin.preferences.editor)')">
Window -> Preferences -> Robot Framework -> Editor</a></code> in <b>Source</b> section. <br/>
It is set by default to be aware of the file type. For .tsv files each Tab will produce item separator, for text files 4 spaces will generated. <br/>
Moreover, <b>jump out of active region</b> behavior can be enabled there. If enabled, instead of inserting defined separator in source editor, RED will move cursor to the end of active region. It may be useful for example for variables edition. 
<br/><br/><img src="images/tab_behaviour.png"/> <br/><br/>
<h3>Validating &amp; revalidating whole project/workspace</h3>
Validation of test case is triggered by any user actions, it is also done during files &amp; project imports.<br/>
Whenever there is a change in multiple files (for instance find/replace) or  big file import/deletion, it is good to force revalidation of project.<br/>
It is done accessing option <b><code>Project -> Clean...</code></b>
<br/><br/><img src="images/gen_1.png"/> <br/><br/>
At the bottom right of RED, progress bar will appear with the status of validation.<br/>
<h3>Automatic source formatting CTRL+SHIFT+F</h3>
<p>Formatting source is Eclipse based mechanism which provides code formatting with several predefined rules which are 
disabled by default. Alternatively it is also possible to use <code>Robot Tidy</code> tool to format the source.
</p>
<p>Source code formatting preferences can be configured at <code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.robotframework.ide.eclipse.main.plugin.preferences.editor.formatter)')">
Window -> Preferences -> Robot Framework -> Editor -> Code Formatter</a></code>.
</p>
<p>It is invoked by right click menu in Source editor or automatically as editor save action, which can be configured at
<code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.robotframework.ide.eclipse.main.plugin.preferences.editor.save)')">
Window -> Preferences -> Robot Framework -> Editor -> Save Actions</a></code><br/></p>
<br/><img src="images/gen_5.png"/> <br/><br/>
<dl class="note">
<dt>Note</dt>
<dd>RED does not support source formatting in *.tsv files
   </dd>
</dl>
<h3>Quick Fix - Ctrl+1 </h3>
Quick Fix is an Eclipse mechanism to provide user with predefined action for solving issues.
If the Quick Fix action is available, light bulb icon is shown next to line number.
<br/><br/><img src="images/gen_6.png"/> <br/><br/>
Quick Fix can be accessed by clicking on underlined item and choosing from right click menu Quick Fix or directly by Ctrl+1.
<h4>Running selected test case</h4>
RED can run or debug one, selected testcase. This can be achieved by altering Run Configuration :
<br/><br/><img src="images/run-selected.gif"/> <br/><br/>
and also by placing cursor on testcase body and using right click menu:
<br/><br/><img src="images/run-selected-editors.gif"/> <br/><br/>
</body>
</html>