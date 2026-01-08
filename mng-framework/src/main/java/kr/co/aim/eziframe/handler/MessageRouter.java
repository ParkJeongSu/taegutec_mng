package kr.co.aim.eziframe.handler;

public interface MessageRouter {
    // false면 내 로직(API), true면 BPEL(기본)
    boolean isBpel(String messageName);
}