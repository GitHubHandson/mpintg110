package com.hybris.datahub.rest.application;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.hybris.datahub.exception.DataHubException;


@Provider
public class DataHubExceptionMapper implements ExceptionMapper<DataHubException>
{

	public Response toResponse(DataHubException e)
	{
		return Response.status(Response.Status.OK).entity(e.getMessage()).build();
	}
}
