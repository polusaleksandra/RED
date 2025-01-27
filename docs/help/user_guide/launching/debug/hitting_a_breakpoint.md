<html>
<head>
<link href="PLUGINS_ROOT/org.robotframework.ide.eclipse.main.plugin.doc.user/help/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<a href="RED/../../../../../help/index.html">RED - Robot Editor User Guide</a> &gt; <a href="RED/../../../../../help/user_guide/user_guide.html">User guide</a> &gt; <a href="RED/../../../../../help/user_guide/launching.html">Launching Tests</a> &gt; <a href="RED/../../../../../help/user_guide/launching/debug.html">Debugging Robot</a> &gt; 
	<h2>Hitting a breakpoint during debug execution</h2>
<p>Whenever debugger suspends the execution there are many useful informations presented to user as well as new
	opportunities to influence the running tests appear. First of all the toolbar buttons gets activated:
	</p>
<img src="images/debug_toolbar.png"/>
<p>moving from left to right:</p>
<ul>
<li><b>Skip All Breakpoints</b> - allow to continue execution onwards without stopping on defined breakpoints
	    (globally disabling all the breakpoints)
	    </li>
<li><b>Resume</b> - <kbd>F8</kbd> described in <a href="../exec_control.html">Controlling execution</a></li>
<li><b>Suspend</b> - as above</li>
<li><b>Terminate</b> - <kbd>Ctrl</kbd>+<kbd>F2</kbd> as above</li>
<li><b>Disconnect</b> - as above</li>
<li><b>Step Into</b> - <kbd>F5</kbd> - each <kbd>F5</kbd> key press will execute active line and move to next 
	    one. If active line consists Keyword or embedded TestCase, test executor will jump into item and execute 
	    it line by line. To exit from executing inherited items use Step Return (<kbd>F7</kbd>)</li>
<li><b>Step Over</b> - <kbd>F6</kbd> - each <kbd>F6</kbd> key press will execute active line and move to next 
	    one. If keyword exists in current line, keyword result will be returned without going into Keyword content</li>
<li><b>Step Return</b> - <kbd>F7</kbd> - allows to return to main TestCase execution from embedded TestCase 
	    or Keyword if Step Into was used before</li>
</ul>
<h3>Debug view</h3>
<p>When execution is suspended the <b>Debug</b> view shows all the frames on current path in execution tree. 
	Bottom part of this path directly corresponds to the tree which can be seen in <b>Execution</b> view as 
	depicted below:
	</p>
<img src="images/debug_debug_view.png"/><br/>
<img src="images/debug_execution_view.png"/>
<p>The bottom frame corresponds to <code>Project</code> suite (this is a directory in file system, so there is a
	little directory decoration visible). Next frame corresponds to <code>Calculations</code> suite (which is a
	<code>calculations.robot</code> file) and the frame above it represents <code>Divisions</code> test inside that 
	suite. Next frames do not correspond to any node inside the execution tree visible in <b>Execution</b> view. It
	can be read that stopped execution is currently inside <code>Divisions</code> test at instruction in line 
	<code>35</code>, which called a keyword <code>Divide</code> which then called another keyword 
	<code>BinaryDivision</code> from line <code>57</code> which finally called library keyword <code>Evaluate</code>
	coming from <code>BuiltIn</code> library at line <code>61</code>. 
	</p>
<p>Additionally you may see that there is a single execution thread (RF executes tests in single thread); the 
	execution is suspended and agent is communicating with RED using localhost at port <code>59344</code>.
	</p>
<h3>Variables view</h3>
<p>Whenever you select some frame inside <b>Debug</b> view the Robot variables defined inside it are shown in
	<b>Variables</b> view. This view handles scalar, list and dictionary variables. The scalar variable only shows 
	its value while the other two types are showing also the their contents inside it. Depending on the type of 
	variable the icon have different color assigned as visible on image below:
	</p>
<img src="images/debug_variables.png"/>
<p>As you can see some of the variables are displayed under <b>Automatic Variables</b> node. This is a place
	where all the variables which are built-in into the Robot are gathered together (refer to <a class="external" href="http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#built-in-variables" target="_blank">
	RF User Guide</a>). All the user variables are displayed on top-level.
	</p>
