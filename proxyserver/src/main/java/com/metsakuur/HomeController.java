package com.metsakuur;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

  private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

  /**
   * Simply selects the home view to render by returning its name.
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String home(Locale locale, Model model) {
    logger.info("Welcome home! The client locale is {}.", locale);

    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

    String formattedDate = dateFormat.format(date);

    model.addAttribute("serverTime", formattedDate);

    return "home";
  }

  // loopback : 모든 요청에 대한 정보를 출력하고 같은 정보를 다시 전달한다.
  @RequestMapping(value = "/loopback", method = RequestMethod.GET)
  @ResponseBody
  public String loopback(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // get all request headers
    @SuppressWarnings("unchecked")
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      System.out.println(headerName + ": " + headerValue);

    }
    // get all request parameters
    @SuppressWarnings("unchecked")
    Map<String, String[]> parameterMap = request.getParameterMap();
    for (String key : parameterMap.keySet()) {
      String[] values = parameterMap.get(key);
      for (String value : values) {
        System.out.println(key + ": " + value);
      }
    }
    // send paramenters back
    for (String key : parameterMap.keySet()) {
      String[] values = parameterMap.get(key);
      for (String value : values) {
        // make reponse text value
        String responseText = key + ": " + value + "\n";
        response.getWriter().write(responseText);

      }
    }
    return null;

  }

}
