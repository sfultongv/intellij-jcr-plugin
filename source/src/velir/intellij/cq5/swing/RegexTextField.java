package velir.intellij.cq5.swing;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.regex.Pattern;

public class RegexTextField extends JTextField {

	static class RegexDocument extends PlainDocument {
		private Pattern pattern;

		public RegexDocument (Pattern pattern) {
			super();
			this.pattern = pattern;
		}

		public void insertString(int offset, String s, AttributeSet a) throws BadLocationException {
			if (s == null) s = "";

			String oldString = getText(0, getLength());
			String newString = oldString.substring(0, offset) + s + oldString.substring(offset);

			if (pattern.matcher(newString).matches()) {
				super.insertString(offset, s, a);
			}

		}
	}

	private Pattern pattern;

	public RegexTextField (Pattern pattern, String init) {
		super(new RegexDocument(pattern), init, 10);

		this.pattern = pattern;
	}

	public boolean isValid() {
		try {
			String s = getText();
			return pattern.matcher(s).matches();
		} catch (NullPointerException npe) { return true; }
	}
}
