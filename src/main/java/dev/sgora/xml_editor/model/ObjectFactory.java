//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.22 at 02:14:28 PM CEST 
//


package dev.sgora.xml_editor.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the dev.sgora.xml_editor.model package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _AccountStatement_QNAME = new QName("", "accountStatement");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dev.sgora.xml_editor.model
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link AccountStatement }
	 */
	public AccountStatement createAccountStatement() {
		return new AccountStatement();
	}

	/**
	 * Create an instance of {@link StatementPeriod }
	 */
	public StatementPeriod createStatementPeriod() {
		return new StatementPeriod();
	}

	/**
	 * Create an instance of {@link AccountState }
	 */
	public AccountState createAccountState() {
		return new AccountState();
	}

	/**
	 * Create an instance of {@link AccountCredit }
	 */
	public AccountCredit createAccountCredit() {
		return new AccountCredit();
	}

	/**
	 * Create an instance of {@link Address }
	 */
	public Address createAddress() {
		return new Address();
	}

	/**
	 * Create an instance of {@link CompanyName }
	 */
	public CompanyName createCompanyName() {
		return new CompanyName();
	}

	/**
	 * Create an instance of {@link FullName }
	 */
	public FullName createFullName() {
		return new FullName();
	}

	/**
	 * Create an instance of {@link Person }
	 */
	public Person createPerson() {
		return new Person();
	}

	/**
	 * Create an instance of {@link TransactionHistory }
	 */
	public TransactionHistory createTransactionHistory() {
		return new TransactionHistory();
	}

	/**
	 * Create an instance of {@link Company }
	 */
	public Company createCompany() {
		return new Company();
	}

	/**
	 * Create an instance of {@link Transaction }
	 */
	public Transaction createTransaction() {
		return new Transaction();
	}

	/**
	 * Create an instance of {@link Account }
	 */
	public Account createAccount() {
		return new Account();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link AccountStatement }{@code >}}
	 */
	@XmlElementDecl(namespace = "", name = "accountStatement")
	public JAXBElement<AccountStatement> createAccountStatement(AccountStatement value) {
		return new JAXBElement<AccountStatement>(_AccountStatement_QNAME, AccountStatement.class, null, value);
	}

}