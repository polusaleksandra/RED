<html>
<head>
<link href="PLUGINS_ROOT/org.robotframework.ide.eclipse.main.plugin.doc.user/help/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<a href="RED/../../../../help/index.html">RED - Robot Editor User Guide</a> &gt; <a href="RED/../../../../help/user_guide/user_guide.html">User guide</a> &gt; <a href="RED/../../../../help/user_guide/working_with_RED.html">Working with RED</a> &gt; 
<h2>Table Editors - general usage hints</h2>
<h3>Jump to Source, View in Table Editor</h3>
Clicking on element in Source and pressing F4 key will open respectful Table editor.<br/>
The same work other way around - element from Table editor is shown in Source.

<h3>Undo - CTRL+Z actions in Table Editors</h3>
Those table actions groups are independent from each other which means that CTRL+Z reverts only in active table editor even when there were other actions performed in another table editor in between.<br/>
Each table editor stores theirs list of performed actions which can be reverted by CTRL+Z. <br/>
Note that due to table to source synchronization, revertible actions are discarded when switching to Source editor. 

<h3>Changing default type in Add new Variable</h3>
Type of new variable in Variable Editor can be controlled by small green arrow next to "...add new xxx":
<br/><br/><img src="images/add_new_var.png"/> <br/><br/>
Scalar type is displayed as default on add action element. Other types are: list and dictionary.<br/>
<h3>Table preferences</h3>
All table related preferences (cell text folding, number of columns, default behaviors) can be configured at <code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.robotframework.ide.eclipse.main.plugin.preferences.editor)')">
Window -> Preferences -> Robot Framework -> Editor</a></code> in <b>Tables</b> section.
<br/><br/><img src="images/table_preferences.png"/> <br/><br/>
<h3>Default number of columns in Test Cases/Keywords editors</h3>
To make Table editors tidy, RED creates predefined numbers of columns.<br/>
<h3>Enter key - what to do after Enter key press during cell edit</h3>
Enter key type can be behave in two ways in RED while editing cell in any of Table editors.<br/>
By default, hitting Enter will end cell edit and move cursor to next cell to the right. If the cell is the last in row (for instance in comment cell), Enter will move cursor to new row.<br/>
Additionally it can be configured that Enter will finish cell edit and cursor will stay on current cell.<br/>
<b>Hint:</b> by pressing Shift+Enter, cursor will move backwards.
</body>
</html>