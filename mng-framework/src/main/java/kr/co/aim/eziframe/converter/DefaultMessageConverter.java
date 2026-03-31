package kr.co.aim.eziframe.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ezieco.eziframe.middleware.event.MessageConverter;
import ezieco.eziframe.middleware.event.MessageType;
import ezieco.eziframe.middleware.event.utils.MessagePoolManager;
import ezieco.eziframe.middleware.exception.MessageConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * <p>Message Body 를 변환하는 로직을 구현합니다.</p>
 * <p>Tibrv와 Kafka 는 String serialize deserialize 한다.</p>
 * <p>RabbitMQ 는 byte[] serialize, deserialize 한다.</p>
 *
 */
//@Component
@Slf4j
@Profile({"pex","tex","scheduler"})
public class DefaultMessageConverter implements MessageConverter {

    private final static String EVENT_PACKAGE = "kr.co.aim.common.format";
    private final ObjectMapper objectMapper;
    private final Map<String, Class<?>> pool;
    
 // 생성자 주입 + 초기화 로직 동시 수행
    public DefaultMessageConverter(ObjectMapper objectMapper) {
        // 1. 스프링이 준 ObjectMapper 받기
        this.objectMapper = objectMapper;
        
        // 2. 내가 필요한 초기화 로직 수행
        MessagePoolManager eventPoolManager = new MessagePoolManager(EVENT_PACKAGE);
        this.pool = eventPoolManager.getPool();
    }
    

    @Override
    public byte[] writeAsByte(Object message) {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new MessageConvertException(e);
        }
    }

    @Override
    public String writeAsString(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new MessageConvertException(e);
        }
    }

    @Override
    public Object read(String messageName, byte[] message) {
        try {
        	return new String(message, java.nio.charset.StandardCharsets.UTF_8);
            //return objectMapper.readValue(message, pool.get(messageName));
        } 
//        catch (IOException e) {
//            throw new MessageConvertException(e);
//        } 
        catch(Exception ex) {
        	throw new MessageConvertException(ex);
        }
    }

    @Override
    public Object read(String messageName, String message) {
        try {
            return objectMapper.readValue(message, pool.get(messageName));
        } catch (IOException e) {
            throw new MessageConvertException(e);
        }
    }

    @Override
    public <T> T read(byte[] message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (IOException e) {
            throw new MessageConvertException(e);
        }
    }

    @Override
    public <T> T read(String message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (IOException e) {
            throw new MessageConvertException(e);
        }
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.JSON;
    }
}
