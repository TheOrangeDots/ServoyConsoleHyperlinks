/*
The MIT License (MIT)

Copyright (c) 2016 The Orange Dots

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.tod.eclipse.servoy.console.hyperlinks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class PatternMatchHandler implements IPatternMatchListenerDelegate {
	static Pattern ElementNamePattern = Pattern.compile("forms\\.([a-zA-z]{1}[a-zA-Z$_\\-0-9]*)(?:\\.elements(\\.[a-zA-z$_]{1}[a-zA-Z$_\\-0-9]*|<[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}>))?");
	
	@Override
	public void connect(TextConsole textConsole) {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void matchFound(PatternMatchEvent patternMatchEvent) {
		TextConsole textConsole = (TextConsole) patternMatchEvent.getSource();
		try {
			int offset = patternMatchEvent.getOffset();
			int length = patternMatchEvent.getLength();

			String linkText = textConsole.getDocument().get(offset, length);

			Matcher m = ElementNamePattern.matcher(linkText);
			if (m.find()) {
				String formName = m.group(1);
				String elementName = m.group(2);
				IHyperlink link = new Hyperlink(formName, elementName);
				textConsole.addHyperlink(link, offset, length);
			}
		} catch (BadLocationException e) {
			System.out.println("Failure adding hyperlink");
			e.printStackTrace();
		}
	}
}