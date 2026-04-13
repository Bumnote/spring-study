package com.laboratory.proxy.pureproxy.decorator;

import com.laboratory.proxy.pureproxy.decorator.code.Component;
import com.laboratory.proxy.pureproxy.decorator.code.DecoratorPatternClient;
import com.laboratory.proxy.pureproxy.decorator.code.MessageDecorator;
import com.laboratory.proxy.pureproxy.decorator.code.RealComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DecoratorPatternTest {

  @Test
  void noDecoratorTest() {
    RealComponent realComponent = new RealComponent();
    DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
    client.execute();
  }

  @Test
  void decorator1() {
    Component realComponent = new RealComponent();
    Component messageDecorator = new MessageDecorator(realComponent);
    DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
    client.execute();
  }
}
