package com.kh.trip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kh.trip.service.NoticeService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class NoticeController {
	@Autowired
	private NoticeService service;
}
