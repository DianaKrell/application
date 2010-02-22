package org.collectionspace.chain.csp.nconfig.impl.parser;

import java.util.List;



/* Like SAX, but simpler. You get reported when you enter and leave a tag, and any text in a tag (or attribute).
 * Attributes are treated as tags which begin with @, immediately within the tag where they appear. 
 * CDATA sections are collapsed into text. PIs are stripped.
 * 
 * You get the complete tag stack each time.
 */

public interface EventConsumer {
	public void start(String tag);
	public void end();
	public void text(String text);
}
