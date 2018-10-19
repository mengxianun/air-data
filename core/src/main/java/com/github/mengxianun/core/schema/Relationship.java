package com.github.mengxianun.core.schema;

import com.github.mengxianun.core.attributes.AssociationType;

public interface Relationship {

	public Table getPrimaryTable();

	public Column getPrimaryColumn();

	public Table getForeignTable();

	public Column getForeignColumn();

	public AssociationType getAssociationType();

}
