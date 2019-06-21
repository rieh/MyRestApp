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

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/db")
@RequestScoped

@DenyAll
public class JPAResource {

    private static String newline = System.getProperty("line.separator");

    @PersistenceContext(unitName = "myPersistenceUnit")
    EntityManager em;
    
    @POST
    @Consumes("text/plain")
    
    @RolesAllowed("Admin")    
    public void createThing() throws Exception {
        Context ctx = new InitialContext();
        // Before getting an EntityManager, start a global transaction
        UserTransaction tran = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        tran.begin();

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
    @RolesAllowed("Employee")
    public String retrieveThing(@QueryParam("id") int id) throws Exception {
    	
        StringBuilder builder = new StringBuilder();
        builder.append("Hello JPA World").append(newline);

        // Compose a JPQL query
        String query = "SELECT t FROM Thing t WHERE t.id = " + id;
        TypedQuery<Thing> q = em.createQuery(query,  Thing.class);

        // Execute the query
        Thing t = q.getSingleResult();
        
        builder.append("Query returned " + t);
        return builder.toString();
    }
    
    @GET
    @Produces("text/plain")
    @Path("/count")    
    @PermitAll
    public String getCount() throws Exception {
    	
        StringBuilder builder = new StringBuilder();
        builder.append("Hello JPA World").append(newline);
        
        // Compose a JPQL query
        String query = "SELECT COUNT(t) FROM Thing t";
        TypedQuery<Long> q = em.createQuery(query,  Long.class);

        // Execute the query
        Long count = q.getSingleResult();
        builder.append("Query returned " + count + " number of things");
        
        return builder.toString();
    }
}
