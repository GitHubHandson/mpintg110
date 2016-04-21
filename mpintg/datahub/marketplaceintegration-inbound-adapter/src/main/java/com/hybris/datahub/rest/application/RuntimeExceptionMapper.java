package com.hybris.datahub.rest.application;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException>
{

	public Response toResponse(RuntimeException e)
	{
		return Response.status(Response.Status.OK).entity(e.getMessage()).build();
	}
}
