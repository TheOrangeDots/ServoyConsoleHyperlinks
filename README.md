# Servoy Console Hyperlink plugin
The `Servoy Console Hyperlinks` plugin matches form and element name patterns in log entries in the Console in [Servoy](http://servoy.com) Developer and converts them to hyperlinks that open the relevant Form Editor and selects the element (if specified) when clicked

Each entry into the console will be scanned for the patterns `forms.xxxx` and `forms.xxxx.elements.yyyy` and if found, converts the matched text into links

# Use case
This plugin was developed to aid in the process of making the UI in Servoy solutions consistent. In an attempt to fix inconsistencies, code was written to analyse forms using the `solutionModel` and write inconsistencies out to the Console in Servoy Developer using `application.output`

However, opening the form in question in a Form Editor and locating the element in question turned out to be very cumbersome and time consuming. Hence this plugin was written to convert the entries in the Console to a hyperlink, so opening the form in a form Editor and selecting the element would be as simple as clicking the link

While this was the use case for writting the plugin, other people might find other usecases for it

# Requirements
- Servoy Developer 7 or higher

# Installation
The Servoy Console Hyperlink plugin for Eclipse/Servoy Developer can be installed through `Help > Install New Software` and specifying https://github.com/TheOrangeDots/ServoyConsoleHyperlinks/raw/master/patternMatcher.updateSite as the update site

# Feature Requests & Bugs
Found a bug or would like to see a new feature implemented? Raise an issue in the [Issue Tracker](https://github.com/TheOrangeDots/ServoyConsoleHyperlinks/issues)

# Contributing
Eager to fix a bug or introduce a new feature? Clone the repository and issue a pull request

# License
The Servoy Console Hyperlink plugin is licensed under MIT License
