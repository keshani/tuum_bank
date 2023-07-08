package com.tuum.bank.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message<T extends Serializable>{
    String queueName;
    String messagePayLoad;
    T msgBody;
}
