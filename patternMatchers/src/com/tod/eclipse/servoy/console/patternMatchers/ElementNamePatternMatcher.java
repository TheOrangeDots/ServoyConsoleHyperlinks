package com.tod.eclipse.servoy.console.patternMatchers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import com.servoy.j2db.util.Debug;

public class ElementNamePatternMatcher implements IPatternMatchListenerDelegate {
	static Pattern ElementNamePattern = Pattern.compile("forms\\.([a-zA-z]{1}[a-zA-Z$_\\-0-9]*)(?:\\.elements(\\.[a-zA-z$_]{1}[a-zA-Z$_\\-0-9]*|<[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}>))");
	
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
				IHyperlink link = new ElementHyperlink(formName, elementName);
				textConsole.addHyperlink(link, offset, length);
			}
		} catch (BadLocationException e) {
			Debug.error("Failure adding hyperlink", e);
		}
	}
}