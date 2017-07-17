package org.datanucleus.test;

import org.datanucleus.util.NucleusLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import mydomain.model.Person;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SimpleTest
{
    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("MyTest");
        // create a single entity in a transaction
        withTx(emf, em -> {
            Person person = new Person(1, "name");
            em.persist(person);
        });
    }

    @After
    public void tearDown() {
        emf.close();
    }

    @Test
    public void testNativeReadAndUpdateRSM()
    {
        // read the entity in using a native query and Result Set Mapping, modify its name, commit
        withTx(emf, em -> {
            Query q = em.createNativeQuery("select * from Person where id = 1", "RSM_TEST");
            Person p = (Person) q.getSingleResult();
            p.setName("RSM name");
        });

        // read the entity in again, confirm the new name
        withTx(emf, em -> {
            Query q = em.createNativeQuery("select * from Person where id = 1", Person.class);
            Person p = (Person) q.getSingleResult();
            assertThat(p.getName(), is("RSM name"));
        });
    }

    @Test
    public void testNativeReadAndUpdateResultClass()
    {
        // read the entity in using a native query and result class, modify, commit
        withTx(emf, em -> {
            Query q = em.createNativeQuery("select * from Person where id = 1", Person.class);
            Person p = (Person) q.getSingleResult();
            p.setName("result class name");
        });

        // read the entity in again, confirm the new name
        withTx(emf, em -> {
            Query q = em.createNativeQuery("select * from Person where id = 1", Person.class);
            Person p = (Person) q.getSingleResult();
            assertThat(p.getName(), is("result class name"));
        });
    }

    private static void withTx(EntityManagerFactory emf, Consumer<EntityManager> c) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            c.accept(em);
            tx.commit();
        } catch (Throwable thr) {
            NucleusLogger.GENERAL.error(">> Exception in test", thr);
            fail("Failed test : " + thr.getMessage());
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }
}
