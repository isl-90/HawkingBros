package com.api;


import com.entity.MsgA;
import com.entity.MsgB;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public interface AdapterApi {
    CompletableFuture<ResponseEntity<MsgB>> execute(MsgA body);
}
