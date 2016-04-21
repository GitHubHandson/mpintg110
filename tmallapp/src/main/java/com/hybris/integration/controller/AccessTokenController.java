/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.integration.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.hybris.integration.model.AccessToken;
import com.hybris.integration.service.datahub.DataHubAuthService;
import com.hybris.integration.service.tmall.AccessTokenService;


/**
 * Access Token Controller
 */
@Controller
@RequestMapping("/view/accesstoken")
public class AccessTokenController
{
	private static Logger LOGGER = LoggerFactory.getLogger(AccessTokenController.class);

	@Value("${oauth.redirect.url}")
	private String oauthRedirectUrl;

	@Value("${oauth.server.url}")
	private String oauthServerUrl;

	@Autowired
	private AccessTokenService accessTokenService;

	@Autowired
	private DataHubAuthService dhAuthService;

	private void addCustomHeader(HttpServletResponse resp)
	{
		resp.setHeader("X-Frame-Options", "SAMEORIGIN");
	}

	/**
	 * @param request
	 * @param response
	 * @return view
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView toOauthPage(HttpServletRequest request, HttpServletResponse response)
	{
		addCustomHeader(response);
		return new ModelAndView("oauth");
	}

	/**
	 * Get code to tmall
	 *
	 * @param token
	 *           Users fill in information.
	 * @param session
	 * @return view
	 */
	@RequestMapping(value = "toOauth")
	public ModelAndView toOauth(AccessToken token, HttpSession session)
	{
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		if (!pattern.matcher(token.getAppkey()).matches() || !pattern.matcher(token.getSecret()).matches())
		{
			return null;
		}
		String state = String.valueOf(System.currentTimeMillis());
		session.setAttribute("state", state);
		session.setAttribute("accessToken", token);

		LOGGER.trace("Authorization infomation , marketplaceStoreId=[" + token.getMarketplaceStoreId() + "].");

		String requestUrl = oauthServerUrl + "/authorize?" + "response_type=code" + "&client_id=" + token.getAppkey()
				+ "&redirect_uri=" + oauthRedirectUrl + "&state=" + state + "&view=web";

		LOGGER.trace("Redirect to TMALL : " + requestUrl);
		return new ModelAndView("redirect:" + requestUrl);
	}

	/**
	 * Go tmall exchange access_token, and the authorization information persistence, then the correspondence between
	 * appkey and integrationId told datahub
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return view
	 */
	@RequestMapping(value = "doOauth")
	public ModelAndView doOauth(HttpServletRequest request, HttpServletResponse response, HttpSession session)
	{
		addCustomHeader(response);
		String code = request.getParameter("code");
		String state = request.getParameter("state");

		AccessToken accessToken = (AccessToken) session.getAttribute("accessToken");
		String sessionState = (String) session.getAttribute("state");
		Map<String, Object> model = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView("oauthResult");

		if (accessToken == null)
		{
			model.put("error", "Authorized accident, loss of data, please try again.");
			return modelAndView.addAllObjects(model);
		}

		LOGGER.trace("Execution authorization information , marketplaceStoreId=[" + accessToken.getMarketplaceStoreId() + "].");

		AccessToken newAccessToken = null;

		if (StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(state) && state.equals(sessionState))
		{

			try
			{
				// Go tmall exchange access_token
				newAccessToken = accessTokenService.get(code, accessToken.getAppkey(), accessToken.getSecret());

				String[] ignoreProperties = new String[]
				{ "appkey", "secret", "integrationId", "authorized", "marketplaceStoreId" };

				BeanUtils.copyProperties(newAccessToken, accessToken, ignoreProperties);

				accessToken.setIntegrationId(accessToken.getMarketplaceStoreId());

				// The authorization information persistence
				accessTokenService.save(accessToken);

				// Correspondence between AppKey and integrationId tell datahub
				dhAuthService.saveAuthInfo(accessToken);

				model.put("integrationId", accessToken.getIntegrationId());

			}
			catch (Exception e)
			{
				model.put("error", e.getMessage());
				return modelAndView.addAllObjects(model);
			}
		}
		else
		{
			String errorMsg = request.getParameter("error_description");
			if (StringUtils.isEmpty(errorMsg))
			{
				errorMsg = "Authorized failed,Please try again later.";
			}
			model.put("error", errorMsg);
		}
		return modelAndView.addAllObjects(model);
	}

	/**
	 * @param request
	 * @return view
	 */
	@RequestMapping(value = "refresh")
	public ModelAndView refresh(HttpServletRequest request)
	{
		String integrationId = request.getParameter("integrationId");
		AccessToken oldToken = null;
		AccessToken newAccessToken = null;
		Map<String, Object> model = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView("oauthResult");

		if (StringUtils.isEmpty(integrationId))
		{
			model.put("error", "404:Missing parameters,refresh failure.");
			return modelAndView.addAllObjects(model);
		}
		try
		{

			oldToken = accessTokenService.get(integrationId);
			newAccessToken = accessTokenService.refresh(oldToken.getAppkey(), oldToken.getSecret(), oldToken.getRefreshToken());

			String[] ignoreProperties = new String[]
			{ "appkey", "secret", "integrationId", "authorized", "marketplaceStoreId" };

			BeanUtils.copyProperties(newAccessToken, oldToken, ignoreProperties);

			// The authorization information persistence
			accessTokenService.save(oldToken);
		}
		catch (Exception e)
		{
			model.put("error", e.getMessage());
			return modelAndView.addAllObjects(model);
		}
		model.put("integrationId", newAccessToken.getIntegrationId());
		return modelAndView.addAllObjects(model);
	}
}
