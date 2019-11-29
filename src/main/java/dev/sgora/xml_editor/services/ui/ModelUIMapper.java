package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.observetree.listener.ChangeListener;
import dev.sgora.xml_editor.element.ComplexElement;
import dev.sgora.xml_editor.element.Element;
import dev.sgora.xml_editor.element.ListElement;
import dev.sgora.xml_editor.element.NumericElement;
import dev.sgora.xml_editor.element.enums.ElementTitleType;
import dev.sgora.xml_editor.element.position.FieldPosition;
import dev.sgora.xml_editor.model.xml.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.model.xml.StatementPeriod;
import dev.sgora.xml_editor.model.xml.Transaction;
import dev.sgora.xml_editor.model.xml.TransactionHistory;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import dev.sgora.xml_editor.services.xml.XMLService;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ModelUIMapper {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private final Model<AccountStatement> model;
	private XMLService xmlService;

	private Pane infoRoot;
	private Pane historyRoot;
	private int rootFieldsCount;

	@Inject
	private ModelUIMapper(Model<AccountStatement> model, XMLService xmlService) {
		this.model = model;
		this.xmlService = xmlService;
		ComplexElement.registerElement = model::addElement;
		model.addListener(this::mapModelToUI);
	}

	public void init(Pane infoRoot, Pane historyRoot) {
		this.infoRoot = infoRoot;
		this.historyRoot = historyRoot;
	}

	private void mapModelToUI() {
		clearElements();
		if(model.getValue() == null)
			return;
		Class modelType = model.getValue().getClass();
		Field[] rootFields = modelType.getDeclaredFields();
		rootFieldsCount = rootFields.length;
		try {
			for (int i = 0; i < rootFields.length; i++) {
				rootFields[i].setAccessible(true);
				Pane root = i < Math.ceil(rootFieldsCount / 2d) ? infoRoot : historyRoot;
				ComplexElement element = new ComplexElement(rootFields[i].get(model.getValue()), new FieldPosition(rootFields[i], model.getValue()), true);
				root.getChildren().add(element.uiElement);
			}
			xmlService.validateXML();
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
		addAccountEndBalance();
	}

	private void addAccountEndBalance() {
		var endBalanceLabel = UIElementFactory.createElementTitle("End Balance", ElementTitleType.SUB);
		var startValue = model.getValue().getPeriod().getStartBalance().doubleValue();
		var balance = new Label(String.valueOf(startValue));
		historyRoot.getChildren().add(UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5, endBalanceLabel, balance));

		var period = getChildOfModelType(model.rootElements, StatementPeriod.class);
		var history = getChildOfModelType(model.rootElements, TransactionHistory.class);
		ListElement transations = (ListElement) getChildOfType(history.children, ListElement.class);
		ChangeListener listener = () -> balance.setText(String.valueOf(calcEndBalance(period, transations)));
		bindTransactionValueChange(transations, listener);
		transations.addListener(() -> {
			bindTransactionValueChange(transations, listener);
			listener.call();
		});
		getChildOfType(period.children, NumericElement.class).addListener(listener);
	}

	private <M extends Element> M getChildOfModelType(List<M> children, Class type) {
		return children.stream().filter(el -> el.modelType == type).findFirst().orElse(null);
	}

	private <M extends Element> M getChildOfType(List<M> children, Class type) {
		return children.stream().filter(el -> el.getClass() == type).findFirst().orElse(null);
	}

	private double calcEndBalance(ComplexElement<StatementPeriod> period, ListElement history) {
		var balance = period.modelValue.getStartBalance().doubleValue();
		return balance + ((List<Transaction>) history.modelValue).stream().mapToDouble(trans -> trans.getAmount().doubleValue()).sum();
	}

	private void bindTransactionValueChange(ListElement transactions, ChangeListener listener) {
		for (Object transaction : transactions.children) {
			var child = getChildOfType(((ComplexElement) transaction).children, NumericElement.class);
			child.clearListeners();
			child.addListener(listener);
		}
	}

	private void clearElements() {
		infoRoot.getChildren().clear();
		historyRoot.getChildren().clear();
	}
}
