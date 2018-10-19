package com.github.mengxianun.core.tree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

	private T data;
	private Node<T> parent;
	private List<Node<T>> children;

	public Node() {
		this.children = new ArrayList<>();
	}

	public Node(T data) {
		this();
		this.data = data;
	}

}