<p>Variable scope (see <a class="external" href="http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#variable-scopes" target="_blank">
	User Guide</a> on this topic) is reflected in this view using icon decoration: <b>G</b>, <b>S</b>, <b>T</b> or <b>L</b>
	is placed on variable icon for <b>Global</b>, <b>Suite</b>, <b>Test</b>, <b>Local</b> scopes. You may find out
	that global-scoped variables are visible for every single stack frame, suite-scoped variables are only visible 
	in a suite frame and frames below, test-scoped variables only in test frame and below while local-scoped variables
	only in current frame. Of course for example <code>${SUITE_NAME}</code> automatic variable (which has suite scope)
	may be visible for all suite frames, however it may have different values as the suites are nested. 
	</p>
<p>For both dictionaries and lists the actual type of the python object is written in <b>Value</b> column. On the picture above
	<b>DotDict[3]</b> for <code>&amp;{dictionary}</code> variable mean that in python this object has type <b>DotDict</b>, 
	the rest mean that there are <code>3</code> elements inside it. Lists are labeled in the same way. 
	Additionally you may display <b>Actual Type</b> column which would also show types of 
	objects for scalar variables and for objects inside list/dictionaries. To do it click on arrow icon in the top 
	right corner of the Variables view, choose <b><code>Layout -> Select Columns...</code></b> and select <b>Actual Type</b>
	column.
	</p>
<p>Variables are send from Robot to RED every time when RED is ordered to suspend the execution. Sometimes you may observe 
	that variables are highlighted with yellow color:
	</p>
<img src="images/debug_vars_changed.png"/>
<p>
	This mean that variable <code>${scalar}</code> either changed the value comparing to previous time when variables
	were send to RED or it didn't existed previously. Same highlighting will be used if you manually change the value.
	</p>
<h3>Changing variables</h3>
<p>Apart from displaying variables, it is possible to change their values when execution gets suspended.
	This can be done through <b>Variables</b> view in 3 possible ways:
	</p>
<ul>
<li>by editing the cell with value in <b>Value</b> column,
		</li>
<li>by choosing <b>Change Value...</b> from context menu of selected variable,
		</li>
<li>inside the panel at the bottom of <b>Variables</b> view.
		</li>
</ul>
<h4>Variable types</h4>
<p>Scalar variables are assigned with provided value. In case of lists or dictionaries just use usual RobotFramework
	separators in order to provide whole new list/dictionary. For example writing:
	</p>
<code>1&nbsp;&nbsp;&nbsp;&nbsp;2&nbsp;&nbsp;&nbsp;&nbsp;3&nbsp;&nbsp;&nbsp;&nbsp;4
	</code>
<p>for list - variable will create a new list consisting 4 elements while writing:
	</p>
<code>a=1&nbsp;&nbsp;&nbsp;&nbsp;b=2&nbsp;&nbsp;&nbsp;&nbsp;c=3
	</code>
<p>for dictionary - variable will create a new dictionary consisting 3 key-value pairs. Alternatively list or 
	dictionary elements may be provided in comma-separated syntax using brackets:</p>
<code>[1,2,3,4]</code> and: <code>{a=1,b=2,c=3}</code>
<p>for lists and dictionaries respectively.
	</p>
<dl class="note">
<dt>Note</dt>
<dd>Beside changing values of top-level variables it is also possible to change the values inside the lists or 
		dictionaries just the way it is described above.
	   </dd>
</dl>
<p>If the value changes successfully the whole variable will be highlighted with yellow color, otherwise
	you will be presented with error message in case of problems.
	</p>
<h3>Editor</h3>
<p>After suspension you may open source file related to any frame by double clicking on it. By default editor for
	top frame is opened. Of course some frames may not have related source (for example frame representing a suite made
	from directory). Remember that RED debugger only supports debugging Robot code so you will not be able to debug
	python code for library keywords (you may however setup a session in which <a href="robot_python_debug.html">both 
	RF &amp; python code is debugged</a>). Frames created for library keywords have special kind of editor which 
	allows to find the source code for this keyword.
	</p>
