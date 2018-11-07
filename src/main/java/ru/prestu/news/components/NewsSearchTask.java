package ru.prestu.news.components;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.prestu.news.services.NewsSearchService;

@Component
public class NewsSearchTask implements Job {

    @Autowired
    private NewsSearchService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        service.searchNews();
    }

}
