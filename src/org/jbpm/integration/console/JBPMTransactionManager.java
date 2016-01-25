package org.jbpm.integration.console;

import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;

public class JBPMTransactionManager implements TransactionManagerLookup 
{
	public Object getTransactionIdentifier(Transaction transaction) {
		return transaction;
	}

	public TransactionManager getTransactionManager(Properties properties)
			throws HibernateException  {
		try {
			return (TransactionManager) new InitialContext()
					.lookup("java:jboss/TransactionManager");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getUserTransactionName() {
		return null;
	}
}
