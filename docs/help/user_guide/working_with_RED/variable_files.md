<html>
<head>
<link href="PLUGINS_ROOT/org.robotframework.ide.eclipse.main.plugin.doc.user/help/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<a href="RED/../../../../help/index.html">RED - Robot Editor User Guide</a> &gt; <a href="RED/../../../../help/user_guide/user_guide.html">User guide</a> &gt; <a href="RED/../../../../help/user_guide/working_with_RED.html">Working with RED</a> &gt; 
<h2>General info about Python variable files used in Robot Test Suites</h2>
RobotFramework allows to use Python variable files in Robot Test Suites in two distinctive ways.<br/>
<b>Direct usage</b> is done by using python file declaration in Settings section as follows:<br/>
<code>Variables  &lt;path_to_python_var_file&gt;</code><br/><br/>
Variable file content will be visible across Test Suite.<br/>
<b>Global usage</b> is a way to use common variable file across any Test Suite.<br/>
In RED, this can be achieved by including variable files in RED.xml under Variable Files tab. <br/>
From Robot perspective this is done by using -V switch in command line.<br/>
<h2>Local Python variable files to deal with missing variables</h2>
In some test environments, Robot specific environments variables are used in test cases or injected as arguments to Robot test runner. <br/>
RED is not aware of such usage thus those variables will be marked as unknown, error marker will be placed.<br/>
In order to include variable name validation without changing test suites, user can create local Python file with list of variable names inside.<br/>
Such file can be included in RED.xml under Variable files section thus making RED aware of previously unknown variables. <br/>
Variables from such file will be visible as Global variables for all Robot files inside Project.<br/>
<dl class="note">
<dt>Note</dt>
<dd>Variables provided in red.xml are used only by RED for validation purpose and are not added to Robot run command line.
    During test execution variables have to be provided by Robot.</dd>
</dl>

Below is a sample body of such Python variable file (examples can be also found in RobotFramework official manual and Python examples). 
<img src="images/var_files_red_xml.gif"/>
<code>
#!python 
<br/>
#Sample variables and values
Scalar = 'value'
UserList = ['value1','value2']
UserDict ={'key1':'value1', 'key2':'value2'}
</code>
<h2>Reloading variable file content</h2>
Since version 0.6.6, RED can automatically invoke refresh of variable files being changed.<br/>
This can be done manually by clicking on Project by RMB and selecting <code>Robot Framework -> Reset Robot Environment</code>
<br/><br/><img src="images/reset_robot_env.gif"/> <br/><br/>
</body>
</html>