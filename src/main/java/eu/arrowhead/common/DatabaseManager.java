/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common;

import eu.arrowhead.common.exception.DuplicateEntryException;
import eu.arrowhead.core.ArrowheadMain;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceConfigurationError;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

public class DatabaseManager {

  private static DatabaseManager instance;
  private static SessionFactory sessionFactory;
  private static final String dbAddress = ArrowheadMain.getProp().getProperty("db_address", "jdbc:mysql://127.0.0.1:3306/log");
  private static final String dbUser = ArrowheadMain.getProp().getProperty("db_user", "root");
  private static final String dbPassword = ArrowheadMain.getProp().getProperty("db_password", "root");
  private static final Logger log = Logger.getLogger(DatabaseManager.class.getName());

  static {
    try {
      if (sessionFactory == null) {
        sessionFactory = new Configuration().configure().setProperty("hibernate.connection.url", dbAddress)
            .setProperty("hibernate.connection.username", dbUser).setProperty("hibernate.connection.password", dbPassword).buildSessionFactory();
      }
    } catch (Exception e) {
      log.fatal("Database connection failed, check the configuration!");
      throw new ServiceConfigurationError("Database connection could not be established, check app.properties.sample!", e);
    }
  }

  private DatabaseManager() {
  }

  public static DatabaseManager getInstance() {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }

  public <T> T get(Class<T> queryClass, int id) {
    T object;
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      object = session.get(queryClass, id);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }

    return object;
  }

  private SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      sessionFactory = new Configuration().configure().setProperty("hibernate.connection.url", dbAddress)
          .setProperty("hibernate.connection.username", dbUser).setProperty("hibernate.connection.password", dbPassword).buildSessionFactory();
    }
    return sessionFactory;
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> queryClass, Map<String, Object> restrictionMap) {
    T object;
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      Criteria criteria = session.createCriteria(queryClass);
      if (restrictionMap != null && !restrictionMap.isEmpty()) {
        for (Entry<String, Object> entry : restrictionMap.entrySet()) {
          criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
      }
      object = (T) criteria.uniqueResult();
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      log.error("get throws exception: " + e.getMessage());
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }

    return object;
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getAll(Class<T> queryClass, Map<String, Object> restrictionMap) {
    List<T> retrievedList;
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      Criteria criteria = session.createCriteria(queryClass);
      if (restrictionMap != null && !restrictionMap.isEmpty()) {
        for (Entry<String, Object> entry : restrictionMap.entrySet()) {
          criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
      }
      retrievedList = (List<T>) criteria.list();
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      log.error("getAll throws exception: " + e.getMessage());
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }

    return retrievedList;
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getAllOfEither(Class<T> queryClass, Map<String, Object> restrictionMap) {
    List<T> retrievedList;
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      Criteria criteria = session.createCriteria(queryClass);
      if (restrictionMap != null && !restrictionMap.isEmpty()) {
        Disjunction disjunction = Restrictions.disjunction();
        for (Entry<String, Object> entry : restrictionMap.entrySet()) {
          disjunction.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
        criteria.add(disjunction);
      }
      retrievedList = (List<T>) criteria.list();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }

    return retrievedList;
  }


  public <T> T save(T object) {
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      session.save(object);
      transaction.commit();
    } catch (ConstraintViolationException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      log.error("DatabaseManager:save throws DuplicateEntryException");
      throw new DuplicateEntryException(
          "There is already an entry in the database with these parameters. Please check the unique fields of the " + object.getClass(),
          Status.BAD_REQUEST.getStatusCode(), DuplicateEntryException.class.getName(), DatabaseManager.class.toString(), e);
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }

    return object;
  }


  public <T> T merge(T object) {
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      session.merge(object);
      transaction.commit();
    } catch (ConstraintViolationException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      log.error("DatabaseManager:merge throws DuplicateEntryException");
      throw new DuplicateEntryException(
          "There is already an entry in the database with these parameters. Please check the unique fields of the " + object.getClass(),
          Status.BAD_REQUEST.getStatusCode(), DuplicateEntryException.class.getName(), DatabaseManager.class.toString(), e);
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }

    return object;
  }

  public <T> void delete(T object) {
    Transaction transaction = null;

    try (Session session = getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      session.delete(object);
      transaction.commit();
    } catch (ConstraintViolationException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      log.error("DatabaseManager:delete throws ConstraintViolationException");
      throw new DuplicateEntryException(
          "There is a reference to this object in another table, which prevents the delete operation. (" + object.getClass() + ")",
          Status.BAD_REQUEST.getStatusCode(), DuplicateEntryException.class.getName(), DatabaseManager.class.toString(), e);
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw e;
    }
  }

  // NOTE this only works well on tables which dont have any connection to any other tables (HQL does not do cascading)
  public void deleteAll(String tableName) {
    Session session = getSessionFactory().openSession();
    String stringQuery = "DELETE FROM " + tableName;
    Query query = session.createQuery(stringQuery);
    query.executeUpdate();
  }
}