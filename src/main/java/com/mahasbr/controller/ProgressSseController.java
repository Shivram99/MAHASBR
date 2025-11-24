package com.mahasbr.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mahasbr.util.UploadProgressStore;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/progress")
@RequiredArgsConstructor
public class ProgressSseController {

  private final UploadProgressStore store;

  @GetMapping(value="/{fileId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter stream(@PathVariable String fileId) {
    SseEmitter emitter = new SseEmitter(0L);
    new Thread(() -> {
      try {
        int last = -1;
        while (true) {
          int p = store.get(fileId);
          if (p != last) {
            emitter.send(p);
            last = p;
          }
          if (p >= 100 || p == -1) break;
          Thread.sleep(300);
        }
      } catch (Exception e) {
        emitter.completeWithError(e);
      } finally {
        emitter.complete();
      }
    }).start();
    return emitter;
  }
}