<img src="images/debug_editor.png"/>
<h4>Instruction pointers</h4>
<p>The editor opened for any frame displays <b>instruction pointer</b> - by default it's a green background
	displayed in line which relates to chosen stack frame. You may also notice that instruction pointer for
	top frame is a bit darker than pointers for other frames. The way the instruction pointers are displayed can be configured
	in preferences: <code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.eclipse.ui.editors.preferencePages.Annotations)')">
	General -> Editors &gt; Text Editors &gt; Annotations</a></code> (change annotations <b>Debug Call Stack</b> for 
	ordinary frame or <b>Debug Current Instruction Pointer</b> for top frame)
	</p>
<p>You may also encounter situation in which current frame is somehow erroneous. This situation is rather unusual
	in local launches (although may happen) but it can be more common in remote debugging sessions. There may be many 
	different causes for such debugging errors but in general it happens when remote code under execution differs
	from the code found locally in RED workspace. For example picture below presents situation in which remotely
	executing <code>types.robot</code> suite calls <code>Log</code> keyword, but in local code there is a call to 
	<code>Log many</code> keyword. As you can see instruction pointer in this situation is RED and there is a problem
	explanation when you hover the cursor over the problematic line.
	</p>
<img src="images/debug_editor_error.png"/>
<p>Similarly as with usual instruction pointer the outlook of erroneous annotations can be also changed in preferences
	(look for <b>Red Erroneous Debug Call Stack</b> and <b>Red Erroneous Debug Current Instruction Pointer</b>).
	</p>
<h4>Showing variables</h4>
<p>The editor shows current values of variables when hovering mouse cursor over any variable name. This is depicted
	on image above, where <code>${scalar}</code> variable is shown to have current value of <code>100</code>.
	</p>
<h4 id="assist_editor">Assistance editor</h4>
<p>Library keyword frames do not display the code, but instead special kind of <b>debugger assistance</b> editor
	is used. For example if you <b>Step Into</b> the library keyword you will see following editor opened:
	</p>
<img src="images/debug_assist_editor.png"/>
<p>One may change <a href="preferences.html">Debugger preferences</a> in order to never suspend inside the 
	library keyword this way.
	</p>
<p>Additionally assistance editor may also describe erroneous debugger states if there is no source in which
	instruction pointer can be shown. You may found yourself in this situation even in local launches when your test
	call some unknown keyword: 
	</p>
<img src="images/debug_assist_editor_error.png"/>
<h3>Continuing</h3>
<p>Whenever you're ready to resume tests execution simply hit <b>Resume</b> button (or <kbd>F8</kbd>) and 
	debugger will suspend on next breakpoint or in next erroneous state (if not disabled in preferences) or whenever 
	you explicitly pause the execution. Apart from that you may perform step. There are 3 kinds of steps:
	</p>
<ul>
<li><b>Step Into</b> <kbd>F5</kbd> - this kind of step is only possible for top stack frame. When performing
		<b>step into</b> the execution will resume only for a single step which will enter inside into the keyword from current
		line.
		<p></p></li>
<li><b>Step Over</b> <kbd>F6</kbd> - this kind of step is possible for every frame on stack and it will 
		behave differently for each of them. In general this kind of step means 'suspend the execution on next keyword
		from instruction pointed by selected stack frame on the same level'.
		<p></p></li>
<li><b>Step Return</b> <kbd>F7</kbd> - similarly to <b>Step Over</b> this action is possible for every frame
		on stack and will have different behavior. This kind of step means 'suspend the execution on next keyword which
		will be executed after selected frame have ended'. For frame related to user keyword this mean that debugger
		will pause on next instruction after this user keyword ends. For test-related frame the debugger will suspend
		at the very first instruction in next test (if any). For suite-related frame the debugger will suspend at very 
		first keyword in next suite (if any). 
		<p></p></li>
</ul>
<p>Of course the debugger will suspend if it encounter e.g. breakpoint inside the code which should be stepped over. 
	</p>
</body>
</html>