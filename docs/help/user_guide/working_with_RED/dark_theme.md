<html>
<head>
<link href="PLUGINS_ROOT/org.robotframework.ide.eclipse.main.plugin.doc.user/help/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<a href="RED/../../../../help/index.html">RED - Robot Editor User Guide</a> &gt; <a href="RED/../../../../help/user_guide/user_guide.html">User guide</a> &gt; <a href="RED/../../../../help/user_guide/working_with_RED.html">Working with RED</a> &gt; 
<h2>Dark theme in RED</h2>
<p>Dark theme is a popular way to reduce eyes strain especially in low-light working environment reducing contrast 
between dark background and display. From version 0.8.1 RED is compatible with Eclipse default dark theme.
</p>
<h3>RED Syntax Coloring profiles</h3>
<p>RED provides 2 default syntax coloring profiles used in RED source &amp; tables editors. Settings can be found at 
<code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.robotframework.ide.eclipse.main.plugin.preferences.editor.syntax)')">
Window -> Preferences -> Robot Framework -> Editor -> Syntax Coloring</a></code>.
</p>
<p><b>Default</b> profile is used for light/default theme in Eclipse and it is the same color palette as in older 
versions of RED. New <b>heliophobia</b> profile is prepared to work with dark background. Any customization of
colors are saved into <b>custom</b> profile. 

<br/>Remember to reopen files after changing syntax coloring profiles.
</p>
<dl class="note">
<dt>Note</dt>
<dd>The syntax coloring will automatically change to <b>heliophobia</b> profile once the theme is changed 
   to dark. Some other colors stored in preferences are also adjusted - for example keyword occurrences annotation
   displayed in source editor.</dd>
</dl>
<img src="images/robot-color-profiles.png"/>
<h3>Changing to Dark theme</h3>
<p>Dark theme can be changed in 
<code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.eclipse.ui.preferencePages.Views)')">
Window -> Preferences -> Appearance</a></code>,
</p>
<p><img src="images/apperence-dark.png"/>
</p>
<p><img src="images/red-dark.png"/>
</p>
<h3>Darkest Dark theme - expanded Dark theme for Eclipse</h3>
<p><img src="images/darkest-dark.png"/>
</p>
<p>Darkest Dark plugin is the external theme which greatly improves Eclipse dark theme making it consistent with 
modified icons and other UI elements for dark profile.
</p>
<p>Darkest Dark Theme can be installed from 
<b><code>Help -> Eclipse Marketplace</code></b> and then be activated from 
<code><a class="command" href="javascript:executeCommand('org.eclipse.ui.window.preferences(preferencePageId=org.eclipse.ui.preferencePages.Views)')">
Window -> Preferences -> Appearance</a></code>
</p>
<p><img src="images/apperence-darkest-dark.png"/>
</p>
</body>
</html>