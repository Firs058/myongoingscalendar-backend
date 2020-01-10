package org.myongoingscalendar.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.myongoingscalendar.SEO.XmlUrl;
import org.myongoingscalendar.SEO.XmlUrlSet;
import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.model.UrlDataDAO;
import org.myongoingscalendar.service.OngoingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(method = GET)
public class SitemapController {
    private final UrlDataDAO urlDataDAO;
    private final OngoingService ongoingService;

    public SitemapController(UrlDataDAO urlDataDAO, OngoingService ongoingService) {
        this.urlDataDAO = urlDataDAO;
        this.ongoingService = ongoingService;
    }

    @RequestMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public XmlUrlSet main(HttpServletRequest request) {
        XmlUrlSet xmlUrlSet = new XmlUrlSet();
        create(request, xmlUrlSet, "", XmlUrl.Priority.HIGH);
        create(request, xmlUrlSet, "/list", XmlUrl.Priority.HIGH);
        create(request, xmlUrlSet, "/about", XmlUrl.Priority.MEDIUM);
        create(request, xmlUrlSet, "/registration", XmlUrl.Priority.MEDIUM);
        create(request, xmlUrlSet, "/login", XmlUrl.Priority.MEDIUM);
        create(request, xmlUrlSet, "/recover", XmlUrl.Priority.MEDIUM);
        List<OngoingEntity> all = ongoingService.findAllByAidIsNotNullOrMalidIsNotNullOrAnnidIsNotNull();
        List<OngoingEntity> ongoings = ongoingService.getCurrentOngoings();
        ongoings.forEach(titlesList -> create(request, xmlUrlSet, "/title/" + titlesList.tid(), XmlUrl.Priority.HIGH));
        all.stream()
                .filter(a -> ongoings.stream().noneMatch(b -> b.tid().equals(a.tid())))
                .forEach(titlesList -> create(request, xmlUrlSet, "/title/" + titlesList.tid(), XmlUrl.Priority.MEDIUM));
        return xmlUrlSet;
    }

    private void create(HttpServletRequest request, XmlUrlSet xmlUrlSet, String link, XmlUrl.Priority priority) {
        xmlUrlSet.addUrl(new XmlUrl(urlDataDAO.getUrlData(request).getAll(link), priority));
    }

    @RequestMapping(value = "/robots.txt")
    public void robot(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String robots = "User-agent: *\n" +
                "Disallow: /admin/\n" +
                "Disallow: /api/\n" +
                "Disallow: /search/\n" +
                "Disallow: /search?\n" +
                "Disallow: /my_calendar\n" +
                "Host: " + urlDataDAO.getUrlData(request).getHost() + "\n" +
                "Sitemap: " + urlDataDAO.getUrlData(request).getDomainAddress() + "/sitemap.xml";
        InputStream stream = new ByteArrayInputStream(robots.getBytes(StandardCharsets.UTF_8));
        response.addHeader("Content-disposition", "filename=robots.txt");
        response.setContentType("text/plain");
        IOUtils.copy(stream, response.getOutputStream());
        response.flushBuffer();
        stream.close();
    }
}