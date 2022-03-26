package com.myself.gmalllogger.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/24 10:48 上午
 */
@RestController
@Slf4j
public class LoggerController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("applog")
    public String getLog(@RequestParam("param") String jsonStr) {

        log.info("jsonStr : {} ", jsonStr);
        kafkaTemplate.send("ods_base_log", jsonStr);

        return "applog success";
    }
}
