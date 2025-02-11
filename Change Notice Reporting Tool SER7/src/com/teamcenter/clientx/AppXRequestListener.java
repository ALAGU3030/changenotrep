package com.teamcenter.clientx;

import com.teamcenter.soa.client.RequestListener;

/**
 * This implemenation of the RequestListener, logs each service request
 * to the console.
 *
 */
public class AppXRequestListener implements RequestListener
{

    /**
     * Called before each request is sent to the server.
     */
    public void serviceRequest ( final Info info )
    {
         // will log the service name when done
    }
    
    /**
     * Called after each response from the server.
     * Log the service operation to the console.
     */
    public void serviceResponse( final Info info )
    {
    }

}
