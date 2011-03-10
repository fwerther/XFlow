package ldapd.server.backend.jdbc ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.Collection ;
import java.util.LinkedList ;
import java.util.ListIterator ;
import java.util.Properties ;
import java.util.Enumeration ;
import java.util.HashMap ;
import java.math.BigInteger ;

import javax.naming.Name ;
import javax.naming.CompoundName ;
import javax.naming.NameParser ;
import javax.naming.NamingException ;
import javax.naming.InvalidNameException ;
import javax.naming.directory.AttributeInUseException ;
import javax.naming.directory.InvalidAttributeValueException ;
import javax.naming.directory.AttributeModificationException ;
import javax.naming.directory.InvalidAttributeIdentifierException ;

//import ldapd.util.NamespaceTools ;
import ldapd.server.schema.Schema ;
import ldapd.server.backend.AtomicBackend ;
import ldapd.server.backend.LdapEntry ;
import ldapd.server.backend.BackendException ;
import ldapd.server.protocol.ProtocolModule ;
import ldapd.server.backend.jdbc.util.JdbcTools ;
import ldapd.server.backend.jdbc.JdbcAttribute ;

import org.apache.avalon.framework.logger.Logger ;
import org.apache.commons.collections.MultiHashMap ;

import java.sql.SQLException ;
import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.ResultSet ;
import java.sql.Statement ;
import java.sql.PreparedStatement ;

/**
 * An entry implementation for the SQL backend.
 * 
 * @task No schema checking going on right now - but this needs to change!
 * @task Also we need to make sure that we are storing dates in the apropriate
 * fashion
 */
