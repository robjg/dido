package org.oddjob.dido.other;

/**
 * Something that can be a Section of something. Examples of a section would
 * be a header or a trailer line.
 * 
 * @author rob
 *
 */
public interface Section {

	/**
	 * The required number of whatever forms the sections, e.g. the number
	 * of lines.
	 * 
	 * @return The required number of what forms the sections. 0 or less is
	 * taken to be a multiplicity of 0 or many.
	 */
	public int getRequired();
	
}
