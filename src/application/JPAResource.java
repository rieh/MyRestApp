/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package application;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/db")
@RequestScoped
public class JPAResource {

    /**
     * The JNDI name for the persistence context is the one defined in web.xml
     */
    private static final String JNDI_NAME = "java:comp/env/jpasample/entitymanager";

    private static String newline = System.getProperty("line.separator");

    @PersistenceContext(unitName = "myPersistenceUnit")
    EntityManager em;
    
    @POST
    @Consumes("text/plain")
    public void createThing()
            throws NamingException, NotSupportedException, SystemException, IllegalStateException, SecurityException,
            HeuristicMixedException, HeuristicRollbackException, RollbackException {
        Context ctx = new InitialContext();
        // Before getting an EntityManager, start a global transaction
        UserTransaction tran = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        tran.begin();

        // Now get the EntityManager from JNDI
        //EntityManager em = (EntityManager) ctx.lookup(JNDI_NAME);
        StringBuilder builder = new StringBuilder().append("Creating a brand new Thing with " + em.getDelegate().getClass()).append(newline);

        // Create a Thing object and persist it to the database
        Thing thing = new Thing();
        em.persist(thing);

        // Commit the transaction
        tran.commit();
        int id = thing.getId();
        builder.append("Created Thing " + id + ":  " + thing).append(newline);
        System.out.println("SKSK: " + builder.toString());
    }

    @GET
    @Produces("text/plain")
    public String retrieveThing() throws SystemException, NamingException {
    	
        StringBuilder builder = new StringBuilder();
        builder.append("Hello JPA World").append(newline);

        // Look up the EntityManager in JNDI
        Context ctx = new InitialContext();
        //EntityManager em = (EntityManager) ctx.lookup(JNDI_NAME);
        // Compose a JPQL query
        String query = "SELECT t FROM Thing t";
        Query q = em.createQuery(query);

        // Execute the query
        List<Thing> things = q.getResultList();
        builder.append("Query returned " + things.size() + " things").append(newline);

        // Let's see what we got back!
        for (Thing thing : things) {
            builder.append("Thing in list " + thing).append(newline);
        }
        return builder.toString();
    }
}