public class JdbcEntryImpl
	implements ldapd.server.backend.LdapEntry
{
	/**
	 * Members
	 */
	private BigInteger		m_id;
	private	BigInteger		m_parentId;
	private	String			m_dn;
	private	String			m_rdn;
	private Date			m_createStamp;
	private Date			m_changeStamp;
	private	String			m_creator;
	private	String			m_changeor;

	private	boolean			m_isNew;
	private	boolean			m_isValid;
	private boolean			m_isTop;
	private HashMap			m_attributes;

	private	Schema			m_schema;

	JdbcEntryImpl ( String	an_dn )
		throws BackendException, NamingException
	{
		m_attributes = new HashMap();
		StringBuilder l_sqlString = new StringBuilder();

		try
		{
			Connection l_conn = getConnection();
			l_conn.setAutoCommit(false);
			l_sqlString.append("SELECT ID, PARENT, DNPATH, ENTRY, TIMESTAMP, ");
			l_sqlString.append("CREATOR_NAME, CHANGE_TIMESTAMP, CHANGEOR_NAME ");
			l_sqlString.append("FROM MAIN WHERE ");
			l_sqlString.append("UPPER(DNPATH) like UPPER('" + an_dn + "')");

			Statement l_statement = l_conn.createStatement();
			ResultSet l_resultSet = l_statement.executeQuery(l_sqlString.toString());
			if ( l_resultSet.next() )
			{
				m_id = JdbcTools.rsGetBigDecimal(l_resultSet, "ID").toBigInteger();
				m_parentId = JdbcTools.rsGetBigDecimal(l_resultSet, "PARENT").toBigInteger();
				m_dn = new String(JdbcTools.rsGetString(l_resultSet, "DNPATH"));
				m_rdn = new String(JdbcTools.rsGetString(l_resultSet, "ENTRY"));
				m_createStamp = JdbcTools.rsGetDate(l_resultSet, "TIMESTAMP");
				m_creator = new String(JdbcTools.rsGetString(l_resultSet, "CREATOR_NAME"));
				m_changeStamp = JdbcTools.rsGetDate(l_resultSet, "CHANGE_TIMESTAMP");
				m_changeor = new String(JdbcTools.rsGetString(l_resultSet, "CHANGEOR_NAME"));
				m_isNew = false;
				m_isValid = true;
				
				m_isTop = false;
				if ( 0 == m_parentId.intValue() )
				{
					System.out.println("JdbcEntryImpl.constructor: Constructing TOP Entrty.");
					m_isTop = true;
				}

				l_resultSet.close();
				l_statement.close();
				loadAttributes(l_conn);
			}
			else
			{
				l_resultSet.close();
				l_statement.close();
				m_dn = new String(an_dn);
				m_isNew = true;
				m_isValid = false;
				m_isTop = false;
			}

			l_conn.close();
		}
		catch ( SQLException eSql )
		{
			throw new BackendException(new String("SQLException: [" + l_sqlString +"]" + eSql.getMessage()));
		}
		catch ( ClassNotFoundException eNullPointer )
		{
			throw new BackendException(new String("ClassNotFoundException: [" + l_sqlString +"]" + eNullPointer.getMessage()));
		}
	}

	public Collection attributes() 
	{
		LinkedList m = new LinkedList();
		return m ;
	}

	public boolean hasAttribute(String an_attributeName) 
	{
		return containsKey(an_attributeName);
	}

	/**
	 * sets the schema 
	 */
	public void setSchema(Schema on_schema)
	{
		m_schema = on_schema;
	}

	/**
	 * sets the rdn and dn path based on the given 
	 * javax Name.
	 * 
	 * @throw NamingException if the given Name does
	 * not match the current DN.
	 */
	public void setName(Name an_name, String an_backendSuffix)
		throws NamingException, BackendException
	{
		StringBuilder l_dnPath = new StringBuilder();

		try
		{
			if ( an_backendSuffix.equals(m_dn) || null == an_name )
			{ // This is the top entry
				m_isTop = true;
				m_rdn = new String();
				l_dnPath = new StringBuilder();
			}
			else
			{ // This is not the top entry, resolve the parent
				m_isTop = false;
				m_rdn = new String(an_name.get(0));
				int l_xx = 1;
				while ( l_xx<an_name.size() )
				{
					if ( l_xx > 1 )
						l_dnPath.append(",");
					l_dnPath.append((String)an_name.get(l_xx++));
				}
				Connection l_conn = getConnection();
				l_conn.setAutoCommit(false);
				StringBuilder l_sqlString = new StringBuilder("SELECT ID FROM MAIN WHERE ");
				l_sqlString.append(" UPPER(DNPATH) like UPPER('" + l_dnPath + "')");

				Statement l_statement = l_conn.createStatement();
				ResultSet l_resultSet = l_statement.executeQuery(l_sqlString.toString());
				if ( l_resultSet.next() )
				{
					m_parentId = l_resultSet.getBigDecimal(1).toBigInteger();
				}
				l_resultSet.close() ;
				l_statement.close() ;
				l_conn.close();
			}
		}
		catch ( ArrayIndexOutOfBoundsException eArray )
		{
			throw new BackendException(new String("NameException:" + eArray.getMessage()));
		}
		catch ( SQLException eSql )
		{
			throw new BackendException(new String("SQLException:" + eSql.getMessage()));
		}
		catch ( ClassNotFoundException eNullPointer )
		{
			throw new BackendException(new String("ClassNotFoundException:" + eNullPointer.getMessage()));
		}
	}

	/**
	 * @return true if this entry does not exist in the backend,
	 * false otherwise.
	 */
	public boolean isNew()
	{
		return m_isNew;
	}

	/**
	 * @return true if this entry has been persisted to a backend,
	 * false otherwise.
	 */
	public boolean isValid()
	{
		return m_isValid;
	}

	/**
	 * Gets this entry's creation timestamp as a Date.
	 *
	 * @return Date representing the creation timestamp of this entry.
	 */
	public Date getCreateTimestamp() 
	{
		return m_createStamp;
	}

	/**
	 * Gets this entry's modification timestamp as a Date.
	 *
	 * @return Date representing the timestamp this entry was last modified.
	 */
	public Date getModifyTimestamp()
	{
		return m_changeStamp;
	}

	/**
	 * Gets the unique distinguished name associated with this entry as it was
	 * supplied during creation without whitespace trimming or character case
	 * conversions.  This version of the DN is kept within the body of this
	 * Entry as an operational attribute so that it could be returned as it was
	 * given to the server w/o normalization effects: case and whitespace will
	 * be entact.
	 *
	 * @return the distinguished name of this entry as a String.
	 */
	public String getEntryDN()
	{
		return m_dn;
	}

	/**
	 * Gets the unique identifier of this entry as a BigInteger.
	 *
	 * @return BigInteger unique identifier of this entry.
	 */
	public BigInteger getEntryID()
	{
		return m_id;
	}

	/**
	 * Gets the unique identifier of this entry's parent entry as a BigInteger.
	 * The parent ID is equal to the entry id <code>
	 * (getEntryID().equals(parentID()) == true) </code> if this entry is a 
	 * root (a.k.a. suffix) entry.
	 *
	 * @return the uid of this entry's parent.
	 */
	public BigInteger getParentID()
	{
		return m_parentId;
	}

	/**
	 * Gets the number of subordinate child entries that exist for this Entry.
	 *
	 * @return the number of subordinates (children).
	 */
	public BigInteger getNumSubordinates()
	{
		return m_id ;
	}

	/**
	 * Gets the normalized unique distinguished name associated with this 
	 * entry. This DN unlike the user specified DN accessed via getDN() is not
	 * an operational attribute composing the body of this Entry.
	 *
	 * The distinguished name is presumed to be normalized by the server naming
	 * subsystem in accordance with schema attribute syntax and attribute
	 * matching rules. The DN is also presumed to be syntacticly correct and
	 * within the namespace of this directory information base from which this
	 * Entry originated.
	 *
	 * @return the normalized distinguished name of this entry as a String.
	 */
	public Name getNormalizedDN()
	{
		try 
		{
			CompoundName n = new CompoundName(m_dn, new Properties());
			return n ;
		} 
		catch (InvalidNameException eInvalidName) 
		{
			System.out.println("Invalid name exception.");
			return null;
		}
	}

	/**
	 * Gets the normalized unique distinguished name of this Entry's parent.
	 *
	 * The distinguished name is presumed to be normalized by the server naming
	 * subsystem in accordance with schema attribute syntax and attribute
	 * matching rules. The DN is also presumed to be syntacticly correct and
	 * within the namespace of this directory information base from which this
	 * Entry originated.
	 *
	 * @return the normalized distinguished name of this Entry's parent.
	 */
	public String getParentDN()
	{
		return("parentDN");
	}

	/**
	 * Gets the distinguished name of the creator of this entry.
	 *
	 * The distinguished name is presumed to be normalized by the server naming
	 * subsystem in accordance with schema attribute syntax and attribute
	 * matching rules. The DN is also presumed to be syntacticly correct and
	 * within the namespace of this directory information base from which this
	 * Entry originated.
	 * 
	 * @return the distinguished name of the creator.
	 */
	public String getCreatorsName()
	{
		return m_creator ;
	}

	/**
	 * Gets the distinguished name of the last modifier of this entry.
	 *
	 * The distinguished name is presumed to be normalized by the server naming
	 * subsystem in accordance with schema attribute syntax and attribute
	 * matching rules. The DN is also presumed to be syntacticly correct and
	 * within the namespace of this directory information base from which this
	 * Entry originated.
	 * 
	 * @return the DN of the user to modify this entry last.
	 */
	public String getModifiersName()
	{
		return m_changeor ;
	}

	/**
	 * Gets the distinguished name of the subschema subentry for this Entry.
	 *
	 * The distinguished name is presumed to be normalized by the server naming
	 * subsystem in accordance with schema attribute syntax and attribute
	 * matching rules. The DN is also presumed to be syntacticly correct and
	 * within the namespace of this directory information base from which this
	 * Entry originated.
	 * 
	 * @return String of the subschema subentry distinguished name.
	 */
	public String getSubschemaSubentryDN()
	{
		return("subschemasubentryDN");
	}

	/**
	 * Gets a single valued attribute by name or returns the first value of a
	 * multivalued attribute.
	 *
	 * @param a_attribName the name of the attribute to lookup.
	 * @return an Object value which is either a String or byte [] or null if
	 * the attribute does not exist.
	 */
	public Object getSingleValue(String an_attribName)
	{
		String n = new String("attribute value");

		return n ;
	}

	/**
	 * Gets a multivalued attribute by name.
	 *
	 * @param a_attribName the name of the attribute to lookup.
	 * @return a Collection or null if no attribute value exists.
	 */
	public Collection getMultiValue(String an_attribName)
	{
		LinkedList m = new LinkedList();
		return m ;
	}

	/**
	 * Returns TRUE if the given key is contained in the 
	 * attributes hash
	 *
	 * @param a_attribName the name of the attribute to lookup.
	 * @return a true if the attribute exists, false if not.
	 */
	private boolean containsKey(String an_attributeName)
	{
		if ( null == m_attributes.get(an_attributeName) )
			return(false);
		else
			return(true);
	}

	/**
	 * Adds a value to this Entry potentially resulting in more than one value
	 * for the attribute/key.
	 * 
	 * @param an_attribName attribute name/key
	 * @param a_value the value to add
	 * @throws InvalidAttributeIdentifierException when an attempt is made to
	 * add to or create an attribute with an invalid attribute identifier.
	 * @throws InvalidAttributeValueException when an attempt is made to add to
	 * an attribute a value that conflicts with the attribute's schema
	 * definition. This could happen, for example, if attempting to add an
	 * attribute with no value when the attribute is required to have at least
	 * one value, or if attempting to add more than one value to a single
	 * valued-attribute, or if attempting to add a value that conflicts with 
	 * the syntax of the attribute.
	 */
	public void addValue(String an_attribName, Object a_value)
		throws
			AttributeInUseException,
			InvalidAttributeValueException,
			InvalidAttributeIdentifierException
	{        
		JdbcAttribute l_attribute = new JdbcAttribute();

		if(null == a_value || null == an_attribName) 
			return ;
		if(!m_isTop && !m_schema.hasAttribute(an_attribName)) 
			throw new InvalidAttributeIdentifierException(an_attribName +
				" is not a valid schema recognized attribute name.") ;
		if(!m_isTop && m_attributes.containsKey(an_attribName) &&
			m_schema.isSingleValue(an_attribName))
			throw new AttributeInUseException("A key for attribute "
				+ an_attribName + " already exists!") ;
		if(!m_isTop && m_schema.isBinary(an_attribName)) {
			byte [] l_value = null ;
			if (a_value.getClass().isArray())	l_value = (byte []) a_value ; 
			else								l_value = ((String) a_value).getBytes() ;

			if(!m_schema.isValidSyntax(an_attribName, l_value)) 
				throw new InvalidAttributeValueException("'" + a_value
						+ "' does not comply with the syntax for attribute "
						+ an_attribName) ;
			l_attribute = new JdbcAttribute(an_attribName, l_value);
				l_attribute.setBinary(true);
		} else {
			String l_value = null ;
			if(a_value.getClass().isArray()) 	l_value = new String((byte []) a_value) ;
			else								l_value = (String) a_value ;

			if(l_value.trim().equals("")) return ;
			if(null!=m_schema && !m_schema.isValidSyntax(an_attribName, l_value)) 
				throw new InvalidAttributeValueException("'" + a_value
						+ "' does not comply with the syntax for attribute "
						+ an_attribName) ;
				
			l_attribute = new JdbcAttribute(an_attribName, l_value);
			System.out.println(l_attribute.toString());
			l_attribute.setBinary(false);
		}

		l_attribute.create();
		putAttribute(l_attribute);
		
	}

	/**
	 * Adds the given JdbcAttribute to the attribute hash. This has to take
	 * into account situations where the attribute value in the hash is 
	 * actually a list of values.
	 *
	 * @param an_attribName attribute name/key
	 */
	private void putAttribute(JdbcAttribute an_attr)
	{	
		if ( !containsKey(an_attr.getName()) )
			m_attributes.put(an_attr.getName(), an_attr);
		else
		{
			Object l_curValue = m_attributes.get(an_attr.getName());
			if ( l_curValue.getClass().getName().compareTo("java.util.LinkedList") == 0 )
			{
				LinkedList l_multiList = (LinkedList)l_curValue ;
				l_multiList.add(an_attr);
				m_attributes.put(an_attr.getName(), l_curValue);
			}
			else
			{
				LinkedList l_multiList = new LinkedList();
				l_multiList.add(l_curValue);
				l_multiList.add(an_attr);
				m_attributes.put(an_attr.getName(), l_multiList);
			}
		}
	}


	/**
	 * Removes the attribute/value pair in this Entry only without affecting
	 * other values that the attribute may have.
	 *
	 * @param an_attribName attribute name/key
	 * @param a_value the value to remove
	 * @throws AttributeModificationException when an attempt is made to modify
	 * an attribute, its identifier, or its values that conflicts with the
	 * attribute's (schema) definition or the attribute's state.  Also thrown
	 * if the specified attribute name does not exist as a key in this Entry.
	 */
	public void removeValue(String an_attribName, Object a_value)
		throws InvalidAttributeIdentifierException
	{
		Object l_object = m_attributes.get(an_attribName);
		if ( null == l_object )
			return;

		if ( l_object.getClass().getName().compareTo("java.util.LinkedList") == 0 )
		{
			LinkedList l_list = (LinkedList)l_object;
			ListIterator l_listIterate = l_list.listIterator();
			while ( l_listIterate.hasNext() )
			{
				JdbcAttribute l_attrib = (JdbcAttribute)l_listIterate.next();
				if ( l_attrib.getValue().equals(a_value) )
					l_attrib.delete();
			}
		}
		else
		{
			JdbcAttribute l_attrib = (JdbcAttribute)l_object;
			if ( l_attrib.getValue().equals(a_value) )
				l_attrib.delete();
		}
	}

	/**
	 * Removes the specified set of attribute/value pairs in this Entry.
	 *
	 * @param an_attribName attribute name/key
	 * @param a_valueArray the set of values to remove
	 * @throws AttributeModificationException when an attempt is made to modify
	 * an attribute, its identifier, or its values that conflicts with the
	 * attribute's (schema) definition or the attribute's state.  Also thrown
	 * if the specified attribute name does not exist as a key in this Entry.
	 */
	public void removeValues(String an_attribName, Object [] a_valueArray)
		throws InvalidAttributeIdentifierException
	{
		Object l_object = m_attributes.get(an_attribName);
		if ( null == l_object )
			return;

		if ( l_object.getClass().getName().compareTo("java.util.LinkedList") == 0 )
		{
			LinkedList l_list = (LinkedList)l_object;
			ListIterator l_listIterate = l_list.listIterator();
			while ( l_listIterate.hasNext() )
			{
				JdbcAttribute l_attrib = (JdbcAttribute)l_listIterate.next();

				for(int l_xx = 0; l_xx < a_valueArray.length; l_xx++) 
				{
					if ( l_attrib.getValue().equals(a_valueArray[l_xx]) )
						l_attrib.delete();
				}
			}
		}
		else
		{
			JdbcAttribute l_attrib = (JdbcAttribute)l_object;
			for(int l_xx = 0; l_xx < a_valueArray.length; l_xx++) 
			{
				if ( l_attrib.getValue().equals(a_valueArray[l_xx]) )
					l_attrib.delete();
			}
		}
	}

	/**
	 * Removes all the attribute/value pairs in this Entry associated with the
	 * attribute.
	 *
	 * @param an_attribName attribute name/key
	 * @param a_value the value to remove
	 * @throws AttributeModificationException when an attempt is made to modify
	 * an attribute, its identifier, or its values that conflicts with the
	 * attribute's (schema) definition or the attribute's state.  Also thrown
	 * if the specified attribute name does not exist as a key in this Entry.
	 */
	public void removeValues(String an_attribName)
		throws InvalidAttributeIdentifierException 
	{
		Object l_object = m_attributes.get(an_attribName);
		if ( null == l_object )
			return;

		if ( l_object.getClass().getName().compareTo("java.util.LinkedList") == 0 )
		{
			LinkedList l_list = (LinkedList)l_object;
			ListIterator l_listIterate = l_list.listIterator();
			while ( l_listIterate.hasNext() )
			{
				JdbcAttribute l_attrib = (JdbcAttribute)l_listIterate.next();
				l_attrib.delete();
			}
		}
		else
		{
			JdbcAttribute l_attrib = (JdbcAttribute)l_object;
			l_attrib.delete();
		}
	}


	/**
	 * Not worh the comment space.
	 *
	 * @param string a_dn  the dn
	 * @throws NamingException
	 */
	public boolean equals(String a_dn)
		throws NamingException
	{
		if ( m_dn.compareTo(a_dn) == 0 )
			return true;
		else
			return(false);
	}

	
	/**
	 * Retrives the attributes from the database.
	 *
	 * @param connection an open database connection
	 * @throws SQLException
	 */
	private	void loadAttributes(Connection an_conn)
		throws SQLException
	{
		StringBuilder l_sqlString = new StringBuilder();
		l_sqlString.append("SELECT NAME, VALUE FROM ATTRIBUTES ");
		l_sqlString.append("WHERE MAINID=" + m_id);
		Statement l_statement = an_conn.createStatement();
		ResultSet l_resultSet = l_statement.executeQuery(l_sqlString.toString());

		while ( l_resultSet.next() )
		{
			JdbcAttribute l_attribute;
			l_attribute = new JdbcAttribute(JdbcTools.rsGetString(l_resultSet, "NAME"), 
				JdbcTools.rsGetObject(l_resultSet, "VALUE"),
				m_id,
				m_rdn);
			l_attribute.update();
			putAttribute(l_attribute);
		}

		l_resultSet.close();
		l_statement.close();
	}


	/**
	 * Stores the entry and attributes to the database.
	 *
	 * @throws BackendException when the database drivers are
	 * not found, or a SQ exception is blown.
	 */
	public void store()
		throws BackendException
	{
		if ( !m_isValid )
		{
			try
			{
				Connection l_conn = getConnection();
				l_conn.setAutoCommit(false);
				StringBuilder l_sqlString = new StringBuilder("SELECT MAX(ID)+1 FROM MAIN");
				Statement l_statement = l_conn.createStatement();
				ResultSet l_resultSet = l_statement.executeQuery(l_sqlString.toString());
				l_resultSet.next();
				m_id = JdbcTools.rsGetBigDecimal(l_resultSet, 1).toBigInteger();
				l_resultSet.close();
				l_statement.close();

				System.out.println("There are " + m_attributes.size() + " attributes");
				Iterator l_attrIterate = m_attributes.values().iterator();

				ListIterator l_listIterate ;
				LinkedList l_curList ;
				JdbcAttribute l_curAttr ;
				Object l_obj ;
				Object loxx;
				while ( l_attrIterate.hasNext() )
				{
					l_obj = l_attrIterate.next();
					System.out.println("ATTR TYPE [" + l_obj.getClass().getName() + "]");
					if ( l_obj.getClass().getName().compareTo("java.util.LinkedList") == 0 )
					{
						l_curList = (LinkedList)l_obj;
						l_listIterate = l_curList.listIterator();
						while ( l_listIterate.hasNext() )
						{
							loxx = l_listIterate.next();
							System.out.println("LL Next Object of Type [" + loxx.getClass().getName() + "]");
							l_curAttr = (JdbcAttribute)loxx ;
							l_curAttr.setEntryID(m_id);
							l_curAttr.setEntryRDN(m_rdn);
							l_curAttr.store(l_conn);
							System.out.println(l_curAttr.toString());
						}
					}
					else
					{
						l_curAttr = (JdbcAttribute)l_obj;
						l_curAttr.setEntryID(m_id);
						l_curAttr.setEntryRDN(m_rdn);
						l_curAttr.store(l_conn);
						System.out.println(l_curAttr.toString());
					}
				}

				if ( m_isNew )
				{
					l_sqlString = new StringBuilder("INSERT INTO MAIN VALUES (");
					l_sqlString.append(m_id + ",");
					l_sqlString.append(m_parentId + ",");
					l_sqlString.append("'" + m_dn + "',");
					l_sqlString.append("'" + m_rdn + "',");
					l_sqlString.append("SYSDATE" + ",");
					l_sqlString.append("'" + "somebody" + "',");
					l_sqlString.append("SYSDATE" + ",");
					l_sqlString.append("'" + "somebody" + "')");
					l_statement = l_conn.createStatement();
					l_statement.executeUpdate(l_sqlString.toString());
					l_statement.close();
				}					
				else
				{
					l_sqlString = new StringBuilder("UPDATE MAIN SET (");
					l_sqlString.append("PARENT=" + m_parentId + ",");
					l_sqlString.append("DNPATH=" + m_dn + ",");
					l_sqlString.append("ENTRY=" + m_rdn + ",");
					l_sqlString.append("CHANGE_TIMESTAMP=" + "SYSDATE" + ",");
					l_sqlString.append("CHANGEOR_NAME=" + "somebody" + ")");
					l_statement = l_conn.createStatement();
					l_statement.executeUpdate(l_sqlString.toString());
					l_statement.close();
				}
				l_conn.commit();
				l_conn.close();
				m_isValid = true;
			}
			catch ( SQLException eSql )
			{
				throw new BackendException(new String("SQLException:" + eSql.getMessage()));
			}
			catch ( ClassNotFoundException eNullPointer )
			{
				throw new BackendException(new String("ClassNotFoundException:" + eNullPointer.getMessage()));
			}
		}	
	}
	public void print()
	{
		System.out.println("JdbcEntry Values:");
		System.out.println("    m_id = " + m_id);
		System.out.println("    m_parentId = " + m_parentId);
		System.out.println("    m_dn = " + m_dn);
		System.out.println("    m_rdn = " + m_rdn);
		System.out.println("    m_createStamp = " + m_createStamp);
		System.out.println("    m_changeStamp = " + m_changeStamp);
		System.out.println("    m_creator = " + m_creator);
		System.out.println("    m_changeor = " + m_changeor);
		System.out.println("    m_changeor = " + m_changeor);
		System.out.println("    m_isNew = " + m_isNew);
		System.out.println("    m_isValid = " + m_isValid);
	}

    private boolean isDebugEnabled()
    {
        return false; //m_backend.getLogger().isDebugEnabled() ;
    }
	private Connection getConnection()
		throws SQLException, ClassNotFoundException 
	{
		Connection	l_conn;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		l_conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:local", "ofac", "ofac");
		l_conn.setAutoCommit(false);
		return(l_conn);
	}
}