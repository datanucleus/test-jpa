package org.datanucleus.test;

import java.util.*;
import org.junit.*;
import javax.persistence.*;

import static org.junit.Assert.*;
import mydomain.model.*;
import org.datanucleus.util.NucleusLogger;

public class MultithreadTest
{
    @Test
    public void testMulti()
    {
        NucleusLogger.GENERAL.info(">> test START");
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyTest");

        try
        {
            // Persist some data
            NucleusLogger.GENERAL.debug(">> Persisting data");
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                // [Add persistence of sample data for the test]

                tx.commit();
            }
            catch (Throwable thr)
            {
                NucleusLogger.GENERAL.error("Exception in test", thr);
                fail("Failed test : " + thr.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
            NucleusLogger.GENERAL.debug(">> Persisted data");

            // Create the Threads
            int THREAD_SIZE = 500;
            final String[] threadErrors = new String[THREAD_SIZE];
            Thread[] threads = new Thread[THREAD_SIZE];
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                final int threadNo = i;
                threads[i] = new Thread(new Runnable()
                {
                    public void run()
                    {
                        String errorMsg = performTest(emf);
                        threadErrors[threadNo] = errorMsg;
                    }
                });
            }

            // Run the Threads
            NucleusLogger.GENERAL.debug(">> Starting threads");
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                threads[i].start();
            }
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                try
                {
                    threads[i].join();
                }
                catch (InterruptedException e)
                {
                    fail(e.getMessage());
                }
            }
            NucleusLogger.GENERAL.debug(">> Completed threads");

            // Process any errors from threads and fail the test if any failed
            for (String error : threadErrors)
            {
                if (error != null)
                {
                    fail(error);
                }
            }
        }
        finally
        {
            // [Clean up data]
        }

        emf.close();
        NucleusLogger.GENERAL.info(">> test END");
    }

    /**
     * Method to perform the test for a Thread.
     * @param emf The EntityManagerFactory
     * @return A string which is null if the EM operations are successful
     */
    protected String performTest(EntityManagerFactory emf)
    {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            // [Add persistence code to perform what is needed by this EM]

            tx.commit();
        }
        catch (Throwable thr)
        {
            NucleusLogger.GENERAL.error("Exception in test", thr);
            return "Failed test : " + thr.getMessage();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        return null;
    }
}
