package ua.translate.controller.support;

import java.io.Serializable;
import java.util.Comparator;

import ua.translate.model.Comment;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * The comparison is primarily based on the creation date-time, from latest to earliest
 * @author Yuriy Phediv
 *
 */

public class CommentComparatorByDate implements Comparator<Comment> {

	@Override
	public int compare(Comment o1, Comment o2) {
		return o2.getCreatingDate().
				compareTo(o1.getCreatingDate());
	}

}
