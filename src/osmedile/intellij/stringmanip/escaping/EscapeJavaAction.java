package osmedile.intellij.stringmanip.escaping;

import osmedile.intellij.stringmanip.AbstractStringManipAction;
import osmedile.intellij.stringmanip.utils.StringEscapeUtil;

/**
 * @author Olivier Smedile
 * @version $Id: EscapeJavaAction.java 16 2008-03-20 19:21:43Z osmedile $
 */
public class EscapeJavaAction extends AbstractStringManipAction {

	@Override
	public String transformByLine(String s) {
        return StringEscapeUtil.escapeJava(s);
    }
}
