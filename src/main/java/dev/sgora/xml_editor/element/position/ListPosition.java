package dev.sgora.xml_editor.element.position;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.List;

public class ListPosition<M> implements ElementPosition<M> {
	private final List list;
	public final Pane listElement;

	public ListPosition(List list, Pane listElement) {
		this.list = list;
		this.listElement = listElement;
	}

	@Override
	public void setModelValue(M value, Node uiElement) throws IllegalAccessException {
		list.set(findChildIndex(uiElement), value);
	}

	public void addElement(int index, M value, Node uiElement) {
		list.add(index, value);
		listElement.getChildren().add(index, uiElement);
	}

	public int removeElement(Node uiElement) {
		int index = listElement.getChildren().indexOf(uiElement);
		list.remove(index);
		listElement.getChildren().remove(uiElement);
		return index;
	}

	private int findChildIndex(Node uiElement) {
		ObservableList<Node> children = listElement.getChildren();
		for (int j = 0; j < children.size(); j++) {
			Node child = children.get(j);
			if (((HBox) child).getChildren().get(0) == uiElement)
				return j;
		}
		return -1;
	}
}
